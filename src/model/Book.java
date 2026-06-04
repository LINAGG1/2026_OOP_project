package model;

public class Book {

    private String bookId;
    private String title;
    private String author;
    private boolean borrowed;

    public Book(String bookId, String title, String author, boolean borrowed) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.borrowed = borrowed;
    }

    public Book(String bookId, String title, String author) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.borrowed = false;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public boolean isBorrowed() {
        return borrowed;
    }

    public void setBorrowed(boolean borrowed) {
        this.borrowed = borrowed;
    }

    @Override
    public String toString() {
        return bookId + ", " + title + ", " + author + ", " +  borrowed;
    }
}
