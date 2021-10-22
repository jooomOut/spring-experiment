package me.jooomout.demorestapistudy.accounts;

import me.jooomout.demorestapistudy.common.ErrorsResource;
import me.jooomout.demorestapistudy.events.*;
import me.jooomout.demorestapistudy.session.SessionManager;
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
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Controller
@RequestMapping(value = "api/account", produces = MediaTypes.HAL_JSON_VALUE)
public class AccountController {

    private final ModelMapper modelMapper;
    private AccountService accountService;
    private SessionManager sessionManager;
    @Autowired
    public AccountController(ModelMapper modelMapper, AccountService accountService, SessionManager sessionManager) {
        this.modelMapper = modelMapper;
        this.accountService = accountService;
        this.sessionManager = sessionManager;
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Validated AccountDto accountDto, BindingResult errors,
                                HttpServletResponse response) {
        if (errors.hasErrors()){
            return badRequest(errors);
        }
        Account formAccount = dtoToEntity(accountDto);
        Account result = accountService.login(formAccount);
        if (result == null){
            errors.reject("loginFail", "ID or PW is not correct.");
            return badRequest(errors);
        }

        sessionManager.createSession(accountDto, response);

        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity logout(HttpServletRequest request){
        sessionManager.expire(request);

        return ResponseEntity.ok().build();
    }

    private ResponseEntity badRequest(BindingResult errors){
        return ResponseEntity.badRequest().body(ErrorsResource.modelOf(errors));
    }
    private Account dtoToEntity(AccountDto accountDto){
        return Account.builder()
                .email(accountDto.getEmail())
                .password(accountDto.getPassword())
                .build();
    }
}
