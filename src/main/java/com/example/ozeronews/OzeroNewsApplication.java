package com.example.ozeronews;

import com.example.ozeronews.models.Head;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class OzeroNewsApplication {

	public static void main(String[] args) {
		SpringApplication.run(OzeroNewsApplication.class, args);
	}

}
