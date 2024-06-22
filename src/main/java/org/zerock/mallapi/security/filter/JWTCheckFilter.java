package org.zerock.mallapi.security.filter;

import com.google.gson.Gson;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.zerock.mallapi.dto.MemberDTO;
import org.zerock.mallapi.util.JWTUtil;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

@Log4j2
// 모든경우에 동작하는 웹시큐리티필터인 OncePerRequestFilter
public class JWTCheckFilter extends OncePerRequestFilter {

    // 어떤경로로 들어오면 필터링을 해야하고
    // 어떤경로로 들어오면 검사를 안해도 되는데(api/login 로그인을해야하니까 당연히 jwt 토큰 없음)
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        // true == not checking

        String path = request.getRequestURI();
        log.info("check uri--------------" + path);

        // false == check
        if (path.startsWith("/api/member/")
                || path.startsWith("/api/products/view/")) {
            return true; // 체크를 하지않음
        }

        return false;

    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeaderStr = request.getHeader("Authorization");

        try {
            // 일반적으로 Bearer + ' ' + JWT 문자로 이루어져있음
            String accessToken = authHeaderStr.substring(7);
            Map<String, Object> claims = JWTUtil.validateToken(accessToken);
            log.info(claims);

            // PreAuthorize를 사용하기위해서
            // securityContextHolder에 로그인한 사용자가 어떤사용자인지에대한 정보를 만들어 넣어줘야함
            String email = (String) claims.get("email");
            // 사실 여기서 pw를 꺼내오는게 아니라 db에서 받아와야하는데
            // 그렇게되면 계속 db접근을 해야함 -> redis와같은 메모리db를 사용하는 방법을 적용할수있음
            String pw = (String) claims.get("pw");
            String nickname = (String) claims.get("nickname");
            Boolean social = (Boolean) claims.get("social");
            List<String> roleNames = (List<String>) claims.get("roleNames");

            MemberDTO memberDTO = new MemberDTO(email, pw, nickname, social.booleanValue(), roleNames);

            // SpringSecurityContext가 사용하는 Token
            UsernamePasswordAuthenticationToken authenticationToken
                    = new UsernamePasswordAuthenticationToken(memberDTO, pw, memberDTO.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            // 다음 필터나 목적지로 가도록 하는 것으로 항상 마지막은 doFilter로 끝남
            filterChain.doFilter(request, response);

        } catch (Exception e) {
            log.error("JWT Check Error ---------------");
            log.error(e.getMessage());

            Gson gson = new Gson();
            String msg = gson.toJson(Map.of("error", "ERROR_ACCESS_TOKEN"));

            response.setContentType("application/json");
            PrintWriter pw = response.getWriter();
            pw.println(msg);
            pw.close();
        }


    }
}
