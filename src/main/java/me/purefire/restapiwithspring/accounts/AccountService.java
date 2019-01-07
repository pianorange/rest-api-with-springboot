package me.purefire.restapiwithspring.accounts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AccountService implements UserDetailsService {

    @Autowired
    AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //Optional 의 orElseThrow 사용 해서 null일시 깔끔하게 예외처리
        Account account = accountRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        //Account 를 SpringSecurity가 이해할수 있는 타입으로 변환(UserDetails)
        //UserDetails interface 들어가서 구현한 클래스 확인해보면 스프링 세큐리티 제공하는 User Class 있어 일일이 구현 할필요없다.
        return new User(account.getEmail(), account.getPassword(), authorities(account.getRoles()));
    }

    /**
     * 각각의 Role을 SimpleGrantedAuthority 로 map 해줌, "ROLE_"이란 prefix 붙인 형태문자열 생성자에 넘김
     * @param roles account에서 읽어온 AccountRole (USER, ADMIN)
     * @return SimpleGrantedAuthority SimpleGrantedAuthority
     */
    private Collection<? extends GrantedAuthority> authorities(Set<AccountRole> roles) {
        return roles.stream()
                .map(r -> {
                    return new SimpleGrantedAuthority("ROLE_" + r.name());
                 })
                .collect(Collectors.toSet());
    }
}
