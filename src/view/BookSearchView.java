package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;

/*
메인 도서 검색 및 대여 신청 화면을 담당하는 View
JComboBox를 통한 조건별 검색, JTable 기반의 도서 목록 시각화 및 로그아웃 기능 구현
*/
public class BookSearchView extends JFrame {
    private JComboBox<String> searchCategoryBox;
    private JTextField searchField;
    private JButton searchButton;
    private JTable bookTable;
    private DefaultTableModel tableModel;
    private JButton rentButton;
    private JButton myPageButton;

    // 세션 종료 처리를 위한 로그아웃 버튼 컴포넌트 선언
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

    // Controller 계층에서 선택된 검색 카테고리 항목을 인출하기 위한 메소드
    public String getSearchCategory() {
        return (String) searchCategoryBox.getSelectedItem();
    }

    // Controller 계층에서 사용자가 입력한 검색어를 인출하기 위한 메소드
    public String getSearchKeyword() {
        return searchField.getText().trim();
    }

    // 테이블에서 마우스로 선택된 행의 도서 ID를 추출하여 반환 (대여 대상 식별용)
    public String getSelectedBookId() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow == -1) return null;
        return (String) bookTable.getValueAt(selectedRow, 0);
    }

    // Controller 계층에서 UI 테이블 데이터를 동적 제어하기 위한 모델 반환 메소드
    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    // Controller 계층과 도서 검색 이벤트를 바인딩하기 위한 리스너 등록 메소드
    public void addSearchListener(ActionListener listener) {
        searchButton.addActionListener(listener);
    }

    // Main 컴포넌트와 마이페이지 전환 이벤트를 바인딩하기 위한 리스너 등록 메소드
    public void addMyPageListener(ActionListener listener) {
        myPageButton.addActionListener(listener);
    }

    // Controller 계층과 도서 대여 이벤트를 바인딩하기 위한 리스너 등록 메소드
    public void addRentListener(ActionListener listener) {
        rentButton.addActionListener(e -> {
            String selectedId = getSelectedBookId();
            
            // 대여 대상 도서 선택 여부 유효성 검증
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

    // Main 계층에서 로그아웃 기능을 제어하기 위한 리스너 등록 통로 메소드
    public void addLogoutListener(ActionListener listener) {
        logoutButton.addActionListener(listener);
    }
}