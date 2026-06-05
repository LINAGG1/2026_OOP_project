package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;

/*
메인 도서 검색 및 대여 신청 화면을 담당하는 View
JComboBox를 통한 조건별 검색 및 JTable 기반의 도서 목록 시각화 구현
*/
public class BookSearchView extends JFrame {
    private JComboBox<String> searchCategoryBox;
    private JTextField searchField;
    private JButton searchButton;
    private JTable bookTable;
    private DefaultTableModel tableModel;
    private JButton rentButton;
    private JButton myPageButton;

    public BookSearchView() {
        // 창 기본 세팅
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
        
        // 셀 더블클릭 시 텍스트 임의 수정을 막기 위한 테이블 모델 선언
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        
        bookTable = new JTable(tableModel);
        bookTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookTable.setRowHeight(25);
        
        // 표에 스크롤 기능 추가 및 테두리 타이틀 설정
        JScrollPane scrollPane = new JScrollPane(bookTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("도서 검색 결과 목록"));
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // 하단 기능 버튼 영역 설정
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        
        // 마이페이지 버튼 생성 및 세팅 
        myPageButton = new JButton("마이페이지");
        myPageButton.setPreferredSize(new Dimension(100, 35));
        
        rentButton = new JButton("대여 신청");
        rentButton.setPreferredSize(new Dimension(100, 35));
        
        // 마이페이지 버튼이 왼쪽, 대여 신청이 오른쪽에 오도록 순서대로 패널에 등록
        bottomPanel.add(myPageButton); 
        bottomPanel.add(rentButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    // Controller가 검색 조건을 획득하기 위한 콤보박스 Getter
    public String getSearchCategory() {
        return (String) searchCategoryBox.getSelectedItem();
    }

    // Controller가 사용자의 입력 검색어를 획득하기 위한 Getter
    public String getSearchKeyword() {
        return searchField.getText().trim();
    }

    // 표에서 사용자가 선택한 행의 도서 ID를 반환하는 메소드 (대여 처리용)
    public String getSelectedBookId() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow == -1) return null;
        return (String) bookTable.getValueAt(selectedRow, 0);
    }

    // Controller가 표에 실제 데이터를 동적으로 채우거나 갱신하기 위한 테이블 모델 획득 메소드
    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    // Controller가 검색 버튼 이벤트를 제어하기 위한 리스너 통로
    public void addSearchListener(ActionListener listener) {
        searchButton.addActionListener(listener);
    }

    // 🛠 [추가 완료] Main.java의 빨간 줄을 없애줄 마이페이지 리스너 통로 메소드
    public void addMyPageListener(ActionListener listener) {
        myPageButton.addActionListener(listener);
    }

    // Controller가 대여 버튼 이벤트를 제어하기 위한 리스너 통로
    public void addRentListener(ActionListener listener) {
        rentButton.addActionListener(e -> {
            String selectedId = getSelectedBookId();
            
            // 아무것도 선택하지 않고 대여 버튼을 눌렀을 때의 1차 유효성 검증
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
}