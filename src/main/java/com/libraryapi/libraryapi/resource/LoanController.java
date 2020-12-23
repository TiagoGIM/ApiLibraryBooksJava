package com.libraryapi.libraryapi.resource;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.libraryapi.libraryapi.dto.BookDto;
import com.libraryapi.libraryapi.dto.LoanDto;
import com.libraryapi.libraryapi.dto.LoanFilterDTO;
import com.libraryapi.libraryapi.dto.RetrunedLoanDto;
import com.libraryapi.libraryapi.model.Book;
import com.libraryapi.libraryapi.model.Loan;
import com.libraryapi.libraryapi.service.IBookService;
import com.libraryapi.libraryapi.service.ILoanService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor // Spreing vai injetar a aplicação automaticamente
public class LoanController {

    private final ILoanService service;
    private final IBookService bookService;
    private final ModelMapper modelMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Long createdLoan(@RequestBody LoanDto loanDto) {

        Book book = bookService.getByIsbn(loanDto.getIsbn())
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.BAD_REQUEST,
            "Book not founded for isbn riquired")
        );

        Loan entity = Loan
        .builder()
        .book(book)
        .loanDate(LocalDate.now())
        .customer(loanDto.getCustomer())
        .build();

        entity = service.save(entity);
        return entity.getId();
    }

    @PatchMapping("{id}")
    public void returnBook( @PathVariable Long id, @RequestBody RetrunedLoanDto dto ){
        Loan loan = service.getById(id).get();
        loan.setReturned(dto.getReturned());

        service.update(loan); 
    }
    
    @GetMapping
    public PageImpl<LoanDto> find(LoanFilterDTO dto, Pageable pageRequest) {

    Page<Loan> result = service.find(dto, pageRequest);

    List<LoanDto> loans = result
    .getContent()
    .stream()
    .map( entity ->{
        Book book = entity.getBook();
        BookDto bookDto = modelMapper.map(book, BookDto.class);
        LoanDto loanDto = modelMapper.map(entity, LoanDto.class);
        loanDto.setBook(bookDto);
        return loanDto;
    })
    .collect(Collectors.toList()); 

    return new PageImpl<LoanDto>(loans, pageRequest, result.getTotalElements() );
  }
} 
