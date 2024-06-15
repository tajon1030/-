package org.zerock.mallapi.service;

import org.springframework.transaction.annotation.Transactional;
import org.zerock.mallapi.domain.Member;
import org.zerock.mallapi.dto.MemberDTO;

@Transactional
public interface MemberService {

    MemberDTO getKakaoMember(String accessToken);

    default MemberDTO entityToDTO(Member member){
        return new MemberDTO(
                member.getEmail(),
                member.getPw(),
                member.getNickname(),
                member.isSocial(),
                member.getMemberRoleList().stream()
                        .map(Enum::name)
                        .toList());
    }
}
