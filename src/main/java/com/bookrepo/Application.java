package com.bookrepo;

import com.bookrepo.repository.BookRepository;
import com.bookrepo.service.BookService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import com.bookrepo.controller.BookController;


@SpringBootApplication
@Import({ BookController.class, BookService.class, BookRepository.class})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}