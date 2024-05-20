package org.zerock.mallapi.domain;

import jakarta.persistence.Embeddable;
import lombok.*;

// 엘리먼트컬렉션
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Builder
public class ProductImage {

    private String fileName;

    private int ord; // 파일순번(0번이 대표이미지)

    public void setOrd(int ord){
        this.ord = ord;
    }
}
