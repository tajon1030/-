package org.zerock.mallapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.mallapi.domain.Todo;
import org.zerock.mallapi.dto.PageRequestDTO;
import org.zerock.mallapi.dto.PageResponseDTO;
import org.zerock.mallapi.dto.TodoDTO;
import org.zerock.mallapi.repository.TodoRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Log4j2
@RequiredArgsConstructor
public class TodoServiceImpl implements TodoService {

    private final TodoRepository todoRepository;

    @Override
    public TodoDTO get(Long tno) {
        Optional<Todo> result = todoRepository.findById(tno);
        Todo todo = result.orElseThrow();
        return entityToDTO(todo);
    }

    @Override
    public Long register(TodoDTO dto) {
        Todo savedTodo = todoRepository.save(dtoToEntity(dto));
        return savedTodo.getTno();
    }

    // 수정이나 삭제의 경우 조회와는 다르게 에러가 나면 문제이므로 리턴값을 void로 한다.
    // 메서드의 리턴값을 void로 설정하여 예외가 발생했을때 해당 메서드를 호출한 곳에서 예외를 처리하도록 하는것이 일반적
    @Override
    public void modify(TodoDTO dto) {
        Todo todo = todoRepository.findById(dto.getTno())
                .orElseThrow();
        todo.changeTitle(dto.getTitle());
        todo.changeContent(dto.getContent());
        todo.changeDueDate(dto.getDueDate());
        todo.changeComplete(dto.isComplete());

        todoRepository.save(todo);
    }

    @Override
    public void remove(Long tno) {
        todoRepository.deleteById(tno);
    }

    @Override
    public PageResponseDTO<TodoDTO> getList(PageRequestDTO pageRequestDTO) {
        // JPA
        Page<Todo> result = todoRepository.search1(pageRequestDTO);

        List<TodoDTO> dtoList = result.get().map(this::entityToDTO).toList();

        PageResponseDTO<TodoDTO> responseDTO = PageResponseDTO.<TodoDTO>withAll()
                .dtoList(dtoList)
                .pageRequestDTO(pageRequestDTO)
                .total(result.getTotalElements())
                .build();

        return responseDTO;
    }
}
