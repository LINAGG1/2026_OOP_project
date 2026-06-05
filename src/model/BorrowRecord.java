package model;

import java.time.LocalDate;

public class BorrowRecord {
    private String userId;
    private Book book;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private boolean returned;

    public BorrowRecord(String userId, Book book) {
        this.userId = userId;
        this.book = book;
        this.borrowDate = LocalDate.now();          // 대여일은 오늘 날짜로
        this.dueDate = LocalDate.now().plusDays(14); // 반납 기한
        this.returned = false;                      // 처음 빌리는 거니 false
    }

    // 기존에 있던 5개짜리 생성자 (파일에서 로딩할 때 사용됨)
    public BorrowRecord(String userId, Book book, LocalDate borrowDate, LocalDate dueDate, boolean returned) {
        this.userId = userId;
        this.book = book;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returned = returned;
    }

    public String getUserId() {
        return userId;
    }

    public Book getBook() {
        return book;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public boolean isReturned() {
        return returned;
    }

    public void returnBook() {
        returned = true;
    }

    @Override
    public String toString() {
        return book.getTitle()
                + " | 대여일: "
                + borrowDate
                + " | 반납기한: "
                + dueDate
                + " | 반납여부: "
                + returned;
    }
}