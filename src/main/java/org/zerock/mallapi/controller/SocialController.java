package org.zerock.mallapi.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.zerock.mallapi.dto.MemberDTO;
import org.zerock.mallapi.dto.MemberModifyDTO;
import org.zerock.mallapi.service.MemberService;
import org.zerock.mallapi.util.JWTUtil;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Log4j2
public class SocialController {

    private final MemberService memberService;

    @GetMapping("/api/member/kakao")
    public Map<String, Object> getMemberFromKakako(String accessToken) {
        log.info("accessToken" + accessToken);

        MemberDTO kakaoMember = memberService.getKakaoMember(accessToken);
        Map<String, Object> claims = kakaoMember.getClaims();

        String jwtAccessToken = JWTUtil.generateToken(claims, 10);
        String jwtRefreshToken = JWTUtil.generateToken(claims, 60 * 24);
        claims.put("accessToken", jwtAccessToken);
        claims.put("refreshToken", jwtRefreshToken);

        return claims;
    }

    @PutMapping("/api/member/modify")
    public Map<String,String> modify(@RequestBody MemberModifyDTO memberModifyDTO){
        log.info("------member modify------------");
        memberService.modifyMember(memberModifyDTO);
        return Map.of("result", "modified");
    }
}
