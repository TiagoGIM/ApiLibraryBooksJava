package com.libraryapi.libraryapi.repository;

import java.time.LocalDate;
import java.util.List;

import com.libraryapi.libraryapi.model.Book;
import com.libraryapi.libraryapi.model.Loan;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class RepositoryLoanTest {
    @Autowired
    TestEntityManager entityManager;

    @Autowired
    RepositoryLoan repository;

    @Test
    @DisplayName("Deve verificar se existe emprestimo nao retornado para o livro")
    public void existsByBookAndNotReturnedTest(){
        // cenario
        Loan loan = createLoanPersistMock(LocalDate.now());
        Book book = loan.getBook();
         
        //execucao
        boolean exists = repository.existsByBookAndNotReturned(book);
        Assertions.assertThat(exists).isTrue();

    }

    @Test
    @DisplayName("Deve buscar por propriedades isbn ou costumer")
    public void findByBookIsbnOrCustomerTest(){
        // cenario
    Loan loan = createLoanPersistMock(LocalDate.now());

    Page<Loan> result = repository.findByBookIsbnOrCustomer("isbm", "customer", PageRequest.of(0,10));
            //verify

    Assertions.assertThat(result.getContent()).hasSize(1);
    Assertions.assertThat(result.getPageable().getPageNumber()).isZero();
    Assertions.assertThat(result.getPageable().getPageSize()).isEqualTo(10);
    Assertions.assertThat(result.getTotalElements()).isEqualTo(1);
    Assertions.assertThat(result.getContent()).contains(loan);
    }

    @Test
    @DisplayName("Deve retornar emprestimos com ate 3 dias de sem devolucao")
    public void findByLoanDateLessThanAndNotReturnedTest(){
    // cenario
    Loan loan = createLoanPersistMock(LocalDate.now().minusDays(5));
    //execution
    List<Loan> result = repository.findByLoanDateLessThanAndNotReturned(LocalDate.now().minusDays(4));
    //verify
    Assertions.assertThat(result).hasSize(1).contains(loan);
    }
    @Test
    @DisplayName("Não deve retornar nenhum emprestimo.")
    public void NotfindByLoanDateLessThanAndNotReturnedTest(){
    // cenario
    createLoanPersistMock(LocalDate.now());
    //execution
    List<Loan> result = repository.findByLoanDateLessThanAndNotReturned(LocalDate.now().minusDays(4));
    //verify
    Assertions.assertThat(result).isEmpty();
    }

    public Loan createLoanPersistMock(LocalDate date){

        Book book = creatValidBook();
        entityManager.persist(book);

        Loan loan = Loan.builder()
        .customer("customer")
        .book(book)
        //.customerEmail("customerEmail")
        .loanDate(date)
        .build();
        entityManager.persist(loan);
        return loan;
    }

    public Book creatValidBook(){
        return Book.builder()
        .author("author")
        .title("title")
        .isbn("isbn")
        .build();
      }
    
}
