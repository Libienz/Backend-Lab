package hello.core.scan;

import hello.core.AutoAppConfig;
import hello.core.member.MemberRepository;
import hello.core.member.MemoryMemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class AutoAppConfigTest {

    @Test
    void basicScan() {

        ApplicationContext ac = new AnnotationConfigApplicationContext(AutoAppConfig.class);
        MemberRepository memoryMemberRepository = ac.getBean("memoryMemberRepository", MemberRepository.class);
        System.out.println("memoryMemberRepository = " + memoryMemberRepository); //빈의 이름은 어떻게 정해짐?
        Assertions.assertThat(memoryMemberRepository).isInstanceOf(MemberRepository.class);
    }
}
