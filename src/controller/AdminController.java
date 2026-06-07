package controller;

import model.Book;
import model.BookRepository;
import view.AdminView;

/*
관리자 화면의 기능을 처리하는 컨트롤러
도서 등록, 삭제 기능을 담당한다.
*/
public class AdminController {

    private BookRepository repository; // 도서 정보 저장소
    private AdminView adminView;       // 관리자 화면

    // 관리자 화면
    public AdminController(BookRepository repository, AdminView adminView) {
        this.repository = repository;
        this.adminView = adminView;
        
        loadExistingBooks();
        
        // 화면 이벤트 등록
        initListeners();
    }

    // 저장된 도서 목록을 화면에 표시
    private void loadExistingBooks() {
        //도서 목록 출력
        for (Book book : repository.getBooks()) {
            adminView.addBookRow(
                book.getBookId(),
                book.getTitle(),
                book.getAuthor(),
                book.isBorrowed()
            );
        }
    }

    // 관리자 화면의 버튼 이벤트 등록
    private void initListeners() {
        
        // 도서 등록 처리
        adminView.addInsertListener(e -> {
            String bookId = adminView.getBookIdInput();
            String title = adminView.getBookTitleInput();
            String author = adminView.getBookAuthorInput();

            // 등록할 도서 객체 생성
            Book book = new Book(bookId, title, author);
            boolean success = addBook(book);

            if (success) { // 등록 성공시 관리자 화면의 도서 목록 테이블 갱신
                // 화면에 새 도서 추가
                adminView.addBookRow(bookId, title, author, false);
                adminView.clearInputFields(); // 입력 필드 초기화
                adminView.showMessage("도서가 성공적으로 등록되었습니다.", "등록 성공", true);
            } else {
                adminView.showMessage("이미 존재하는 도서 ID입니다.", "등록 오류", false);
            }
        });

        // 도서 삭제 처리
        adminView.addDeleteListener(e -> {
            String selectedBookId = adminView.getSelectedBookId();
            if (selectedBookId == null) return;

            // 선택한 도서 삭제
            boolean success = removeBook(selectedBookId);

            if (success) { 
                // 화면에서도 삭제
                adminView.removeSelectedRow();
                adminView.showMessage("선택한 도서가 성공적으로 삭제되었습니다.", "삭제 성공", true);
            } else {
                adminView.showMessage("도서가 존재하지 않거나 현재 대여 중인 도서는 삭제할 수 없습니다.", "삭제 오류", false);
            }
        });
    }

    // 도서 등록
    public boolean addBook(Book book) {
        if (repository.findBookById(book.getBookId()) != null) {
            return false;
        }
        repository.addBook(book); // 중복이 아닌 경우 저장소에 신규 도서를 추가
        return true;
    }

    // 도서 삭제
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
