package me.jooomout.demorestapistudy.accounts;

import me.jooomout.demorestapistudy.common.ErrorsResource;
import me.jooomout.demorestapistudy.events.*;
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

import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Controller
@RequestMapping(value = "api/account", produces = MediaTypes.HAL_JSON_VALUE)
public class AccountController {

    private final ModelMapper modelMapper;
    private AccountService accountService;

    @Autowired
    public AccountController(ModelMapper modelMapper, AccountService accountService) {
        this.modelMapper = modelMapper;
        this.accountService = accountService;
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Validated AccountDto accountDto, BindingResult errors) {
        if (errors.hasErrors()){
            return badRequest(errors);
        }
        Account formAccount = dtoToEntity(accountDto);
        Account result = accountService.login(formAccount);
        if (result == null){
            return ResponseEntity.notFound().build();
        }
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
