package me.jooomout.demorestapistudy.accounts;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class AccountServiceTest {

    @Autowired
    AccountService accountService;
    @Autowired
    AccountRepository accountRepository;

    @Test
    void findByUserName(){
        // GIVEN
        String userName = "rimeilo324@naver.com";
        String password = "jjjjj";
        Account account = Account.builder()
                .email(userName)
                .password(password)
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build()
                ;
        accountRepository.save(account);

        // WHEN
        UserDetailsService userDetailsService = (UserDetailsService) accountService;
        UserDetails userDetails = userDetailsService.loadUserByUsername(userName);

        //THEN
        assertThat(userDetails.getPassword()).isEqualTo(password);
    }
}