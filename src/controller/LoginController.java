package controller;

import model.BookRepository;
import model.User;

public class LoginController {

    private BookRepository repository;

    public LoginController(BookRepository repository) {
        this.repository = repository;
    }

    public User login(String userId, String password) {
        return repository.login(userId, password);
    }
}