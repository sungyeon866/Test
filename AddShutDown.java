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
public class Main {
    public static void main(String[] args) {
        new StartingFrame();
    }
}
