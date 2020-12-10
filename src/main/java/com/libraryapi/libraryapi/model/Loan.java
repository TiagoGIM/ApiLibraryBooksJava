package com.libraryapi.libraryapi.model;

import java.time.LocalDate;

import javax.annotation.Generated;
import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity

public class Loan {
    @Id
    private Long id;

    private String costumer;

    private Book book;

    private LocalDate loanDate;

    private Boolean returned;
}
