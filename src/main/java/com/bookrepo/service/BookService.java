package com.bookrepo.service;

import com.bookrepo.domain.Book;
import com.bookrepo.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    public List<Book> getAllBooks() {
        return bookRepository.getAllBooks();
    }

    public void save(Book book) {
        bookRepository.save(book);
    }
}
