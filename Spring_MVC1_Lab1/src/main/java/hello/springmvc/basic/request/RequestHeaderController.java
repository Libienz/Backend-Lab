package hello.springmvc.basic.request;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

@Slf4j
@RestController
public class RequestHeaderController {

    //스프링 애노테이션 기반 컨트롤러는 다양한 파라미터를 지원한다.
    @RequestMapping("/headers")
    public String headers(HttpServletRequest request,
                          HttpServletResponse response,
                          HttpMethod httpMethod, //GET, POST, DELETE 등등
                          Locale locale, //언어 정보
                          @RequestHeader MultiValueMap<String,String> headerMap, //헤더를 한번에 다 받는다. 맵에
                          @RequestHeader("host") String host, //헤더 하나만 가져오는
                          @CookieValue(value = "myCookie", required = false) String cookie) { //required의 디폴트는 true -> false로 하면 없어도 된다.

        log.info("request={}", request);
        log.info("response={}", response);
        log.info("httpMethod={}", httpMethod);
        log.info("locale={}", locale);
        log.info("headerMap={}", headerMap);
        log.info("header host={}", host);
        log.info("myCookie={}", cookie);
        return "ok";
    }
}
