package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.comment.CommentResponseDto;
import ru.practicum.ewm.dto.comment.NewCommentDto;
import ru.practicum.ewm.dto.comment.UpdateCommentUserRequest;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.mapper.CommentMapper;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.model.comment.Comment;
import ru.practicum.ewm.model.comment.CommentStatus;
import ru.practicum.ewm.model.event.Event;
import ru.practicum.ewm.repository.CommentRepository;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.UserRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Transactional
    @Override
    public CommentResponseDto addComment(Long userId, Long eventId, NewCommentDto dto) {
        User author = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
                "Добавление комментария. Пользователь с ID: " + userId + " не найден."));

        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException(
                "Добавление комментария. Событие с ID: " + eventId + " не найдено."));

        Comment newComment = CommentMapper.dtoToComment(
                dto,
                CommentStatus.PENDING,
                LocalDateTime.now(),
                LocalDateTime.now(),
                author,
                event
        );

        Comment addedComment = commentRepository.save(newComment);
        log.info("Создан новый комментарий с ID: {}.", addedComment.getId());
        return CommentMapper.commentToResponseDto(addedComment);
    }

    @Transactional
    @Override
    public CommentResponseDto patchCommentById(UpdateCommentUserRequest dto) {
        Comment oldComment = commentRepository.findById(dto.getId()).orElseThrow(() -> new NotFoundException(
                "Обновление комментария. Комментарий с ID: " + dto.getId() + " не найден."));

        if (!oldComment.getAuthor().getId().equals(dto.getUserId())) {
            log.error("Обновление комментария. Переданное ID пользователя не совпадает с ID автора комментария.");
            throw new ValidationException("Обновление комментария. " +
                    "Переданное ID пользователя не совпадает с ID автора комментария.");
        } else if (!userRepository.existsById(dto.getUserId())) {
            log.error("Обновление комментария. Пользователь с ID: {} не найден.", dto.getUserId());
            throw new NotFoundException("Обновление комментария. " +
                    "Пользователь с ID: " + dto.getUserId() + " не найден.");
        }

        if (!oldComment.getEvent().getId().equals(dto.getEventId())) {
            log.error("Обновление комментария. Переданное ID события не совпадает с ID события комментария.");
            throw new ValidationException("Обновление комментария. " +
                    "Переданное ID события не совпадает с ID события комментария.");
        } else if (!eventRepository.existsById(dto.getEventId())) {
            log.error("Обновление комментария. Событие с ID: {} не найдено.", dto.getEventId());
            throw new NotFoundException("Обновление комментария. Событие с ID: " + dto.getEventId() + " не найдено.");
        }

        oldComment.setContent(dto.getContent());
        oldComment.setUpdatedAt(LocalDateTime.now());

        Comment patchedComment = commentRepository.save(oldComment);
        log.info("Данные комментария с ID: {} обновлены.", oldComment.getId());
        return CommentMapper.commentToResponseDto(patchedComment);
    }

    @Transactional
    @Override
    public void removeCommentById(Long userId, Long eventId, Long commentId) {
        Comment removedComment = commentRepository.findById(commentId).orElseThrow(() -> new NotFoundException(
                "Удаление комментария. Комментарий с ID: " + commentId + " не найден."));

        if (!removedComment.getAuthor().getId().equals(userId)) {
            log.error("Удаление комментария. Переданное ID пользователя не совпадает с ID автора комментария.");
            throw new ValidationException("Удаление комментария. " +
                    "Переданное ID пользователя не совпадает с ID автора комментария.");
        } else if (!userRepository.existsById(userId)) {
            log.error("Удаление комментария. Пользователь с ID: {} не найден.", userId);
            throw new NotFoundException("Удаление комментария. " +
                    "Пользователь с ID: " + userId + " не найден.");
        }

        if (!removedComment.getEvent().getId().equals(eventId)) {
            log.error("Удаление комментария. Переданное ID события не совпадает с ID события комментария.");
            throw new ValidationException("Удаление комментария. " +
                    "Переданное ID события не совпадает с ID события комментария.");
        } else if (!eventRepository.existsById(eventId)) {
            log.error("Удаление комментария. Событие с ID: {} не найдено.", eventId);
            throw new NotFoundException("Удаление комментария. Событие с ID: " + eventId + " не найдено.");
        }

        commentRepository.deleteById(commentId);
        log.info("Комментарий с ID: {} удален.", commentId);
    }

}