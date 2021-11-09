package me.jooomout.demorestapistudy.events;

import me.jooomout.demorestapistudy.accounts.Account;
import me.jooomout.demorestapistudy.accounts.AccountAdapter;
import me.jooomout.demorestapistudy.accounts.CurrentUser;
import me.jooomout.demorestapistudy.common.ErrorsResource;
import org.apache.coyote.Response;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.persistence.Entity;
import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Controller
@RequestMapping(value = "api/events", produces = MediaTypes.HAL_JSON_VALUE)
public class EventController {

    private final ModelMapper modelMapper;
    private final EventValidator eventValidator;
    private EventService eventService;
    @Autowired
    public EventController(ModelMapper modelMapper, EventValidator eventValidator, EventService eventService) {
        this.modelMapper = modelMapper;
        this.eventValidator = eventValidator;
        this.eventService = eventService;
    }

    @PostMapping
    public ResponseEntity createEvent(@RequestBody @Validated EventDto eventDto,
                                      BindingResult errors,
                                      //Errors errors,
                                      @CurrentUser Account account) {
        var validation = validateEvent(eventDto, errors);
        if (validation != null)
            return validation;

        // Service Login
        Event event = modelMapper.map(eventDto, Event.class);
        Event newEvent = eventService.createEvent(event, account);

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

        Page<Event> page = eventService.getEvents(pageable);

        var pagedResources = assembler.toModel(page, EventResource::new);
        pagedResources.add(Link.of("/docs/index.html#resources-events-list").withRel("profile"));
        if (account != null){
            pagedResources.add(linkTo(EventController.class).withRel("create-event"));
        }
        return ResponseEntity.ok(pagedResources);
    }

    @GetMapping("/{id}")
    public ResponseEntity getEvent(@PathVariable Integer id,
                                   @CurrentUser Account account){
        Event event;
        try {
            event = eventService.getEvent(id);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build(); //TODO: 에러 메세지 담기
        }
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
        var validation = validateEvent(eventDto, errors);
        if (validation != null)
            return validation;

        Event updatedEvent;
        try {
            updatedEvent = eventService.updateEvent(id, eventDto, account);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build(); //TODO: 에러 메세지 담기
        } catch (AuthorizationServiceException e){
            return new ResponseEntity(HttpStatus.UNAUTHORIZED); //TODO 에러 메세지 담기
        }

        EventResource eventResource = new EventResource(updatedEvent);
        eventResource.add(Link.of("/docs/index.html#resources-events-update").withRel("profile"));
        return ResponseEntity.ok(eventResource);
    }

    private ResponseEntity badRequest(Errors errors){
        return ResponseEntity.badRequest().body(ErrorsResource.modelOf(errors));
    }

    private ResponseEntity validateEvent(EventDto eventDto, BindingResult errors) {
        if (errors.hasErrors()){
            return badRequest(errors);
        }
        eventValidator.validate(eventDto, errors);
        if (errors.hasErrors()){
            return badRequest(errors);
        }
        return null;
    }
}
