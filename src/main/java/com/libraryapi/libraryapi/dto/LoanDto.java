package com.libraryapi.libraryapi.dto;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor 
@AllArgsConstructor
public class LoanDto {
  private Long id;
  private String isbm;
  private String customer;
  private BookDto book;
}
