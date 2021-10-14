package me.jooomout.demorestapistudy.events;

import me.jooomout.demorestapistudy.accounts.Account;
import me.jooomout.demorestapistudy.accounts.AccountAdapter;
import me.jooomout.demorestapistudy.accounts.CurrentUser;
import me.jooomout.demorestapistudy.common.ErrorsResource;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Controller
@RequestMapping(value = "api/events", produces = MediaTypes.HAL_JSON_VALUE)
public class EventController {

    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;
    private final EventValidator eventValidator;

    public EventController(EventRepository eventRepository, ModelMapper modelMapper, EventValidator eventValidator) {
        this.eventRepository = eventRepository;
        this.modelMapper = modelMapper;
        this.eventValidator = eventValidator;
    }

    @PostMapping
    public ResponseEntity createEvent(@RequestBody @Validated EventDto eventDto,
                                      BindingResult errors,
                                      //Errors errors,
                                      @CurrentUser Account account) {
        if (errors.hasErrors()){
            return badRequest(errors);
        }
        eventValidator.validate(eventDto, errors);
        if (errors.hasErrors()){
            return badRequest(errors);
        }

        Event event = modelMapper.map(eventDto, Event.class);
        event.update();
        event.setManager(account);
        Event newEvent = this.eventRepository.save(event);

        // Hateos
        WebMvcLinkBuilder selfLinkBuilder = linkTo(EventController.class).slash(newEvent.getId());
        URI createdUri = selfLinkBuilder.toUri();

        EventResource eventResource = new EventResource(event);
        eventResource.add(linkTo(EventController.class).withRel("query-events"));
        eventResource.add(selfLinkBuilder.withRel("update-event")); // 수정은 PUT 이라 링크가 같아도 괜찮다.
        eventResource.add(Link.of("/docs/index.html#resources-events-create").withRel("profile"));
        return ResponseEntity.created(createdUri).body(eventResource);
    }

    @GetMapping
    public ResponseEntity queryEvents(Pageable pageable,
                                      PagedResourcesAssembler<Event> assembler,
                                      @CurrentUser Account account){
        Page<Event> page = this.eventRepository.findAll(pageable); // page, size, sort 파라미터로 자동 완성해줌
        var pagedResources = assembler.toModel(page, e -> new EventResource(e));
        pagedResources.add(Link.of("/docs/index.html#resources-events-list").withRel("profile"));
        if (account != null){
            pagedResources.add(linkTo(EventController.class).withRel("create-event"));
        }
        return ResponseEntity.ok(pagedResources);
    }

    @GetMapping("/{id}")
    public ResponseEntity getEvent(@PathVariable Integer id,
                                   @CurrentUser Account account){
        Optional<Event> optionalEvent = eventRepository.findById(id);
        if (optionalEvent.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        Event event = optionalEvent.get();
        EventResource eventResource = new EventResource(event);
        eventResource.add(Link.of("/docs/index.html#resources-events-get").withRel("profile")); // index.adoc 에 이름이 정의되어 있다.
        if (event.getManager() != null && event.getManager().equals(account)){
            eventResource.add(linkTo(EventController.class).slash(event.getId()).withRel("update-event"));
        }
        return ResponseEntity.ok(eventResource);

    }

    @PutMapping("/{id}")
    public ResponseEntity updateEvent(@PathVariable Integer id,
                                      @RequestBody @Valid EventDto eventDto,
                                      BindingResult errors,
                                      //Errors errors,
                                      @CurrentUser Account account){
        Optional<Event> optionalEvent = eventRepository.findById(id);
        if (optionalEvent.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        if (errors.hasErrors()){
            return badRequest(errors);
        }
        eventValidator.validate(eventDto, errors);
        if (errors.hasErrors()){
            return badRequest(errors);
        }
        Event existingEvent = optionalEvent.get();
        if(existingEvent.getManager() != null &&!existingEvent.getManager().equals(account)){
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
        modelMapper.map(eventDto, existingEvent);
        Event updatedEvent = eventRepository.save(existingEvent);

        EventResource eventResource = new EventResource(updatedEvent);
        eventResource.add(Link.of("/docs/index.html#resources-events-update").withRel("profile"));
        return ResponseEntity.ok(eventResource);
    }
    private ResponseEntity badRequest(Errors errors){
        return ResponseEntity.badRequest().body(ErrorsResource.modelOf(errors));
    }

}
