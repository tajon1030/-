package org.zerock.mallapi.repository;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.mallapi.domain.Cart;
import org.zerock.mallapi.domain.CartItem;
import org.zerock.mallapi.domain.Member;
import org.zerock.mallapi.domain.Product;
import org.zerock.mallapi.dto.CartItemListDTO;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@Log4j2
class CartRepositoryTest {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Test
    public void testListOfMember() {
        String email = "user1@aaa.com";

        List<CartItemListDTO> cartItemListDTOList =
                cartItemRepository.getItemsOfCartDTOByEmail(email);

        for (CartItemListDTO cartItemListDTO : cartItemListDTOList) {
            log.info(cartItemListDTO);
        }
    }

    @Test
    @Transactional
    @Commit
    public void testInsertByProduct() {
        String email = "user1@aaa.com";
        Long pno = 2L;
        int qty = 4;

        // 이메일과 상품번호로 장바구니아이템이 있었는지 확인
        // 없으면 추가 있으면 수량변경 업데이트
        CartItem cartItem = cartItemRepository.getItemOfPno(email, pno);

        // 이미 사용자의 장바구니에 담겨있을경우
        if (cartItem != null) {
            cartItem.changeQty(qty);
            cartItemRepository.save(cartItem);
            return;
        }

        // 사용자의 장바구니에 장바구니 아이템을 만들어서 저장
        // 장바구니자체가 없을경우
        Optional<Cart> cartOfMember = cartRepository.getCartOfMember(email);
        Cart cart = null;
        if (cartOfMember.isEmpty()) {
            Member member = Member.builder()
                    .email(email)
                    .build();

            Cart tmpCart = Cart.builder()
                    .owner(member)
                    .build();

            cart = cartRepository.save(tmpCart);
        } else { // 장바구니는있으나 해당상품이 장바구니아이템에 없는경우
            cart = cartOfMember.get();
        }

        Product product = Product.builder()
                .id(pno)
                .build();
        cartItem = CartItem.builder()
                .cart(cart)
                .product(product)
                .qty(qty)
                .build();

        cartItemRepository.save(cartItem);
    }

    @Test
    @Commit
    @Transactional
    public void testUpdateByCino() {
        Long cino = 1L;
        int qty = 4;

        Optional<CartItem> result = cartItemRepository.findById(cino);

        CartItem cartItem = result.orElseThrow();

        cartItem.changeQty(qty);

        cartItemRepository.save(cartItem);
    }

    @Test
    public void testDeleteThenList() {
        Long cino = 1L;
        Long cno = cartItemRepository.getCartFromItem(cino);
        cartItemRepository.deleteById(cino);
        List<CartItemListDTO> cartItemList = cartItemRepository.getItemsOfCartDTOByCart(cno);

        for (CartItemListDTO dto : cartItemList) {
            log.info(dto);
        }
    }
}