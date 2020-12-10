package com.libraryapi.libraryapi.resource;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.libraryapi.libraryapi.dto.LoanDto;
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
        //LoanDto dto =loanDtoMock(); // essa abordagem falha.
        LoanDto dto = LoanDto.builder().costumer("costumer").isbm("123").build();
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

    private LoanDto loanDtoMock(){
        return LoanDto
        .builder()
        .costumer("leitor")
        .isbm("1234")
        .build();
    }
}
