package org.zerock.mallapi.repository;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.mallapi.domain.Product;
import org.zerock.mallapi.dto.PageRequestDTO;

import java.util.Arrays;
import java.util.UUID;

@SpringBootTest
@Log4j2
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    public void testInsert() {
        for (int i = 0; i < 10; i++) {
            Product product = Product.builder().pname("Test " + i).pdesc("desc").price(300).build();
            product.addImageString(UUID.randomUUID() + "_IMAGE1.jpg");
            product.addImageString(UUID.randomUUID() + "_IMAGE2.jpg");
            productRepository.save(product);
        }
    }

    @Commit
    @Transactional
    @Test
    public void testDelete() {
        Long pno = 2L;
        productRepository.updateToDelete(2L, true);
    }

    @Test
    public void testUpdate() {
        Product product = productRepository.selectOne(1L).get();
        product.changePrice(2000);

        // clearList를 안하고 다른 arrayList로 갈아버리면 문제가 심각해짐
        // jpa가 arrayList를 관리하고있기때문에 물고있는 컬렉션을 계속 사용하도록 해야함
        product.clearList();
        product.addImageString(UUID.randomUUID() + "_PIMAGE1.jpg");
        product.addImageString(UUID.randomUUID() + "_PIMAGE2.jpg");
        product.addImageString(UUID.randomUUID() + "_PIMAGE3.jpg");

        productRepository.save(product);
    }

    @Test
    public void testList() {
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("id").descending());
        Page<Object[]> result = productRepository.selectList(pageRequest);
        result.getContent().forEach(arr -> log.info(Arrays.toString(arr)));
    }

    @Test
    public void testSearch(){
        PageRequestDTO pageRequestDTO = PageRequestDTO.builder().build();
        productRepository.searchList(pageRequestDTO);
    }
}
