package org.zerock.mallapi.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.zerock.mallapi.dto.PageRequestDTO;
import org.zerock.mallapi.dto.PageResponseDTO;
import org.zerock.mallapi.dto.ProductDTO;
import org.zerock.mallapi.service.ProductService;
import org.zerock.mallapi.util.CustomFileUtil;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
@Log4j2
public class ProductController {

    private final CustomFileUtil fileUtil;

    private final ProductService productService;

//    @PostMapping("/")
//    public Map<String, String> register(ProductDTO productDTO) {
//        log.info("register: " + productDTO);
//        List<MultipartFile> files = productDTO.getFiles();
//        List<String> uploadedFileNames = fileUtil.saveFiles(files);
//        productDTO.setUploadFileNames(uploadedFileNames);
//
//        log.info(uploadedFileNames);
//
//        return Map.of("RESULT", "SUCCESS");
//    }

    @GetMapping("/view/{fileName}")
    public ResponseEntity<Resource> viewFile(@PathVariable("fileName") String fileName) {
        return fileUtil.getFile(fileName);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/list")
    public PageResponseDTO<ProductDTO> list(PageRequestDTO pageRequestDTO) {
        return productService.getList(pageRequestDTO);
    }

    @PostMapping("/")
    public Map<String, Long> register(ProductDTO productDTO) {
        // 파일 업로드 우선
        List<MultipartFile> files = productDTO.getFiles();

        List<String> uploadFileNames = fileUtil.saveFiles(files);

        productDTO.setUploadFileNames(uploadFileNames);

        log.info(uploadFileNames);

        Long id = productService.register(productDTO);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return Map.of("result", id);
    }

    @GetMapping("/{pno}")
    public ProductDTO read(@PathVariable("pno") Long pno) {
        return productService.get(pno);
    }

    @PutMapping("/{pno}")
    public Map<String, String> modify(@PathVariable("pno") Long pno, ProductDTO productDTO) {

        productDTO.setPno(pno);

        // db저장된 기존 old product
        ProductDTO oldProductDTO = productService.get(pno);

        // 완전 새롭게 업로드하려는 파일(files) 저장
        List<MultipartFile> files = productDTO.getFiles();
        List<String> currentUploadFileNames = fileUtil.saveFiles(files);
        // 이전과 같게 유지되는 파일들(uploadFileNames) + 새롭게 더한 파일들
        List<String> uploadedFileNames = productDTO.getUploadFileNames();
        if (currentUploadFileNames != null && !currentUploadFileNames.isEmpty()) {
            // uploadedFileNames가 productDTO.getUploadFileNames() 리스트를 참조하고 있기 때문에,
            // uploadedFileNames에 대한 변경이 곧 productDTO.getUploadFileNames() 리스트의 변경을 의미
            uploadedFileNames.addAll(currentUploadFileNames);
        }
        // 수정
        productService.modify(productDTO);


        // 실제파일삭제처리
        // 저장됐던 기존파일들
        List<String> oldFileNames = oldProductDTO.getUploadFileNames();
        if (oldFileNames != null && !oldFileNames.isEmpty()) {
            // 기존파일들중에 수정하면서 삭제해야하는 파일 골라내기
            List<String> removeFiles = oldFileNames.stream()
                    .filter(fileName -> !uploadedFileNames.contains(fileName))
                    .toList();
            // 삭제
            fileUtil.deleteFiles(removeFiles);
        } // end if

        return Map.of("RESULT","SUCCESS");
    }

    @DeleteMapping("/{pno}")
    public Map<String,String> remove(@PathVariable("pno") Long pno){
        List<String> oldFileNames = productService.get(pno).getUploadFileNames();

        productService.remove(pno);

        // 파일 삭제
        fileUtil.deleteFiles(oldFileNames);

        return Map.of("RESULT","SUCCESS");
    }
}