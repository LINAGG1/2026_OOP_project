package model;

import java.util.ArrayList;

public class User {

    private String userId;
    private String password;
    private String name;
    private String role;

    // 현재 대여 중 + 과거 대여 이력 모두 저장
    private ArrayList<BorrowRecord> borrowRecords;

    // 생성자
    public User(String userId, String password, String name, String role) {
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.role = role;

        this.borrowRecords = new ArrayList<>();
    }

    // 로그인 비밀번호 확인
    public boolean checkPassword(String password) {
        return this.password.equals(password);
    }

    // 관리자 여부 확인
    public boolean isAdmin() {
        return role.equalsIgnoreCase("ADMIN");
    }

    // 현재 대여 중인 책이 있는지 확인
    public boolean hasBorrowedBooks() {

        for (BorrowRecord record : borrowRecords) {
            if (!record.isReturned()) {
                return true;
            }
        }

        return false;
    }

    public String getUserId() {
        return userId;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    // 전체 대여 이력 반환
    public ArrayList<BorrowRecord> getBorrowRecords() {
        return borrowRecords;
    }

    // 현재 대여 중인 도서 목록 반환
    public ArrayList<Book> getBorrowedBooks() {

        ArrayList<Book> books = new ArrayList<>();

        for (BorrowRecord record : borrowRecords) {

            if (!record.isReturned()) {
                books.add(record.getBook());
            }
        }

        return books;
    }

    // 도서 대여
    public static final int MAX_BORROW = 5;
    public boolean borrowBook(Book book) {
        if (getCurrentBorrowCount() >= MAX_BORROW) {
            return false; // 최대 대여 수 초과
        }

        borrowRecords.add(new BorrowRecord(userId, book));
        return true;
    }

    public void addLoadedRecord(BorrowRecord record) {
        if (this.borrowRecords == null) {
            this.borrowRecords = new ArrayList<>();
        }
        if (!this.borrowRecords.contains(record)) {
            this.borrowRecords.add(record);
        }
    }

    // 도서 반납
    public void returnBook(Book book) {

        for (BorrowRecord record : borrowRecords) {

            if (record.getBook().getBookId().equals(book.getBookId())
                    && !record.isReturned()) {

                record.returnBook();
                break;
            }
        }
    }

    // 현재 대여 중인 도서 수
    public int getCurrentBorrowCount() {

        int count = 0;

        for (BorrowRecord record : borrowRecords) {
            if (!record.isReturned()) {
                count++;
            }
        }

        return count;
    }

    @Override
    public String toString() {
        return userId + "," + password + "," + name + "," + role;
    }
}