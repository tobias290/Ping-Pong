package pong;

import processing.core.PApplet;

/**
 * This is the paddle object which moves up and down the side of the game. <br>
 * There will be a left and right paddle for either side of the game.
 *
 * @author Toby Essex
 */
public class Paddle {
    /**
     * This constant represents the speed the paddle can move up or down.
     *
     */
    private final float SPEED_CONSTANT = 5;
    /**
     * This represents the parent class (PingPong) so we can access all the methods and variables declared is the PApplet class.
     */
    private PApplet parent;

    /**
     * This represents the height of the paddle (in pixels).
     */
    private int height = 100;

    /**
     * This represents the width of the paddle (in pixels).
     */
    private int width = 10;

    /**
     * This is used to know if the paddle if on the left or right side of the board.
     */
    private boolean isLeft;

    /**
     * This holds the pucks current position along the X axis (horizontal position).
     */
    private float x = 0;

    /**
     * This holds the pucks current position along the Y axis (vertical position).
     */
    private float y = 0;
    /**
     * This is used to keep track of the paddle's (player's) score.
     */
    private int score = 0;

    /**
     * Paddle constructor.
     *
     * @param parent Represents the PingPong class to we can access any methods in the PApplet class.
     * @param is_left Tells the object whether this will be the left or right paddle (so we can position it correctly).
     */
    Paddle(PApplet parent, boolean is_left) {
        this.parent = parent;
        this.isLeft = is_left;
    }

    void reset() {
        score = 0;
        x = 0;
        y = 0;
    }

    /**
     * This displays the object and sets it colour and shape and position.
     */
    void show() {
        parent.fill(255);

        float _x = x + (isLeft ? 10 : parent.width - 10);
        float _y = y + (parent.height / 2);

        parent.rectMode(parent.CENTER);
        parent.rect(_x, _y, width, height);
    }

    /**
     * This moves the paddle (either up or down).
     *
     * @param is_up Tell the method whether the paddle is going to move up or down.
     */
    void move(boolean is_up) {
        // Checks the paddles position to make sure it can't move above or below the game boundaries
        // The ternary operator is used to know whether we are checking the top (is_up) or the bottom (!is_up)
        if (is_up ? getY() - (getHeight() / 2) <= 10 : getY() + (getHeight() / 2) >= parent.height - 10) return;

        y = is_up ? y - SPEED_CONSTANT : y + SPEED_CONSTANT;
    }

    /**
     * Depending on which part of the paddle was hit will determine which angle to return the puck.
     *
     * @param puck Puck instance is passed so we can get the Y coordinate.
     * @return - Returns the correct angle to return the puck.
     */
    int getReturnAngle(Puck puck) {
        float paddle_y_top = getY() - (getHeight() / 2);

        float puck_y = puck.getY();

        float base_num = puck_y - paddle_y_top;

        if (base_num < 12.5) {
            return PaddleSection.TOP;
        } else if (base_num >= 12.5 && base_num < 25) {
            return PaddleSection.TOP_MIDDLE;
        } else if (base_num >= 25 && base_num < 37.5) {
            return PaddleSection.TOP_BOTTOM;
        } else if (base_num >= 37.5 && base_num < 50) {
            return PaddleSection.MIDDLE;
        } else if (base_num >= 50 && base_num < 62.5) {
            return PaddleSection.MIDDLE;
        } else if (base_num >= 62.5 && base_num < 75) {
            return PaddleSection.BOTTOM_TOP;
        } else if (base_num >= 75 && base_num < 87.5) {
            return PaddleSection.BOTTOM_MIDDLE;
        } else {
            // Else the number is in the bottom section
            return PaddleSection.BOTTOM;
        }
    }

    /**
     * This gives the paddle (player) a point. <br>
     * This is called when the puck has hit the other side.
     */
    void givePoint() {
        score++;
    }

    /**
     * This returns the paddle's (player's) score.
     */
    int getScore() {
        return score;
    }

    /**
     * Gets the height of the paddle.
     */
    int getHeight() {
        return height;
    }

    /**
     * Gets the width of the paddle.
     */
    int getWidth() {
        return width;
    }

    /**
     * Gets the x position of the paddle.
     */
    float getX() {
        return x + (isLeft ? 10 : parent.width - 10);
    }

    /**
     * Gets the y position of the paddle.
     */
    float getY() {
        return y + (parent.height / 2);
    }

    /**
     * This represents the different angles to return the puck according to what section of the paddle was hit.
     */
    private static class PaddleSection {
        static final int TOP = -45;
        static final int TOP_MIDDLE = -30;
        static final int TOP_BOTTOM = -15;
        static final int MIDDLE = 0;
        static final int BOTTOM_TOP = 15;
        static final int BOTTOM_MIDDLE = 30;
        static final int BOTTOM = 45;
    }
}
