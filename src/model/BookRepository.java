package model;

import java.util.ArrayList;
import java.io.*;

public class BookRepository {

  private ArrayList<Book> books;
  private ArrayList<User> users;

  public BookRepository() {
      books = new ArrayList<>();
      users = new ArrayList<>();

      loadBooks();
      loadUsers();
    }

    // =====================
    // 도서 관리
    // =====================

  public void addBook(Book book) {
    books.add(book);
    saveBooks();
  }

  public void removeBook(String bookId) {

    Book book = findBookById(bookId);

      if (book != null) {
        books.remove(book);
        saveBooks();
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

  public ArrayList<Book> searchBookByTitle(String title) {

    ArrayList<Book> result = new ArrayList<>();

      for (Book book : books) {

        if (book.getTitle().contains(title)) {
          result.add(book);
        }
      }

      return result;
    }

  public ArrayList<Book> searchBookByAuthor(String author) {

    ArrayList<Book> result = new ArrayList<>();

      for (Book book : books) {

        if (book.getAuthor().contains(author)) {
          result.add(book);
        }
      }

      return result;
    }

  public Book searchBookByBookId(String bookId) {
    return findBookById(bookId);
  }

  public ArrayList<Book> getBooks() {
    return books;
  }

    // =====================
    // 회원 관리
    // =====================

  public boolean registerUser(User user) {

    if (findUserById(user.getUserId()) != null) {
      return false;
    }

    users.add(user);

    saveUsers();
    return true;
  }

  public void addUser(User user) {
    users.add(user);
    saveUsers();
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

    // =====================
    // 로그인
    // =====================

  public User login(String userId, String password) {

    User user = findUserById(userId);

      if (user != null && user.checkPassword(password)) {

        return user;
      }

    return null;
  }

    // =====================
    // 대여
    // =====================

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
    saveBooks();
    return true;
  }

    // =====================
    // 반납
    // =====================

  public boolean returnBook(String userId, String bookId) {

    User user = findUserById(userId);
    Book book = findBookById(bookId);

    if (user == null || book == null) {
      return false;
    }

    book.setBorrowed(false);

    user.returnBook(book);
    saveBooks();
    return true;
  }

  public void saveBooks() {

    try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream("books.txt"), "UTF-8"))) {
      for (Book book : books) {
        pw.println(book.getBookId() + "," + book.getTitle() + "," + book.getAuthor() + "," + book.isBorrowed());
      }
    } catch (IOException e) {
      e.printStackTrace();  
    }
  }

  private void loadBooks() {
    File file = new File("books.txt");
    if (!file.exists()) {
      return;
    }
    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
      String line;
      while ((line = br.readLine()) != null) {
        String[] data = line.split(",");
        if (data.length == 4) {
          books.add(
            new Book(
              data[0],
              data[1],
              data[2],
              Boolean.parseBoolean(data[3])
            )
          );
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void saveUsers() {

    try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream("users.txt"), "UTF-8"))) {
      for (User user : users) {
        pw.println(user.getUserId() + "," + user.getPassword() + "," + user.getName() + "," + user.getRole());
      }
    } catch (IOException e) {
      e.printStackTrace();  
    }
  }

  private void loadUsers() {
    File file = new File("users.txt");
    if (!file.exists()) {
      return;
    }
    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
      String line;
      while ((line = br.readLine()) != null) {
        String[] data = line.split(",");
        if (data.length == 4) {
          users.add(
            new User(
              data[0],
              data[1],
              data[2],
              data[3]
            )
          );
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

  }
}