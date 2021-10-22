package me.jooomout.demorestapistudy.configs;

import org.springframework.boot.autoconfigure.security.oauth2.authserver.OAuth2AuthorizationServerConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;


/*
* ResourceServerConfigureAdapter 가 WebSecurityConfigurerAdapter 보다 높은 순위에 있다.
* */
@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {
    @Override
    public void configure(ResourceServerSecurityConfigurer resources){
        resources.resourceId("event");
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .anonymous()
                    .and()
                .authorizeRequests()
                    .mvcMatchers(HttpMethod.GET, "/session-info")
                        .permitAll()
                    .mvcMatchers(HttpMethod.GET, "/api/**")
                        .permitAll()
                    .mvcMatchers(HttpMethod.POST, "/api/account/login")
                        .permitAll()
                    .anyRequest()
                        .authenticated()
                /*.and()
                .exceptionHandling()
                .accessDeniedHandler(new OAuth2AuthorizationServerConfiguration())*/
                ;

    }
}
