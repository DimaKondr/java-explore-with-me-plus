package ru.practicum.ewm.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.model.event.Event;
import ru.practicum.ewm.model.event.EventState;

import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@Table(name = "request")
@AllArgsConstructor
@NoArgsConstructor
public class ParticipationRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created")
    private LocalDateTime created;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id")
    private User requester;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private EventState status;
}
