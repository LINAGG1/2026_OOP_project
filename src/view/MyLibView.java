package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;

/*
회원 마이페이지 및 도서 반납 화면을 담당하는 View
대여 현황 목록 시각화(JTable) 및 선택 도서 반납 요청 기능 구현
*/
public class MyLibView extends JFrame {
    private JLabel userInfoLabel;
    private JLabel rentCountLabel;
    private JTable rentTable;
    private DefaultTableModel tableModel;
    private JButton returnButton;

    public MyLibView() {
        // 프레임 기본 초기화 설정
        setTitle("도서관 관리 시스템 - 마이페이지");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // 상단 유저 정보 표시 영역 패널 설정
        JPanel topPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));
        
        userInfoLabel = new JLabel("회원 정보: 불러오는 중...", SwingConstants.LEFT);
        userInfoLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        
        rentCountLabel = new JLabel("대여 가능 권수: 계산 중...", SwingConstants.LEFT);
        rentCountLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        
        topPanel.add(userInfoLabel);
        topPanel.add(rentCountLabel);
        add(topPanel, BorderLayout.NORTH);

        // 중앙 대여 현황 표 영역 설정 (JTable 활용)
        String[] columnNames = {"도서 ID", "도서명", "대여일", "반납 기한"};
        
        // 셀 임의 수정 방지를 위한 익명 클래스 기반 테이블 모델 정의
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        
        rentTable = new JTable(tableModel);
        rentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        rentTable.setRowHeight(25);
        
        JScrollPane scrollPane = new JScrollPane(rentTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("나의 대여 현황 목록"));
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // 하단 반납 기능 버튼 영역 설정
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 15));
        returnButton = new JButton("반납 요청");
        returnButton.setPreferredSize(new Dimension(100, 35));
        bottomPanel.add(returnButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    // Controller 계층에서 인증된 사용자 세션 정보를 상단 라벨에 업데이트하기 위한 메소드
    public void updateUserInfo(String userName, int currentRentCount, int maxRentCount) {
        userInfoLabel.setText("회원 정보: " + userName + " 님 환영합니다.");
        rentCountLabel.setText("대여 현황: " + currentRentCount + "권 대여 중 / (추가 대여 가능: " + (maxRentCount - currentRentCount) + "권)");
    }

    // 테이블에서 마우스로 선택된 행의 도서 ID를 추출하여 반환 (반납 대상 식별용)
    public String getSelectedBookId() {
        int selectedRow = rentTable.getSelectedRow();
        if (selectedRow == -1) return null;
        return (String) rentTable.getValueAt(selectedRow, 0);
    }

    // Controller 계층에서 UI 테이블 데이터를 동적 제어하기 위한 모델 반환 메소드
    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    // Controller 계층과 도서 반납 이벤트를 바인딩하기 위한 리스너 등록 메소드
    public void addReturnListener(ActionListener listener) {
        returnButton.addActionListener(e -> {
            String selectedId = getSelectedBookId();
            
            // 반납 대상 도서 선택 여부 검증
            if (selectedId == null) {
                JOptionPane.showMessageDialog(this, 
                        "반납할 도서를 목록에서 선택해 주세요.", 
                        "반납 오류", 
                        JOptionPane.WARNING_MESSAGE);
            } else {
                listener.actionPerformed(e);
            }
        });
    }

    // 독립 화면 기능 검증을 위한 임시 테스트 메인 메소드
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MyLibView view = new MyLibView();
            
            // 도서 반납 검증 성공 시 작동할 테스트용 임시 리스너 연결
            view.addReturnListener(e -> {
                JOptionPane.showMessageDialog(view, "반납 검증 통과! (컨트롤러로 데이터 이동)");
            });
            
            view.setVisible(true);
        });
    }
}
