package com.libraryapi.libraryapi.service;

import java.util.Optional;

import com.libraryapi.libraryapi.dto.LoanFilterDTO;
import com.libraryapi.libraryapi.model.Loan;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ILoanService {
    
	Loan save(Loan loan);

	Optional<Loan> getById(Long id);

	Loan update(Loan loan);

	Page<Loan> find(LoanFilterDTO filter, Pageable pageable);

}
