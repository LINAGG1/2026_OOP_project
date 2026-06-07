package controller;

import model.Book;
import model.BookRepository;
import view.AdminView;

/*
관리자 전용 비즈니스 로직 제어 및 화면 연동을 담당하는 Controller
의존성 주입을 통한 Model 및 View 제어, 도서 추가·삭제 이벤트 핸들링 및 데이터 동기화 수행
*/
public class AdminController {

    private BookRepository repository; // 도서 데이터를 관리하는 Repository 객체 참조 변수
    private AdminView adminView;       // 관리자 전용 GUI 화면 객체 참조 변수

    // Repository 및 View 객체를 생성자 파라미터로 주입받아 초기화 및 리스너 바인딩 기동
    public AdminController(BookRepository repository, AdminView adminView) {
        this.repository = repository;
        this.adminView = adminView;
        
        // 💡 컨트롤러가 켜질 때 기존 저장된 도서 목록을 화면 테이블에 로드합니다.
        loadExistingBooks();
        
        // 컨트롤러 기동 시 뷰의 이벤트 리스너 일괄 초기화 및 바인딩
        initListeners();
    }

    // 저장소에서 기존 도서 데이터를 가져와 뷰에 삽입하는 메소드
    private void loadExistingBooks() {
        // 💡 [수정] getTableModel()을 쓰지 않고, AdminView에 구현된 addBookRow를 바로 활용합니다.
        // 만약 뷰 내부에 테이블을 전체 초기화(지우기)하는 메소드(예: clearTable())가 따로 있다면 
        // 여기에 adminView.clearTable(); 같은 형태로 호출해주셔도 좋습니다.

        // 저장소의 모든 도서를 순회하며 뷰에 추가
        for (Book book : repository.getBooks()) {
            adminView.addBookRow(
                book.getBookId(),
                book.getTitle(),
                book.getAuthor(),
                book.isBorrowed()
            );
        }
    }

    // 기존 Main.java에 분산되어 있던 관리자 기능 이벤트 리스너 수거 및 통합 바인딩 메소드
    private void initListeners() {
        
        // 신규 도서 등록 버튼 클릭 이벤트 처리 및 데이터 모델 동기화
        adminView.addInsertListener(e -> {
            String bookId = adminView.getBookIdInput();
            String title = adminView.getBookTitleInput();
            String author = adminView.getBookAuthorInput();

            // 도서 객체 생성 및 비즈니스 등록 로직 검증 호출
            Book book = new Book(bookId, title, author);
            boolean success = addBook(book);

            if (success) { // 등록 성공시 관리자 화면의 도서 목록 테이블을 즉시 갱신
                // 뷰 내부로 캡슐화된 행 추가 메소드 호출을 통한 UI 동기화
                adminView.addBookRow(bookId, title, author, false);
                adminView.clearInputFields(); // 다음 도서 등록을 위해 입력 필드를 초기화
                adminView.showMessage("도서가 성공적으로 등록되었습니다.", "등록 성공", true);
            } else {
                adminView.showMessage("이미 존재하는 도서 ID입니다.", "등록 오류", false);
            }
        });

        // 기존 도서 데이터 영구 삭제 버튼 클릭 이벤트 처리 및 UI 동기화
        adminView.addDeleteListener(e -> {
            String selectedBookId = adminView.getSelectedBookId();
            if (selectedBookId == null) return;

            // 비즈니스 삭제 로직 검증 호출
            boolean success = removeBook(selectedBookId);

            if (success) { // 삭제 성공 시 화면에서도 선택한 도서 행을 제거하여 데이터와 동기화
                // 뷰 내부로 캡슐화된 행 제거 메소드 호출을 통한 UI 동기화
                adminView.removeSelectedRow();
                adminView.showMessage("선택한 도서가 성공적으로 삭제되었습니다.", "삭제 성공", true);
            } else {
                adminView.showMessage("도서가 존재하지 않거나 현재 대여 중인 도서는 삭제할 수 없습니다.", "삭제 오류", false);
            }
        });
    }

    // 신규 도서 등록 중복 검증 및 저장소 추가 비즈니스 로직
    public boolean addBook(Book book) {
        if (repository.findBookById(book.getBookId()) != null) {
            return false;
        }
        repository.addBook(book); // 중복이 아닌 경우 저장소에 신규 도서를 추가
        return true;
    }

    // 대상 도서 존재 여부 및 대여 상태 검증 후 영구 삭제 비즈니스 로직
    public boolean removeBook(String bookId) {
        Book book = repository.findBookById(bookId);

        if (book == null) {
            return false;
        }

        if (book.isBorrowed()) {
            return false;
        }

        repository.removeBook(bookId); // 삭제 조건을 만족하면 저장소에서 해당 도서를 제거
        return true;
    }
}
