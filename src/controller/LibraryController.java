package controller;

import java.util.ArrayList;
import model.BookRepository;
import model.Book;
import model.User;
import model.BorrowRecord;
import view.BookSearchView;
import view.MyLibView;

/*
사용자 기능을 처리하는 컨트롤러
도서 검색, 대여, 반납, 마이페이지 기능을 담당한다.
*/
public class LibraryController { 
    
    private BookRepository repository; // 데이터 저장소
    private User currentUser;          // 현재 로그인한 사용자
    private BookSearchView searchView; // 검색 화면
    private MyLibView myLibView;       // 마이페이지 화면

    // 화면과 데이터를 연결하고 초기 설정 수행
    public LibraryController(BookRepository repository, User user, BookSearchView searchView, MyLibView myLibView) { 
        this.repository = repository;
        this.currentUser = user;
        this.searchView = searchView;
        this.myLibView = myLibView;

        // 이벤트 등록 및 초기 데이터 표시
        initListeners();
        loadInitialData();
    }

    // 전체 도서 목록을 검색 화면에 표시
    private void loadInitialData() {
        searchView.clearTable(); // 기존 검색 결과를 초기화
        for (Book book : repository.getBooks()) { // 저장소의 전체 도서 목록을 조회하여 화면 테이블에 출력
            searchView.addBookRow(book.getBookId(), book.getTitle(), book.getAuthor(), book.isBorrowed());
        }
    }

    // 사용자 화면의 버튼 이벤트 등록
    private void initListeners() {

        // 검색 버튼 클릭 시 조건에 맞는 도서 조회
        searchView.addSearchListener(e -> {
            String category = searchView.getSearchCategory();
            String keyword = searchView.getSearchKeyword();

            searchView.clearTable();

            // 검색어가 없으면 전체 도서 목록 표시
            if (keyword.isEmpty()) {
                for (Book book : repository.getBooks()) {
                    searchView.addBookRow(book.getBookId(), book.getTitle(), book.getAuthor(), book.isBorrowed());
                }
                return;
            }

            // 선택한 검색 조건에 따라 도서 검색
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

        // 대여 신청 처리
        searchView.addRentListener(e -> {
            String selectedBookId = searchView.getSelectedBookId();
            if (selectedBookId == null) return;

            // 도서 대여 처리
            boolean success = borrowBook(currentUser.getUserId(), selectedBookId);

            if (success) {
                // 대여 후 화면 정보 갱신
                myLibView.updateUserInfo(currentUser.getName(), currentUser.getCurrentBorrowCount(), 5);
                loadInitialData();
                searchView.showMessage("선택하신 도서의 대여 처리가 완료되었습니다.", "대여 성공", true);
            } else {
                searchView.showMessage("현재 대여 중이거나 대여 가능 권수(최대 5권)를 초과하여 대여할 수 없습니다.", "대여 오류", false);
            }
        });

        // 마이페이지 화면 열기
        searchView.addMyPageListener(e -> {
            myLibView.clearTable();

            // 현재 대여 중인 도서 목록 표시
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
        // 반납 요청 처리
        myLibView.addReturnListener(e -> {
            String selectedBookId = myLibView.getSelectedBookId();
            if (selectedBookId == null) return;

            // 도서 반납 처리
            boolean success = returnBook(currentUser.getUserId(), selectedBookId);

            if (success) {
                myLibView.showMessage("도서 반납 요청이 성공적으로 처리되었습니다.", "반납 성공", true);

                // 반납 후 마이페이지 정보 갱신
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
                // 검색 화면 정보 갱신
                loadInitialData();
            } else {
                myLibView.showMessage("도서 반납 처리에 실패하였습니다.", "반납 오류", false);
            }
        });
    }

    // 대여 가능 여부를 확인한 후 대여 처리
    public boolean borrowBook(String userId, String bookId) { 
         
        User user = repository.findUserById(userId);
        if (user == null || user.getCurrentBorrowCount() >= 5) {
            return false;
        }
        return repository.borrowBook(userId, bookId);
    }

    // 도서 반납 처리
    public boolean returnBook(String userId, String bookId) { 
        return repository.returnBook(userId, bookId);
    }
    
    // 제목으로 도서 검색
    public ArrayList<Book> searchBookByTitle(String title) { 
        return repository.searchBookByTitle(title);
    }
     
    // 저자로 도서 검색
    public ArrayList<Book> searchBookByAuthor(String author) { 
        return repository.searchBookByAuthor(author);
    }

    // ID로 도서 검색
    public Book searchBookByBookId(String bookId) { 
        return repository.searchBookByBookId(bookId);
    }
}
