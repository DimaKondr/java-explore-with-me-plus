package ru.practicum.ewm.repository;

import org.springframework.boot.data.autoconfigure.web.DataWebProperties;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.model.Compilation;

import java.util.List;

@Repository
public interface CompilationRepository extends JpaRepository<Compilation, Long> {

    @Query("""
            SELECT c FROM Compilation c
            LEFT JOIN c.events e
            WHERE (:pinned IS NULL OR c.pinned = :pinned)
            GROUP BY c.id
            HAVING (:size IS NULL OR COUNT(e) = :size)
            """)
    List<Compilation> findAllByPinnedFilter(Boolean pinned, Long size, Pageable pageable);

}
