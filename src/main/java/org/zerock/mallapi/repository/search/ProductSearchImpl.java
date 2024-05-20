package org.zerock.mallapi.repository.search;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPQLQuery;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.zerock.mallapi.domain.Product;
import org.zerock.mallapi.domain.QProduct;
import org.zerock.mallapi.domain.QProductImage;
import org.zerock.mallapi.dto.PageRequestDTO;
import org.zerock.mallapi.dto.PageResponseDTO;
import org.zerock.mallapi.dto.ProductDTO;

import java.util.List;
import java.util.Objects;

@Log4j2
public class ProductSearchImpl extends QuerydslRepositorySupport implements ProductSearch {

    public ProductSearchImpl() {
        super(Product.class);
    }

    @Override
    public PageResponseDTO<ProductDTO> searchList(PageRequestDTO requestDTO) {

        log.info("---------------searchList-----------------");

        Pageable pageable = PageRequest.of(
                requestDTO.getPage() - 1,
                requestDTO.getSize(),
                Sort.by("id").descending()
        );

        QProduct product = QProduct.product;
        QProductImage productImage = QProductImage.productImage;

        JPQLQuery<Product> query = from(product);
        // product에 있는 imagegList를 productImage로 간주해서 leftJoin할것임
        query.leftJoin(product.imageList, productImage);
        query.where(productImage.ord.eq(0));

        Objects.requireNonNull(getQuerydsl()).applyPagination(pageable, query);

        List<Tuple> productList = query.select(product, productImage).fetch();
        long count = query.fetchCount();

        return null;
    }
}
