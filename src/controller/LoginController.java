package controller;

import model.BookRepository;
import model.User;
import view.LoginView;
import view.AdminView;
import view.BookSearchView;
import view.MyLibView;
import javax.swing.*;

/*
로그인 관련 인증 요청 처리 및 권한별 메인 세션 전환을 제어하는 총괄 Controller
의존성 주입을 통한 저장소 연동, 인터페이스 기반 회원가입·로그인 이벤트 핸들링 및 화면 라우팅 수행
*/
public class LoginController { 

    private BookRepository repository; // 도서 및 사용자 데이터를 관리하는 Repository 객체 참조 변수
    private LoginView loginView;       // 최초 진입점인 로그인 GUI 화면 객체 참조 변수

    // Repository 및 LoginView 객체를 주입받아 초기화하고 세션 기동 리스너 바인딩 수행
    public LoginController(BookRepository repository, LoginView loginView) { 
        this.repository = repository;
        this.loginView = loginView;
        
        // 컨트롤러 기동 시 로그인 화면의 이벤트 리스너 일괄 바인딩
        initListeners();
    }
 
    // 기존 Main.java에 분산되어 있던 회원인증 관련 이벤트 리스너 수거 및 통합 바인딩 메소드
    private void initListeners() {
        
        // 회원가입 서브밋 이벤트 처리 및 중복 계정 비즈니스 검증 연동
        loginView.addRegisterListener((id, pw, name) -> {
            User newUser = new User(id, pw, name, "USER");
            boolean success = repository.registerUser(newUser);

            if (success) {
                // 뷰 캡슐화 메소드 호출을 통한 JDialog 팝업 닫기 및 성공 안내 제어
                loginView.disposeRegisterDialog();
                JOptionPane.showMessageDialog(loginView, "회원가입이 성공적으로 완료되었습니다.", "회원가입 성공", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(loginView, "이미 존재하는 아이디입니다.", "회원가입 오류", JOptionPane.ERROR_MESSAGE);
            }
        });

        // 로그인 인증 이벤트 처리 및 권한별 메인 세션 화면 분기 제어
        loginView.addLoginListener(e -> {
            String id = loginView.getIdInput();
            String pw = loginView.getPwInput();

            // 입력한 계정 정보를 이용하여 로그인 인증 수행
            User user = login(id, pw);

            if (user == null) { // 인증 실패 시 로그인 오류 메시지 출력
                JOptionPane.showMessageDialog(loginView, "아이디 또는 비밀번호가 올바르지 않습니다.", "로그인 오류", JOptionPane.ERROR_MESSAGE);
                return;
            }

            JOptionPane.showMessageDialog(loginView, user.getName() + "님 환영합니다.", "로그인 성공", JOptionPane.INFORMATION_MESSAGE);
            
            // 로그인 완료 시 인증 창 시각화 종료 및 권한별 메인 세션 라우팅 실행
            loginView.setVisible(false);
            executeSession(user); // 로그인 성공 후 사용자 권한에 맞는 화면으로 전환
        });
    }

    // 인증된 사용자 권한(ADMIN / USER) 판별에 따른 메인 프레임 동적 기동 및 제어권 이양 메소드
    private void executeSession(User user) {
        if (isAdmin(user)) { // 관리자 여부를 판별하여 관리자 또는 일반 사용자 화면으로 분기
            AdminView adminView = new AdminView();
            // 관리자 전용 컨트롤러를 생성하여 뷰와 저장소 제어권을 완전히 위임
            new AdminController(repository, adminView);
            
            // 세션 종료 후 로그인 화면 복원을 위한 로그아웃 리스너 가로채기 연동
            adminView.addLogoutListener(ev -> {
                adminView.dispose();
                loginView.setVisible(true);
            });
            adminView.setVisible(true); // 관리자 메인 화면 실행
        } else {
            BookSearchView searchView = new BookSearchView();
            MyLibView myLibView = new MyLibView();
            // 일반 사용자 전용 컨트롤러를 생성하여 멀티 뷰와 저장소 제어권을 완전히 위임
            new LibraryController(repository, user, searchView, myLibView);
            
            // 세션 종료 후 로그인 화면 복원을 위한 로그아웃 리스너 가로채기 연동
            searchView.addLogoutListener(ev -> {
                searchView.dispose();
                myLibView.dispose();
                loginView.setVisible(true);
            });
            searchView.setVisible(true);
        }
    }

    // 저장소 엔티티를 통한 로그인 인증 요청 처리 및 결과 유저 객체 반환 비즈니스 로직
    public User login(String userId, String password) { 
        return repository.login(userId, password); // 저장소에 로그인 인증을 요청하고 결과를 반환
    }
    
    // 유저 객체의 관리자 권한 보유 여부 검증 비즈니스 로직
    public boolean isAdmin(User user) { 
        return user != null && user.isAdmin(); // 로그인한 사용자가 관리자 권한을 보유하고 있는지 확인
    }
}
