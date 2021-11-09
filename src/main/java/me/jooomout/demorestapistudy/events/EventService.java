package me.jooomout.demorestapistudy.events;

import me.jooomout.demorestapistudy.accounts.Account;
import org.springframework.stereotype.Service;

@Service
public class EventService {
    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository){
        this.eventRepository = eventRepository;
    }

    public Event createEvent(Event event, Account account){
        event.update();
        event.setManager(account);
        return this.eventRepository.save(event);
    }
}
