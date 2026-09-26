package com.example.bbs.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentForm {

    @NotBlank(message = "{error.comment.content.blank}")
    @Size(max=100, message = "{error.comment.content.size}")
    private String content;
}
