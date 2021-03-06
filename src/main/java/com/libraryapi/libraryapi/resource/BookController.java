package com.libraryapi.libraryapi.resource;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import com.libraryapi.libraryapi.dto.BookDto;
import com.libraryapi.libraryapi.dto.LoanDto;
import com.libraryapi.libraryapi.model.Book;
import com.libraryapi.libraryapi.model.Loan;
import com.libraryapi.libraryapi.service.IBookService;
import com.libraryapi.libraryapi.service.ILoanService;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.DeleteMapping;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Api("BOOK API")
@Slf4j
public class BookController {

  private final ILoanService serviceLoan;
  private final IBookService service;
  private final ModelMapper modelMapper;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @ApiOperation("CREATE A BOOK")
  public BookDto create(@RequestBody @Valid BookDto dto) {
    log.info("Creating a book for isbn : {} ", dto.getIsbn());
    Book entity = modelMapper.map(dto, Book.class);
    entity = service.save(entity);

    return modelMapper.map(entity, BookDto.class);
  }

  @GetMapping("/{id}")
  @ApiOperation("FIND BOOK BY ID")
  public BookDto findByid(@PathVariable Long id) {
    return service.getById(id).map(book -> modelMapper.map(book, BookDto.class))
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
  }

  @GetMapping
  public Page<BookDto> find(BookDto dto, Pageable pageRequest) {

    Book filter = modelMapper.map(dto, Book.class);

    Page<Book> result = service.find(filter, pageRequest);

    List<BookDto> listOfBooks = result.getContent()
    .stream()
    .map(entity -> modelMapper.map(entity, BookDto.class))
    .collect(Collectors.toList());

    return new PageImpl<BookDto>(listOfBooks, pageRequest, result.getTotalElements() );
  }

  @GetMapping("/hello")
  public String hello(){
    return "hello";
  }


  @DeleteMapping("{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ApiResponses({
    @ApiResponse(code =204,message="deleted with sucess"),
    @ApiResponse(code =401, message ="operation invalid")
  })
  public void delete(@PathVariable Long id){
    log.info("Deleting book of isbn : {} ", id);
    Book book = service.getById(id)
    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND))
    ;
    service.delete(book);
  }
  
  @PutMapping("{id}")
  public BookDto update(@PathVariable Long id,@RequestBody BookDto dto){
    Book book = service.getById(id)
    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND))
    ;
    book.setAuthor(dto.getAuthor()); // a treta ta aqui 
    book.setTitle(dto.getTitle()); //esses metodos tao retornando null
    book = service.update(book);
    return modelMapper.map(book, BookDto.class);
  }

  @GetMapping("{id}/loan")
  public Page<LoanDto> loansByBooks(@PathVariable Long id, Pageable pageable){

    Book book = service
    .getById(id)
    .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND));

    Page<Loan> result = serviceLoan.getLoansByBook(book,pageable);

    List<LoanDto> list =result.getContent()
    .stream()
    .map( loan -> { 
      BookDto bookDto = modelMapper.map(loan.getBook(), BookDto.class);
      LoanDto loanDto = modelMapper.map(loan , LoanDto.class);
      loanDto.setBook(bookDto);
      return loanDto;
    })
    .collect(Collectors.toList());

    return  new PageImpl<LoanDto>(list, pageable, result.getTotalElements() );
  }
}

