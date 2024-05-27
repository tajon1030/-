package org.zerock.mallapi.security.handler;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

// api 서버의경우 인증이 잘못됐을때 어떻게든 메시지를 전달해줘야함
// 상태코드를 어떻게 할것인가에 대한 논쟁이 있는데 대부분 200으로 리턴하는데 메시지를 로그인잘못됏다고 보내줌
@Log4j2
public class ApiLoginFailHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        Gson gson = new Gson();

        String jsonStr = gson.toJson(Map.of("error", "ERROR_LOGIN"));

        response.setContentType("application/json");
        PrintWriter printWriter = response.getWriter();
        printWriter.println(jsonStr);
        printWriter.close();
    }
}
