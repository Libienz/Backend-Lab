package hello.servlet.web.frontcontroller;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter @Setter
public class ModelView {
    //view의 논리적 이름을 제공함으로써 viewPath가 변경되더라도 유연한 대처 가능
    //한부분만 고치면 된다!
    private String viewName; //뷰의 논리적 이름
    private Map<String, Object> model = new HashMap<>();

    public ModelView(String viewName) {
        this.viewName = viewName;
    }
}
