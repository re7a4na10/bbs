package com.example.bbs.service;

import com.example.bbs.model.Comment;
import com.example.bbs.model.User;
import com.example.bbs.repository.CommentRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

/**
 * コメントサービスクラス
 */
@Service
public class CommentService {
    private final CommentRepository commentRepository;

    /**
     * コンストラクタ
     * @param commentRepository
     */
    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    /**
     * コメントIDでコメントを取得
     * @param id
     * @return
     */
    public Optional<Comment> findById(Long id) {
        return commentRepository.findById(id);
    }

    /**
     * コメントの投稿IDでコメントを取得
     * @param postId
     * @return
     */
    public List<Comment> findByPostId(Long postId) {
        return commentRepository.findByPostId(postId);
    }

    /**
     * コメントの保存
     * @param comment
     * @return
     */
    public Comment save(Comment comment) {
        return commentRepository.save(comment);
    }

    /**
     * コメントの削除
     * @param id
     */
    public void deleteById(Long id) {
        commentRepository.deleteById(id);
    }

    /**
     * ログインユーザがコメントの所有者かどうか判定
     * @param comment
     * @param user
     * @return
     */
    public boolean verifyOwnership(Comment comment, User user) {
        if(comment.getUser() == null) {
            return false;
        }
        if (!comment.getUser().getId().equals(user.getId())) {
            return false;
        }
        return true;
    }
}
