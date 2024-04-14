package org.zerock.mallapi.service;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.mallapi.domain.Todo;
import org.zerock.mallapi.dto.PageRequestDTO;
import org.zerock.mallapi.dto.PageResponseDTO;
import org.zerock.mallapi.dto.TodoDTO;
import org.zerock.mallapi.repository.TodoRepository;

import java.time.LocalDate;

@SpringBootTest
@Log4j2
@Transactional
class TodoServiceImplTest {

    @Autowired
    TodoService todoService;

    @Autowired
    TodoRepository todoRepository;

    @BeforeEach
    public void insertDefaultData() {

        for (int i = 1; i <= 105; i++) {
            todoRepository.save(Todo.builder()
                    .title("test..." + i)
                    .content("content..." + i)
                    .complete(false)
                    .dueDate(LocalDate.of(2024, 4, 15))
                    .build());
        }
        todoRepository.flush();
    }

    @Test
    public void testGet() {
        Long tno = 50L;

        log.info(todoService.get(tno));
    }

    @Test
    public void testRegister() {
        TodoDTO todoDTO = TodoDTO.builder()
                .title("Title.....")
                .content("Content..")
                .dueDate(LocalDate.of(2024, 6, 1))
                .build();

        Long register = todoService.register(todoDTO);
        log.info(register);
    }

    @Test
    public void testGetList(){
        PageRequestDTO requestDTO = PageRequestDTO.builder().build();
        PageResponseDTO<TodoDTO> list = todoService.getList(requestDTO);
        log.info(list);
    }
}