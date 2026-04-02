package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.model.request.ParticipationRequest;
import ru.practicum.ewm.model.request.RequestStatus;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("checkstyle:Regexp")
@Repository
public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {

    @Query("""
            select r
            from ParticipationRequest r
            where r.requester.id = :userId
            """)
    List<ParticipationRequest> findAllByUserId(long userId);

    @Query("""
            UPDATE ParticipationRequest r
            SET r.status = :state
            WHERE r.id = :requestId
            """)
    int changeState(long requestId, RequestStatus state);

    Long countByEvent_IdAndStatus(Long eventId, RequestStatus status);

    List<ParticipationRequest> findAllByEvent_Id(Long eventId);

    List<ParticipationRequest> findAllByIdInAndStatusOrderByCreatedAsc(List<Long> requestsIds, RequestStatus status);

    Optional<ParticipationRequest> findByRequester_IdAndEvent_Id(Long userId, Long eventId);
}