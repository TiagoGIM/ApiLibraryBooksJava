package com.libraryapi.libraryapi.resource;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.modelmapper.ModelMapper;

import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.libraryapi.libraryapi.dto.BookDto;
import com.libraryapi.libraryapi.exceptions.BusinessException;
import com.libraryapi.libraryapi.model.Book;
import com.libraryapi.libraryapi.service.IBookService;

import org.springframework.http.MediaType;
import org.assertj.core.util.Arrays;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest
@AutoConfigureMockMvc
public class BookControllerTest {
  // route for post methods
  static String BOOK_APP = "/api/books";

  @Autowired
  MockMvc mvc;

  @MockBean
  IBookService service;

  @Test
  @DisplayName("Deve criar um livro com sucesso")
  public void createBookWithSucess() throws Exception {

    Book savedBook = Book.builder().id(0L).author("Author").isbn("1213212").title("AS aventuras de Pi").build();

    BookDto dto_mock = BookDto.builder().author("Author").isbn("1213212").title("AS aventuras de Pi").build();

    BDDMockito.given(service.save(Mockito.any(Book.class))).willReturn(savedBook);

    String json = new ObjectMapper().writeValueAsString(dto_mock);

    MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(BOOK_APP)
        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(json);
    mvc.perform(request).andExpect(status().isCreated())
        .andExpect(jsonPath("id").value(0L))
        .andExpect(jsonPath("title").value(dto_mock.getTitle()))
        .andExpect(jsonPath("author").value(dto_mock.getAuthor()))
        .andExpect(jsonPath("isbn").value(dto_mock.getIsbn()));
  }

  @Test
  @DisplayName("Deve lançar erro de validação quando não houver dados completos")
  public void createInvalidBookTest() throws Exception {

    String json = new ObjectMapper().writeValueAsString(new BookDto());

    MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(BOOK_APP)
        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(json);

    mvc.perform(request)
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("errors", hasSize(3)));
  }

  @Test
  @DisplayName("Lança error ao tentar cadastrar isbn existente")
  public void createBookWithDuplicatedIsbn() throws Exception {

    BookDto bookValido = createNewBook();
    String json = new ObjectMapper().writeValueAsString(bookValido);
    String msgError = "Isbn já cadastrado";
    BDDMockito.given(service.save(Mockito.any(Book.class)))
      .willThrow(new BusinessException(msgError));

    MockHttpServletRequestBuilder request = MockMvcRequestBuilders
      .post(BOOK_APP)
      .contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON)
      .content(json);

    mvc.perform(request)
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("errors", hasSize(1)))
      .andExpect(jsonPath("errors[0]").value(msgError))
      ;
  }
  @Test
  @DisplayName("Deve obter infos de um livro by id")
  public void getBookDetailsByidTest() throws Exception{
    //cenario (given)
    Long id=1L;
    Book book =Book.builder()
      .id(id)
      .title(createNewBook().getTitle())
      .author(createNewBook().getAuthor())
      .isbn(createNewBook().getIsbn())
      .build();

    BDDMockito.given(service.getById(id)).willReturn(Optional.of(book));
    //execucao (when)
    MockHttpServletRequestBuilder request = MockMvcRequestBuilders
      .get(BOOK_APP.concat("/"+id))
      .accept(MediaType.APPLICATION_JSON);

      mvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(jsonPath("id").value(id))
        .andExpect(jsonPath("title").value(createNewBook().getTitle()))
        .andExpect(jsonPath("author").value(createNewBook().getAuthor()))
        .andExpect(jsonPath("isbn").value(createNewBook().getIsbn()));
  }
  @Test
  @DisplayName("Deve retornar resource notFound quando o livro nao existir")
  public void canNotFindThisBookTest() throws Exception{
    //cenario
    Long id=1L;
    BDDMockito.given(service.getById(id)).willReturn(Optional.empty());
    //execucao (when)
    MockHttpServletRequestBuilder request = MockMvcRequestBuilders
      .get(BOOK_APP.concat("/"+id))
      .accept(MediaType.APPLICATION_JSON);

      mvc.perform(request)
      .andExpect(status().isNotFound());

  }
  
  @Test
  @DisplayName("Deve deletar um livro")
  public void deleteBookTest() throws Exception{
    //cenario
    Long id=1L;
    Book book =Book
      .builder()
      .id(id)
      .build();

    BDDMockito.given(service.getById(id)).willReturn(Optional.of(book));
    //execucao (when)
    MockHttpServletRequestBuilder request = MockMvcRequestBuilders
      .delete(BOOK_APP.concat("/"+id));

    mvc.perform(request)
      .andExpect(status().isNoContent());
  }
  @Test
  @DisplayName("Deve retornar resource not found quando não encontrar livro para deletar")
  public void canNotDeleteBookTest() throws Exception{
    //cenario
    Long id=1L;
    BDDMockito.given(service.getById(id)).willReturn(Optional.empty());
    //execucao (when)
    MockHttpServletRequestBuilder request = MockMvcRequestBuilders
      .delete(BOOK_APP.concat("/"+id));

    mvc.perform(request)
      .andExpect(status().isNotFound());

  }

  @Test
  @DisplayName("Deve atualizar um livro")
  public void updateBookTest() throws Exception{
    Long id=1L;
    String json = new ObjectMapper().writeValueAsString(createNewBook());

    Book updatingBook =Book.builder()
      .id(id)
      .title("some book")
      .author("some guy")
      .isbn("1213212")
      .build();
    
      BDDMockito.given(service.getById(id)).willReturn(Optional.of(updatingBook));

      Book updatedBook =Book.builder()
      .id(id)
      .title("novo nome")
      .author("novo author")
      .isbn("123")
      .build();
    
    BDDMockito.given(service.update(updatingBook)).willReturn(updatedBook);

    MockHttpServletRequestBuilder request = MockMvcRequestBuilders
    .put(BOOK_APP.concat("/"+id))
    .content(json)
    .accept(MediaType.APPLICATION_JSON)
    .contentType(MediaType.APPLICATION_JSON)
    ;

    mvc.perform(request)
      .andExpect(status().isOk())
      .andExpect(jsonPath("id").value(id))
      .andExpect(jsonPath("title").value(createNewBook().getTitle()))
      .andExpect(jsonPath("author").value(createNewBook().getAuthor()))
      .andExpect(jsonPath("isbn").value(createNewBook().getIsbn()))
      ;
  }

  @Test
  @DisplayName("Deve retornar 404 quando não encontrar livro para atualizar")
  public void canNotUpdateBookTest() throws Exception{
    Long id=1L;
    Book book =Book.builder()
      .id(id)
      .title(createNewBook().getTitle())
      .author(createNewBook().getAuthor())
      .isbn(createNewBook().getIsbn())
      .build();
    String json = new ObjectMapper().writeValueAsString(book);

    BDDMockito.given(service.getById(id)).willReturn(Optional.empty());

    MockHttpServletRequestBuilder request = MockMvcRequestBuilders
    .put(BOOK_APP.concat("/"+id))
    .content(json)
    .accept(MediaType.APPLICATION_JSON)
    .contentType(MediaType.APPLICATION_JSON)
    ;

    mvc.perform(request)
      .andExpect(status().isNotFound())
    ;
  }

  @Test
  @DisplayName("Deve filtrar livros")
  public void findBooksTest() throws Exception {
    //cenario
    Long id =1L;
    Book book = Book.builder()
    .author(createNewBook().getAuthor())
    .id(id)
    .isbn(createNewBook().getIsbn())
    .title(createNewBook().getTitle()) 
    .build();

    BDDMockito.given( service.find( Mockito.any(Book.class), Mockito.any(Pageable.class)) )
              .willReturn( new PageImpl<Book>( Arrays.asList(book), PageRequest.of(0 ,100) , 1) );
    //execucao
    String queryString = String.format( "?title=%s$author%s&page=0&size=100",
            book.getTitle(), book.getAuthor());
    
    MockHttpServletRequestBuilder request = MockMvcRequestBuilders
      .get(BOOK_APP.concat(queryString))
      .accept(MediaType.APPLICATION_JSON);

    //verify
    mvc.perform(request) 
      .andExpect( status().isOk())
      .andExpect(jsonPath("content", Matchers.hasSize(1)))
      ;

  }
  private BookDto createNewBook(){
    return BookDto
    .builder()
    .author("Author")
    .isbn("1213212")
    .title("AS aventuras de Pi")
    .build();
  }
}
