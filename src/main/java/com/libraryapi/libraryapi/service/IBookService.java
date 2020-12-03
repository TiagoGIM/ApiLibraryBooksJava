package com.libraryapi.libraryapi.service;

import java.util.Optional;

import com.libraryapi.libraryapi.model.Book;

public interface IBookService {

	Book save(Book any);
	
    Optional<Book> getById(Long id); //Optional testa se é null

    void delete(Book book);

    Book update(Book book);
}
