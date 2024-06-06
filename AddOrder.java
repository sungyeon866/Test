/*
컴퓨터 전원 끄기
window

public class Shutdown {
    public static void main(String[] args) {
        try {
            Runtime.getRuntime().exec("shutdown -s -t 0");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
linux/mac
import java.io.IOException;

public class Shutdown {
    public static void main(String[] args) {
        try {
            Runtime.getRuntime().exec("shutdown -h now");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
* */
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Timer;
import java.util.TimerTask;
import java.util.LinkedHashMap;
import java.util.Map;
class TextFieldPanel extends Panel {
    private TextField idField;
    private TextField passwordField;

    public TextFieldPanel() {
        setLayout(new GridLayout(3, 2, 10, 10));
        add(new Label("ID:"));
        idField = new TextField();
        add(idField);

        add(new Label("Password:"));
        passwordField = new TextField();
        passwordField.setEchoChar('*');
        add(passwordField);
    }

    public String getIdFieldText() {
        return idField.getText();
    }

    public String getPasswordFieldText() {
        return passwordField.getText();
    }

    public void clearFields() {
        idField.setText("");
        passwordField.setText("");
    }

    public TextField getIdField() {
        return idField;
    }

    public TextField getPasswordField() {
        return passwordField;
    }
}

class Login {
    private TextFieldPanel textFieldPanel;

    public Login(TextFieldPanel textFieldPanel) {
        this.textFieldPanel = textFieldPanel;
    }

    public boolean login() {
        String id = textFieldPanel.getIdFieldText();
        String password = textFieldPanel.getPasswordFieldText();
        String line;
        try (BufferedReader reader = new BufferedReader(new FileReader("users.txt"))) {
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts[0].equals(id) && parts[1].equals(password)) {
                    System.out.println("Login successful");
                    int remainingTime = parts.length > 2 ? Integer.parseInt(parts[2]) : 0;
                    new MainFrame();
                    new TimerApp(id, remainingTime);
                    return true;
                }
            }
            System.out.println("Login failed");
        } catch (IOException ex) {
            System.out.println("Error reading user data");
        }
    return false;
    }
}

class LoginButton extends Button {
    public LoginButton(Login login, Frame frame) {
        super("Login");
        setPreferredSize(new Dimension(100, 30));
        addActionListener(e -> {
            boolean check = login.login();
            if(check){
                frame.dispose();
            }
            else{
                JOptionPane.showMessageDialog(null, "Login failed");
            }
        });
    }
}

class SignUpButton extends Button {
    public SignUpButton(Frame frame) {
        super("Sign up");
        setPreferredSize(new Dimension(100, 30));
        addActionListener(e -> {
            frame.dispose();
            new SignupAppAWT();
        });
    }
}

class StartingFrame extends Frame {
    public StartingFrame() {
        Frame frame = new Frame();
        frame.setSize(300, 200);
        frame.setTitle("Login");
        setLayout(new BorderLayout());

        TextFieldPanel textFieldPanel = new TextFieldPanel();
        frame.add(textFieldPanel, BorderLayout.NORTH);

        Login login = new Login(textFieldPanel);

        Panel buttonPanel = new Panel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        Button loginButton = new LoginButton(login, frame);
        buttonPanel.add(loginButton);

        Button signUpButton = new SignUpButton(frame);
        buttonPanel.add(signUpButton);

        frame.add(buttonPanel, BorderLayout.EAST);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                frame.dispose();
            }
        });
        frame.setVisible(true);
    }
}

class SignupAppAWT extends Frame {
    private TextFieldPanel textFieldPanel;
    private Label messageLabel;
    private Button signupButton;

    public SignupAppAWT() {
        setTitle("회원가입");
        setSize(300, 200);
        setLayout(new BorderLayout());

        textFieldPanel = new TextFieldPanel();
        signupButton = new Button("Sign up");
        signupButton.setPreferredSize(new Dimension(100, 30));
        signupButton.addActionListener(this::actionPerformed);

        add(textFieldPanel.getIdField().getParent(), BorderLayout.NORTH);

        Panel panel = new Panel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10)); // Changed to GridLayout
        panel.add(signupButton);
        messageLabel = new Label("");
        messageLabel.setPreferredSize(new Dimension(200, 30));
        panel.add(messageLabel, BorderLayout.SOUTH);
        add(panel, BorderLayout.CENTER);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                dispose();
            }
        });
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        String id = textFieldPanel.getIdFieldText();
        String password = textFieldPanel.getPasswordFieldText();

        if (id.isEmpty() || password.isEmpty()) {
            messageLabel.setText("ID or Password cannot be empty");
        } else {
            if (isIdTaken(id)) {
                messageLabel.setText("ID already taken");
            } else {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter("users.txt", true))) {
                    writer.write(id + ":" + password);
                    writer.newLine();
                    messageLabel.setText("Signup successful");
                    textFieldPanel.clearFields();
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            dispose();
                            new StartingFrame();
                        }
                    }, 2000);
                } catch (IOException ex) {
                    messageLabel.setText("Error saving user data");
                }
            }
        }
    }

    private boolean isIdTaken(String id) {
        try (BufferedReader reader = new BufferedReader(new FileReader("users.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts[0].equals(id)) {
                    return true;
                }
            }
        } catch (IOException ex) {
            messageLabel.setText("Error reading user data");
        }
        return false;
    }
}

class TimerApp extends Frame {
    private Label timeLabel;
    private TextField inputField;
    private Button addButton;
    private Timer timer;
    private int remainingTime; // Remaining time in seconds
    private String userId;

    public TimerApp(String userId, int remainingTime) {
        this.userId = userId;
        this.remainingTime = remainingTime;

        setTitle("Timer");
        setSize(400, 200);
        setLayout(new GridLayout(3, 2));

        timeLabel = new Label("00:00:00", Label.CENTER);
        timeLabel.setFont(new Font("Arial", Font.BOLD, 36));
        add(timeLabel);

        Label inputLabel = new Label("Add Time (sec):");
        add(inputLabel);

        inputField = new TextField();
        add(inputField);

        addButton = new Button("Add Time");
        addButton.addActionListener(this::addTime);
        add(addButton);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                saveRemainingTime();
                if (timer != null) {
                    timer.cancel();
                }
                dispose();
            }
        });
        if(remainingTime > 0){
            startTimer();
        }
        updateTimeLabel();

        setVisible(true);
    }

    private void addTime(ActionEvent e) {
        try {
            int timeToAdd = Integer.parseInt(inputField.getText());
            remainingTime += timeToAdd;
            updateTimeLabel();
            inputField.setText("");

            if (remainingTime > 0 && (timer == null || timerHasStopped())) {
                startTimer();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number.");
        }
    }

    private boolean timerHasStopped() {
        // Check if the timer has stopped
        return remainingTime > 0 && timer == null;
    }

    private void startTimer() {
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (remainingTime > 0) {
                    remainingTime--;
                    updateTimeLabel();
                } else {
                    timer.cancel();
                    timer = null;
                    JOptionPane.showMessageDialog(null, "Time's up!");
                  Timer timer2 = new Timer();
                    timer2.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            saveRemainingTime();
                            dispose();
                            shutDown();
                        }
                    }, 1000);
                }
            }
        }, 0, 1000);
    }
    public void shutDown() {
        try {
            Runtime.getRuntime().exec("shutdown -s -t 0");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void updateTimeLabel() {
        int hours = remainingTime / 3600;
        int minutes = (remainingTime % 3600) / 60;
        int seconds = remainingTime % 60;
        timeLabel.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
    }

    private void saveRemainingTime() {
        File oldFile = new File("users.txt");
        File newFile = new File("users_tmp.txt");
        try (BufferedReader reader = new BufferedReader(new FileReader(oldFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(newFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts[0].equals(userId)) {
                    writer.write(userId + ":" + parts[1] + ":" + remainingTime);
                } else {
                    writer.write(line);
                }
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
              try {
                  Thread.sleep(500);
              if (!oldFile.delete()) {
                System.err.println("Failed to delete the original file");
                return;
            }
            Files.move(newFile.toPath(), oldFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("File updated successfully.");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            System.err.println("Failed to replace the original file with the updated file.");
        }
    }
}


class MenuOrderSystem3 extends JFrame {
    private Map<String, Integer> menuPrices = new LinkedHashMap<>();
    private Map<String, Integer> cart = new LinkedHashMap<>();
    private JPanel cartPanel = new JPanel();
    private JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    private JScrollPane scrollPane = new JScrollPane(cartPanel,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    private JLabel totalPriceLabel = new JLabel("Total: 0원");
    private JButton payButton = new JButton("결제하기");
    private JButton homeButton = new JButton("초기화면");

    public MenuOrderSystem3() {
        setTitle("Menu Order System");
        setSize(700, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        initializeMenuPrices();
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setPreferredSize(new Dimension(700, 200));

        // 카테고리별 메뉴
        JPanel drinksPanel = createMenuPanel(new String[]{"콜라", "사이다", "환타", "밀키스", "웰치스"});
        JPanel noodlesPanel = createMenuPanel(new String[]{"신라면", "진라면", "삼양라면", "불닭볶음면", "짜파게티", "열라면"});
        JPanel snacksPanel = createMenuPanel(new String[]{"포카칩", "바나나킥", "새우깡", "감자깡", "죠리퐁", "맛동산"});

        // 카테고리 설정
        tabbedPane.addTab("음료", drinksPanel);
        tabbedPane.addTab("라면", noodlesPanel);
        tabbedPane.addTab("과자", snacksPanel);

        setFontRecursively(tabbedPane, new Font("Malgun Gothic", Font.PLAIN, 14));

        add(tabbedPane, BorderLayout.NORTH);

        cartPanel.setLayout(new GridBagLayout());
        scrollPane.setPreferredSize(new Dimension(700, 500));
        add(scrollPane, BorderLayout.CENTER);

        totalPanel.add(totalPriceLabel);
        totalPanel.add(payButton);
        totalPanel.add(homeButton);
        add(totalPanel, BorderLayout.SOUTH);

        setFontRecursively(totalPanel, new Font("Malgun Gothic", Font.PLAIN, 14));

        payButton.addActionListener(this::performPayment);
        homeButton.addActionListener(e -> {
            new MainFrame();
            dispose();
        });

        setVisible(true);
    }

    private void setFontRecursively(Component component, Font font) {
        component.setFont(font);
        if (component instanceof Container) {
            for (Component child : ((Container) component).getComponents()) {
                setFontRecursively(child, font);
            }
        }
    }

    // 카테고리별 메뉴 생성 버튼 만들기
    private JPanel createMenuPanel(String[] items) {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        for (String item : items) {
            JButton button = new JButton(item);
            button.addActionListener(e -> addItemToCart(item));
            button.setFont(new Font("Malgun Gothic", Font.PLAIN, 14));
            panel.add(button);
        }
        return panel;
    }

    // 장바구니에 항목 추가
    private void addItemToCart(String item) {
        cart.put(item, cart.getOrDefault(item, 0) + 1);
        updateCart();
    }

    // 결제하기 버튼 누르면 다이얼로그 창
    private void performPayment(ActionEvent e) {
        showPaymentDialog();
    }

    // 결제 다이얼로그
    private void showPaymentDialog() {
        JDialog dialog = new JDialog(this, "결제 확인", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(700, 500);

        JTextArea detailsArea = new JTextArea("주문목록\n" + getPaymentDetails() + "\n결제 금액: " + getTotal() + "원");
        detailsArea.setEditable(false);
        JScrollPane detailsScrollPane = new JScrollPane(detailsArea,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        dialog.add(detailsScrollPane, BorderLayout.CENTER);

        // 결제 확인 (결제, 취소 버튼)
        JPanel buttonPanel = new JPanel();
        JButton confirmButton = new JButton("결제");
        JButton cancelButton = new JButton("취소");

        confirmButton.setFont(new Font("Malgun Gothic", Font.PLAIN, 14));
        cancelButton.setFont(new Font("Malgun Gothic", Font.PLAIN, 14));

        confirmButton.addActionListener(ev -> {
            resetApplication();
            dialog.dispose();
        });

        cancelButton.addActionListener(ev -> dialog.dispose());

        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        setFontRecursively(buttonPanel, new Font("Malgun Gothic", Font.PLAIN, 14));

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // 주문 목록 리스트
    private String getPaymentDetails() {
        StringBuilder details = new StringBuilder();
        cart.forEach((item, quantity) -> details.append(item).append(" - ").append(quantity)
                .append("개 (가격: ").append(menuPrices.get(item) * quantity).append("원)\n"));
        return details.toString();
    }

    // 주문 목록 결제 금액
    private int getTotal() {
        return cart.entrySet().stream().mapToInt(entry -> entry.getValue() * menuPrices.get(entry.getKey())).sum();
    }

    // 장바구니 초기화
    private void resetApplication() {
        cart.clear();
        updateCart();
    }

    // 장바구니 업데이트. 수량, 가격 표시
    private void updateCart() {
        cartPanel.removeAll();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.NORTHWEST;

        int total = 0;
        int row = 0;
        for (String item : cart.keySet()) {
            int col = 0;
            gbc.weighty = (row == cart.size() - 1) ? 1 : 0;
            addLabel(cartPanel, item, gbc, col++, row);
            addLabel(cartPanel, cart.get(item) + " 개 ", gbc, col++, row);
            addButton(cartPanel, "+", e -> {
                cart.put(item, cart.get(item) + 1);
                updateCart();
            }, gbc, col++, row);
            addButton(cartPanel, "-", e -> {
                if (cart.get(item) > 1) {
                    cart.put(item, cart.get(item) - 1);
                } else {
                    cart.remove(item);
                }
                updateCart();
            }, gbc, col++, row);
            addLabel(cartPanel, "가격: " + (menuPrices.get(item) * cart.get(item)) + "원", gbc, col++, row);
            addButton(cartPanel, "삭제", e -> {
                cart.remove(item);
                updateCart();
            }, gbc, col++, row);

            row++;
            total += menuPrices.get(item) * cart.get(item);
        }

        totalPriceLabel.setText("총 금액: " + total + "원");
        cartPanel.revalidate();
        cartPanel.repaint();
        scrollPane.getVerticalScrollBar().setValue(0);
    }

    // 라벨 생성
    private void addLabel(JPanel panel, String text, GridBagConstraints gbc, int x, int y) {
        gbc.gridx = x;
        gbc.gridy = y;
        JLabel label = new JLabel(text);
        label.setFont(new Font("Malgun Gothic", Font.PLAIN, 14));
        panel.add(label, gbc);
    }

    // 버튼 생성
    private void addButton(JPanel panel, String text, ActionListener action, GridBagConstraints gbc, int x, int y) {
        gbc.gridx = x;
        gbc.gridy = y;
        JButton button = new JButton(text);
        button.setFont(new Font("Malgun Gothic", Font.PLAIN, 14));
        button.addActionListener(action);
        panel.add(button, gbc);
    }

    // 메뉴 가격 설정
    private void initializeMenuPrices() {
        menuPrices.put("콜라", 1000);
        menuPrices.put("사이다", 1000);
        menuPrices.put("환타", 1200);
        menuPrices.put("밀키스", 1500);
        menuPrices.put("웰치스", 1300);
        menuPrices.put("신라면", 2000);
        menuPrices.put("진라면", 2100);
        menuPrices.put("삼양라면", 1900);
        menuPrices.put("불닭볶음면", 2200);
        menuPrices.put("짜파게티", 2000);
        menuPrices.put("열라면", 2100);
        menuPrices.put("포카칩", 1500);
        menuPrices.put("바나나킥", 1100);
        menuPrices.put("새우깡", 1000);
        menuPrices.put("감자깡", 950);
        menuPrices.put("죠리퐁", 900);
        menuPrices.put("맛동산", 800);
    }
}

class MainFrame extends JFrame {

    public MainFrame() {
        // 초기화면
        setTitle("Main Menu");
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());

        // 주문하기, 종료 버튼
        JButton orderButton = new JButton("주문하기");
        JButton exitButton = new JButton("종료");

        // 버튼 사이즈 설정
        Dimension buttonSize = new Dimension(150, 50);
        orderButton.setPreferredSize(buttonSize);
        exitButton.setPreferredSize(buttonSize);

        // 폰트, 글자 크기 설정
        Font buttonFont = new Font("Malgun Gothic", Font.BOLD, 20);
        orderButton.setFont(buttonFont);
        exitButton.setFont(buttonFont);

        // 버튼 클릭 -> 메뉴주문창
        orderButton.addActionListener(e -> {
            new MenuOrderSystem3();
            dispose();
        });

        exitButton.addActionListener(e -> System.exit(0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;

        add(orderButton, gbc);

        gbc.gridx = 1;
        add(exitButton, gbc);

        setVisible(true);
    }
}
public class Main {
    public static void main(String[] args) {
        new StartingFrame();
    }
}
