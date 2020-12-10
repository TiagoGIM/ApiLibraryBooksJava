package com.libraryapi.libraryapi.resource;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.libraryapi.libraryapi.dto.LoanDto;
import com.libraryapi.libraryapi.model.Book;
import com.libraryapi.libraryapi.model.Loan;
import com.libraryapi.libraryapi.service.IBookService;
import com.libraryapi.libraryapi.service.ILoanService;

import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest (controllers = LoanController.class)
@AutoConfigureMockMvc
public class LoanControllerTestOld {

    @Autowired
    MockMvc mvc;

    @MockBean
    private IBookService bookService;

    @MockBean
    private ILoanService loanService;

    static final String LOAN_APP = "/api/loans";

    @Test
    @DisplayName("Deve criar um emprestimo")
    public void createLoanTest() throws Exception {

        LoanDto dto = LoanDto.builder().costumer("costumer").isbm("123").build();
        String json = new ObjectMapper().writeValueAsString(dto);

        long id = 1L;
        Book returnedBook = Book.builder().id(id).isbn("123").build();

        BDDMockito.given(bookService.getByIsbn("123"))
        .willReturn(Optional.of(returnedBook));

        Loan loan = Loan.builder()
        .id(id)
        .costumer("costumer")
        .book(returnedBook)
        .loanDate(LocalDate.now())
        .build()
        ;

        BDDMockito.given(
            loanService.save(
                Mockito.any(Loan.class)))
                .willReturn(loan);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_APP)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(json)
        ;

        mvc.perform(request)
        .andExpect( status().isCreated() );
    }

}
