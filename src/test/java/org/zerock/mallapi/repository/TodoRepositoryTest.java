package org.zerock.mallapi.repository;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.mallapi.domain.Todo;

import java.time.LocalDate;
import java.util.Optional;

@SpringBootTest
@Log4j2
@Transactional
class TodoRepositoryTest {

    @Autowired
    TodoRepository todoRepository;

    Todo todo = Todo.builder()
            .title("test")
            .content("content")
            .complete(false)
            .dueDate(LocalDate.of(2024, 4, 15))
            .build();

    @BeforeEach
    public void insertDefaultData() {
        todoRepository.save(todo);

        for (int i = 1; i <= 100; i++) {
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
    @Order(1)
    public void testInsert() {
        Todo save = todoRepository.save(Todo.builder()
                .title("test2")
                .content("content2")
                .complete(false)
                .dueDate(LocalDate.of(2024, 4, 15))
                .build());

        log.info(save);
    }

    @Test
    @Order(2)
    public void testRead() {
        Long tno = 1L;

        Optional<Todo> result = todoRepository.findById(tno);

        Assertions.assertEquals(todo.getTitle(), result.orElseThrow().getTitle());
    }

    @Test
    public void testUpdate() {
        Optional<Todo> result = todoRepository.findById(1L);

        Todo todo1 = result.orElseThrow();

        todo1.changeTitle("titleChange");

        todoRepository.saveAndFlush(todo1);
    }

    @Test
    public void testPaging() {
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("tno").descending());

        Page<Todo> todoPage = todoRepository.findAll(pageRequest);

        log.info(todoPage.getTotalPages());
        log.info(todoPage.getContent());
    }
}