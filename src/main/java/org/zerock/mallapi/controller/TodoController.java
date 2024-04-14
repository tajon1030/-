package org.zerock.mallapi.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;
import org.zerock.mallapi.dto.PageRequestDTO;
import org.zerock.mallapi.dto.PageResponseDTO;
import org.zerock.mallapi.dto.TodoDTO;
import org.zerock.mallapi.service.TodoService;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/api/todo")
public class TodoController {

    private final TodoService todoService;

    @GetMapping("/{tno}")
    public TodoDTO get(@PathVariable Long tno) {
        return todoService.get(tno);
    }

    @GetMapping("/list")
    public PageResponseDTO<TodoDTO> list(PageRequestDTO pageRequestDTO) {

        log.info("list..." + pageRequestDTO);

        return todoService.getList(pageRequestDTO);
    }

    @PostMapping("/")
    public Map<String, Long> register(@RequestBody TodoDTO dto) {
        Long tno = todoService.register(dto);
        return Map.of("tno", tno);
    }

    @PutMapping("/{tno}")
    public Map<String, String> modify(@PathVariable Long tno,
                                      @RequestBody TodoDTO dto) {
        dto.setTno(tno); // pathVariable로 받은 tno값과 dto로 받은 tno값을 일치시키기위함
        todoService.modify(dto);
        return Map.of("RESULT", "SUCCESS");
    }

    // Delete는 일반적으로 페이로드(요청본문)을 포함하지 않으며,
    // 요청 URI의 쿼리스트링이나 PathVariable로 리소스를 식별하여 삭제한다.
    @DeleteMapping("/{tno}")
    public Map<String, String> remove(@PathVariable Long tno) {
        todoService.remove(tno);
        return Map.of("RESULT", "SUCCESS");
    }
}
