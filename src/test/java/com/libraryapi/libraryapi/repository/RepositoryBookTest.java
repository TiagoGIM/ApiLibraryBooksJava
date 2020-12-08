package com.libraryapi.libraryapi.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

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
    @DisplayName("Deve retornar true se isbn já existir no cadastro")
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
    @Test
    @DisplayName("Deve obter livro por ID")
    public void findByIdTest(){
        //cenario
        String isbn = "123";
        Book Newbook = Book.builder()
            .title("title")
            .isbn(isbn)
            .author("author")
            .build();
            Newbook = entityManager.persist(Newbook);
        //execucao
        Optional<Book> foundedBook = repository.findById(Newbook.getId());
        //verify
        assertThat(foundedBook.isPresent()).isTrue();
    }
    @Test
    @DisplayName("Deve salvar um livro")
    public void updateTest(){
        //cenario
        Book newbook = creatValidBook();
        newbook.setIsbn("2");
        //execucao 
        Book savedBook = repository.save(newbook);
        //verify
        assertThat(savedBook.getId()).isNotNull();

    }

    @Test
    @DisplayName("Deve deletar um livro")
    public void deleteTest(){
        //cenario
        String isbn = "123";
        Book book = Book.builder()
            .title("title")
            .isbn(isbn)
            .author("author")
            .build();
            book = entityManager.persist(book);
        Book foundedBook = entityManager.find(Book.class, book.getId());
        repository.delete(foundedBook);
        //aqui optei por usar o proprio repository 
        Optional<Book> deletedBook = repository.findById(foundedBook.getId());//entityManager.find(Book.class, book.getId());
        assertThat(deletedBook).isEmpty();

    }

    private Book creatValidBook(){
        return Book.builder()
        .author("author")
        .title("title")
        .isbn("isbn")
        .build();
      }
    
    
}
