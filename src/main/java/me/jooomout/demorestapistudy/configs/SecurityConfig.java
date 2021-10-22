package me.jooomout.demorestapistudy.configs;

import me.jooomout.demorestapistudy.accounts.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

import java.nio.file.Path;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter { // adapter 상속받는 순간 boot가 제공하는 설정이 적용되지 않음.

    @Autowired
    AccountService accountService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Bean
    public TokenStore tokenStore() {
        return new InMemoryTokenStore();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    @Override
    protected  void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(accountService)
                .passwordEncoder(passwordEncoder);
    }

    @Override
    public void configure(WebSecurity web){ // HTTP 이전에 무시.
        web.ignoring().mvcMatchers("/docs/index.html");
        web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations()); // 일반적인 정적데이터 ex-파비콘?
    }

    /*
    * Spring Security 내부로 들어와 Filter Chain을 타게 된다.
    * 하지만 스태틱 파일을 혀용할 것이라면 그냥 Filter 밖에서 처리해주는게 그나마 서버에 부하가 덜 감.
    * */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .anonymous()
                    .and()
                .formLogin()
                    .and()
                .authorizeRequests()
                    .antMatchers(HttpMethod.GET,"/session-info").permitAll()
                    .antMatchers(HttpMethod.POST,"/api/account/**").permitAll()
                    .antMatchers(HttpMethod.GET, "/api/**").permitAll() // api/** 의 GET 모두 허용
                    .anyRequest().authenticated() // 나머지는 권한 있어야 함
                .and()
                    .csrf().disable();
                ;
    }


}
