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

    // 로그인 화면 생성 및 인증 핸들링 공통 메소드
    public static void showLogin(BookRepository repository) {
        LoginView loginView = new LoginView();
        LoginController loginController = new LoginController(repository);

        // 회원가입 완료 이벤트 연동
        loginView.addRegisterListener((id, pw, name) -> {
            User newUser = new User(id, pw, name, "USER");
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

        // 로그인 버튼 인증 이벤트 처리 및 화면 전환 제어
        loginView.addLoginListener(e -> {
            String id = loginView.getIdInput();
            String pw = loginView.getPwInput();

            User user = loginController.login(id, pw);

            if (user == null) {
                JOptionPane.showMessageDialog(
                        loginView,
                        "아이디 또는 비밀번호가 올바르지 않습니다."
                );
                return;
            }

            JOptionPane.showMessageDialog(
                    loginView,
                    user.getName() + "님 환영합니다."
            );

            loginView.setVisible(false);
            executeSession(user, repository);
        });

        loginView.setVisible(true);
    }

    // 사용자 권한별 메인 화면 제어 및 비즈니스 데이터 동기화 메소드
    private static void executeSession(User user, BookRepository repository) {
        // =========================
        // 관리자 전용 제어 영역
        // =========================
        if (user.isAdmin()) {

            AdminView adminView = new AdminView();
            AdminController adminController = new AdminController(repository);

            // 기존 도서 목록 전체 조회 및 테이블 행 데이터 삽입
            for (Book book : repository.getBooks()) {
                adminView.getTableModel().addRow(
                        new Object[]{
                                book.getBookId(),
                                book.getTitle(),
                                book.getAuthor(),
                                book.isBorrowed() ? "대여중" : "대여가능"
                        });
            }

            // 신규 도서 등록 이벤트 처리 및 데이터 모델 동기화
            adminView.addInsertListener(ev -> {
                String bookId = adminView.getBookIdInput();
                String title = adminView.getBookTitleInput();
                String author = adminView.getBookAuthorInput();

                Book book = new Book(bookId, title, author);
                
                // 기존 void 반환형 규격에 맞춘 도서 등록 메소드 호출
                adminController.addBook(book);

                adminView.getTableModel().addRow(
                        new Object[]{
                                book.getBookId(),
                                book.getTitle(),
                                book.getAuthor(),
                                "대여가능"
                        });

                adminView.clearInputFields();
                JOptionPane.showMessageDialog(adminView, "도서가 등록되었습니다.");
            });

            // 기존 도서 데이터 영구 삭제 이벤트 연동
            adminView.addDeleteListener(ev -> {
                String selectedBookId = adminView.getSelectedBookId();
                if (selectedBookId == null) return;

                // 기존 void 반환형 규격에 맞춘 도서 삭제 메소드 호출
                adminController.removeBook(selectedBookId);

                for (int i = 0; i < adminView.getTableModel().getRowCount(); i++) {
                    String idInTable = (String) adminView.getTableModel().getValueAt(i, 0);
                    if (idInTable.equals(selectedBookId)) {
                        adminView.getTableModel().removeRow(i);
                        break;
                    }
                }

                JOptionPane.showMessageDialog(adminView, "도서가 삭제되었습니다.");
            });

            adminView.setVisible(true);
        }

        // =========================
        // 일반 사용자 전용 제어 영역
        // =========================
        else {

            BookSearchView searchView = new BookSearchView();
            MyLibView myLibView = new MyLibView();
            
            // 마이페이지 회원별 세션 정보 인출 및 컴포넌트 데이터 초기화
            myLibView.updateUserInfo(user.getName(), user.getCurrentBorrowCount(), 5);
            LibraryController libraryController = new LibraryController(repository);

            // 시스템 보유 전체 도서 목록 매핑 및 시각화 테이블 연동
            for (Book book : repository.getBooks()) {
                searchView.getTableModel().addRow(
                        new Object[]{
                                book.getBookId(),
                                book.getTitle(),
                                book.getAuthor(),
                                book.isBorrowed() ? "대여중" : "대여가능"
                        });
            }

            // 마이페이지 활성화 및 사용자 실시간 대여 기록 데이터 로드
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

            // 대여 도서 반납 요청 처리 및 테이블 뷰 상태 실시간 갱신
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

            // JComboBox 선택 카테고리 조건에 따른 도서 검색 결과 동적 매핑
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
                } else if (category.equals("저자")) {
                    for (Book book : libraryController.searchBookByAuthor(keyword)) {
                        searchView.getTableModel().addRow(
                                new Object[]{
                                        book.getBookId(),
                                        book.getTitle(),
                                        book.getAuthor(),
                                        book.isBorrowed() ? "대여중" : "대여가능"
                                    });
                    }
                } else if (category.equals("도서 ID")) {
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

            // 도서 대여 신청 처리 및 도서 데이터 대여 가능 상태 상태 연동
            searchView.addRentListener(ev -> {
                String bookId = searchView.getSelectedBookId();
                libraryController.borrowBook(user.getUserId(), bookId);

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
                JOptionPane.showMessageDialog(searchView, "대여 처리가 수행되었습니다.");
            });

            searchView.setVisible(true);
        }
    }

    public static void main(String[] args) {
        // 프로그램 메인 엔트리포인트 스레드 기동 및 로그인 화면 로드
        SwingUtilities.invokeLater(() -> showLogin(new BookRepository()));
    }
}
