package com.libraryapi.libraryapi.resource;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.libraryapi.libraryapi.dto.LoanDto;
import com.libraryapi.libraryapi.dto.LoanFilterDTO;
import com.libraryapi.libraryapi.dto.RetrunedLoanDto;
import com.libraryapi.libraryapi.exceptions.BusinessException;
import com.libraryapi.libraryapi.model.Book;
import com.libraryapi.libraryapi.model.Loan;
import com.libraryapi.libraryapi.service.IBookService;
import com.libraryapi.libraryapi.service.ILoanService;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest (controllers = LoanController.class)
@AutoConfigureMockMvc
public class LoanControllerTest {
    static final String LOAN_APP = "/api/loans";

    @Autowired
    MockMvc mvc;

    @MockBean
    private IBookService bookService;

    @MockBean
    private ILoanService loanService;

    @Test
    @DisplayName("Deve criar um emprestimo")
    public void createLoanTest() throws Exception {
        //cenario
        Long idEmprestimo =1L;
        LoanDto dto =loanDtoMock(); // essa abordagem falha.
        String json = new ObjectMapper().writeValueAsString(dto);

        Book returnedBook = Book.builder().id(idEmprestimo).isbn("123").build();

        BDDMockito.given(bookService.getByIsbn("123"))
            .willReturn(Optional.of(returnedBook));

            Loan loan = Loan.builder()
            .id(idEmprestimo)
            .costumer("costumer")
            .book(returnedBook)
            .loanDate(LocalDate.now())
            .build()
            ;
       
		BDDMockito.given(
            loanService.save(
                Mockito.any(Loan.class)))
                .willReturn(loan);

        //execucao - requisicao de teste
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_APP)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(json)
        ;
        //verify
        mvc.perform(request)
        .andExpect( status().isCreated() )
        .andExpect( content().string( "1" ))
        ; 
    }

    @Test
    @DisplayName("Deve dar erro ao tentar criar emprestimo com isbm inexistente")
    public void createLoanNoExistentIsbm() throws Exception {
        //cenario
        LoanDto dto = LoanDto.builder().costumer("costumer").isbm("123").build();
        String json = new ObjectMapper().writeValueAsString(dto);
       
        BDDMockito.given(bookService.getByIsbn("123"))
        .willReturn(Optional.empty());

        //execucao - requisicao de teste
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_APP)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(json)
        ;
        //verify
        mvc.perform(request)
        .andExpect( status().isBadRequest() )
        .andExpect( jsonPath( "errors", Matchers.hasSize(1) ))
        .andExpect( jsonPath( "errors[0]").value("Book not founded for isbm riquired"))
        ;  
}
@Test
@DisplayName("Deve dar erro ao tentar criar emprestimo com isbm já emprestado")
public void LoanedBookErrorOnCreatedLoanTest() throws Exception {
    //cenario
    LoanDto dto = LoanDto.builder().costumer("costumer").isbm("123").build();
    String json = new ObjectMapper().writeValueAsString(dto);
   
    Book returnedBook = Book.builder().isbn("123").build();

    BDDMockito.given(bookService.getByIsbn("123"))
        .willReturn(Optional.of(returnedBook));

    BDDMockito.given(
        loanService.save(
            Mockito.any(Loan.class)))
            .willThrow( new BusinessException("Boook Alread loaned") );

    //execucao - requisicao de teste
    MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_APP)
    .accept(MediaType.APPLICATION_JSON)
    .contentType(MediaType.APPLICATION_JSON)
    .content(json)
    ;
    //verify
    mvc.perform(request)
    .andExpect( status().isBadRequest() )
    .andExpect( jsonPath( "errors", Matchers.hasSize(1) ))
    .andExpect( jsonPath( "errors[0]").value("Boook Alread loaned"))
    ;  
}
@Test
@DisplayName("Deve retornar livro")
public void returnedBookTest() throws Exception{
    //cenario  returned:True
    RetrunedLoanDto returned = RetrunedLoanDto.builder().returned(true).build();
    String json = new ObjectMapper().writeValueAsString(returned);

    Loan loan = Loan.builder().id(1L).build();
    BDDMockito.given(loanService.getById(1L)) 
    .willReturn(Optional.of(loan));

    mvc.perform(
        MockMvcRequestBuilders.patch(LOAN_APP+"/1 ")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(json)
    ).andExpect(status().isOk());

    Mockito.verify(loanService,Mockito.times(1)).update(loan);
    }
    @Test
    @DisplayName("Deve filtrar emprestimos")
    public void findLoanssTest() throws Exception {
        //cenario
        Book book =Book.builder().id(1L).isbn("123").build();

        Loan loan = Loan.builder()
        .id(1L)
        .returned(true)
        .costumer("costumer") //customer
        .book(book)
        .loanDate(LocalDate.now())
        .build();

        BDDMockito.given( loanService.find( Mockito.any(LoanFilterDTO.class), Mockito.any(Pageable.class)) )
                .willReturn( new PageImpl<Loan>( Arrays.asList(loan)  , PageRequest.of(0 ,10) , 1) );
        //execucao
        String queryString = String.format( "?isbn=%s&costumer=%s&page=0&size=10",
            book.getIsbn(), loan.getCostumer());
        
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
        .get(LOAN_APP.concat(queryString))
        .accept(MediaType.APPLICATION_JSON);
        //verify
        mvc.perform(request) 
        .andExpect( status().isOk() )
        .andExpect(jsonPath("content", Matchers.hasSize(1)))
        .andExpect(jsonPath("totalElements").value(1))
        .andExpect(jsonPath("pageable.pageSize").value(10))
        .andExpect(jsonPath("pageable.pageNumber").value(0))
        ;

    }

    private LoanDto loanDtoMock(){
        return LoanDto
        .builder()
        .costumer("leitor")
        .isbm("123")
        .build();
    }
}
