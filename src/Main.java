import controller.AdminController;
import controller.LibraryController;
import controller.LoginController;
import model.Book;
import model.BookRepository;
import model.User;
import view.AdminView;
import view.BookSearchView;
import view.LoginView;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {

        // Repository 하나만 생성해서 공유
        BookRepository repository = new BookRepository();

        // 테스트용 도서
        repository.addBook(
                new Book("001", "자바 프로그래밍", "홍길동"));

        repository.addBook(
                new Book("002", "자료구조", "김철수"));

        repository.addBook(
                new Book("003", "운영체제", "이영희"));

        repository.addBook(
                new Book("004", "데이터베이스", "박민수"));

        // 테스트용 계정
        repository.registerUser(
                new User("admin", "1234", "관리자", "ADMIN"));

        repository.registerUser(
                new User("test", "1234", "사용자", "USER"));

        // 로그인 관련 객체 생성
        LoginView loginView = new LoginView();

        LoginController loginController =
                new LoginController(repository);

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