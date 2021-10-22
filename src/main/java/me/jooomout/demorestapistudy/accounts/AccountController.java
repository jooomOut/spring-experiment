package me.jooomout.demorestapistudy.accounts;

import me.jooomout.demorestapistudy.common.ErrorsResource;
import me.jooomout.demorestapistudy.session.SessionConst;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping(value = "api/account", produces = MediaTypes.HAL_JSON_VALUE)
public class AccountController {

    private final ModelMapper modelMapper;
    private final AccountService accountService;
    @Autowired
    public AccountController(ModelMapper modelMapper, AccountService accountService) {
        this.modelMapper = modelMapper;
        this.accountService = accountService;
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Validated AccountDto accountDto, BindingResult errors,
                                HttpServletRequest request) {
        if (errors.hasErrors()){
            return badRequest(errors);
        }
        Account result = accountService.login(dtoToEntity(accountDto));
        if (result == null){
            errors.reject("loginFail", "ID or PW is not correct.");
            return badRequest(errors);
        }

        HttpSession session = request.getSession(true);
        session.setAttribute(SessionConst.LOGIN_ACCOUNT, result);

        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity logout(HttpServletRequest request){
        HttpSession session = request.getSession(false);
        if (session != null){
            session.invalidate();
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
