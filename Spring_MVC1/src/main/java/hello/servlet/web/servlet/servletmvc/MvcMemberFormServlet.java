package hello.servlet.web.servlet.servletmvc;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//Controller역할
@WebServlet(name = "mvcMemberFormServlet", urlPatterns ="/servlet-mvc/members/new-form" )
public class MvcMemberFormServlet extends HttpServlet {

    //Controller로 요청이 들어오면 jsp로 넘어가준다.
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String viewPath = "/WEB-INF/views/new-form.jsp";
        RequestDispatcher dispatcher = request.getRequestDispatcher(viewPath);//경로 이동
        //redirect는 다시 요청을 받고 처리하는 반면(URL 바뀜) dispatcher는 서버안에서 내부적으로 바꿔치기(URL 바뀌지 않음)
        dispatcher.forward(request, response); //jsp로 전환
    }
}
