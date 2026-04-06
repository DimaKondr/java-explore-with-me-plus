package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.model.comment.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
}