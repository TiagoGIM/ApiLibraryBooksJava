package com.libraryapi.libraryapi.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.Arrays;
import java.util.List;

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
//
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
//
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
    Book returnedBook = Book
    .builder()
    .id(0L)
    .author("author")
    .title("title")
    .isbn("isbn")
    .build();
    Mockito.when(repository.save(book))
      .thenReturn(returnedBook
      );
    //execução
    Book savedBook = service.save(book);
    assertThat(savedBook.getId()).isNotNull();
    assertThat(savedBook.getAuthor()).isEqualTo("author");
    assertThat(savedBook.getIsbn()).isEqualTo("isbn");
    assertThat(savedBook.getTitle()).isEqualTo("title");
  }

  @Test
  @DisplayName("Deve lançar erro de negocio ao tentar salvar livro com isbm duplicado")
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
  @Test
  @DisplayName("Deve obter livro por ID")
  public void getBookbyIDTest(){
    //Cenario
    Long id=1L;
    Book book = creatValidBook();
    book.setId(id);
    Mockito.when(repository.findById(id)).thenReturn(Optional.of(book));
    //execucao
    Optional<Book> foundBook = service.getById(id);
    //verify
    assertThat(foundBook.isPresent()).isTrue();
  }

  @Test
  @DisplayName("Deve retornar Not Found livro por ID")
  public void CanNotgetBookbyIDTest(){
    //Cenario
    Long id=0L;
    
    Mockito.when(repository.findById(id)).thenReturn(Optional.empty());
    //execucao
    Optional<Book> book = service.getById(id);
    //verify
    assertThat(book).isNotPresent();
  }

  @Test
  @DisplayName("Deve deletar um livro")
  public void DeleteBookTest(){
    //cenario
    Book book = Book.builder().id(0L).build();
    //execucao
    org.junit.jupiter.api.Assertions.assertDoesNotThrow( () -> service.delete(book));
    //verify
    Mockito.verify(repository, Mockito.times(1)).delete(book); //chamou uma vez?
  }

  @Test
  @DisplayName("Deve lançar erro ao passar ID inexistente")
  public void deleteInvalidBookTest(){
    //cenario
    Book book = new Book();
    //execucao e verificaçao
    org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,
     () -> service.delete(book));
    
    Mockito.verify(repository, Mockito.never()).delete(book); //garantir que nunca chame o metodo delete()
  }
  @Test
  @DisplayName("Deve atualizar livro")
  public void updateBookTest(){
    //cenario 
    Long id =1L;

    Book bookUpdated = creatValidBook();
    bookUpdated.setId(id);

    Book bookForUpdate = Book.builder().id(id).build();

    Mockito.when(repository.save(bookForUpdate)).thenReturn(bookUpdated);
    //execucao
    org.junit.jupiter.api.Assertions.assertDoesNotThrow( () -> service.update(bookForUpdate));
    //verificação
    //Mockito.verify(repository, Mockito.times(1)).save(bookBanco);
    Book book = service.update(bookForUpdate);
    assertThat(book.getId()).isEqualTo( bookUpdated.getId() );
    assertThat(book.getAuthor()).isEqualTo( bookUpdated.getAuthor() );
    assertThat(book.getIsbn()).isEqualTo( bookUpdated.getIsbn() );
    assertThat(book.getTitle()).isEqualTo( bookUpdated.getTitle() );
  }

  @Test
  @DisplayName("Deve lançar erro ao tentar editar um livro inexistente")
  public void uptadeInvalidBookTest(){
    //cenario
    Book book = new Book();
    //execucao
    org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,
     () -> service.update(book));
    
    Mockito.verify(repository, Mockito.never()).save(book); //garantir que nunca chame o metodo delete()
  }
  @Test
  @DisplayName("Deve retornar lista de livros")
  public void listOfBooksTest(){
    //cenario
    Book book = creatValidBook();

    PageRequest pageRequest = PageRequest.of(0,10);

    List<Book> mockListOfBook = Arrays.asList(book);
    Page<Book> page = new PageImpl<Book>(mockListOfBook, pageRequest , 1);

    Mockito.when(repository.findAll(Mockito.any(Example.class) , Mockito.any(PageRequest.class)))
      .thenReturn(page);
    //execucao
    Page<Book> result = service.find(book, pageRequest);

    //verify
    assertThat(result.getTotalElements()).isEqualTo(1);
    assertThat(result.getContent()).isEqualTo(mockListOfBook);
    assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
    assertThat(result.getPageable().getPageSize()).isEqualTo(10);
    
    
  }

  private Book creatValidBook(){
    return Book.builder()
    .author("author")
    .title("title")
    .isbn("isbn")
    .build();
  }
}
