package pong;

import processing.core.PApplet;

/**
 * This is the puck object which moves along the game.
 *
 * @author Toby Essex
 */
public class Puck {
    /**
     * This represents the height and width of the puck (in pixels). <br>
     * As it's round a diameter was set instead of a separate left and right variable.
     */
    private final int DIAMETER = 20;
    /**
     * This constant is used to keep the puck's speed the same which ever direction it is moving.
     */
    private final float SPEED_CONSTANT = 5;
    /**
     * This represents the parent class (PingPong) so we can access all the methods and variables declared is the PApplet class.
     */
    private PApplet parent;
    /**
     * This holds the pucks current position along the X axis (horizontal position).
     */
    private float x = 0;
    /**
     * This holds the pucks current position along the Y axis (vertical position).
     */
    private float y = 0;
    /**
     * This holds the speed of the puck on the X axis (horizontal speed).
     */
    private float xSpeed;

    /**
     * This holds the speed of the puck on the Y axis (vertical speed).
     */
    private float ySpeed;

    /**
     * This is used to know whether the puck is moving left or right.
     */
    private boolean isMovingRight = true;


    /**
     * Puck constructor.
     *
     * @param parent Represents the PingPong class to we can access any methods in the PApplet class.
     */
    Puck(PApplet parent) {
        this.parent = parent;

        reset();
    }

    /**
     * This resets the position of the puck when it hits the sides.
     */
    void reset() {
        // Reset moving direction
        isMovingRight = true;

        x = 0;
        y = 0;

        // Random angle to start the puck in
        float angle = parent.random(parent.TWO_PI);

        // This stops the puck starting at an obscure angle
        while (
                (angle * parent.RAD_TO_DEG >= 45 && angle * parent.RAD_TO_DEG <= 90)   ||
                (angle * parent.RAD_TO_DEG >= 90 && angle * parent.RAD_TO_DEG <= 135)  ||
                (angle * parent.RAD_TO_DEG >= 225 && angle * parent.RAD_TO_DEG <= 270) ||
                (angle * parent.RAD_TO_DEG >= 270 && angle * parent.RAD_TO_DEG <= 315)
                ) {
            // Recalculate the angle
            angle = parent.random(parent.TWO_PI);
        }

        // This determines whether the puck is going left or right and sets the variable accordingly
        if (angle < parent.PI * 1.5f && angle > parent.PI / 2) {
            isMovingRight = false;
        }

        // Sets the X and Y speed
        // Multiply speed constant by cos and sin so it speed it's always the same which ever direction it moves
        xSpeed = SPEED_CONSTANT * PApplet.cos(angle);
        ySpeed = SPEED_CONSTANT * PApplet.sin(angle);
    }

    /**
     * This returns the x position.
     */
    float getX() {
        return x + (parent.width / 2);
    }

    /**
     * This returns the y position.
     */
    float getY() {
        return y + (parent.height / 2);
    }

    /**
     * This displays the object and sets it colour and shape and position.
     */
    void show() {
        parent.fill(255);

        parent.ellipse(getX(), getY(), DIAMETER, DIAMETER);
    }

    /**
     * This moves the puck (either left or right).
     */
    void move() {
        x += xSpeed;
        y += ySpeed;
    }

    /**
     * Returns whether the puck has hit a side of the game.
     */
    boolean hasHitSide() {
        float _x = getX();
        return _x > parent.width || _x < 0;
    }

    /**
     * Returns whether the puck has hit the top or the bottom of the game.
     */
    boolean hasHitTopOrBottom() {
        float _y = getY();
        return _y > parent.height - DIAMETER / 2 || _y < DIAMETER / 2;
    }

    /**
     * Checks to see whether the puck has collided (hit) a paddle.
     *
     * @param paddle This could either be the left of right paddle depending on the direction of the puck.
     * @return Returns whether it has collided (hit) a paddle or not.
     */
    boolean hasHitPaddle(Paddle paddle) {
        float _y = getY();
        float _x = getX();

        // Get the top and bottom of the paddle's y position
        // This is done by getting the y (which is the middle of the paddle's y) and then either adding (for top) or subtracting (for bottom) half the height
        float paddle_y_top = paddle.getY() - (paddle.getHeight() / 2);
        float paddle_y_bottom = paddle.getY() + (paddle.getHeight() / 2);

        // Check to see if the puck is within the top and bottom of the paddle
        boolean is_correct_height = _y >= paddle_y_top && _y <= paddle_y_bottom;

        // Check to see whether the x of the puck is the same or greater (or less than) than the x of the paddle (if so then it has collided)
        // Depending on whether the puck if moving left or right decides whether x needs to greater or less than the x of the paddle
        // To get the x to represent the edge of the paddle we need to either add (for when the puck if moving left) or subtract (for when the puck if moving right) 1.5 x width
        boolean has_collided_with_paddle_x = isMovingRight ? _x >= paddle.getX() - paddle.getWidth() * 1.5 : _x <= paddle.getX() + paddle.getWidth() * 1.5;

        // The puck and to be true for both x and y to have hit the paddle
        return is_correct_height && has_collided_with_paddle_x;
    }

    /**
     * This is called when the puck is bouncing of a paddle.
     *
     * @param angle The angle to point the puck
     */
    void bounceOfPaddle(int angle) {
        // If the puck is moving left then we need the opposite angle to what was given
        // See "Bounce Angles.png" for more information
        if (!isMovingRight) {
            if (angle == 0) {
                // If it is 0 set it to 180
                angle = 180;
            } else {
                // This check to see whether the angle is negative or not and does the correct calculation accordingly
                angle = angle < 0 ? -180 - angle : 180 - 30;
            }
        }

        // The angle needs to be converted to radians to work
        float angle_in_radians = angle * parent.DEG_TO_RAD;

        // Set the correct speed and angle
        // Multiply speed constant by cos and sin so it speed it's always the same which ever direction it moves
        xSpeed = SPEED_CONSTANT * PApplet.cos(angle_in_radians);
        ySpeed = SPEED_CONSTANT * PApplet.sin(angle_in_radians);

        // Reverse the direction
        xSpeed *= -1;

        // Tell the game the puck is moving in the opposite direction to what it just moving
        isMovingRight = !isMovingRight;
    }

    /**
     * This is called when the puck if bouncing of either the top or bottom of the game.
     */
    void bounceOfTopOrBottom() {
        ySpeed *= -1;
    }

    /**
     * Returns whether the puck if moving left or right.
     */
    boolean isMovingRight() {
        return isMovingRight;
    }
}
