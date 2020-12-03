package com.libraryapi.libraryapi.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.libraryapi.libraryapi.model.Book;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class RepositoryBookTest {
    @Autowired
    TestEntityManager entityManager;

    @Autowired
    RepositoryBook repository;

    @Test
    @DisplayName("Deve retornar true se se isbn existir no cadastro")
    public void returnTrueWhenIsbnExist() throws Exception{
        //cenario
        String isbn = "123";
        Book book = Book.builder()
            .title("title")
            .isbn(isbn)
            .author("author")
            .build();
        entityManager.persist(book);
        //execucao
        boolean exists = repository.existsByIsbn(isbn);
        //verify
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Deve retornar false se se isbn existir no cadastro")
    public void returnFalseWhenIsbnExist() throws Exception{
        //cenario
        String isbn = "123";
        //execucao
        boolean exists = repository.existsByIsbn(isbn);
        //verify
        assertThat(exists).isFalse();
    }
}
