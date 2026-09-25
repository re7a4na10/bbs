package com.example.bbs.service;

import com.example.bbs.model.Like;
import com.example.bbs.model.Post;
import com.example.bbs.model.User;

import com.example.bbs.repository.LikeRepository;
import java.util.Optional;

import org.springframework.stereotype.Service;

/**
 * いいねサービスクラス
 */
@Service
public class LikeService {
    private final LikeRepository likeRepository;
    
    /**
     * コンストラクタ
     * @param commentRepository
     */
    public LikeService(LikeRepository likeRepository) {
        this.likeRepository = likeRepository;
    }

    /**
     * いいねの切り替え処理
     * @param user
     * @param post
     */
    public void toggleLike(User user, Post post) {
        Optional<Like> existingLike = likeRepository.findByUserAndPost(user, post);

        if (existingLike.isPresent()) {
            // いいねを削除
            likeRepository.delete(existingLike.get());
        } else {
            // いいねを登録
            Like like = new Like();
            like.setPost(post);
            like.setUser(user);
            likeRepository.save(like);   
        }
    }

    /**
     * いいねの存在確認
     * @param post
     * @param user
     * @return
     */
    public boolean isLikedByUser(Post post, User user) {
        return likeRepository.existsByPostAndUser(post, user);
    }

    /**
     * いいねの件数取得
     * @param post
     * @return
     */
    public int countLikesForPost(Post post) {
        return likeRepository.countByPost(post);
    }


}
