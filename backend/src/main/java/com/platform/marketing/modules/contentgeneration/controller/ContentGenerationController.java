package com.platform.marketing.modules.contentgeneration.controller;

import com.platform.marketing.modules.contentgeneration.entity.ContentGeneration;
import com.platform.marketing.modules.contentgeneration.dto.ContentGenerationDto;
import com.platform.marketing.modules.contentgeneration.service.ContentGenerationService;
import com.platform.marketing.util.ResponseEntity;
import com.platform.marketing.util.ResponsePageDataEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/content-generation")
public class ContentGenerationController {

    private final ContentGenerationService service;

    public ContentGenerationController(ContentGenerationService service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("hasPermission('content-generation:list')")
    public ResponseEntity<ResponsePageDataEntity<ContentGeneration>> list(@RequestParam(defaultValue = "") String status,
                                                                          @RequestParam(defaultValue = "") String channel,
                                                                          @RequestParam(defaultValue = "") String keyword,
                                                                          @RequestParam(defaultValue = "0") int page,
                                                                          @RequestParam(defaultValue = "10") int size) {
        Page<ContentGeneration> p = service.search(status, channel, keyword, PageRequest.of(page, size));
        return ResponseEntity.success(new ResponsePageDataEntity<>(p.getTotalElements(), p.getContent()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasPermission('content-generation:view')")
    public ResponseEntity<ContentGeneration> get(@PathVariable String id) {
        return service.findById(id)
                .map(ResponseEntity::success)
                .orElse(ResponseEntity.fail(404, "Not Found"));
    }

    @PostMapping
    @PreAuthorize("hasPermission('content-generation:create')")
    public ResponseEntity<ContentGeneration> create(@RequestBody ContentGenerationDto dto) {
        return ResponseEntity.success(service.create(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasPermission('content-generation:update')")
    public ResponseEntity<ContentGeneration> update(@PathVariable String id, @RequestBody ContentGenerationDto dto) {
        return ResponseEntity.success(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasPermission('content-generation:delete')")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.success(null);
    }
}
