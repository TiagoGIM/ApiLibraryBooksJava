package com.libraryapi.libraryapi.repository;

import com.libraryapi.libraryapi.model.Book;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositoryBook extends JpaRepository<Book, Long> {}

