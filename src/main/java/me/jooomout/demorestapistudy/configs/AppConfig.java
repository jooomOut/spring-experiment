package me.jooomout.demorestapistudy.configs;

import me.jooomout.demorestapistudy.accounts.Account;
import me.jooomout.demorestapistudy.accounts.AccountRole;
import me.jooomout.demorestapistudy.accounts.AccountService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
public class AppConfig {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public ApplicationRunner applicationRunner() {
        return new ApplicationRunner() {

            @Autowired
            AccountService accountService;

            @Override
            public void run(ApplicationArguments args) throws Exception {
                Account jOut = Account.builder()
                        .email("jOut@naver.com")
                        .password("jjjjj")
                        .roles(Set.of(AccountRole.USER, AccountRole.ADMIN))
                        .build();
                accountService.saveAccount(jOut);
            }
        };
    }
}
