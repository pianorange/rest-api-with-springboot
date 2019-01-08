package me.purefire.restapiwithspring.configs;

import me.purefire.restapiwithspring.accounts.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

/**
 * WebSecurityConfigurerAdapter 상속하면 Springboot 기본 Security 설정 적용되지않음
 * 커스텀 설정해야함
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    AccountService accountService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Bean
    public TokenStore tokenStore() {
        return new InMemoryTokenStore();
    }

    /**
     * AuthenticationManager를 다른 AuthenticationServer나 ResourceServer 가 참조할 수 있도록
     * WebSecurityConfigurerAdapter 의 authenticationManagerBean() Override 하고
     * Bean 어노테이션으로 으로 등록해야한다.
     * @return AuthenticationManager AuthenticationManager
     * @throws Exception 예외외     */
   @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }


    /**
     * AuthenticationManager 생성하기 위한 메소드 Override 해서 재정의
     *  재정의 내용 : 내가 만든 accountService와 passwordEncoder를 사용하게 설정함
     * @param auth
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        auth.userDetailsService(accountService)
                .passwordEncoder(passwordEncoder);

    }

    /**
     * Security Filter 적용 여부 설정하는 메소드 오버라이드 해서 재정의
     * PathRequest.toStaticResources().atCommonLocations 스프링 제공 static 리소스파일들
     * 적용되면 인증 안될 시 스프링 세큐리티에서 제공하는 기본 로그인 페이지로 넘어감
     * @param web
     * @throws Exception
     */
    @Override
    public void configure(WebSecurity web) throws Exception {

        web.ignoring().mvcMatchers("/docs/index.html");
        web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());

    }

    /**
     * http 적용하면 스프링 세큐리티 안에 들어옴
     * 요청에 필요로 하는 인증이 anonymous 익명 (아무나)인지,
     * StaticResources 인지 확인하고 맞으면 시큐리티 허용하라
     *
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
//       http.authorizeRequests()
//        .mvcMatchers("/docs/index.html").anonymous()
//        .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }
}
