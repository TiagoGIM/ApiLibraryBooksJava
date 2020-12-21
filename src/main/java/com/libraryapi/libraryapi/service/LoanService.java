package com.libraryapi.libraryapi.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.libraryapi.libraryapi.dto.LoanFilterDTO;
import com.libraryapi.libraryapi.exceptions.BusinessException;
import com.libraryapi.libraryapi.model.Book;
import com.libraryapi.libraryapi.model.Loan;
import com.libraryapi.libraryapi.repository.RepositoryLoan;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class LoanService implements ILoanService {

    private RepositoryLoan repository;

    // injeção de dependencia
    public LoanService(RepositoryLoan repository) {
        this.repository = repository;
    }

    @Override
    public Loan save(Loan loan) {
        if (repository.existsByBookAndNotReturned(loan.getBook())) {
            throw new BusinessException("Boook Alread loaned");
        }
        return repository.save(loan);
    }

    @Override
    public Optional<Loan> getById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Loan update(Loan loan) {
        return repository.save(loan);
    }

    @Override
    public Page<Loan> find(LoanFilterDTO filter, Pageable pageRequest) {
        return repository.findByBookIsbnOrCustomer(filter.getIsbm(), filter.getCustomer(), pageRequest);
    }

    @Override
    public Page<Loan> getLoansByBook(Book book, Pageable pageable) {
        return repository.findByBook(book, pageable);
    }

    @Override
    public List<Loan> getAllLateLoans() {
        final long loanDays = 4;
        LocalDate threeDaysAgo = LocalDate.now().minusDays(loanDays);

        return repository.findByLoanDateLessThanAndNotReturned(threeDaysAgo);
    }
}
    