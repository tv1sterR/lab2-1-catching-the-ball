package controller;

import javafx.animation.AnimationTimer;
import javafx.animation.FillTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import viewmodel.GameViewModel;

import java.net.URL;
import java.util.ResourceBundle;

public class GameController implements Initializable {

    @FXML private Pane gamePane;
    @FXML private Circle ball;
    @FXML private Label scoreLabel;
    @FXML private Label statusLabel;
    @FXML private Label timerLabel;
    @FXML private Label pauseLabel;
    @FXML private Label gameOverLabel;
    @FXML private Label finalScoreLabel;



    private final GameViewModel viewModel = new GameViewModel();
    private AnimationTimer movementTimer;
    private boolean paused = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Привязки
        ball.centerXProperty().bind(viewModel.ballXProperty());
        ball.centerYProperty().bind(viewModel.ballYProperty());

        scoreLabel.textProperty().bind(viewModel.scoreProperty().asString("Счёт: %d"));

        statusLabel.textProperty().bind(
                javafx.beans.binding.Bindings.when(viewModel.bonusActiveProperty())
                        .then("Статус: бонус x2")
                        .otherwise("Статус: обычный режим")
        );

        // Реакция на бонус — прозрачность + цвет
        viewModel.bonusActiveProperty().addListener((obs, oldV, newV) -> {
            if (newV) {
                ball.setOpacity(0.3);

                FillTransition ft = new FillTransition(Duration.seconds(0.5), ball);
                ft.setToValue(Color.ORANGE);
                ft.play();

            } else {
                ball.setOpacity(1.0);

                FillTransition ft = new FillTransition(Duration.seconds(0.5), ball);
                ft.setToValue(Color.RED);
                ft.play();
            }
        });

        viewModel.gameActiveProperty().addListener((obs, oldV, newV) -> {
            if (!newV) {
                movementTimer.stop();
                gameOverLabel.setVisible(true);
                ball.setOpacity(0.2);
                pauseLabel.setText("");

                // Блокируем клики
                gamePane.setDisable(true);
            }
        });

        viewModel.gameActiveProperty().addListener((obs, oldV, newV) -> {
            if (!newV) {
                movementTimer.stop();
                gameOverLabel.setVisible(true);
                finalScoreLabel.setText("Ваш счёт: " + viewModel.scoreProperty().get());
                finalScoreLabel.setVisible(true);

                ball.setOpacity(0.2);
                pauseLabel.setText("");

                gamePane.setDisable(true);
            }
        });



        // Движение шарика
        movementTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (viewModel.gameActiveProperty().get()) {
                    viewModel.updateBallPosition(
                            gamePane.getWidth(),
                            gamePane.getHeight(),
                            ball.getRadius()
                    );
                    timerLabel.setText(viewModel.getTimeLeft() + " сек");
                }
            }
        };

        movementTimer.start();
    }

    @FXML
    private void handleMouseClick(MouseEvent event) {

        // Двойной клик — пауза
        if (event.getClickCount() == 2) {
            togglePause();
            return;
        }

        double dx = event.getX() - ball.getCenterX();
        double dy = event.getY() - ball.getCenterY();
        double dist = Math.sqrt(dx * dx + dy * dy);

        // Попадание по шарику
        if (dist <= ball.getRadius()) {
            viewModel.addScore();
            viewModel.moveBallRandom(
                    gamePane.getWidth(),
                    gamePane.getHeight(),
                    ball.getRadius()
            );
            return;
        }

        // Мимо — шарик отскакивает от курсора
        viewModel.repelFromCursor(event.getX(), event.getY());
    }

    private void togglePause() {
        paused = !paused;

        if (paused) {
            movementTimer.stop();
            viewModel.pause();
            pauseLabel.setText("Пауза");
        } else {
            movementTimer.start();
            viewModel.resume();
            pauseLabel.setText("");
        }
    }
}
