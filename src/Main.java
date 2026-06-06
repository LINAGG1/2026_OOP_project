import controller.LoginController;
import model.BookRepository;
import view.LoginView;
import javax.swing.*;

/*
어플리케이션의 엔트리포인트를 담당하는 시스템 최상위 실행 클래스
공통 데이터 저장소 및 최초 진입 화면을 생성한 후 세션 제어권을 컨트롤러에 완전히 위임
*/
public class Main {
    public static void main(String[] args) {
        // 프로그램 메인 엔트리포인트 스레드 기동 및 런타임 환경 인스턴스 생성
        SwingUtilities.invokeLater(() -> {
            
            // 1. 시스템 전역 공통 데이터 저장소(Model) 초기화
            BookRepository repository = new BookRepository();
            
            // 2. 최초 진입점 사용자 레이아웃 화면(View) 객체 생성
            LoginView loginView = new LoginView();
            
            // 3. 로그인 세션 총괄 컨트롤러 생성 및 생성자 주입을 통한 시스템 제어권 이양
            new LoginController(repository, loginView);
            
            // 4. 시스템 사용자 인증 화면 시각화 기동
            loginView.setVisible(true);
        });
    }
}