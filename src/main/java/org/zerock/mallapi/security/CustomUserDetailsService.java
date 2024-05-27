package org.zerock.mallapi.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.zerock.mallapi.domain.Member;
import org.zerock.mallapi.dto.MemberDTO;
import org.zerock.mallapi.repository.MemberRepository;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Log4j2
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    // 여기서 username은 id에 해당하는값(우리는 email)
    // 리턴값은 UserDetials 인터페이스를 구현하는 UserDetails를 상속받는 UserDTO
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        log.info("--------------loadUserByUsername------------");

        Member member = memberRepository.getWithRoles(username);

        if (member == null) {
            throw new UsernameNotFoundException("Not Found");
        }

        return new MemberDTO(
                member.getEmail(),
                member.getPw(),
                member.getNickname(),
                member.isSocial(),
                member.getMemberRoleList()
                        .stream().map(Enum::name)
                        .collect(Collectors.toList())
        );
    }
}
