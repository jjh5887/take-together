package me.powerarc.taketogether.event;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface EventRepository extends JpaRepository<Event, Long> {
    Page<Event> findByNameContains(String name, Pageable pageable);

    Page<Event> findByDepartureContains(String departure, Pageable pageable);

    Page<Event> findByDestinationContains(String destination, Pageable pageable);

    Page<Event> findByDepartureTimeBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);

    Page<Event> findByArrivalTimeBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);

    Page<Event> findByDepartureTimeBetweenAndArrivalTimeBetween(LocalDateTime startD, LocalDateTime endD, LocalDateTime startA, LocalDateTime endA, Pageable pageable);

    boolean existsByName(String name);
}
