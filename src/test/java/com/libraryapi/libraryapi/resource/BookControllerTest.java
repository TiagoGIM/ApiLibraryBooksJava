package com.libraryapi.libraryapi.resource;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.libraryapi.libraryapi.dto.BookDto;
import com.libraryapi.libraryapi.exceptions.BusinessException;
import com.libraryapi.libraryapi.model.Book;
import com.libraryapi.libraryapi.service.IBookService;

import org.springframework.http.MediaType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
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

  private BookDto createNewBook(){
    return BookDto
    .builder()
    .author("Author")
    .isbn("1213212")
    .title("AS aventuras de Pi")
    .build();
  }
}
