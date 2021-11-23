package me.powerarc.taketogether.taxi;

import me.powerarc.taketogether.account.AccountService;
import me.powerarc.taketogether.event.Event;
import me.powerarc.taketogether.event.EventService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class TaxiService extends EventService {
    public TaxiService(TaxiRepository taxiRepository, AccountService accountService, ModelMapper modelMapper) {
        super(taxiRepository, accountService, modelMapper);
        clazz = Taxi.class;
    }

    public Page<Event> getEventByKind(String kind, Pageable pageable) {
        return ((TaxiRepository) eventRepository).findByKind(kind, pageable);
    }
}
