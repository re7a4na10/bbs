package com.example.bbs.controller;

import com.example.bbs.dto.CommentForm;
import com.example.bbs.dto.PostForm;
import com.example.bbs.model.Post;
import com.example.bbs.model.User;
import com.example.bbs.service.PostService;
import com.example.bbs.service.UserService;

import jakarta.validation.Valid;

import com.example.bbs.service.LikeService;
import com.example.bbs.service.CommentService;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;
    private final UserService userService;
    private final CommentService commentService;
    private final LikeService likeService;

    /**
     * コンストラクタ
     * @param postService
     * @param userService
     * @param commentService
     * @param likeService
     */
    public PostController(
        PostService postService, 
        UserService userService, 
        CommentService commentService, 
        LikeService likeService) {
        
        this.postService = postService;
        this.userService = userService;
        this.commentService = commentService;
        this.likeService = likeService;
    }

    /**
     * 投稿一覧
     * @param keyword
     * @param matchType
     * @param sortBy
     * @param sortOrder
     * @param page
     * @param model
     * @return
     */
    @GetMapping
    public String listPosts(
        @RequestParam(value="keyword", required = false) String keyword, 
        @RequestParam(value="matchType", required = false, defaultValue = "contains") String matchType, 
        @RequestParam(value="sortBy", required = false, defaultValue = "createdAt") String sortBy, 
        @RequestParam(value="sortOrder", required = false, defaultValue = "asc") String sortOrder, 
        @RequestParam(value="page", required = false, defaultValue = "0") int page, 
        Model model) {

        // 現在ログインしているユーザーを取得
        User loggedInUser = userService.getCurrentUser();
        model.addAttribute("loggedInUserId", loggedInUser.getId());

        // サイズを固定（例：3件ずつ表示）
        int size = 3;

        // 検索フォームの入力値
        Page<Post> posts;
        if (keyword != null && !keyword.isEmpty() && matchType != null && !matchType.isEmpty()){
            posts = postService.searchPosts(keyword, matchType, sortBy, sortOrder, page, size);
        } else {
            posts = postService.findAll( sortBy, sortOrder, page, size);
        }

        model.addAttribute("posts", posts);

        return "posts/list";
    }

    /**
     * 投稿詳細
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/{id}")
    public String viewPost(@PathVariable Long id, Model model) {

        // 現在ログインしているユーザーを取得
        User loggedInUser = userService.getCurrentUser();

        // 投稿情報をモデルに渡す
        Post post = postService.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid post Id:" + id));
        
        // ログインユーザがこの投稿にいいねしているかどうか判定
        boolean isLiked = likeService.isLikedByUser(post, loggedInUser);

        // この投稿のいいねの数を取得
        int likeCount = likeService.countLikesForPost(post);

        // コメントフォームを追加
        if (!model.containsAttribute("commentForm")) {
            model.addAttribute("commentForm", new CommentForm());
        }

        model.addAttribute("post", post);
        model.addAttribute("comments", commentService.findByPostId(id));
        model.addAttribute("loggedInUserId", loggedInUser.getId());
        model.addAttribute("isLiked", isLiked);
        model.addAttribute("likeCount", likeCount);

        return "posts/detail";
    }

    /**
     * 新規投稿フォーム
     * @param model
     * @return
     */
    @GetMapping("/new")
    public String newPostForm(Model model) {
        model.addAttribute("post", new Post());
        return "posts/new";
    }

    /**
     * 投稿の保存
     * @param postForm
     * @param result
     * @param model
     * @return
     */
    @PostMapping
    public String createPost(
        @Valid @ModelAttribute("post") PostForm postForm, 
        BindingResult result, 
        Model model) {
        
        // バリデーションエラーがある場合、エラー情報を含めたフォーム画面へ戻す
        if (result.hasErrors()) {
            model.addAttribute("post", postForm);
            return "posts/new";
        }

        // 現在ログインしているユーザーを取得
        User loggedInUser = userService.getCurrentUser();
        
        // バリデーションが通ったら、DTOの内容をエンティティに変換
        Post post = new Post();
        post.setTitle(postForm.getTitle());
        post.setContent(postForm.getContent());
        post.setUser(loggedInUser);

        postService.save(post);
        return "redirect:/posts";
    }

    /**
     * 投稿の編集フォーム
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/{id}/edit")
    public String editPostForm(@PathVariable Long id, Model model) {
        // ログインユーザを取得
        User loggedInUser = userService.getCurrentUser();
               
        // 投稿を取得
        Post post = postService.findById(id).orElseThrow(() -> new RuntimeException("post not found"));
        
        // 投稿の所有者を確認
        if (!postService.verifyOwnership(post, loggedInUser)) {
            // 所有者でない場合はエラーをスローまたはリダイレクト
            return "redirect:/posts?error=notAuthorized";
        }

        model.addAttribute("post", post);
        return "posts/edit";
    }

    /**
     * 投稿の更新
     * @param id
     * @param post
     * @return
     */
    @PostMapping("/{id}")
    public String updatePost(@PathVariable Long id, @ModelAttribute Post post) {

        // ログインユーザを取得
        User loggedInUser = userService.getCurrentUser();
               
        // 投稿を取得
        Post existingPost = postService.findById(id).orElseThrow();
        
        // 投稿の所有者を確認
        if (!postService.verifyOwnership(existingPost, loggedInUser)) {
            // 所有者でない場合はエラーをスローまたはリダイレクト
            return "redirect:/posts?error=notAuthorized";
        }

        // 投稿を更新
        post.setId(id);
        post.setUser(loggedInUser);
        postService.save(post);
        return "redirect:/posts";
    }

    /**
     * 投稿の削除
     * @param id
     * @return
     */
    @PostMapping("/{id}/delete")
    public String deletePost(@PathVariable Long id) {
        // ログインユーザを取得
        User loggedInUser = userService.getCurrentUser();

        // 投稿を取得
        Post post = postService.findById(id).orElseThrow(() -> new RuntimeException("post not found"));
        
        // 投稿の所有者を確認
        if (!postService.verifyOwnership(post, loggedInUser)) {
            // 所有者でない場合はエラーをスローまたはリダイレクト
            return "redirect:/posts?error=notAuthorized";
        }
        // 投稿を削除
        postService.deleteById(id);
        return "redirect:/posts";
    }
} 
