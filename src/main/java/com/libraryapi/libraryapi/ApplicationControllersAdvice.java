package com.libraryapi.libraryapi;

import org.springframework.validation.BindingResult;
import com.libraryapi.libraryapi.exceptions.ApiErrors;
import com.libraryapi.libraryapi.exceptions.BusinessException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

//torna visivel para todos os controllers os handlers.(injeta em todos?)
@RestControllerAdvice
public class ApplicationControllersAdvice {
    
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
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity handleResponseStatusException(ResponseStatusException ex){
      return new ResponseEntity( new ApiErrors(ex) , ex.getStatus());
    }

}
