package com.example.libraryapp.controller;

import com.example.libraryapp.dto.BookDTO;
import com.example.libraryapp.service.BookService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/books")
public class BookController {
    private final BookService service;

    public BookController(BookService service) {
        this.service = service;
    }

    @GetMapping
    public String list(Model model, HttpSession session) {
        model.addAttribute("books", service.findAll());
        // pobieramy z sesji tytuł ostatnio dodanej książki (może być null)
        model.addAttribute("lastBookTitle", session.getAttribute("lastBookTitle"));
        return "books";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("book", new BookDTO());
        return "add-book";
    }

    @PostMapping
    public String create(@ModelAttribute("book") BookDTO dto, HttpSession session) {
        BookDTO saved = service.create(dto);
        // zapisujemy do sesji tytuł właśnie dodanej książki
        session.setAttribute("lastBookTitle", saved.getTitle());
        return "redirect:/books";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("book", service.findById(id));
        return "edit-book";
    }

    @PostMapping("/edit/{id}")
    public String update(@PathVariable Long id,
                         @ModelAttribute("book") BookDTO dto) {
        service.update(id, dto);
        return "redirect:/books";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        service.delete(id);
        return "redirect:/books";
    }
}
