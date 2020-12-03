package com.libraryapi.libraryapi.exceptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.validation.BindingResult;

public class ApiErrors {
    private List<String> errors;
    public ApiErrors(BindingResult bResult){
        this.errors = new ArrayList<>();
        bResult.getAllErrors()
            .forEach(error -> this.errors.add(error.getDefaultMessage()));
    }
    public ApiErrors(BusinessException exception){
        this.errors = Arrays.asList(exception.getMessage());
    }

    public List<String> getErrors() {
        return errors;
    }

}
