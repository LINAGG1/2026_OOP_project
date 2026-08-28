# 2026_OOP_project

# 📚 도서관 관리 시스템

Java Swing과 MVC(Model-View-Controller) 패턴을 활용하여 구현한 도서관 관리 시스템입니다.

사용자는 로그인 후 도서를 검색하고 대출 및 반납할 수 있으며,
관리자는 도서를 등록하고 삭제할 수 있습니다.

## 📌 프로젝트 개요

### 프로젝트 목적

Java의 객체지향 프로그래밍과 MVC 디자인 패턴을 활용하여
도서관의 기본적인 도서 관리 및 대출 업무를 구현하였습니다.

### 주요 기능

- 회원가입
- 로그인
- 도서 검색
- 도서 대출
- 도서 반납
- 대여 중인 도서 확인
- 관리자 도서 등록
- 관리자 도서 삭제
- 관리자 / 일반 사용자 권한 구분

## 🏗️ 프로젝트 구조

```text
src
├── Main.java
│
├── model
│   ├── Book.java
│   ├── User.java
│   ├── BorrowRecord.java
│   └── BookRepository.java
│
├── controller
│   ├── LoginController.java
│   ├── LibraryController.java
│   └── AdminController.java
│
└── view
    ├── LoginView.java
    ├── BookSearchView.java
    ├── MyLibView.java
    └── AdminView.java
