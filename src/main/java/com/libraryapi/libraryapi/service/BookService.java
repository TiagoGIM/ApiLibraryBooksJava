package com.libraryapi.libraryapi.service;

import com.libraryapi.libraryapi.model.Book;
import com.libraryapi.libraryapi.repository.RepositoryBook;

import org.springframework.stereotype.Service;


@Service
public class BookService implements IBookService {
    
    private RepositoryBook repository;
    //injeção de dependencia
    public BookService( RepositoryBook repository){
        this.repository = repository;
    }

    @Override
    public Book save(Book book){
        return repository.save(book);
    }
}
