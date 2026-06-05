package controller;

import model.Book;
import model.BookRepository;
/*
관리자 전용 기능을 처리하는 Controller
도서 추가 및 삭제 기능을 담당
*/
public class AdminController {

    private BookRepository repository; //도서 데이터를 관리하는 Repository

    public AdminController(BookRepository repository) { //Repository 객체를 전달받아 연결
        this.repository = repository;
    }

    public boolean addBook(Book book) {

    if (repository.findBookById(book.getBookId()) != null) {
        return false;
    }

    repository.addBook(book);
    return true;
}

    public boolean removeBook(String bookId) {

        Book book = repository.findBookById(bookId);

        if (book == null) {
            return false;
        }

        if (book.isBorrowed()) {
            return false;
        }

        repository.removeBook(bookId);
        return true;
    }
}
