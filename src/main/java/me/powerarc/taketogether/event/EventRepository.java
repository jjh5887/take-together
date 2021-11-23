package me.powerarc.taketogether.event;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface EventRepository<T extends Event> extends JpaRepository<T, Long> {
    Page<T> findByNameContains(String name, Pageable pageable);

    Page<T> findByDepartureContains(String departure, Pageable pageable);

    Page<T> findByDestinationContains(String destination, Pageable pageable);

    Page<T> findByDepartureTimeBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);

    Page<T> findByArrivalTimeBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);

    Page<T> findByDepartureTimeBetweenAndArrivalTimeBetween(LocalDateTime startD, LocalDateTime endD, LocalDateTime startA, LocalDateTime endA, Pageable pageable);

    boolean existsByName(String name);
}
