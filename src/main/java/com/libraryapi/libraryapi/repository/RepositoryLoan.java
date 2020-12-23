package com.libraryapi.libraryapi.repository;

import java.time.LocalDate;
import java.util.List;

import com.libraryapi.libraryapi.model.Book;
import com.libraryapi.libraryapi.model.Loan;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositoryLoan extends JpaRepository <Loan, Long>{
 
  @Query("select case when ( count(l.id) >0 ) "+
    "then true else false end from Loan l where l.book =:book and"+
    "(  l.returned is null or l.returned is false)")
	boolean existsByBookAndNotReturned(@Param("book") Book book);
  //peguei do codigo de matheus, bem provavel que seja implementado na aula seguinte
  @Query("select l from Loan as l join l.book as b"+
    " where b.isbn = :isbn or l.customer= :customer")
	Page<Loan> findByBookIsbnOrCustomer(
    @Param("isbn") String isbn, 
    @Param("customer") String customer, 
    Pageable pageable
  );
Page<Loan> findByBook(Book book, Pageable pageable);

@Query("select l from Loan l where l.loanDate <= :threeDaysAgo and"+
" (l.returned is null or l.returned is false)")
List<Loan> findByLoanDateLessThanAndNotReturned(@Param("threeDaysAgo") LocalDate threeDaysAgo);
	

}
