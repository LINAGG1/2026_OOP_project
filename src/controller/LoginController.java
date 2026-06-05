package controller;

import model.BookRepository;
import model.User;

/*
로그인 관련 요청을 처리하는 Controller
View 계층에서 전달받은 인증 정보를 Repository 계층으로 전달
 */
public class LoginController { 

    private BookRepository repository; // 도서 및 사용자 데이터를 관리하는 Repository 객체

    public LoginController(BookRepository repository) { // Repository 객체를 주입받아 초기화
        this.repository = repository;
    }
 
    public User login(String userId, String password) { // 로그인 인증 요청 처리 및 유저 객체 반환
        return repository.login(userId, password);
    }
    
    public boolean isAdmin(User user) { // 관리자 계정 권한 여부 확인
        return user != null && user.isAdmin();
    }
}