package me.powerarc.taketogether.taxi;

import me.powerarc.taketogether.event.Event;
import me.powerarc.taketogether.event.EventRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface TaxiRepository extends EventRepository<Taxi> {

    Page<Event> findByKind(String kind, Pageable pageable);
}


