package org.zerock.mallapi.service;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.zerock.mallapi.dto.PageRequestDTO;
import org.zerock.mallapi.dto.PageResponseDTO;
import org.zerock.mallapi.dto.ProductDTO;

import java.util.List;
import java.util.UUID;

@SpringBootTest
@Log4j2
class ProductServiceImplTest {

    @Autowired
    private ProductService productService;

    @Test
    public void testList() {
        PageRequestDTO pageRequestDTO = PageRequestDTO.builder().build();
        PageResponseDTO<ProductDTO> responseDTO = productService.getList(pageRequestDTO);
        log.info(responseDTO.getDtoList());
    }

    @Test
    public void testRegister(){
        ProductDTO productDTO = ProductDTO.builder()
                .pname("새로운")
                .pdesc("신규추가")
                .price(100)
                .build();

        productDTO.setUploadFileNames(
                List.of(
                        UUID.randomUUID() + "_" + "Test1.jpg",
                        UUID.randomUUID() + "_" + "Test2.jpg"));

        productService.register(productDTO);
    }
}