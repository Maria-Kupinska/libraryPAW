package com.example.libraryapp.controller;

import com.example.libraryapp.dto.BookDTO;
import com.example.libraryapp.service.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class ApiBookController {

    private final BookService service;

    public ApiBookController(BookService service) {
        this.service = service;
    }

    @GetMapping
    public List<BookDTO> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDTO> getOne(@PathVariable Long id) {
        return ResponseEntity.of(service.findByIdOptional(id));
    }

    @PostMapping
    public BookDTO create(@RequestBody BookDTO dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookDTO> update(
        @PathVariable Long id,
        @RequestBody BookDTO dto
    ) {
        return ResponseEntity.of(service.updateOptional(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (service.exists(id)) {
            service.delete(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
