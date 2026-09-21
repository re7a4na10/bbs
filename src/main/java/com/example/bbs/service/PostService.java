package com.example.bbs.service;

import com.example.bbs.model.Post;
import com.example.bbs.model.User;
import com.example.bbs.repository.PostRepository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 投稿サービスクラス
 */
@Service
public class PostService {
    private final PostRepository postRepository;

    /**
     * コンストラクタ
     * @param postRepository
     */
    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    /**
     * 投稿一覧の取得
     * @param sortBy
     * @param sortOrder
     * @param page
     * @param size
     * @return
     */
    public Page<Post> findAll(String sortBy, String sortOrder, int page, int size) {
       // Sort.Orderを使ってソート順を設定
        Sort.Order order;
        if (sortOrder.equals("asc")) {
            order = new Sort.Order(Sort.Direction.ASC, sortBy);
        } else {
            order = new Sort.Order(Sort.Direction.DESC, sortBy);
        }
        // Sortオブジェクトを作成
        Sort sort = Sort.by(order);
        // ページ情報を作成
        Pageable pageable = PageRequest.of(page, size, sort);

        return postRepository.findAll(pageable);
    }

    /**
     * 投稿をキーワードで検索
     * @param keyword
     * @param matchType
     * @param sortBy
     * @param sortOrder
     * @param page
     * @param size
     * @return
     */
    public Page<Post> searchPosts(String keyword, String matchType, String sortBy, String sortOrder, int page, int size) {
        // Sort.Orderを使ってソート順を設定
        Sort.Order order;
        if (sortOrder.equals("asc")) {
            order = new Sort.Order(Sort.Direction.ASC, sortBy);
        } else {
            order = new Sort.Order(Sort.Direction.DESC, sortBy);
        }
        // Sortオブジェクトを作成
        Sort sort = Sort.by(order);

        // ページ情報を作成
        Pageable pageable = PageRequest.of(page, size, sort);

        switch (matchType) {
            // 前方一致
            case "startsWith":
                return postRepository.findByTitleStartingWithOrContentStartingWith(keyword, keyword, pageable); 
            // 後方一致
            case "endsWith":
                return postRepository.findByTitleEndingWithOrContentEndingWith(keyword, keyword, pageable);
            // 部分一致
            case "contains":
            default:
                return postRepository.findByTitleContainingOrContentContaining(keyword, keyword, pageable);
        }
    }

    /**
     * 投稿のIDで検索
     * @param id
     * @return
     */
    public Optional<Post> findById(Long id) {
        return postRepository.findById(id);
    }

    /**
     * 投稿の保存
     * @param post
     * @return
     */
    public Post save(Post post) {
        return postRepository.save(post);
    }

    /**
     * 投稿の削除
     * @param id
     */
    public void deleteById(Long id) {
        postRepository.deleteById(id);
    }

    /**
     * ログインユーザが投稿の所有者かどうか判定
     * @param post
     * @param user
     * @return
     */
    public boolean verifyOwnership(Post post, User user) {
        if(post.getUser() == null) {
            return false;
        }
        if (!post.getUser().getId().equals(user.getId())) {
            return false;
        }
        return true;
    }
}
