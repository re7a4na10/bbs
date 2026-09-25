package com.example.bbs.model;

import org.hibernate.annotations.CreationTimestamp;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

/**
 * いいねモデルクラス
 */
@Entity 
@Getter 
@Setter
@Table (name = "likes")
public class Like {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne 
    @JoinColumn (name = "user_id")
    private User user;

    @ManyToOne 
    @JoinColumn (name = "post_id")
    private Post post;

    @Column (name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp 
    private LocalDateTime createdAt;
}
