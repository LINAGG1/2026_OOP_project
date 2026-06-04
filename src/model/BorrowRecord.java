package model;

import java.time.LocalDate;

public class BorrowRecord {

    private Book book;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private boolean returned;

    public BorrowRecord(Book book) {
        this.book = book;
        this.borrowDate = LocalDate.now();
        this.dueDate = borrowDate.plusDays(14);
        this.returned = false;
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
