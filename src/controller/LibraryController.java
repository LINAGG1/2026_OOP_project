package controller;

import model.BookRepository;

public class LibraryController {

    private BookRepository repository;

    public LibraryController(BookRepository repository) {
        this.repository = repository;
    }

    public boolean borrowBook(String userId, String bookId) {
        return repository.borrowBook(userId, bookId);
    }

    public boolean returnBook(String userId, String bookId) {
        return repository.returnBook(userId, bookId);
    }
}