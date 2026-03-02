package viewmodel;

import javafx.animation.*;
import javafx.beans.property.*;
import javafx.util.Duration;
import model.GameModel;

import java.util.Random;

public class GameViewModel {

    private final GameModel model = new GameModel();
    private final Random random = new Random();

    private double dx = 3;
    private double dy = 3;

    private final Timeline bonusTimer;
    private final Timeline bonusDuration;
    private final Timeline gameCountdown;

    private int timeLeft = 60;

    public GameViewModel() {

        bonusTimer = new Timeline(new KeyFrame(Duration.seconds(10), e -> activateBonus()));
        bonusTimer.setCycleCount(Animation.INDEFINITE);

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
    public void repelFromCursor(double mouseX, double mouseY) {
        double dxNew = model.ballXProperty().get() - mouseX;
        double dyNew = model.ballYProperty().get() - mouseY;

        double len = Math.sqrt(dxNew * dxNew + dyNew * dyNew);
        if (len == 0) return;

        dx = dxNew / 15;
        dy = dyNew / 15;
    }

}
