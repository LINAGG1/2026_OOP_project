package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;

/*
메인 도서 검색 및 대여 신청 화면을 담당하는 View
JComboBox를 통한 조건별 검색, JTable 기반 도서 데이터 시각화 갱신 은닉화 및 이벤트 바인딩 인터페이스 구현
*/
public class BookSearchView extends JFrame {
    private JComboBox<String> searchCategoryBox;
    private JTextField searchField;
    private JButton searchButton;
    private JTable bookTable;
    private DefaultTableModel tableModel;
    private JButton rentButton;
    private JButton myPageButton;
    private JButton logoutButton;

    public BookSearchView() {
        // 프레임 기본 초기화 설정
        setTitle("도서관 관리 시스템 - 메인 검색");
        setSize(650, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // 상단 검색 바 영역 패널 설정
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 15));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        
        String[] categories = {"도서명", "저자", "도서 ID"};
        searchCategoryBox = new JComboBox<>(categories);
        searchField = new JTextField(25);
        searchButton = new JButton("검색");
        
        searchPanel.add(searchCategoryBox);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        add(searchPanel, BorderLayout.NORTH);

        // 중앙 도서 목록 표 영역 설정 (JTable 활용)
        String[] columnNames = {"도서 ID", "도서명", "저자", "대여 가능 여부"};
        
        // 셀 임의 수정 방지를 위한 익명 클래스 기반 테이블 모델 정의
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        
        bookTable = new JTable(tableModel);
        bookTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookTable.setRowHeight(25);
        
        // 표에 스크롤 기능 구성 및 컴포넌트 배치
        JScrollPane scrollPane = new JScrollPane(bookTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("도서 검색 결과 목록"));
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // 하단 기능 버튼 영역 패널 설정 (마이페이지, 대여 신청, 로그아웃 버튼 컴포넌트 배치)
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        
        myPageButton = new JButton("마이페이지");
        myPageButton.setPreferredSize(new Dimension(100, 35));
        
        rentButton = new JButton("대여 신청");
        rentButton.setPreferredSize(new Dimension(100, 35));

        logoutButton = new JButton("로그아웃");
        logoutButton.setPreferredSize(new Dimension(100, 35));
        
        bottomPanel.add(logoutButton);
        bottomPanel.add(myPageButton); 
        bottomPanel.add(rentButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    // Controller 계층에서 선택된 검색 카테고리 항목을 인출하기 위한 Getter 메소드
    public String getSearchCategory() {
        return (String) searchCategoryBox.getSelectedItem();
    }

    // Controller 계층에서 사용자가 입력한 검색어를 인출하기 위한 Getter 메소드
    public String getSearchKeyword() {
        return searchField.getText().trim();
    }

    // 테이블에서 마우스로 선택된 행의 도서 ID를 추출하여 반환 (대여 대상 식별용)
    public String getSelectedBookId() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow == -1) return null;
        return (String) bookTable.getValueAt(selectedRow, 0);
    }

    // 외부 계층에서 전달받은 도서 데이터를 기반으로 검색 결과 행을 추가하는 시각화 메소드
    public void addBookRow(String id, String title, String author, boolean isBorrowed) {
        String status = isBorrowed ? "대여중" : "대여가능";
        tableModel.addRow(new Object[]{id, title, author, status});
    }

    // 테이블 내부의 기존 전체 검색 결과 데이터를 일괄 소거하는 화면 갱신 메소드
    public void clearTable() {
        tableModel.setRowCount(0);
    }

    // 비즈니스 렌탈 처리 결과에 따른 알림 메시지 팝업 출력 제어 메소드
    public void showMessage(String message, String title, boolean isSuccess) {
        int messageType = isSuccess ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE;
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }

    // Controller 계층과 도서 검색 이벤트를 바인딩하기 위한 리스너 등록 메소드
    public void addSearchListener(ActionListener listener) {
        searchButton.addActionListener(listener);
    }

    // Controller 계층과 마이페이지 화면 전환 이벤트를 바인딩하기 위한 리스너 등록 메소드
    public void addMyPageListener(ActionListener listener) {
        myPageButton.addActionListener(listener);
    }

    // Controller 계층과 도서 대여 신청 이벤트를 바인딩하기 위한 리스너 등록 메소드
    public void addRentListener(ActionListener listener) {
        rentButton.addActionListener(e -> {
            String selectedId = getSelectedBookId();
            
            // 대여 대상 도서 선택 여부 유효성 1차 검증
            if (selectedId == null) {
                JOptionPane.showMessageDialog(this, 
                        "대여할 도서를 목록에서 선택해 주세요.", 
                        "대여 오류", 
                        JOptionPane.WARNING_MESSAGE);
            } else {
                listener.actionPerformed(e);
            }
        });
    }

    // Controller 계층에서 로그아웃 세션 처리를 바인딩하기 위한 리스너 등록 통로 메소드
    public void addLogoutListener(ActionListener listener) {
        logoutButton.addActionListener(listener);
    }
}