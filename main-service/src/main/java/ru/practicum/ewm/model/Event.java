package ru.practicum.ewm.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Builder
@Getter
@Table(name = "event")
@AllArgsConstructor
@NoArgsConstructor
// заглушка (пустой временный файл)
public class Event {

    @Id
    private Long id;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "event_compilation", // Имя промежуточной таблицы
            joinColumns = @JoinColumn(name = "event_id"), // Ключ текущей сущности
            inverseJoinColumns = @JoinColumn(name = "compilation_id") // Ключ связанной сущности
    )
    private List<Compilation> compilations;

}
