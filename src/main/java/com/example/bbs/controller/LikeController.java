package com.example.bbs.controller;

import com.example.bbs.model.Post;
import com.example.bbs.model.User;

import com.example.bbs.service.LikeService;
import com.example.bbs.service.PostService;
import com.example.bbs.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller 
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
    public String toggleLike(@PathVariable Long postId) {
        
        // 現在ログインしているユーザーを取得
        User loggedInUser = userService.getCurrentUser();
        // 投稿を取得
        Post post = postService.findById(postId).orElseThrow(() -> new IllegalArgumentException("Invalid post Id:" + postId));

        // いいねの切り替え処理
        likeService.toggleLike(loggedInUser, post);

        return "redirect:/posts/" + postId;
    }
}
