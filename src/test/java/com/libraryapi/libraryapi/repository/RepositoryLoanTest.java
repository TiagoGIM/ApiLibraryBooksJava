package com.libraryapi.libraryapi.repository;

import java.time.LocalDate;

import com.libraryapi.libraryapi.model.Book;
import com.libraryapi.libraryapi.model.Loan;

import org.assertj.core.api.Assertions;
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
public class RepositoryLoanTest {
    @Autowired
    TestEntityManager entityManager;

    @Autowired
    RepositoryLoan repository;

    @Test@DisplayName("Deve verificar se existe emprestimo nao retornado para o livro")
    public void existsByBookAndNotReturnedTest(){
        // cenario
        Book book = creatValidBook();
        entityManager.persist(book);

        Loan loan = Loan.builder()
        .costumer("costumer")
        .book(book)
        .loanDate(LocalDate.now())
        .build();
        entityManager.persist(loan);
        //execucao
        boolean exists = repository.existsByBookAndNotReturned(book);
        Assertions.assertThat(exists).isTrue();

    }

    public static Book creatValidBook(){
        return Book.builder()
        .author("author")
        .title("title")
        .isbn("isbn")
        .build();
      }
    
}
