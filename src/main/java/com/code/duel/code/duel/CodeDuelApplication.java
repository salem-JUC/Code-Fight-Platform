package com.code.duel.code.duel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class CodeDuelApplication {

	public static void main(String[] args) {
		SpringApplication.run(CodeDuelApplication.class, args);
	}

}
