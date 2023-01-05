package hello.servlet.web.frontcontroller.v5;

import hello.servlet.web.frontcontroller.ModelView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface MyHandlerAdapter {

    boolean supports(Object handler); //컨트롤러가 넘어왔을 때 핸들러를 지원할 수 있는지
    ModelView handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws ServletException, IOException; //컨트롤러 호출
}
