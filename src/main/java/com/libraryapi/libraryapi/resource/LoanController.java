package com.libraryapi.libraryapi.resource;

import java.time.LocalDate;

import com.libraryapi.libraryapi.dto.LoanDto;
import com.libraryapi.libraryapi.model.Book;
import com.libraryapi.libraryapi.model.Loan;
import com.libraryapi.libraryapi.service.IBookService;
import com.libraryapi.libraryapi.service.ILoanService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.http.HttpStatus;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor // Spreing vai injetar a aplicação automaticamente
public class LoanController {

    private final ILoanService service;
    private final IBookService bookService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Long createdLoan(@RequestBody LoanDto loanDto) {

        Book book = bookService.getByIsbn(loanDto.getIsbm())
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.BAD_REQUEST,
            "Book not founded for isbm riquired")
        );

        Loan entity = Loan
        .builder()
        .book(book)
        .loanDate(LocalDate.now())
        .costumer(loanDto.getCostumer())
        .build();

        entity = service.save(entity);
        return entity.getId();
    }
}
