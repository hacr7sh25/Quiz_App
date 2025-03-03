import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;
import java.io.File;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class QuizApp extends JFrame implements ActionListener {
    private JLabel questionLabel, timerLabel;
    private JRadioButton[] options;
    private ButtonGroup optionsGroup;
    private JButton nextButton, restartButton;
    private Timer timer;
    private int timeLeft = 10; // Timer in seconds

    private String[][] questions = {
        {"What is the capital of France?", "Berlin", "Paris", "Madrid", "Rome", "1"},
        {"Which is the largest planet?", "Earth", "Mars", "Jupiter", "Venus", "2"},
        {"What is 2 + 2?", "3", "4", "5", "6", "1"}
    };

    private int currentQuestion = 0;
    private int score = 0;

    public QuizApp() {
        // Frame properties
        setTitle("Quiz Application");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Header panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(44, 62, 80)); // Dark blue-gray color
        headerPanel.setLayout(new FlowLayout());
        timerLabel = new JLabel("Time left: " + timeLeft + "s");
        timerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        timerLabel.setForeground(Color.WHITE);
        headerPanel.add(timerLabel);
        add(headerPanel, BorderLayout.NORTH);

        // Question panel
        JPanel questionPanel = new JPanel();
        questionPanel.setLayout(new GridLayout(5, 1));
        questionLabel = new JLabel();
        questionLabel.setFont(new Font("Arial", Font.BOLD, 16));
        questionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        questionPanel.add(questionLabel);

        // Options
        options = new JRadioButton[4];
        optionsGroup = new ButtonGroup();
        for (int i = 0; i < 4; i++) {
            options[i] = new JRadioButton();
            options[i].setFont(new Font("Arial", Font.PLAIN, 14));
            options[i].setBackground(new Color(236, 240, 241)); // Light gray background
            optionsGroup.add(options[i]);
            questionPanel.add(options[i]);
        }

        add(questionPanel, BorderLayout.CENTER);

        // Buttons panel
        JPanel buttonPanel = new JPanel();
        nextButton = new JButton("Next");
        nextButton.setFont(new Font("Arial", Font.BOLD, 14));
        nextButton.setBackground(new Color(39, 174, 96)); // Green button
        nextButton.setForeground(Color.WHITE);
        nextButton.addActionListener(this);
        buttonPanel.add(nextButton);

        restartButton = new JButton("Restart");
        restartButton.setFont(new Font("Arial", Font.BOLD, 14));
        restartButton.setBackground(new Color(192, 57, 43)); // Red button
        restartButton.setForeground(Color.WHITE);
        restartButton.addActionListener(e -> restartQuiz());
        restartButton.setVisible(false);
        buttonPanel.add(restartButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Load first question
        loadQuestion();
        setVisible(true);
    }

    private void loadQuestion() {
        if (currentQuestion < questions.length) {
            timeLeft = 10;
            updateTimerLabel();
            startTimer();

            questionLabel.setText(questions[currentQuestion][0]);
            for (int i = 0; i < 4; i++) {
                options[i].setText(questions[currentQuestion][i + 1]);
                options[i].setSelected(false);
            }
        } else {
            showResult();
        }
    }

    private void startTimer() {
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                if (timeLeft > 0) {
                    timeLeft--;
                    updateTimerLabel();
                } else {
                    timer.cancel();
                    playSound("buzzer.wav"); // Play wrong answer sound on timeout
                    nextQuestion();
                }
            }
        }, 1000, 1000);
    }

    private void updateTimerLabel() {
        timerLabel.setText("Time left: " + timeLeft + "s");
    }

    private void nextQuestion() {
        int correctAnswerIndex = Integer.parseInt(questions[currentQuestion][5]);
        if (options[correctAnswerIndex].isSelected()) {
            playSound("success.wav"); // Play correct answer sound
            score++;
        } else {
            playSound("buzzer.wav"); // Play wrong answer sound
        }
        currentQuestion++;
        loadQuestion();
    }

    private void showResult() {
        timer.cancel();
        playSound("end.wav"); // Play quiz end sound
        JOptionPane.showMessageDialog(this, "Quiz Over! Your score: " + score + "/" + questions.length);
        nextButton.setEnabled(false);
        restartButton.setVisible(true);
    }

    private void restartQuiz() {
        currentQuestion = 0;
        score = 0;
        nextButton.setEnabled(true);
        restartButton.setVisible(false);
        loadQuestion();
    }

    private void playSound(String soundFile) {
        try {
            File file = new File(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(file));
            clip.start();
        } catch (Exception e) {
            System.out.println("Error playing sound: " + e.getMessage());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        timer.cancel();
        nextQuestion();
    }

    public static void main(String[] args) {
        new QuizApp();
    }
}
