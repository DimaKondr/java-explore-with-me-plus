package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.model.ParticipationRequest;

@Repository
public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {
}
