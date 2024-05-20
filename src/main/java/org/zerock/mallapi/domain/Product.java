package org.zerock.mallapi.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
//연관관계나 엘리먼트컬렉션을 맺을때에는 toString에서 일단은 제외하고 생각하기(테스트하기 수월하게)
@ToString(exclude = "imageList")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String pname;

    private int price;

    private String pdesc;

    private boolean delFlag;

    // 게시글-댓글의 관계는 생명주기가 다르기때문에 연관관계를 맺지만
    // 상품의 이미지의 경우에는 생명주기가 같으므로
    // ElementCollection 이 옳다고 판단하였음
    // 기본 지연로딩
    // 종속적이기때문에 product를 지우면 연관되어있는 productImage 테이블 데이터들도 함께 사라짐(진짜파일은 직접지워야)
    @ElementCollection
    @Builder.Default
    private List<ProductImage> imageList = new ArrayList<>();

    public void changePrice(int price) {
        this.price = price;
    }

    public void changeDesc(String desc) {
        this.pdesc = desc;
    }

    public void changeName(String name) {
        this.pname = name;
    }

    public void changeDel(boolean delFlag){
        this.delFlag = delFlag;
    }

    // 엘리먼트컬렉션을 사용할때 주의해야할점은
    // 엘리먼트컬렉션은 주인공이 되지않는다는점으로
    // 상품의 이미지를 수정하는것은 상품정보를 수정하는것이기때문에
    // 엔티티에서 엘리먼트컬렉션의 처리를 해주는것이 좋다.
    public void addImage(ProductImage image) {
        // 매니징의 주체가 본인(productImage)가 아닌 product
        image.setOrd(imageList.size());
        imageList.add(image);
    }

    public void addImageString(String fileName) {
        ProductImage productImage = ProductImage.builder()
                .fileName(fileName)
                .build();

        addImage(productImage);
    }

    public void clearList() {
        this.imageList.clear();
    }
}