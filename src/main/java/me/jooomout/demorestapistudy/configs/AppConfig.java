package me.jooomout.demorestapistudy.configs;

import me.jooomout.demorestapistudy.accounts.Account;
import me.jooomout.demorestapistudy.accounts.AccountRepository;
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
            @Autowired
            AppProperties appProperties;
            @Override
            public void run(ApplicationArguments args) throws Exception {
                Account admin = Account.builder()
                        .email("admin@email.com")
                        //.email(appProperties.getAdminUsername())
                        .password("jjjjj")
                        .roles(Set.of(AccountRole.ADMIN))
                        .build();
                Account user = Account.builder()
                        .email("user@email.com")
                        .password("jjjjj")
                        .roles(Set.of(AccountRole.USER))
                        .build();
                accountService.saveAccount(admin);
                accountService.saveAccount(user);
            }
        };
    }
}
