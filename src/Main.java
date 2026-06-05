import controller.AdminController;
import controller.LibraryController;
import controller.LoginController;
import model.Book;
import model.BookRepository;
import model.User;
import view.AdminView;
import view.BookSearchView;
import view.LoginView;
import view.MyLibView;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {

        // Repository 하나만 생성해서 공유
        BookRepository repository = new BookRepository();


        // 로그인 관련 객체 생성
        LoginView loginView = new LoginView();

        LoginController loginController =
                new LoginController(repository);

        // 회원가입 완료 이벤트 연결
        loginView.addRegisterListener((id, pw, name) -> {

            // 입력받은 정보로 일반 사용자 권한을 가진 새 유저 생성
            User newUser = new User(id, pw, name, "USER");

            // 레포지토리에 회원 등록 처리
            boolean success = repository.registerUser(newUser);

            if (success) {
                // 회원가입 성공 시 나경이가 구현한 파일 저장 메소드 호출
                // 메소드명에 맞춰 주석 해제해서 사용해!
            } else {
                JOptionPane.showMessageDialog(
                        loginView,
                        "이미 존재하는 아이디입니다.",
                        "회원가입 오류",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        // 로그인 버튼 이벤트 연결
        loginView.addLoginListener(e -> {

            String id = loginView.getIdInput();
            String pw = loginView.getPwInput();

            User user = loginController.login(id, pw);

            // 로그인 실패
            if (user == null) {

                JOptionPane.showMessageDialog(
                        loginView,
                        "아이디 또는 비밀번호가 올바르지 않습니다."
                );

                return;
            }

            // 로그인 성공
            JOptionPane.showMessageDialog(
                    loginView,
                    user.getName() + "님 환영합니다."
            );

            loginView.dispose();

            // =========================
            // 관리자 화면
            // =========================
            if (user.isAdmin()) {

                AdminView adminView = new AdminView();

                AdminController adminController =
                        new AdminController(repository);

                // 기존 도서 목록 표시
                for (Book book : repository.getBooks()) {

                    adminView.getTableModel().addRow(
                            new Object[]{
                                    book.getBookId(),
                                    book.getTitle(),
                                    book.getAuthor(),
                                    book.isBorrowed()
                                            ? "대여중"
                                            : "대여가능"
                            });
                }

                // 도서 등록
                adminView.addInsertListener(ev -> {

                    String bookId =
                            adminView.getBookIdInput();

                    String title =
                            adminView.getBookTitleInput();

                    String author =
                            adminView.getBookAuthorInput();

                    Book book =
                            new Book(bookId, title, author);

                    adminController.addBook(book);

                    adminView.getTableModel().addRow(
                            new Object[]{
                                    book.getBookId(),
                                    book.getTitle(),
                                    book.getAuthor(),
                                    "대여가능"
                            });

                    adminView.clearInputFields();

                    JOptionPane.showMessageDialog(
                            adminView,
                            "도서가 등록되었습니다."
                    );
                });

                // 도서 삭제
                adminView.addDeleteListener(ev -> {

                    String selectedBookId =
                            adminView.getSelectedBookId();

                    if (selectedBookId == null) {
                        return;
                    }

                    adminController.removeBook(
                            selectedBookId);

                    for (int i = 0;
                         i < adminView.getTableModel().getRowCount();
                         i++) {

                        String idInTable =
                                (String) adminView
                                        .getTableModel()
                                        .getValueAt(i, 0);

                        if (idInTable.equals(
                                selectedBookId)) {

                            adminView.getTableModel()
                                    .removeRow(i);

                            break;
                        }
                    }

                    JOptionPane.showMessageDialog(
                            adminView,
                            "도서가 삭제되었습니다."
                    );
                });

                adminView.setVisible(true);
            }

            // =========================
            // 일반 사용자 화면
            // =========================
            else {

                BookSearchView searchView =
                        new BookSearchView();

                MyLibView myLibView = 
                        new MyLibView();

                LibraryController libraryController =
                        new LibraryController(repository);

                // 처음 실행 시 전체 도서 표시
                for (Book book : repository.getBooks()) {

                    searchView.getTableModel().addRow(
                            new Object[]{
                                    book.getBookId(),
                                    book.getTitle(),
                                    book.getAuthor(),
                                    book.isBorrowed()
                                            ? "대여중"
                                            : "대여가능"
                            });
                }

                // 마이페이지 이동 버튼 이벤트 연결
                searchView.addMyPageListener(ev -> {
                    // 마이페이지 열 때마다 테이블 초기화 후 최신 대여 데이터 매핑
                    myLibView.getTableModel().setRowCount(0);
                    for (Book book : repository.getBooks()) {
                        // 대여된 도서 목록을 마이페이지 표에 동적 동기화
                        if (book.isBorrowed()) {
                            myLibView.getTableModel().addRow(new Object[]{
                                    book.getBookId(), 
                                    book.getTitle(), 
                                    book.getAuthor(), 
                                    "2026-06-05", // 반납일 예시 더미 매핑
                                    "2026-06-12"
                            });
                        }
                    }
                    myLibView.setVisible(true); // 마이페이지 화면 활성화
                });

                // 마이페이지 내부의 도서 반납 버튼 이벤트 연결
                myLibView.addReturnListener(ev -> {
                    String selectedBookId = myLibView.getSelectedBookId();
                    if (selectedBookId == null) return;

                    boolean success = libraryController.returnBook(user.getUserId(), selectedBookId);
                    if (success) {
                        JOptionPane.showMessageDialog(myLibView, "반납이 완료되었습니다.");
                        
                        // 반납 후 마이페이지 및 메인 검색창 테이블 동시 실시간 동기화
                        myLibView.getTableModel().setRowCount(0);
                        searchView.getTableModel().setRowCount(0);
                        for (Book book : repository.getBooks()) {
                            searchView.getTableModel().addRow(new Object[]{
                                    book.getBookId(), book.getTitle(), book.getAuthor(), book.isBorrowed() ? "대여중" : "대여가능"
                            });
                            if (book.isBorrowed()) {
                                myLibView.getTableModel().addRow(new Object[]{
                                        book.getBookId(), book.getTitle(), book.getAuthor(), "2026-06-05", "2026-06-12"
                                });
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(myLibView, "반납 처리에 실패했습니다.");
                    }
                });

                // 검색 버튼
                searchView.addSearchListener(ev -> {

                    String category =
                            searchView.getSearchCategory();

                    String keyword =
                            searchView.getSearchKeyword();

                    searchView.getTableModel().setRowCount(0);

                    if (keyword.isEmpty()) {
                      for (Book book : repository.getBooks()) {
                        searchView.getTableModel().addRow(
                            new Object[]{
                                    book.getBookId(),
                                    book.getTitle(),
                                    book.getAuthor(),
                                    book.isBorrowed()
                                            ? "대여중"
                                            : "대여가능"
                            });
                      }
                      return;
                    }

                    if (category.equals("도서명")) {

                        for (Book book :
                                libraryController.searchBookByTitle(keyword)) {

                            searchView.getTableModel().addRow(
                                    new Object[]{
                                            book.getBookId(),
                                            book.getTitle(),
                                            book.getAuthor(),
                                            book.isBorrowed()
                                                    ? "대여중"
                                                    : "대여가능"
                                    });
                        }
                    }

                    else if (category.equals("저자")) {

                        for (Book book :
                                libraryController.searchBookByAuthor(keyword)) {

                            searchView.getTableModel().addRow(
                                    new Object[]{
                                            book.getBookId(),
                                            book.getTitle(),
                                            book.getAuthor(),
                                            book.isBorrowed()
                                                    ? "대여중"
                                                    : "대여가능"
                                    });
                        }
                    }

                    else if (category.equals("도서 ID")) {

                        Book book =
                                libraryController.searchBookByBookId(keyword);

                        if (book != null) {

                            searchView.getTableModel().addRow(
                                    new Object[]{
                                            book.getBookId(),
                                            book.getTitle(),
                                            book.getAuthor(),
                                            book.isBorrowed()
                                                    ? "대여중"
                                                    : "대여가능"
                                    });
                        }
                    }
                });

                // 대여 버튼
                searchView.addRentListener(ev -> {

                    String bookId =
                            searchView.getSelectedBookId();

                    boolean success =
                            libraryController.borrowBook(
                                    user.getUserId(),
                                    bookId
                            );

                    if (success) {

                      searchView.getTableModel().setRowCount(0);
                      for (Book book : repository.getBooks()) {
                        searchView.getTableModel().addRow(
                            new Object[]{
                                    book.getBookId(),
                                    book.getTitle(),
                                    book.getAuthor(),
                                    book.isBorrowed()
                                            ? "대여중"
                                            : "대여가능"
                            });
                      }

                      JOptionPane.showMessageDialog(
                              searchView,
                              "대여가 완료되었습니다."
                      );
                      

                    } else {

                        JOptionPane.showMessageDialog(
                                searchView,
                                "대여할 수 없습니다."
                        );
                    }
                });

                searchView.setVisible(true);
            }
        });

        SwingUtilities.invokeLater(() ->
                loginView.setVisible(true));
    }
}