package com.libraryapi.libraryapi.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.libraryapi.libraryapi.exceptions.BusinessException;
import com.libraryapi.libraryapi.model.Book;
import com.libraryapi.libraryapi.repository.RepositoryBook;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTeste {

  IBookService service;

  @MockBean   //simula comportamento do repository
  RepositoryBook repository;

  @BeforeEach
  public void setUp(){
      this.service = new BookService(repository);
  }

  @Test
  @DisplayName("Deve salvar um livro")
  public void saveBookTest(){
    //cenario
    Book book = creatValidBook();
    Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(false);
    Mockito.when(repository.save(book))
      .thenReturn(Book
      .builder()
      .id(0L)
      .author("author")
      .title("title")
      .isbn("isbn")
      .build()
      );
    //execução
    Book savedBook = service.save(book);
    //assertThat(savedBook.getId()).isNotNull();
    assertThat(savedBook.getAuthor()).isEqualTo("author");
    assertThat(savedBook.getIsbn()).isEqualTo("isbn");
    assertThat(savedBook.getTitle()).isEqualTo("title");
  }

  @Test
  @DisplayName("Deve lançar erro de negocio ao lançar isbm duplicado")
  public void shouldNotSaveBookWithDuplicatedIsbn(){
    //cenario 
    Book book = creatValidBook();
    Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(true);
    //execucao
    Throwable exception = Assertions.catchThrowable(()-> service.save(book));
    //verificacao
    assertThat(exception).isInstanceOf(BusinessException.class)
    .hasMessage("Isbn já cadastrado");
    Mockito.verify(repository, Mockito.never()).save(book);

  }
  private Book creatValidBook(){
    return Book.builder()
    .author("author")
    .title("title")
    .isbn("isbn")
    .build();
  }
}