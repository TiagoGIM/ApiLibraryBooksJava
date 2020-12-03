package com.libraryapi.libraryapi.service;

import java.util.Optional;

import com.libraryapi.libraryapi.exceptions.BusinessException;
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
        if (repository.existsByIsbn(book.getIsbn())){
            throw new BusinessException("Isbn já cadastrado");
        }
        return repository.save(book);
    }

    @Override
    public Optional<Book> getById(Long id){
        return Optional.empty();
    }
}