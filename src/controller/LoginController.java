package controller;

import model.BookRepository;
import model.User;
/*
로그인 관련 요청을 처리하는 Controller
View에서 전달받은 로그인 정보를 Repository에 전달
 */
public class LoginController { 

    private BookRepository repository; // 도서 및 사용자 데이터를 관리하는 Repository

    public LoginController(BookRepository repository) { //Repository 객체를 전달받아 Controller와 연결
        this.repository = repository;
    }
 
    public User login(String userId, String password) { //로그인 요청 처리
        return repository.login(userId, password);
    }
    
    public boolean isAdmin(User user) { //관리자 계정 여부 확인
        return user != null && user.isAdmin();
    }
}
