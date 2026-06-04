package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;

/*
관리자 전용 도서 관리 화면을 담당하는 View
신규 도서 등록 폼 구성 및 JTable을 통한 도서 삭제 기능 구현
*/
public class AdminView extends JFrame {
    private JTextField idField;
    private JTextField titleField;
    private JTextField authorField;
    private JButton addButton;
    private JTable bookTable;
    private DefaultTableModel tableModel;
    private JButton deleteButton;

    public AdminView() {
        // 창 기본 세팅
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

        // 등록 버튼 배치
        addButton = new JButton("도서 등록");
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(addButton, gbc);

        add(formPanel, BorderLayout.NORTH);

        // 중앙 전체 도서 목록 표 영역 설정 (JTable 활용)
        String[] columnNames = {"도서 ID", "도서명", "저자", "대여 상태"};
        
        // 셀 더블클릭 수정 방지
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

    // Controller가 입력된 신규 도서 정보를 읽어가기 위한 Getter 메소드들
    public String getBookIdInput() { return idField.getText().trim(); }
    public String getBookTitleInput() { return titleField.getText().trim(); }
    public String getBookAuthorInput() { return authorField.getText().trim(); }

    // 도서 추가 성공 후 입력창을 깨끗하게 비워주기 위한 텍스트 초기화 메소드
    public void clearInputFields() {
        idField.setText("");
        titleField.setText("");
        authorField.setText("");
    }

    // 표에서 사용자가 선택한 행의 도서 ID를 반환하는 메소드 (삭제 처리용)
    public String getSelectedBookId() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow == -1) return null;
        return (String) bookTable.getValueAt(selectedRow, 0);
    }

    // Controller가 표 데이터를 직접 갱신(행 추가/삭제)하기 위한 테이블 모델 획득 메소드
    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    // Controller가 도서 등록 버튼 이벤트를 제어하기 위한 리스너 통로
    public void addInsertListener(ActionListener listener) {
        addButton.addActionListener(e -> {
            String id = getBookIdInput();
            String title = getBookTitleInput();
            String author = getBookAuthorInput();

            // 신규 도서 등록 시 빈 값이 있는지 1차 유효성 검증
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

    // Controller가 도서 삭제 버튼 이벤트를 제어하기 위한 리스너 통로
    public void addDeleteListener(ActionListener listener) {
        deleteButton.addActionListener(e -> {
            String selectedId = getSelectedBookId();
            
            // 삭제할 도서를 선택하지 않았을 때의 1차 유효성 검증
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

    // 화면 독립 테스트를 위한 임시 메인 메소드
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AdminView view = new AdminView();
            
            // 등록 버튼 검증 통과 시 테스트용 임시 리스너 연결
            view.addInsertListener(e -> {
                JOptionPane.showMessageDialog(view, "등록 검증 통과! (컨트롤러로 데이터 이동)");
                view.clearInputFields();
            });
            
            // 삭제 버튼 검증 통과 시 테스트용 임시 리스너 연결
            view.addDeleteListener(e -> {
                JOptionPane.showMessageDialog(view, "삭제 검증 통과! (컨트롤러로 데이터 이동)");
            });
            
            view.setVisible(true);
        });
    }
}