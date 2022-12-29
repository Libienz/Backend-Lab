package hello.servlet.web.frontcontroller.v1;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface ControllerV1 {

    //throws 호출하는 쪽으로 예외를 던짐
    //throw 예외를 발생시킴
    void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;
}
