package com.example.bbs.controller;

import com.example.bbs.model.Post;
import com.example.bbs.model.User;
import com.example.bbs.service.LikeService;
import com.example.bbs.service.PostService;
import com.example.bbs.service.UserService;
import java.util.HashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.*;


@RestController 
@RequestMapping("/posts")
public class LikeController {
        
    private final LikeService likeService;
    private final PostService postService;
    private final UserService userService;

    /**
     * コンストラクタ
     * @param likeService
     * @param postService
     * @param userService
     */
    public LikeController(LikeService likeService, PostService postService, UserService userService) {
        this.likeService = likeService;
        this.postService = postService;
        this.userService = userService;
    }

    /**
     * いいねの切り替え処理
     * @param postId
     * @return
     */
    @PostMapping("/{postId}/like")
    public Map<String,Object> toggleLike(@PathVariable Long postId) {

        // レスポンスの変数を定義
        Map<String, Object> response = new HashMap<>();
        
        // 現在ログインしているユーザーを取得
        User loggedInUser = userService.getCurrentUser();
        // 投稿を取得
        Post post = postService.findById(postId).orElseThrow(() -> new IllegalArgumentException("Invalid post Id:" + postId));

        // いいねを切り替える（登録 or 削除）
        likeService.toggleLike(loggedInUser, post);

        // ログインユーザがこの投稿にいいねしているかどうか判定
        boolean isLiked = likeService.isLikedByUser(post, loggedInUser);

        // この投稿のいいねの数を取得
        int likeCount = likeService.countLikesForPost(post);

        // JSONレスポンスを返す
        response.put("isLiked", isLiked);
        response.put("likeCount", likeCount);

        return response;
    }
}
