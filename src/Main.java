import controller.AdminController;
import controller.LibraryController;
import controller.LoginController;
import model.Book;
import model.BookRepository;
import model.User;
import model.BorrowRecord;
import view.AdminView;
import view.BookSearchView;
import view.LoginView;
import view.MyLibView;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {

        // Repository 객체 생성 및 초기화 (생성자 내부에서 파일 로드 자동 수행)
        BookRepository repository = new BookRepository();

        // 로그인 뷰 및 컨트롤러 객체 생성
        LoginView loginView = new LoginView();
        LoginController loginController = new LoginController(repository);

        // 회원가입 완료 이벤트 연동
        loginView.addRegisterListener((id, pw, name) -> {

            // 신규 사용자 객체 생성 (기본 권한: USER)
            User newUser = new User(id, pw, name, "USER");

            // 저장소에 회원 등록 수행 (메소드 내부에서 saveUsers 자동 수행)
            boolean success = repository.registerUser(newUser);

            if (!success) {
                JOptionPane.showMessageDialog(
                        loginView,
                        "이미 존재하는 아이디입니다.",
                        "회원가입 오류",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        // 로그인 버튼 이벤트 연동
        loginView.addLoginListener(e -> {

            String id = loginView.getIdInput();
            String pw = loginView.getPwInput();

            User user = loginController.login(id, pw);

            // 로그인 실패 처리
            if (user == null) {

                JOptionPane.showMessageDialog(
                        loginView,
                        "아이디 또는 비밀번호가 올바르지 않습니다."
                );

                return;
            }

            // 로그인 성공 처리
            JOptionPane.showMessageDialog(
                    loginView,
                    user.getName() + "님 환영합니다."
                );

            loginView.dispose();

            // =========================
            // 관리자 화면 제어
            // =========================
            if (user.isAdmin()) {

                AdminView adminView = new AdminView();
                AdminController adminController = new AdminController(repository);

                 // 로그아웃 버튼 추가 
                adminView.addLogoutListener(ev -> {

                    repository.logout();
                    adminView.dispose();

                    showLogin(repository);
                });
                
                // 기존 도서 목록 전체 조회 및 테이블 추가
                for (Book book : repository.getBooks()) {

                    adminView.getTableModel().addRow(
                            new Object[]{
                                    book.getBookId(),
                                    book.getTitle(),
                                    book.getAuthor(),
                                    book.isBorrowed() ? "대여중" : "대여가능"
                            });
                }

                // 신규 도서 등록 이벤트 처리
                adminView.addInsertListener(ev -> {

                    String bookId = adminView.getBookIdInput();
                    String title = adminView.getBookTitleInput();
                    String author = adminView.getBookAuthorInput();

                    Book book = new Book(bookId, title, author);

                    // 관리자 도서 등록 요청 수행
                    boolean success = adminController.addBook(book);

                    // 동일 도서 ID 존재 여부 검증
                    if (!success) {
                        JOptionPane.showMessageDialog(
                                adminView,
                                "이미 존재하는 도서 ID입니다."
                        );
                        return;
                    }

                    adminView.getTableModel().addRow((
                            new Object[]{
                                    book.getBookId(),
                                    book.getTitle(),
                                    book.getAuthor(),
                                    "대여가능"
                            }));

                    adminView.clearInputFields();

                    JOptionPane.showMessageDialog(
                            adminView,
                            "도서가 등록되었습니다."
                    );
                });

                // 기존 도서 삭제 이벤트 처리
                adminView.addDeleteListener(ev -> {

                    String selectedBookId = adminView.getSelectedBookId();

                    if (selectedBookId == null) {
                        return;
                    }

                    boolean success =
                        adminController.removeBook(selectedBookId);

                    if (!success) {

                        JOptionPane.showMessageDialog(
                                adminView,
                                "대여중인 도서는 삭제할 수 없습니다."
                        );

                        return;
                    }

                    for (int i = 0; i < adminView.getTableModel().getRowCount(); i++) {

                        String idInTable = (String) adminView.getTableModel().getValueAt(i, 0);

                        if (idInTable.equals(selectedBookId)) {
                            adminView.getTableModel().removeRow(i);
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
            // 일반 사용자 화면 제어
            // =========================
            else {

                BookSearchView searchView = new BookSearchView();
                MyLibView myLibView = new MyLibView();
                myLibView.updateUserInfo(
                    user.getName(),
                    user.getCurrentBorrowCount(),
                    5
            );
                LibraryController libraryController = new LibraryController(repository);

                // 전체 도서 목록 기본 출력
                for (Book book : repository.getBooks()) {

                    searchView.getTableModel().addRow(
                            new Object[]{
                                    book.getBookId(),
                                    book.getTitle(),
                                    book.getAuthor(),
                                    book.isBorrowed() ? "대여중" : "대여가능"
                            });
                }

                // 마이페이지 전환 및 데이터 동기화 이벤트 처리
                searchView.addMyPageListener(ev -> {

                myLibView.getTableModel().setRowCount(0);

                for (BorrowRecord record : user.getBorrowRecords()) {

                    if (!record.isReturned()) {

                        myLibView.getTableModel().addRow(
                             new Object[]{
                                record.getBook().getBookId(),
                                record.getBook().getTitle(),
                                record.getBorrowDate(),
                                record.getDueDate()
                            }
                        );
                    }
                  }

                myLibView.setVisible(true);
            });

                // 대여 도서 반납 이벤트 처리
                myLibView.addReturnListener(ev -> {
                    String selectedBookId = myLibView.getSelectedBookId();
                    if (selectedBookId == null) return;

                    boolean success = libraryController.returnBook(user.getUserId(), selectedBookId);
                    if (success) {
                        JOptionPane.showMessageDialog(myLibView, "반납이 완료되었습니다.");
                        
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

                // 도서 검색 이벤트 처리
                searchView.addSearchListener(ev -> {

                    String category = searchView.getSearchCategory();
                    String keyword = searchView.getSearchKeyword();

                    searchView.getTableModel().setRowCount(0);

                    if (keyword.isEmpty()) {
                        for (Book book : repository.getBooks()) {
                            searchView.getTableModel().addRow(
                                new Object[]{
                                        book.getBookId(),
                                        book.getTitle(),
                                        book.getAuthor(),
                                        book.isBorrowed() ? "대여중" : "대여가능"
                                });
                        }
                        return;
                    }

                    if (category.equals("도서명")) {

                        for (Book book : libraryController.searchBookByTitle(keyword)) {

                            searchView.getTableModel().addRow(
                                    new Object[]{
                                            book.getBookId(),
                                            book.getTitle(),
                                            book.getAuthor(),
                                            book.isBorrowed() ? "대여중" : "대여가능"
                                    });
                        }
                    }

                    else if (category.equals("저자")) {

                        for (Book book : libraryController.searchBookByAuthor(keyword)) {

                            searchView.getTableModel().addRow(
                                    new Object[]{
                                            book.getBookId(),
                                            book.getTitle(),
                                            book.getAuthor(),
                                            book.isBorrowed() ? "대여중" : "대여가능"
                                    });
                        }
                    }

                    else if (category.equals("도서 ID")) {

                        Book book = libraryController.searchBookByBookId(keyword);

                        if (book != null) {

                            searchView.getTableModel().addRow(
                                    new Object[]{
                                            book.getBookId(),
                                            book.getTitle(),
                                            book.getAuthor(),
                                            book.isBorrowed() ? "대여중" : "대여가능"
                                    });
                        }
                    }
                });

                // 도서 대여 신청 이벤트 처리
                searchView.addRentListener(ev -> {

                    String bookId = searchView.getSelectedBookId();

                    boolean success = libraryController.borrowBook(
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
                                        book.isBorrowed() ? "대여중" : "대여가능"
                                });
                        }

                        JOptionPane.showMessageDialog(
                                searchView,
                                "대여가 완료되었습니다."
                        );

                    } else {

                        JOptionPane.showMessageDialog(
                                searchView,
                                "대여 불가 (대여 권수 초과 또는 이미 대여중인 도서)"
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
