package controller;

import java.util.ArrayList;
import model.BookRepository;
import model.Book;
/*
일반 사용자의 도서 대여 및 반납 기능을 처리하는 Controller 
*/
public class LibraryController { 
    
    private BookRepository repository; //도서 및 사용자 정보를 관리하는 Repository

    public LibraryController(BookRepository repository) { //Repository 객체를 전달받아 연결
        this.repository = repository;
    }

    public boolean borrowBook(String userId, String bookId) { //도서 대여 요청 처리
        return repository.borrowBook(userId, bookId);
    }

    public boolean returnBook(String userId, String bookId) { //도서 반납 요청 처리
        return repository.returnBook(userId, bookId);
    }
   
    public ArrayList<Book> searchBookByTitle(String title) { //책 제목으로 도서를 검색
        return repository.searchBookByTitle(title);
    }
    
    public ArrayList<Book> searchBookByAuthor(String author) { //저자로 도서 검색
        return repository.searchBookByAuthor(author);
    }

    public Book searchBookByBookId(String bookId) { //도서 ID로 도서 검색
        return repository.searchBookByBookId(bookId);
    }
}
