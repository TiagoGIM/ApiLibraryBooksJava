package com.libraryapi.libraryapi.service;

import java.util.Optional;

import com.libraryapi.libraryapi.model.Book;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IBookService {

	Book save(Book any);
	
    Optional<Book> getById(Long id); //Optional testa se é null

    void delete(Book book);

    Book update(Book book);

	Page<Book> find(Book filter, Pageable pageRequest );

	Optional<Book> getByIsbn(String isbn);
}
