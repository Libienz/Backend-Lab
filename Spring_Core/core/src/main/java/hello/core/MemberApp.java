package hello.core;

import hello.core.member.Grade;
import hello.core.member.Member;
import hello.core.member.MemberService;
import hello.core.member.MemberServiceImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class MemberApp {

    public static void main(String[] args) {

/*
        AppConfig appConfig = new AppConfig();
        MemberService memberService = appConfig.memberService();
*/
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class); //어노테이션 기반 config
        MemberService memberService = applicationContext.getBean("memberService", MemberService.class); //두번째 인자는 타입


        //MemberService memberService = new MemberServiceImpl();
        Member memberA = new Member(1L, "memberA", Grade.VIP);
        memberService.join(memberA);

        Member findMember = memberService.findMember(1L);
        System.out.println("find Member = " + findMember);
        System.out.println("new member = " + memberA);


    }
}
