package controller;

import java.util.ArrayList;
import model.BookRepository;
import model.Book;
import model.User;
import model.BorrowRecord;
import view.BookSearchView;
import view.MyLibView;

/*
일반 사용자의 도서 검색, 대여, 반납 및 마이페이지 현황 연동을 처리하는 Controller
의존성 주입을 통한 데이터 모델 제어 및 멀티 뷰 패키지 컴포넌트 간의 상태 동기화 수행
*/
public class LibraryController { 
    
    private BookRepository repository; // 도서 및 사용자 데이터를 관리하는 Repository 객체 참조 변수
    private User currentUser;          // 현재 시스템에 로그인된 사용자 세션 객체 참조 변수
    private BookSearchView searchView; // 도서 검색 및 대여 신청 GUI 화면 객체 참조 변수
    private MyLibView myLibView;       // 개인 대여 현황 및 반납 전용 GUI 화면 객체 참조 변수

    // 비즈니스 데이터 저장소, 유저 세션 및 상호작용할 모든 뷰 객체를 주입받아 초기화 수행
    public LibraryController(BookRepository repository, User user, BookSearchView searchView, MyLibView myLibView) { 
        this.repository = repository;
        this.currentUser = user;
        this.searchView = searchView;
        this.myLibView = myLibView;

        // 컨트롤러 바인딩 기동 시 뷰의 이벤트 리스너 일괄 연동 및 테이블 초기 데이터 로드
        initListeners();
        loadInitialData();
    }

    // 시스템 시동 시 보유 중인 전체 도서 목록을 검색 뷰의 테이블에 초기화 바인딩하는 메소드
    private void loadInitialData() {
        searchView.clearTable();
        for (Book book : repository.getBooks()) {
            searchView.addBookRow(book.getBookId(), book.getTitle(), book.getAuthor(), book.isBorrowed());
        }
    }

    // 기존 Main.java에 분산되어 있던 사용자 기능 이벤트 리스너 수거 및 통합 바인딩 메소드
    private void initListeners() {

        // JComboBox 카테고리 선택 조건 및 키워드 기반 도서 동적 검색 이벤트 처리
        searchView.addSearchListener(e -> {
            String category = searchView.getSearchCategory();
            String keyword = searchView.getSearchKeyword();

            searchView.clearTable();

            // 검색어 미입력 시 전체 도서 목록 리로드 처리
            if (keyword.isEmpty()) {
                for (Book book : repository.getBooks()) {
                    searchView.addBookRow(book.getBookId(), book.getTitle(), book.getAuthor(), book.isBorrowed());
                }
                return;
            }

            // 카테고리 매핑 조건 분기를 통한 비즈니스 검색 로직 수행 및 UI 반영
            if (category.equals("도서명")) {
                for (Book book : searchBookByTitle(keyword)) {
                    searchView.addBookRow(book.getBookId(), book.getTitle(), book.getAuthor(), book.isBorrowed());
                }
            } else if (category.equals("저자")) {
                for (Book book : searchBookByAuthor(keyword)) {
                    searchView.addBookRow(book.getBookId(), book.getTitle(), book.getAuthor(), book.isBorrowed());
                }
            } else if (category.equals("도서 ID")) {
                Book book = searchBookByBookId(keyword);
                if (book != null) {
                    searchView.addBookRow(book.getBookId(), book.getTitle(), book.getAuthor(), book.isBorrowed());
                }
            }
        });

        // 사용자가 선택한 특정 도서 객체 기반 대여 신청 이벤트 처리 및 화면 상태 갱신
        searchView.addRentListener(e -> {
            String selectedBookId = searchView.getSelectedBookId();
            if (selectedBookId == null) return;

            // 대여 비즈니스 로직 호출 및 결과 검증
            boolean success = borrowBook(currentUser.getUserId(), selectedBookId);

            if (success) {
                // 실시간 대여 수량 반영 및 메인 검색 테이블 상태 동동기화
                myLibView.updateUserInfo(currentUser.getName(), currentUser.getCurrentBorrowCount(), 5);
                loadInitialData();
                searchView.showMessage("선택하신 도서의 대여 처리가 완료되었습니다.", "대여 성공", true);
            } else {
                searchView.showMessage("현재 대여 중이거나 대여 가능 권수(최대 5권)를 초과하여 대여할 수 없습니다.", "대여 오류", false);
            }
        });

        // 마이페이지 활성화 요청 시 실시간 미반납 대여 기록 추출 및 가시화 연동
        searchView.addMyPageListener(e -> {
            myLibView.clearTable();

            // 현재 사용자가 보유 중인 활성화 대여 기록 순회 및 행 데이터 삽입
            for (BorrowRecord record : currentUser.getBorrowRecords()) {
                if (!record.isReturned()) {
                    myLibView.addBorrowRow(
                            record.getBook().getBookId(),
                            record.getBook().getTitle(),
                            record.getBorrowDate().toString(),
                            record.getDueDate().toString()
                    );
                }
            }
            myLibView.setVisible(true);
        });

        // 대여 도서 반납 버튼 클릭 이벤트 처리, 상위 저장소 동기화 및 메인/서브 뷰 상태 갱신
        myLibView.addReturnListener(e -> {
            String selectedBookId = myLibView.getSelectedBookId();
            if (selectedBookId == null) return;

            // 반납 비즈니스 로직 호출 및 결과 검증
            boolean success = returnBook(currentUser.getUserId(), selectedBookId);

            if (success) {
                myLibView.showMessage("도서 반납 요청이 성공적으로 처리되었습니다.", "반납 성공", true);

                // 반납 완료에 따른 마이페이지 사용자 정보 및 테이블 데이터 실시간 리프레시
                myLibView.updateUserInfo(currentUser.getName(), currentUser.getCurrentBorrowCount(), 5);
                myLibView.clearTable();
                for (BorrowRecord record : currentUser.getBorrowRecords()) {
                    if (!record.isReturned()) {
                        myLibView.addBorrowRow(
                                record.getBook().getBookId(),
                                record.getBook().getTitle(),
                                record.getBorrowDate().toString(),
                                record.getDueDate().toString()
                        );
                    }
                }
                // 메인 검색 뷰 도서 상태 동기화
                loadInitialData();
            } else {
                myLibView.showMessage("도서 반납 처리에 실패하였습니다.", "반납 오류", false);
            }
        });
    }

    // 사용자별 최대 대여 가능 권수(5권) 선행 검증 및 저장소 대여 요청 트랜잭션 처리
    public boolean borrowBook(String userId, String bookId) { 
         
        User user = repository.findUserById(userId);
        if (user == null || user.getCurrentBorrowCount() >= 5) {
            return false;
        }
        return repository.borrowBook(userId, bookId);
    }

    // 도서 반납 요청 전달 및 트랜잭션 수행 처리
    public boolean returnBook(String userId, String bookId) { 
        return repository.returnBook(userId, bookId);
    }
    
    // 대상 키워드가 포함된 도서 제목 검색 데이터 인출
    public ArrayList<Book> searchBookByTitle(String title) { 
        return repository.searchBookByTitle(title);
    }
     
    // 대상 키워드가 포함된 저자명 검색 데이터 인출
    public ArrayList<Book> searchBookByAuthor(String author) { 
        return repository.searchBookByAuthor(author);
    }

    // 도서 고유 ID 기반 단건 도서 데이터 조회
    public Book searchBookByBookId(String bookId) { 
        return repository.searchBookByBookId(bookId);
    }
}