package me.jooomout.demorestapistudy.events;

import me.jooomout.demorestapistudy.accounts.Account;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
public class EventService {
    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;

    public EventService(EventRepository eventRepository, ModelMapper modelMapper){
        this.eventRepository = eventRepository;
        this.modelMapper = modelMapper;
    }

    public Event createEvent(Event event, Account account){
        event.update();
        event.setManager(account);
        return this.eventRepository.save(event);
    }

    public Page<Event> getEvents(Pageable pageable){
        return eventRepository.findAll(pageable); // page, size, sort 파라미터로 자동 완성해줌
    }

    public Event getEvent(Integer id) throws EntityNotFoundException {
        Optional<Event> optionalEvent = eventRepository.findById(id);
        if (optionalEvent.isEmpty()){
            throw new EntityNotFoundException("event is not found, id is " + id);
        }

        return optionalEvent.get();
    }

    public Event updateEvent(Integer id, EventDto eventForUpdate, Account account) {
        Optional<Event> optionalEvent = eventRepository.findById(id);
        if (optionalEvent.isEmpty()){
            throw new EntityNotFoundException("event is not found, id is " + id);
        }
        Event existingEvent = optionalEvent.get();
        if(existingEvent.getManager() != null &&!existingEvent.getManager().equals(account)){
            //return new ResponseEntity(HttpStatus.UNAUTHORIZED);
            throw new AuthorizationServiceException("only same user can update event");
        }
        modelMapper.map(eventForUpdate, existingEvent);
        return eventRepository.save(existingEvent);
    }
}
