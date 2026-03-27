package ru.practicum.ewm.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Getter
@Table(name = "user")
@AllArgsConstructor
@NoArgsConstructor
// заглушка (пустой временный файл)
public class User {

    @Id
    private Long id;

}
