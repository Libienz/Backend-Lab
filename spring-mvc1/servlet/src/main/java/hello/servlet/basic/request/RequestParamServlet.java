package hello.servlet.basic.request;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/*
1. 파라미터 전송 기능
 */

@WebServlet(name = "requestParamServlet", urlPatterns = "/request-param")
public class RequestParamServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("[전체 파라미터 조회] - start");
        request.getParameterNames().asIterator().forEachRemaining(paramName -> System.out.println(paramName + "=" + request.getParameter(paramName)));

        System.out.println("[전체 파라미터 조회] - end");
        System.out.println();

        System.out.println("[단일 파라미터 조회] - start");
        String username = request.getParameter("username"); //키값넣어서 해당키에 해당하는 값 잡아온다. 단일 조회!
        String age = request.getParameter("age"); //키값넣어서 해당키에 해당하는 값 잡아온다. 단일 조회!

        System.out.println("username = " + username);
        System.out.println("age = " + age);
        System.out.println("[단일 파라미터 조회] - end");

        //?username=hello&username=hello2 이런 경우가 있다
        //이럴 경우 단일 조회하면 앞쪽에 있는 게 걸려 넘어옴 뒤에 있는 것도 보고 싶다면 이름이 같은 복수 파라미터 조회!
        System.out.println("[이름이 같은 복수 파라미터 조회] - start");
        String[] usernames = request.getParameterValues("username"); //getParameterValues -> 얘는 배열 반환! for each로 찍어볼 수 있다.
        for (String name : usernames) {
            System.out.println("name = " + name);
        }

        System.out.println("[이름이 같은 복수 파라미터 조회] - end");

    }
}
