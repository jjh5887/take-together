package me.powerarc.taketogether.event;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByNameContains(String name);

    List<Event> findByDepartureContains(String departure);

    List<Event> findByDestinationContains(String destination);

    List<Event> findByDepartureTimeBetween(LocalDateTime start, LocalDateTime end);

    List<Event> findByArrivalTimeBetween(LocalDateTime start, LocalDateTime end);

    List<Event> findByDepartureTimeBetweenAndArrivalTimeBetween(LocalDateTime startD, LocalDateTime endD, LocalDateTime startA, LocalDateTime endA);

    boolean existsByName(String name);
}
