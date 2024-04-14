package org.zerock.mallapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


// SuperBuilder는 빌더 패턴을 생성하는 데 사용되는 애노테이션 중 하나로
// 부모 클래스의 필드와 메서드를 자식 클래스의 빌더에 포함시킬 수 있도록 도와줍니다.
@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageRequestDTO {

    @Builder.Default
    private int page = 1;

    @Builder.Default
    private int size = 10;
}
