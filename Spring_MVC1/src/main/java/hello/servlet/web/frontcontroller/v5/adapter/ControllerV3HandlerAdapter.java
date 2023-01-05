package hello.servlet.web.frontcontroller.v5.adapter;

import hello.servlet.web.frontcontroller.ModelView;
import hello.servlet.web.frontcontroller.v3.ControllerV3;
import hello.servlet.web.frontcontroller.v5.MyHandlerAdapter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ControllerV3HandlerAdapter implements MyHandlerAdapter {

    @Override
    public boolean supports(Object handler) {
        return (handler instanceof ControllerV3); //ControllerV3의 인스턴스가 넘어오면 true 아니면 false

    }

    @Override
    public ModelView handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws ServletException, IOException {
        ControllerV3 controllerV3 = (ControllerV3) handler;

        Map<String, String> paramMap = createParamMap(request);
        ModelView mv = controllerV3.process(paramMap);

        return mv;
    }

    private static Map<String, String> createParamMap(HttpServletRequest request) {

        Map<String, String> paramMap = new HashMap<>();

        //getParameterNames는 파라미터의 이름들을 enumeration으로 가져온다
        //?username=이근희 이렇게 넘어오면 username이 저장되는 것
        //이거를 asIterator로 돌리고 Iterator의 forEachRemaining에 람다식을 넘기는 것
        request.getParameterNames().asIterator().forEachRemaining(paramName -> paramMap.put(paramName, request.getParameter(paramName)));
        return paramMap;
    }
}
