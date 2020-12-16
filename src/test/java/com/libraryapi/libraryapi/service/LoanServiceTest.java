package com.libraryapi.libraryapi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;

import java.time.LocalDate;

import com.libraryapi.libraryapi.exceptions.BusinessException;
import com.libraryapi.libraryapi.model.Book;
import com.libraryapi.libraryapi.model.Loan;
import com.libraryapi.libraryapi.repository.RepositoryLoan;

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
public class LoanServiceTest {


    ILoanService service;

    @MockBean   //simula comportamento do repository
    RepositoryLoan repository;

    @BeforeEach
    public void setUp(){
        this.service = new LoanService(repository);
    }
    
    @Test
    @DisplayName("Deve salvar um emprestimo")
    public void SaveLoanTest(){
        Long id =1L;
        Loan loan = Loan.builder()
        .costumer("costumer") 
        .book(Book.builder().id(id).build())
        .loanDate(LocalDate.now())
        .build();

        Loan savedLoan =  Loan.builder()
        .id(id)
        .costumer(loan.getCostumer())
        .book(loan.getBook())
        .loanDate(loan.getLoanDate())
        .returned(false)
        .build();
        Mockito.when(repository.existsByBookAndNotReturned(Book.builder().id(id).build())).thenReturn(false);
        Mockito.when(repository.save(loan)).thenReturn(savedLoan);

        Loan returnedLoan = service.save(loan);

        assertThat(returnedLoan.getId()).isEqualTo(id);
        assertThat(returnedLoan.getBook()).isEqualTo(loan.getBook());
        assertThat(returnedLoan.getCostumer()).isEqualTo(loan.getCostumer());

        Mockito.verify(repository, times(1)).save(loan);

    }

    @Test
    @DisplayName("Deve reatornar erro de negocio ao tentar usar livro emprestado")
    public void loanedBookSaveTest(){
        Long id =1L;
        Loan loan = Loan.builder()
        .costumer("costumer")
        .book(Book.builder().id(id).build())
        .loanDate(LocalDate.now())
        .build();

        Mockito.when(repository.existsByBookAndNotReturned(Book.builder().id(id).build())).thenReturn(true);

        Throwable exception = Assertions.catchThrowable(()-> service.save(loan));
        assertThat(exception)
            .isInstanceOf(BusinessException.class)
            .hasMessage("Boook Alread loaned");
        
        Mockito.verify(repository, Mockito.never()).save(loan);

    }
    @Test
    @DisplayName("Deve atualizar um empréstimo")
    public void updateLoanTest() {
        Long id =1L;
        Loan loan = Loan.builder()
        .id(id)
        .returned(true)
        .costumer("costumer")
        .book(Book.builder().id(id).build())
        .loanDate(LocalDate.now())
        .build();
     

        Mockito.when(repository.save(loan)).thenReturn(loan);

        Loan updatedLoan =service.update(loan); 

        assertThat(updatedLoan.getReturned()).isTrue();
        Mockito.verify(repository).save(loan);
    }
}
