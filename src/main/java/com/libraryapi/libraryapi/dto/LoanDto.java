package com.libraryapi.libraryapi.dto;



import javax.validation.constraints.NotEmpty;

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
  @NotEmpty
  private String isbm;
  @NotEmpty
  private String customer;
  private BookDto book;
  @NotEmpty
  private String email;
}
