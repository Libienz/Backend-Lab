package Libienz.hellospring;

import Libienz.hellospring.repository.MemberRepository;
import Libienz.hellospring.repository.MemoryMemberRepository;
import Libienz.hellospring.service.MemberService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfig {

    @Bean
    public MemberService memberService() {
        return new MemberService(memberRepository());
    }
    @Bean
    public MemoryMemberRepository memberRepository() {
        return new MemoryMemberRepository();
    }
}
