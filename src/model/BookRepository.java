package model;

import java.util.ArrayList;
import java.io.*;
import java.time.LocalDate;

public class BookRepository {

    private final String base = System.getProperty("user.dir");

    private ArrayList<Book> books;
    private ArrayList<User> users;
    private ArrayList<BorrowRecord> borrowRecords;

    public BookRepository() {
        books = new ArrayList<>();
        users = new ArrayList<>();
        borrowRecords = new ArrayList<>();

        loadBooks();
        loadUsers();
        loadBorrowRecords();
    }

    // =====================
    // 도서 관리
    // =====================

    public boolean addBook(Book book) {
        if (findBookById(book.getBookId()) != null) {
            return false;
        }
        books.add(book);
        saveBooks();
        return true;
    }

    public boolean removeBook(String bookId) {

        Book book = findBookById(bookId);

        if (book == null) {
            return false;
        }

        books.remove(book);
        saveBooks();
        return true;
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

        if (user == null || book == null) return false;
        if (book.isBorrowed()) return false;

        // 유저 제한 체크
        if (!user.borrowBook(book)) return false;

        book.setBorrowed(true);

        borrowRecords.add(new BorrowRecord(userId, book));

        saveBooks();
        saveBorrowRecords();

        return true;
    }

    // =====================
    // 반납
    // =====================

    public boolean returnBook(String userId, String bookId) {

        User user = findUserById(userId);
        Book book = findBookById(bookId);

        if (user == null || book == null) return false;
        if (!book.isBorrowed()) return false;

        user.returnBook(book);
        book.setBorrowed(false);

        // 기록 업데이트
        for (BorrowRecord r : borrowRecords) {
            if (r.getBook().getBookId().equals(bookId) && !r.isReturned()) {
                r.returnBook();
                break;
            }
        }

        saveBooks();
        saveBorrowRecords();

        return true;
    }

    // =====================
    // 파일 저장 (books)
    // =====================

    public void saveBooks() {

        try (PrintWriter pw = new PrintWriter(
                new OutputStreamWriter(
                        new FileOutputStream(new File(base, "books.txt")),
                        "UTF-8"))) {

            for (Book book : books) {
                pw.println(
                        book.getBookId() + "," +
                        book.getTitle() + "," +
                        book.getAuthor() + "," +
                        book.isBorrowed()
                );
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // =====================
    // 파일 저장 (users)
    // =====================

    private void saveUsers() {

        try (PrintWriter pw = new PrintWriter(
                new OutputStreamWriter(
                        new FileOutputStream(new File(base, "users.txt")),
                        "UTF-8"))) {

            for (User user : users) {
                pw.println(
                        user.getUserId() + "," +
                        user.getPassword() + "," +
                        user.getName() + "," +
                        user.getRole()
                );
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // =====================
    // borrowRecords 저장
    // =====================
    private void saveBorrowRecords() {

        try (PrintWriter pw = new PrintWriter(
                new OutputStreamWriter(
                        new FileOutputStream(new File(base, "borrowRecords.txt")),
                        "UTF-8"))) {

            for (BorrowRecord r : borrowRecords) {
                pw.println(
                        r.getUserId() + "," +
                        r.getBook().getBookId() + "," +
                        r.getBorrowDate() + "," +
                        r.getDueDate() + "," +
                        r.isReturned()
                );
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // =====================
    // borrowRecords 로딩 
    // =====================
    private void loadBorrowRecords() {

        File file = new File(base, "borrowRecords.txt");
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), "UTF-8"))) {

            String line;

            while ((line = br.readLine()) != null) {

                String[] data = line.split(",");

                if (data.length == 5) {

                    String userId = data[0];
                    String bookId = data[1];
                    LocalDate borrowDate = LocalDate.parse(data[2]);
                    LocalDate dueDate = LocalDate.parse(data[3]);
                    boolean returned = Boolean.parseBoolean(data[4]);

                    Book book = findBookById(bookId);
                    if (book == null) continue;

                    // 1. 파일에서 복구한 대여 기록 객체 생성
                    BorrowRecord record = new BorrowRecord(userId, book, borrowDate, dueDate, returned);

                    // 2. 전체 대여 리스트에 추가
                    borrowRecords.add(record);

                    // 3. 유저 객체를 찾아 해당 대여 기록(record)을 통째로 주입
                    User user = findUserById(userId);
                    if (user != null) {
                        user.addLoadedRecord(record); 
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // =====================
    // users 로딩
    // =====================

    private void loadUsers() {
        File file = new File(base, "users.txt");
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), "UTF-8"))) {

            String line;

            while ((line = br.readLine()) != null) {

                String[] data = line.split(",");

                if (data.length == 4) {
                    users.add(new User(
                            data[0],
                            data[1],
                            data[2],
                            data[3]
                    ));
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // =====================
    // books 로딩
    // =====================

    private void loadBooks() {

        File file = new File(base, "books.txt");
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), "UTF-8"))) {

            String line;

            while ((line = br.readLine()) != null) {

                String[] data = line.split(",");

                if (data.length == 4) {

                    books.add(new Book(
                            data[0],
                            data[1],
                            data[2],
                            Boolean.parseBoolean(data[3])
                    ));
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}