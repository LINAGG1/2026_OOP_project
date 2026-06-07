package controller;

import model.BookRepository;
import model.User;
import view.LoginView;
import view.AdminView;
import view.BookSearchView;
import view.MyLibView;
import javax.swing.*;

/*
로그인과 회원가입 기능을 처리하는 컨트롤러
사용자 권한에 따라 화면을 전환한다.
*/
public class LoginController { 

    private BookRepository repository; // 데이터 저장소
    private LoginView loginView;       // 로그인 화면  
    
    // 저장소와 로그인 화면 연결
    public LoginController(BookRepository repository, LoginView loginView) { 
        this.repository = repository;
        this.loginView = loginView;
        
        // 로그인 화면 이벤트 등록
        initListeners();
    }
 
    // 로그인 및 회원가입 이벤트 등록
    private void initListeners() {
        
        // 회원가입 요청 처리
        loginView.addRegisterListener((id, pw, name) -> {
            User newUser = new User(id, pw, name, "USER");
            boolean success = repository.registerUser(newUser);

            if (success) {
                // 회원가입 완료 후 창 닫기
                loginView.disposeRegisterDialog();
                JOptionPane.showMessageDialog(loginView, "회원가입이 성공적으로 완료되었습니다.", "회원가입 성공", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(loginView, "이미 존재하는 아이디입니다.", "회원가입 오류", JOptionPane.ERROR_MESSAGE);
            }
        });

        // 로그인 요청 처리
        loginView.addLoginListener(e -> {
            String id = loginView.getIdInput();
            String pw = loginView.getPwInput();

            // 로그인 정보 확인
            User user = login(id, pw);

            // 인증 실패 시 로그인 오류 메시지 출력
            if (user == null) { 
                JOptionPane.showMessageDialog(loginView, "아이디 또는 비밀번호가 올바르지 않습니다.", "로그인 오류", JOptionPane.ERROR_MESSAGE);
                return;
            }

            JOptionPane.showMessageDialog(loginView, user.getName() + "님 환영합니다.", "로그인 성공", JOptionPane.INFORMATION_MESSAGE);
            
            // 로그인 창 숨기고 메인 화면 실행
            loginView.setVisible(false);
            executeSession(user); // 로그인 성공 후 사용자 권한에 맞는 화면으로 전환
        });
    }

    // 로그인 성공 후 권한에 맞는 화면 실행
    private void executeSession(User user) {
        if (isAdmin(user)) { // 관리자 계정인지 확인
            AdminView adminView = new AdminView();
            // 관리자 화면 실행
            new AdminController(repository, adminView);
            
            // 로그아웃 시 다시 로그인 화면 표시
            adminView.addLogoutListener(ev -> {
                adminView.dispose();
                loginView.setVisible(true);
            });
            adminView.setVisible(true); // 관리자 메인 화면 실행
        } else {
            BookSearchView searchView = new BookSearchView();
            MyLibView myLibView = new MyLibView();
            // 일반 사용자 기능 컨트롤러 생성
            new LibraryController(repository, user, searchView, myLibView);
            
            searchView.addLogoutListener(ev -> {
                searchView.dispose();
                myLibView.dispose();
                loginView.setVisible(true);
            });
            searchView.setVisible(true);
        }
    }

    // 로그인 정보 확인
    public User login(String userId, String password) { 
        return repository.login(userId, password); 
    }
    
    // 관리자 계정 여부 확인
    public boolean isAdmin(User user) { 
        return user != null && user.isAdmin();
    }
}
