package com.libraryapi.libraryapi.resource;

import javax.validation.Valid;

import com.libraryapi.libraryapi.dto.BookDto;
import com.libraryapi.libraryapi.exceptions.ApiErrors;
import com.libraryapi.libraryapi.exceptions.BusinessException;
import com.libraryapi.libraryapi.model.Book;
import com.libraryapi.libraryapi.service.IBookService;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/books")
public class BookController {
  
  
  //injenção de dependencia por construtor
  private IBookService service;
  private ModelMapper modelMapper;
  public BookController(IBookService service,ModelMapper modelMapper){
    this.modelMapper = modelMapper;
    this.service = service;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public BookDto create(@RequestBody @Valid BookDto dto){
    // mapeia todas as props de mesmo nome.
    Book entity = modelMapper.map(dto, Book.class);
    entity = service.save(entity);

    return modelMapper.map(entity, BookDto.class);
  } 
  
  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ApiErrors handleValidationExcepitions(MethodArgumentNotValidException e) {
    BindingResult bindingResult = e.getBindingResult();
    return new ApiErrors(bindingResult);
  }
  @ExceptionHandler(BusinessException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ApiErrors handleBusinessException(BusinessException ex){
    return new ApiErrors(ex);
  }
}
