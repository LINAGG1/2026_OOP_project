package model;

import java.util.ArrayList;

public class User {

    private String userId;
    private String password;
    private String name;
    private String role;

    private ArrayList<Book> borrowedBooks;

    // 생성자
    public User(String userId, String password, String name, String role) {
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.role = role;
        this.borrowedBooks = new ArrayList<>();
    }

    // 로그인 비밀번호 확인 - 보안 상 없으면 좋음
    public boolean checkPassword(String password) {
        return this.password.equals(password);
    }

    // 관리자 여부 확인
    public boolean isAdmin() {
        return role.equalsIgnoreCase("ADMIN");
    }

    public boolean hasBorrowedBooks() {
        return !borrowedBooks.isEmpty();
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

    public ArrayList<Book> getBorrowedBooks() {
        return borrowedBooks;
    }


    public void borrowBook(Book book) {
        borrowedBooks.add(book);
    }


    public void returnBook(Book book) {
        borrowedBooks.remove(book);
    }

    @Override
    public String toString() {
        return userId + "," + password + "," + name + "," + role;
    }
}