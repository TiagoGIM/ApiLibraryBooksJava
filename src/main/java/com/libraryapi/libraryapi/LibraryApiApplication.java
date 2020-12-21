package com.libraryapi.libraryapi;

	import java.util.Arrays;
import java.util.List;

import com.libraryapi.libraryapi.service.EmailService;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling //agendamento de tarefas
@SpringBootApplication
public class LibraryApiApplication {
	//contextualizando dependencia desconhecida pelo spring
	@Bean
	public ModelMapper modelMapper(){
		return new ModelMapper();
	}
	public static void main(String[] args) {
		SpringApplication.run(LibraryApiApplication.class, args);
	}

}
