package org.zerock.mallapi.security.handler;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.zerock.mallapi.dto.MemberDTO;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

// 인증에 성공했을때 어떻게 할것인지
// 빈으로 등록하지않는데 securityConfig 에서 추가해서 쓸거기때문
@Log4j2
public class ApiLoginSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("-----------------");
        log.info(authentication);
        log.info("-----------------");

        MemberDTO memberDTO = (MemberDTO) authentication.getPrincipal();

        Map<String, Object> claims = memberDTO.getClaims();

        claims.put("accessToken", "");
        claims.put("refreshToken", "");

        // Map형태로 만든 claims를 json형식으로 만들기 (gson이용)
        Gson gson = new Gson();
        String jsonStr = gson.toJson(claims);

        response.setContentType("application/json; charset=UTF-8");

        PrintWriter printWriter = response.getWriter();
        printWriter.println(jsonStr);
        printWriter.close();
    }
}
