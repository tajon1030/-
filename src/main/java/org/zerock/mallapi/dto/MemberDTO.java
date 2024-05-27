package org.zerock.mallapi.dto;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// security에서 사용하는 userDetails DTOType(User.class)을 이용
public class MemberDTO extends User {

    private String email, pw, nickname;

    private boolean social;

    // MemberRole로 하면 화면쪽 처리가 힘들어지므로 String으로
    private List<String> roleNames = new ArrayList<>();

    public MemberDTO(String email, String pw, String nickname, boolean social, List<String> roleNames){
        super(
                email,
                pw,
                // 권한을 스프링시큐리티가 사용하는 권한으로 만들어줌
                roleNames.stream().map(str -> new SimpleGrantedAuthority("ROLE_"+str))
                        .collect(Collectors.toList())
        );

        this.email = email;
        this.pw = pw;
        this.nickname = nickname;
        this.social = social;
        this.roleNames = roleNames;
    }

    // security를 jwt문자열을 만들어서 주고받을것인데
    // 그 처리를 위한 jwt내용(claims)을 만들 메서드
    public Map<String, Object> getClaims(){
        Map<String, Object> dataMap =  new HashMap<>();

        dataMap.put("email", email);
        dataMap.put("nickname",nickname);
        dataMap.put("social",social);
        dataMap.put("roleNames",roleNames);

        return dataMap;
    }

}
