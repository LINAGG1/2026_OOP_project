package model;

import java.util.ArrayList;

public class BookRepository {
  private ArrayList<Book> books;
  private ArrayList<User> users;

  public BookRepository() {
    this.books = new ArrayList<>();
    this.users = new ArrayList<>();
  }

  public void addBook(Book book) {
    books.add(book);
  }

  public void removeBook(String bookId) {
    Book book = findBookById(bookId);

    if (book != null) {
      books.remove(book);
    }
  }

  public Book findBookById(String bookId) {
    for (Book book : books) {
      if (book.getBookId().equals(bookId)) {
        return book;
      }
    }
    return null;
  }

  public ArrayList<Book> getBooks() {
    return books;
  }

  public void addUser(User user) {
    users.add(user);
  }

  public User findUserById(String userId) {
    for (User user : users) {
      if (user.getUserId().equals(userId)) {
        return user;
      }
    }
    return null;
  }

  public ArrayList<User> getUsers() {
    return users;
  }

  // 로그인 기능
  public User login(String userId, String password) {
    User user = findUserById(userId);
    if (user != null && user.checkPassword(password)) {
      return user;
    }
    return null;
  }

  public boolean borrowBook(String userId, String bookId) {
    User user = findUserById(userId);
    Book book = findBookById(bookId);

    if (user == null || book == null) {
      return false;
    }

    if (book.isBorrowed()) {
      return false;
    }

    book.setBorrowed(true);
    user.borrowBook(book);

    return true;
  }

  // 반납 기능
  public boolean returnBook(String userId, String bookId) {
    User user = findUserById(userId);
    Book book = findBookById(bookId);

    if (user == null || book == null) {
      return false;
    }

    book.setBorrowed(false);
    user.returnBook(book);

    return true;
  }
  
}
