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
        // 창 기본 세팅
        setTitle("도서관 관리 시스템 - 마이페이지");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
        
        // 셀 더블클릭 수정 방지
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

    // Controller가 로그인한 유저 세션 정보를 상단 라벨에 업데이트하기 위한 메소드
    public void updateUserInfo(String userName, int currentRentCount, int maxRentCount) {
        userInfoLabel.setText("회원 정보: " + userName + " 님 환영합니다.");
        rentCountLabel.setText("대여 현황: " + currentRentCount + "권 대여 중 / (추가 대여 가능: " + (maxRentCount - currentRentCount) + "권)");
    }

    // 표에서 사용자가 선택한 행의 도서 ID를 반환하는 메소드 (반납 처리용)
    public String getSelectedBookId() {
        int selectedRow = rentTable.getSelectedRow();
        if (selectedRow == -1) return null;
        return (String) rentTable.getValueAt(selectedRow, 0);
    }

    // Controller가 표에 대여 중인 목록 데이터를 채우거나 원본 모델을 제어하기 위한 메소드
    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    // Controller가 반납 버튼 이벤트를 제어하기 위한 리스너 통로
    public void addReturnListener(ActionListener listener) {
        returnButton.addActionListener(e -> {
            String selectedId = getSelectedBookId();
            
            // 아무것도 선택하지 않고 반납 버튼을 눌렀을 때의 1차 유효성 검증
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

    // 화면 독립 테스트를 위한 임시 메인 메소드
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MyLibView view = new MyLibView();
            
            // 검증 통과 시 작동할 테스트용 임시 리스너 연결
            view.addReturnListener(e -> {
                JOptionPane.showMessageDialog(view, "반납 검증 통과! (컨트롤러로 데이터 이동)");
            });
            
            view.setVisible(true);
        });
    }
}
