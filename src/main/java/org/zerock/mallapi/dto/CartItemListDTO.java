package org.zerock.mallapi.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CartItemListDTO {

    private Long cino;

    private int qty;

    private String pname;

    private int price;

    private String imageFile;

    private Long pno;

    public CartItemListDTO(Long cino, int qty, String pname, int price, Long pno, String imageFile) {
        this.cino = cino;
        this.qty = qty;
        this.pname = pname;
        this.price = price;
        this.pno = pno;
        this.imageFile = imageFile;
    }
}
