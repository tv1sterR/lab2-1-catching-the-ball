package model;

import javafx.beans.property.*;

public class GameModel {

    private final IntegerProperty score = new SimpleIntegerProperty(0);
    private final BooleanProperty bonusActive = new SimpleBooleanProperty(false);
    private final BooleanProperty gameActive = new SimpleBooleanProperty(true);

    private final DoubleProperty ballX = new SimpleDoubleProperty(400);
    private final DoubleProperty ballY = new SimpleDoubleProperty(275);

    public IntegerProperty scoreProperty() { return score; }
    public BooleanProperty bonusActiveProperty() { return bonusActive; }
    public BooleanProperty gameActiveProperty() { return gameActive; }

    public DoubleProperty ballXProperty() { return ballX; }
    public DoubleProperty ballYProperty() { return ballY; }

    public void addScore(int delta) { score.set(score.get() + delta); }

    public void setBonusActive(boolean v) { bonusActive.set(v); }
    public boolean isBonusActive() { return bonusActive.get(); }

    public void setGameActive(boolean v) { gameActive.set(v); }

    public void setBallPosition(double x, double y) {
        ballX.set(x);
        ballY.set(y);
    }
}
