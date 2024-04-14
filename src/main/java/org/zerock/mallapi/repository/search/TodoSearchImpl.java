package org.zerock.mallapi.repository.search;

import com.querydsl.jpa.JPQLQuery;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.zerock.mallapi.domain.QTodo;
import org.zerock.mallapi.domain.Todo;
import org.zerock.mallapi.dto.PageRequestDTO;

import java.util.List;

// querydsl을 사용하기 위해서 구현하는 인터페이스와 이름을 동일하게 만들어 줘야함(인터페이스명Impl)
@Log4j2
public class TodoSearchImpl extends QuerydslRepositorySupport implements TodoSearch {

    public TodoSearchImpl() {
        super(Todo.class);
    }

    @Override
    public Page<Todo> search1(PageRequestDTO pageRequestDTO) {
        log.info("search1..............");
        QTodo todo = QTodo.todo;

        JPQLQuery<Todo> query = from(todo);

        // spring 3.대부터 applyPagination 사용 가능(pageable 사용가능함)
        // pageRequest 는 0부터 시작 pageRequestDTO는 1부터 시작이므로 -1처리
        Pageable pageable = PageRequest.of(pageRequestDTO.getPage() -1,
                pageRequestDTO.getSize(),
                Sort.by("tno").descending());

        this.getQuerydsl().applyPagination(pageable, query);

        List<Todo> list = query.fetch();// 목록데이터
        long total = query.fetchCount();

        return new PageImpl<>(list, pageable, total);
    }
}
