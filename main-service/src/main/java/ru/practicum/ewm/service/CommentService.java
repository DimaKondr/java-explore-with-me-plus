package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.comment.CommentResponseDto;
import ru.practicum.ewm.dto.comment.NewCommentDto;
import ru.practicum.ewm.dto.comment.UpdateCommentUserRequest;

public interface CommentService {

    CommentResponseDto addComment(Long userId, Long eventId, NewCommentDto dto);

    CommentResponseDto patchCommentById(UpdateCommentUserRequest dto);

    void removeCommentById(Long userId, Long eventId, Long commentId);
}