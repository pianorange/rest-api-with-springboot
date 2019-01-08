package me.purefire.restapiwithspring.configs;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AppConfig {
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // DelegatingPasswordEncoder 약간 특이한 passwordEncoder
        // 인코딩된 password 앞에 prefix 붙여서 어떤 방식으로 인코딩 됬는지도 구분함
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
