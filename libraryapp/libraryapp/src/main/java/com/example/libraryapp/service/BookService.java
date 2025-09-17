package com.example.libraryapp.service;

import com.example.libraryapp.dto.BookDTO;
import com.example.libraryapp.entity.Book;
import com.example.libraryapp.repository.BookRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class BookService {
    private final BookRepository repo;
    private final SimpMessagingTemplate messagingTemplate;

    public BookService(BookRepository repo, SimpMessagingTemplate messagingTemplate) {
        this.repo = repo;
        this.messagingTemplate = messagingTemplate;
    }

    public List<BookDTO> findAll() {
        return repo.findAll()
                   .stream()
                   .map(b -> new BookDTO(b.getId(), b.getTitle(), b.getAuthor()))
                   .collect(Collectors.toList());
    }

    public Optional<BookDTO> findByIdOptional(Long id) {
        return repo.findById(id)
                   .map(b -> new BookDTO(b.getId(), b.getTitle(), b.getAuthor()));
    }

    public BookDTO findById(Long id) {
        return findByIdOptional(id).orElseThrow();
    }

    public BookDTO create(BookDTO dto) {
        Book b = new Book(dto.getTitle(), dto.getAuthor());
        Book saved = repo.save(b);
        BookDTO out = new BookDTO(saved.getId(), saved.getTitle(), saved.getAuthor());
        messagingTemplate.convertAndSend("/topic/bookAdded", out);
        return out;
    }

    public Optional<BookDTO> updateOptional(Long id, BookDTO dto) {
        return repo.findById(id).map(b -> {
            b.setTitle(dto.getTitle());
            b.setAuthor(dto.getAuthor());
            Book saved = repo.save(b);
            BookDTO out = new BookDTO(saved.getId(), saved.getTitle(), saved.getAuthor());
            messagingTemplate.convertAndSend("/topic/bookUpdated", out);
            return out;
        });
    }

    public BookDTO update(Long id, BookDTO dto) {
        return updateOptional(id, dto).orElseThrow();
    }

    public void delete(Long id) {
        repo.deleteById(id);
        messagingTemplate.convertAndSend("/topic/bookDeleted", id);
    }

    public boolean exists(Long id) {
        return repo.existsById(id);
    }
}
