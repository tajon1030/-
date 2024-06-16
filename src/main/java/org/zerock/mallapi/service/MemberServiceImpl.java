package org.zerock.mallapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.zerock.mallapi.domain.Member;
import org.zerock.mallapi.domain.MemberRole;
import org.zerock.mallapi.dto.MemberDTO;
import org.zerock.mallapi.dto.MemberModifyDTO;
import org.zerock.mallapi.repository.MemberRepository;

import java.util.LinkedHashMap;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Log4j2
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public MemberDTO getKakaoMember(String accessToken) {
        // accessToken을 이용해서 사용자 정보를 가져옴
        // 카카오연동
        String nickname = getFromKakaoAccessToken(accessToken);

        // 현재 db와 처리
        Optional<Member> result = memberRepository.findById(nickname);

        // 기존 db에 회원정보가 있을경우
        if (result.isPresent()) {
            return entityToDTO(result.get());
        }

        // 없을경우
        Member socialMember = makeMember(nickname);
        memberRepository.save(socialMember);
        return entityToDTO(socialMember);
    }

    @Override
    public void modifyMember(MemberModifyDTO memberModifyDTO) {
        Optional<Member> result = memberRepository.findById(memberModifyDTO.getEmail());

        Member member = result.orElseThrow();

        // 사용자 정보를 수정했다면 이제는 소셜회원이 아님
        member.changeNickName(memberModifyDTO.getNickname());
        member.changeSocial(false);
        member.changePw(passwordEncoder.encode(memberModifyDTO.getPw()));

        memberRepository.save(member);
    }

    private Member makeMember(String nickname) {
        String tempPassword = makeTempPassword();
        Member member = Member.builder()
                .email(nickname)
                .nickname("Social Member")
                .pw(passwordEncoder.encode(tempPassword))
                .social(true)
                .build();
        member.addRole(MemberRole.USER);

        return member;
    }

    private String getFromKakaoAccessToken(String accessToken) {
        String kakaoGetUserURL = "https://kapi.kakao.com/v2/user/me";

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        //Authorization: Bearer ${ACCESS_TOKEN}
        headers.add("Authorization", "Bearer " + accessToken);
        //Content-type: application/x-www-form-urlencoded;charset=utf-8
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<String> httpEntity = new HttpEntity<>(headers);

        UriComponents uriBuilder = UriComponentsBuilder.fromHttpUrl(kakaoGetUserURL).build();

        ResponseEntity<LinkedHashMap> response = restTemplate.exchange(uriBuilder.toUri()
                , HttpMethod.GET
                , httpEntity
                , LinkedHashMap.class);

        log.info(response);

        LinkedHashMap<String, LinkedHashMap> body = response.getBody();

        LinkedHashMap<String, String> kakaoAccount = body.get("properties");

        String nickname = kakaoAccount.get("nickname");

        log.info("nickname " + nickname);

        return nickname;
    }


    /**
     * 10자리의 임시 패스워드를 만드는 메서드
     *
     * @return
     */
    private String makeTempPassword() {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < 10; i++) {
            stringBuffer.append((char) ((int) (Math.random() * 55) + 65));
        }

        return stringBuffer.toString();
    }
}
