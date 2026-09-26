package com.example.bbs.controller;

import com.example.bbs.dto.CommentForm;
import com.example.bbs.model.Comment;
import com.example.bbs.model.Post;
import com.example.bbs.model.User;
import com.example.bbs.service.CommentService;
import com.example.bbs.service.PostService;
import com.example.bbs.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * コメントコントローラー
 */
@Controller
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;
    private final PostService postService;
    private final UserService userService;
    /**
     * コンストラクタ
     * @param commentService
     * @param postService
     * @param userService
     */
    public CommentController(CommentService commentService, PostService postService, UserService userService) {
        this.commentService = commentService;
        this.postService = postService;
        this.userService = userService;
    }

    /**
     * コメントを追加する
     * @param postId
     * @param commentForm
     * @param result
     * @param ra
     * @return
     */
    @PostMapping("/add")
    public String addComment(
        @RequestParam Long postId, 
        @Valid @ModelAttribute("commentForm") CommentForm commentForm, 
        BindingResult result, 
        RedirectAttributes ra) {
    
        // バリデーションエラーがある場合、エラー情報を含めたフォーム画面へ戻す
        if (result.hasErrors()) {
            // バリデーションエラーがあればリダイレクト先にエラー情報を渡す
            ra.addFlashAttribute("org.springframework.validation.BindingResult.commentForm", result);
            ra.addFlashAttribute("commentForm", commentForm);
            return "redirect:/posts/" + postId;
        }
    
        // 投稿が存在するか確認
        Post post = postService.findById(postId).orElseThrow(() -> new IllegalArgumentException("Invalid post Id:" + postId));
        // 現在ログインしているユーザーを取得
        User loggedInUser = userService.getCurrentUser();

        Comment comment = new Comment();
        // コメントに投稿、内容、ユーザーを設定
        comment.setPost(post);
        comment.setContent(commentForm.getContent());
        comment.setUser(loggedInUser);
        commentService.save(comment);
        return "redirect:/posts/" + postId;
    }

    /**
     * コメントを削除する
     * @param id
     * @return
     */
    @PostMapping("/{id}/delete")
    public String deleteComment(@PathVariable Long id, @RequestParam Long postId) {
        // ログインユーザを取得
        User loggedInUser = userService.getCurrentUser();
        // コメントが存在するか確認
        Comment comment = commentService.findById(id).orElseThrow(() -> new RuntimeException("Comment not found"));

        // 投稿の所有者を確認
        if (!commentService.verifyOwnership(comment, loggedInUser)) {
            // 所有者でない場合はエラーをスローまたはリダイレクト
            return "redirect:/posts/" + postId + "?error=notAuthorized";
        }

        commentService.deleteById(id);
        return "redirect:/posts/" + postId;
    }
}
