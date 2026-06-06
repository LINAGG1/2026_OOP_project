package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/*
사용자 로그인 및 회원가입 화면을 담당하는 View
GridBagLayout 기반의 컴포넌트 배치, 입력값 1차 유효성 검증 및 컨트롤러 데이터 전송 인터페이스 구현
*/
public class LoginView extends JFrame {
    private JLabel titleLabel;
    private JLabel idLabel;
    private JLabel pwLabel;
    private JTextField idField;
    private JPasswordField pwField;
    private JButton loginButton;
    private JButton registerButton;

    // 회원가입용 팝업 다이얼로그 객체 참조 변수
    private JDialog registerDialog;
    private JTextField regIdField;
    private JPasswordField regPwField;
    private JTextField regNameField;

    public LoginView() {
        // 프레임 기본 초기화 설정
        setTitle("도서관 관리 시스템 - 로그인");
        setSize(380, 260);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 타이틀 영역 배치
        titleLabel = new JLabel("도서관 관리 시스템", SwingConstants.CENTER);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 22));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(25, 0, 10, 0)); 
        add(titleLabel, BorderLayout.NORTH);

        // 입력 필드 영역 설정 (GridBagLayout 사용)
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // 아이디 라벨 및 입력창 배치
        idLabel = new JLabel("아이디 : ", SwingConstants.RIGHT);
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        inputPanel.add(idLabel, gbc);

        idField = new JTextField();
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        inputPanel.add(idField, gbc);

        // 비밀번호 라벨 및 입력창 배치
        pwLabel = new JLabel("비밀번호 : ", SwingConstants.RIGHT);
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        inputPanel.add(pwLabel, gbc);

        pwField = new JPasswordField();
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0;
        inputPanel.add(pwField, gbc);

        add(inputPanel, BorderLayout.CENTER);

        // 버튼 영역 배치
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        loginButton = new JButton("로그인");
        registerButton = new JButton("회원가입");
        
        loginButton.setPreferredSize(new Dimension(90, 30));
        registerButton.setPreferredSize(new Dimension(90, 30));
        
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // 회원가입 버튼 클릭 시 JDialog 팝업 연동
        registerButton.addActionListener(e -> showRegisterDialog());
    }

    // 회원가입을 위한 JDialog 팝업 창 생성 및 1차 데이터 입력 누락 검증
    private void showRegisterDialog() {
        registerDialog = new JDialog(this, "회원가입", true);
        registerDialog.setSize(280, 240);
        registerDialog.setLocationRelativeTo(this);
        registerDialog.setLayout(new BorderLayout());
        
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        GridBagConstraints dGbc = new GridBagConstraints();
        dGbc.fill = GridBagConstraints.HORIZONTAL;
        dGbc.insets = new Insets(6, 4, 6, 4);

        regIdField = new JTextField(12);
        regPwField = new JPasswordField(12);
        regNameField = new JTextField(12);

        dGbc.gridx = 0; dGbc.gridy = 0; dGbc.weightx = 0;
        p.add(new JLabel("아이디 : ", SwingConstants.RIGHT), dGbc);
        dGbc.gridx = 1; dGbc.gridy = 0; dGbc.weightx = 1.0;
        p.add(regIdField, dGbc);

        dGbc.gridx = 0; dGbc.gridy = 1; dGbc.weightx = 0;
        p.add(new JLabel("비밀번호 : ", SwingConstants.RIGHT), dGbc);
        dGbc.gridx = 1; dGbc.gridy = 1; dGbc.weightx = 1.0;
        p.add(regPwField, dGbc);

        dGbc.gridx = 0; dGbc.gridy = 2; dGbc.weightx = 0;
        p.add(new JLabel("이름 : ", SwingConstants.RIGHT), dGbc);
        dGbc.gridx = 1; dGbc.gridy = 2; dGbc.weightx = 1.0;
        p.add(regNameField, dGbc);
        
        JButton submitButton = new JButton("가입 완료");
        submitButton.setPreferredSize(new Dimension(100, 35));

        registerDialog.add(p, BorderLayout.CENTER);
        registerDialog.add(submitButton, BorderLayout.SOUTH);

        // 회원가입 데이터 입력값 1차 형식 누락 여부 유효성 검사
        submitButton.addActionListener(ev -> {
            String id = regIdField.getText().trim();
            String pw = new String(regPwField.getPassword()).trim();
            String name = regNameField.getText().trim();

            if (id.isEmpty() || pw.isEmpty() || name.isEmpty()) {
                JOptionPane.showMessageDialog(registerDialog, 
                        "모든 항목을 빠짐없이 입력해 주세요.", 
                        "입력 오류", 
                        JOptionPane.WARNING_MESSAGE);
            } else {
                // 비즈니스 로직 및 알림 처리를 임의로 수행하지 않고 Controller 계층 리스너 호출 위임
                if (registerListener != null) {
                    registerListener.onRegister(id, pw, name);
                }
            }
        });

        registerDialog.setVisible(true);
    }

    // 회원가입 처리가 완료되어 JDialog 창을 동적으로 닫기 위한 제어 메소드
    public void disposeRegisterDialog() {
        if (registerDialog != null && registerDialog.isShowing()) {
            registerDialog.dispose();
        }
    }

    // 회원가입 완료 데이터를 Controller 계층으로 라우팅하기 위한 인터페이스 정의
    public interface RegisterListener {
        void onRegister(String id, String pw, String name);
    }
    private RegisterListener registerListener;

    // Controller 계층과 회원가입 서브밋 이벤트를 바인딩하기 위한 리스너 등록 메소드
    public void addRegisterListener(RegisterListener listener) {
        this.registerListener = listener;
    }

    // Controller 계층에서 사용자가 입력한 아이디와 비밀번호 데이터를 인출하기 위한 Getter 메소드
    public String getIdInput() { return idField.getText().trim(); }
    public String getPwInput() { return new String(pwField.getPassword()).trim(); }
    
    // 로그인 버튼 클릭 시 입력 컴포넌트 포커싱 유효성 검사 및 Controller 리스너 연동
    public void addLoginListener(ActionListener listener) {
        loginButton.addActionListener(e -> {
            String id = getIdInput();
            String pw = getPwInput();

            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                        "아이디를 입력해 주세요.", 
                        "로그인 오류", 
                        JOptionPane.WARNING_MESSAGE);
                idField.requestFocus();
            } else if (pw.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                        "비밀번호를 입력해 주세요.", 
                        "로그인 오류", 
                        JOptionPane.WARNING_MESSAGE);
                pwField.requestFocus();
            } else {
                listener.actionPerformed(e);
            }
        });
    }

    // 독립 화면 단위 테스트 및 기능 검증을 위한 임시 엔트리포인트 메소드
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginView view = new LoginView();
            
            // 로그인 유효성 검증 통과 여부 식별용 임시 테스트 리스너 연동
            view.addLoginListener(e -> {
                JOptionPane.showMessageDialog(view, "로그인 입력 검증 통과 확인 (컨트롤러 바인딩 준비 완료)");
            });
            
            view.setVisible(true);
        });
    }
}