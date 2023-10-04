package hello.servlet.basic.request;

import org.springframework.util.StreamUtils;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@WebServlet(name = "requestBodyStringServlet", urlPatterns = "/request-body-string")
public class RequestBodyStringServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        //API 방식에서 데이터를 꺼내는 방법은 아래와 같다.
        ServletInputStream inputStream = request.getInputStream(); //API방식에서 message body의 내용을 byte code형태로 얻는다.
        String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);//byte code를 String으로 copy하는데 encoding 정보는 UTF_8 (byte를 문자로, 문자를 byte로 바꿀 때는 encoding정보를 알려주어야 함!

        System.out.println("messageBody = " + messageBody);
        response.getWriter().write("ok");

    }
}
