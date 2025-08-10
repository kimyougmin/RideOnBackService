package com.ll.rideon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@EntityScan(basePackages = "com.ll.rideon.domain")
public class RideOnApplication {

	public static void main(String[] args) {
		SpringApplication.run(RideOnApplication.class, args); // Application Context getBean -> Configuration -> Spring Bean 다 등록하는 메소드를 생성해준다.
		// Spring Bean 칸이 1개 -> 행위를 해뒀을껀데 그 행위에 대해서 필요한 클래스들을 빈에다가 올렸다가
		// 자바의 객체 지향 프로그래밍
	}

	// ServiceConfiguration -> UserService
	// UserService : login() , logout()

	// static

	// new Class -> method call

	// 1. 객체 생성 x -> static -> stack

	// Data Transfer Object (DTO)
//	UserLoginRequest -> User -> data 를 찾아와야함
//
//	login : id , password , token
//
//	class UserLoginRequest {
//		private Long id;
//		private String password;
//		private String token;
//	}
//
//	Controller(UserLoginRequest request) {
//		Service.login(request);
//	}
//
//	Service(UserLoginRequest request) {
//		request -> domain
//				정적 메소드를 써서 바로 변환하고 싶음 -> 1줄 // 정적팩토리 메서드 패턴
//		User user = new User(); -> 1만명 이렇게 되면 ...
//		user.toEntity();
//
//			->	User.toEntity(request);
//
//	}
//
//	User {
//
//		public static toEntity(UserLoginRequest request) {
//			return new User(id, password);
//		}
//	}

}
