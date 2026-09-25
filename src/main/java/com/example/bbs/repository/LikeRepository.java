package com.example.bbs.repository;

import com.example.bbs.model.Like;
import com.example.bbs.model.Post;
import com.example.bbs.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByUserAndPost(User user, Post post);

    boolean existsByPostAndUser(Post post, User user);

    int countByPost(Post post);
}
