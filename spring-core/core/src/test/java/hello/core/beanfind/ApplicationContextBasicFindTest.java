package hello.core.beanfind;

import hello.core.AppConfig;
import hello.core.member.MemberService;
import hello.core.member.MemberServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

public class ApplicationContextBasicFindTest {

    AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);

    @Test
    @DisplayName("빈 이름으로 조회")
    void findBeanByName() {
        MemberService memberService = ac.getBean("memberService", MemberService.class);
        assertThat(memberService).isInstanceOf(MemberServiceImpl.class); //가져온 빈의 객체가 멤버서비스 임플과 같은지
    }

    @Test
    @DisplayName("이름 없이 타입으로만 조회")
    //같은 타입의 빈이 여러개면 좀 곤란
    void findBeanByType() {
        MemberService memberService = ac.getBean(MemberService.class);
        assertThat(memberService).isInstanceOf(MemberServiceImpl.class); //가져온 빈의 객체가 멤버서비스 임플과 같은지
    }

    @Test
    @DisplayName("구체 타입으로 조회")
    void findBeanByType2() {
        MemberService memberService = ac.getBean(MemberServiceImpl.class); //이렇게 해도 상관없다 좋은 코드는 아님 구현체에 의존
        assertThat(memberService).isInstanceOf(MemberServiceImpl.class); //가져온 빈의 객체가 멤버서비스 임플과 같은지
    }

    @Test
    @DisplayName("빈 이름으로 조회 실패") //항상 테스트는 실패 테스를 만들어야 함
    void findBeanByNameFail() {
        //MemberService memberService = ac.getBean("memberServic", MemberService.class); //NoSuchBeanDefinitionException
        assertThrows(NoSuchBeanDefinitionException.class,
                () -> ac.getBean("memberServic", MemberService.class)); //오른쪽을 실행하면 왼쪽의 예외가 터져야한다 Test
    }




}
