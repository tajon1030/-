package org.zerock.mallapi.repository.search;

import org.springframework.data.domain.Page;
import org.zerock.mallapi.domain.Todo;
import org.zerock.mallapi.dto.PageRequestDTO;

public interface TodoSearch {

    Page<Todo> search1(PageRequestDTO pageRequestDTO);
}
