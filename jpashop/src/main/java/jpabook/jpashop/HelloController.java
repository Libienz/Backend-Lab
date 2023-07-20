package jpabook.jpashop;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloController {

    /**
     *
     * @param model : 모델에 데이터를 실어서 뷰에 넘길 수 있도록 한다.
     * @return 화면이름 : hello.html을 뷰로써 반환 (.html이 관례로 붙는다)
     */
    @GetMapping("hello")
    public String hello(Model model) {
        model.addAttribute("data", "hello!!!");
        return "hello";
    }

}
