package Libienz.hellospring.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HelloController {

    @GetMapping("hello")
    public String hello(Model model) {
        model.addAttribute("data","hello!!");
        return "hello";
    }

    @GetMapping("hello-mvc")
    public String helloMvc(@RequestParam(value = "name",required = false) String name, Model model) {
        model.addAttribute("name", name);
        return "hello-template";
    }

    @GetMapping("hello-string")
    @ResponseBody //http에서 헤더부와 바디부가 있는데 바디를 직접 넣어주겠다는 뜻
    public String helloString(@RequestParam("name") String name) {
        return "hello "+name;
    }

    @GetMapping("hello-api")
    @ResponseBody //자바 객체를 http 요청의 바디내용으로 매핑하여 클라이언트로 전송 리소스 파일을 찾아가지 않는다.
    public Hello helloApi(@RequestParam("name") String name) {
        Hello hello = new Hello();
        hello.setName(name);
        return hello;
    }
    static class Hello {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
