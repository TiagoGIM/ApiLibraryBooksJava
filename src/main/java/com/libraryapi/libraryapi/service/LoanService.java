package com.libraryapi.libraryapi.service;

import java.util.Optional;

import com.libraryapi.libraryapi.dto.LoanFilterDTO;
import com.libraryapi.libraryapi.exceptions.BusinessException;
import com.libraryapi.libraryapi.model.Loan;
import com.libraryapi.libraryapi.repository.RepositoryLoan;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Loan update(Loan loan) {

        return repository.save(loan);
    }


    @Override
    public Page<Loan> find(LoanFilterDTO filter, Pageable pageRequest) {
        //provavelmente vai dar erro devido a ter escrito "costumer"
        return repository.findByBookIsbnOrCustomer(filter.getIsbm(), filter.getCustomer(), pageRequest);
    }
    
    
}
