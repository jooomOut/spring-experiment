package me.jooomout.demorestapistudy.accounts;

import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    @Autowired
    PasswordEncoder passwordEncoder;

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
        accountService.saveAccount(account);

        // WHEN
        UserDetailsService userDetailsService = (UserDetailsService) accountService;
        UserDetails userDetails = userDetailsService.loadUserByUsername(userName);

        //THEN
        assertThat(passwordEncoder.matches(password, userDetails.getPassword())).isTrue();
    }

    @Test
    void findByUsernameFail(){
        String username = "random";
        // 1
        /*try {
            accountService.loadUserByUsername(username);
            fail("supposed to be failed");
        } catch(UsernameNotFoundException e){
            assertThat(e.getMessage()).containsSequence(username);
        }*/

        // 2
        assertThrows(UsernameNotFoundException.class,
                () -> accountService.loadUserByUsername(username));

    }
}