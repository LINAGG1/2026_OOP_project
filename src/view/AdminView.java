package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;

/*
관리자 전용 도서 관리 및 세션 제어를 담당하는 View
GridBagLayout 기반 등록 폼, JTable 데이터 시각화 갱신 은닉화 및 이벤트 바인딩 인터페이스 구현
*/
public class AdminView extends JFrame {
    private JTextField idField;
    private JTextField titleField;
    private JTextField authorField;
    private JButton addButton;
    private JTable bookTable;
    private DefaultTableModel tableModel;
    private JButton deleteButton;
    private JButton logoutButton;

    public AdminView() {
        // 프레임 기본 초기화 설정
        setTitle("도서관 관리 시스템 - 관리자 모드");
        setSize(650, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // 상단 신규 도서 등록 폼 영역 패널 설정 (GridBagLayout 사용)
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("신규 도서 등록"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // 도서 ID 입력 항목 배치
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        formPanel.add(new JLabel("도서 ID : ", SwingConstants.RIGHT), gbc);
        idField = new JTextField(10);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        formPanel.add(idField, gbc);

        // 도서명 입력 항목 배치
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        formPanel.add(new JLabel("도서명 : ", SwingConstants.RIGHT), gbc);
        titleField = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0;
        formPanel.add(titleField, gbc);

        // 저자 입력 항목 배치
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        formPanel.add(new JLabel("저자 : ", SwingConstants.RIGHT), gbc);
        authorField = new JTextField(15);
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 1.0;
        formPanel.add(authorField, gbc);

        // 하단 버튼 배치 영역 설정 (등록 버튼 및 로그아웃 버튼 배치)
        JPanel buttonContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        addButton = new JButton("도서 등록");
        logoutButton = new JButton("로그아웃");
        buttonContainer.add(addButton);
        buttonContainer.add(logoutButton);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(buttonContainer, gbc);

        add(formPanel, BorderLayout.NORTH);

        // 중앙 전체 도서 목록 표 영역 설정 (JTable 활용)
        String[] columnNames = {"도서 ID", "도서명", "저자", "대여 상태"};
        
        // 셀 더블클릭 수정 방지 익명 클래스 정의
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        
        bookTable = new JTable(tableModel);
        bookTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookTable.setRowHeight(25);
        
        JScrollPane scrollPane = new JScrollPane(bookTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("현재 보유 도서 목록"));
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // 하단 도서 삭제 기능 버튼 영역 설정
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        deleteButton = new JButton("선택 도서 삭제");
        deleteButton.setPreferredSize(new Dimension(130, 35));
        bottomPanel.add(deleteButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    // Controller 계층에서 입력된 신규 도서 정보를 인출하기 위한 Getter 메소드
    public String getBookIdInput() { return idField.getText().trim(); }
    public String getBookTitleInput() { return titleField.getText().trim(); }
    public String getBookAuthorInput() { return authorField.getText().trim(); }

    // 도서 추가 완료 후 입력 필드 초기화 수행 메소드
    public void clearInputFields() {
        idField.setText("");
        titleField.setText("");
        authorField.setText("");
    }

    // 테이블에서 마우스로 선택된 행의 도서 ID를 추출하여 반환 (삭제 대상 식별용)
    public String getSelectedBookId() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow == -1) return null;
        return (String) bookTable.getValueAt(selectedRow, 0);
    }

    // 외부 계층에서 전달받은 도서 단건 데이터를 테이블 행 데이터로 동적 추가하는 메소드
    public void addBookRow(String id, String title, String author, boolean isBorrowed) {
        String status = isBorrowed ? "대여중" : "대여가능";
        tableModel.addRow(new Object[]{id, title, author, status});
    }

    // 현재 선택되어 삭제 승인된 특정 도서 ID 행 데이터를 테이블 상에서 동적 제거하는 메소드
    public void removeSelectedRow() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow != -1) {
            tableModel.removeRow(selectedRow);
        }
    }

    // 테이블 내부의 전체 행 데이터를 일괄 소거하는 데이터 초기화 메소드
    public void clearTable() {
        tableModel.setRowCount(0);
    }

    // 비즈니스 처리 결과에 따른 안내 메시지 팝업 출력 제어 메소드
    public void showMessage(String message, String title, boolean isSuccess) {
        int messageType = isSuccess ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE;
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }

    // Controller 계층과 도서 등록 이벤트를 바인딩하기 위한 리스너 등록 메소드
    public void addInsertListener(ActionListener listener) {
        addButton.addActionListener(e -> {
            String id = getBookIdInput();
            String title = getBookTitleInput();
            String author = getBookAuthorInput();

            // 데이터 필수 입력 누락 여부 1차 유효성 검증
            if (id.isEmpty() || title.isEmpty() || author.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                        "도서 정보(ID, 제목, 저자)를 모두 입력해 주세요.", 
                        "등록 오류", 
                        JOptionPane.WARNING_MESSAGE);
            } else {
                listener.actionPerformed(e);
            }
        });
    }

    // Controller 계층과 도서 삭제 이벤트를 바인딩하기 위한 리스너 등록 메소드
    public void addDeleteListener(ActionListener listener) {
        deleteButton.addActionListener(e -> {
            String selectedId = getSelectedBookId();
            
            // 삭제 대상 도서의 목록 선택 여부 1차 유효성 검증
            if (selectedId == null) {
                JOptionPane.showMessageDialog(this, 
                        "삭제할 도서를 목록에서 선택해 주세요.", 
                        "삭제 오류", 
                        JOptionPane.WARNING_MESSAGE);
            } else {
                listener.actionPerformed(e);
            }
        });
    }

    // Controller 계층에서 세션 로그아웃 기능을 처리하기 위한 리스너 등록 통로 메소드
    public void addLogoutListener(ActionListener listener) {
        logoutButton.addActionListener(listener);
    }

    // 독립 화면 기능 검증 및 레이아웃 갱신 테스트를 위한 임시 메인 메소드
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AdminView view = new AdminView();
            
            // 도서 행 추가 기능 자체 테스트 검증
            view.addBookRow("B01", "객체지향 설계 원칙", "신승준", false);
            view.addBookRow("B02", "자바 Swing 가이드", "정수인", true);
            
            // 도서 등록 검증 성공 시 작동할 테스트용 임시 리스너 연결
            view.addInsertListener(e -> {
                view.showMessage("도서 등록 요청이 컨트롤러로 정상 송신되었습니다.", "등록 성공", true);
                view.clearInputFields();
            });
            
            // 도서 삭제 검증 성공 시 작동할 테스트용 임시 리스너 연결
            view.addDeleteListener(e -> {
                view.removeSelectedRow();
                view.showMessage("선택된 도서 행이 삭제되었습니다.", "삭제 성공", true);
            });

            // 로그아웃 작동 테스트용 임시 리스너 연결
            view.addLogoutListener(e -> {
                view.showMessage("로그아웃 세션 종료 요청 감지", "알림", true);
            });
            
            view.setVisible(true);
        });
    }
}