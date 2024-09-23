package com.ayds.zeday;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@PropertySource("file:${user.dir}/.env")
@SpringBootApplication
public class ZeroDayApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZeroDayApplication.class, args);
	}

}
