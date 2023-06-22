package org.curso.springboot.msvc.oauth.msvcoauth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class MsvcOauthApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsvcOauthApplication.class, args);
	}

}
