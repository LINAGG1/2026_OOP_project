package controller;

import model.Book;
import model.BookRepository;

public class AdminController {

    private BookRepository repository;

    public AdminController(BookRepository repository) {
        this.repository = repository;
    }

    public void addBook(Book book) {
        repository.addBook(book);
    }

    public void removeBook(String bookId) {
        repository.removeBook(bookId);
    }
}