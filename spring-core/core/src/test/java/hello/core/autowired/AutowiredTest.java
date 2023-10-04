package hello.core.autowired;

import hello.core.member.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.Optional;


public class AutowiredTest {


    @Test
    void AutowiredOption() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(TestBean.class);
    }



    static class TestBean {


        @Autowired(required = false)
        public void setNoBean1(Optional<Member> member) {

            System.out.println("setNoBean1 = " + member);
        }


        @Autowired
        public void setNoBean2(@Nullable Member member) {

            System.out.println("setNoBean2 = " + member);
        }


        @Autowired(required = false)
        public void setNoBean3(Member member) {

            System.out.println("setNoBean3 = " + member);
        }

    }

}
