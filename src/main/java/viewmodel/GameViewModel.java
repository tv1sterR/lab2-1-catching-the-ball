package viewmodel;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.*;
import javafx.util.Duration;
import model.GameModel;

import java.util.Random;

public class GameViewModel {

    private final GameModel model = new GameModel();
    private final Random random = new Random();

    public double dx = 3;
    public double dy = 3;

    private final Timeline bonusTimer;
    private final Timeline bonusDuration;
    private final Timeline gameCountdown;

    private int timeLeft = 60;

    public GameViewModel() {

        bonusTimer = new Timeline(new KeyFrame(Duration.seconds(10), e -> activateBonus()));
        bonusTimer.setCycleCount(Timeline.INDEFINITE);

        bonusDuration = new Timeline(new KeyFrame(Duration.seconds(3), e -> deactivateBonus()));

        gameCountdown = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            timeLeft--;
            if (timeLeft <= 0) model.setGameActive(false);
        }));
        gameCountdown.setCycleCount(60);

        bonusTimer.play();
        gameCountdown.play();
    }

    public IntegerProperty scoreProperty() { return model.scoreProperty(); }
    public BooleanProperty bonusActiveProperty() { return model.bonusActiveProperty(); }
    public BooleanProperty gameActiveProperty() { return model.gameActiveProperty(); }

    public DoubleProperty ballXProperty() { return model.ballXProperty(); }
    public DoubleProperty ballYProperty() { return model.ballYProperty(); }

    public int getTimeLeft() { return timeLeft; }

    public void updateBallPosition(double width, double height, double radius) {
        double nextX = model.ballXProperty().get() + dx;
        double nextY = model.ballYProperty().get() + dy;

        if (nextX <= radius || nextX >= width - radius) dx = -dx;
        if (nextY <= radius || nextY >= height - radius) dy = -dy;

        model.setBallPosition(model.ballXProperty().get() + dx,
                model.ballYProperty().get() + dy);
    }

    public void addScore() {
        int delta = model.isBonusActive() ? 2 : 1;
        model.addScore(delta);
    }

    public void moveBallRandom(double width, double height, double radius) {
        double newX = radius + random.nextDouble() * (width - 2 * radius);
        double newY = radius + random.nextDouble() * (height - 2 * radius);
        model.setBallPosition(newX, newY);
    }

    // УБЕГАНИЕ ОТ КУРСОРА
    public void repelFromCursor(double mouseX, double mouseY) {
        double bx = model.ballXProperty().get();
        double by = model.ballYProperty().get();

        double dxNew = bx - mouseX;
        double dyNew = by - mouseY;

        double len = Math.sqrt(dxNew * dxNew + dyNew * dyNew);
        if (len == 0) return;

        // Нормализуем
        dxNew /= len;
        dyNew /= len;

        // Сила отталкивания
        double force = 0.4; // чем больше, тем быстрее убегает

        // ПЛАВНОЕ изменение скорости (интерполяция)
        dx = dx * 0.85 + dxNew * force;
        dy = dy * 0.85 + dyNew * force;
    }


    private void activateBonus() {
        model.setBonusActive(true);
        bonusDuration.playFromStart();
    }

    private void deactivateBonus() {
        model.setBonusActive(false);
    }

    public void pause() {
        bonusTimer.pause();
        bonusDuration.pause();
        gameCountdown.pause();
    }

    public void resume() {
        bonusTimer.play();
        bonusDuration.play();
        gameCountdown.play();
    }
}
