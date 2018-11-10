package pong;

import processing.core.PApplet;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

import java.io.FileInputStream;
import java.io.InputStream;


/**
 * Main game class this manges the game and and its objects.
 *
 * @author Toby Essex
 */
public class PingPong extends PApplet {
    /**
     * This is the maximum score a player can have (aka. The game is over once a player reached 10 points).
     */
    private final int MAX_SCORE = 21;

    /**
     * This is used to determine the current state of the game. <br>
     * By default it is set to the start screen therefore the start menu will be displayed first.
     */
    private GameState currentGameState = GameState.START_MENU;
    /**
     * This represents the puck.
     */
    private Puck puck;
    /**
     * This represents the left paddle.
     */
    private Paddle leftPaddle;
    /**
     * This represents the right paddle.
     */
    private Paddle rightPaddle;
    /**
     * This is used to know when the <strong>'w'</strong> key is being pressed.
     */
    private boolean wPressed = false;
    /**
     * This is used to know when the <strong>'s'</strong> key is being pressed.
     */
    private boolean sPressed = false;
    /**
     * This is used to know when the <strong>up</strong> arrow is being pressed.
     */
    private boolean upPressed = false;
    /**
     * This is used to know when the <strong>down</strong> arrow is being pressed.
     */
    private boolean downPressed = false;

    /**
     * Main method this is the entry point for the game.
     *
     * @param args This is an array of supplied arguments from the command line
     */
    public static void main(String[] args) {
        PApplet.main("pong.PingPong");
    }

    /**
     * Prepare any settings for the game.
     */
    public void settings() {
        size(800, 600);
    }

    /**
     * Set up any var or information for the game such as instantiate objects.
     */
    public void setup() {
        puck = new Puck(this);
        leftPaddle = new Paddle(this, true);
        rightPaddle = new Paddle(this, false);
    }

    /**
     * Called every frame.
     */
    public void draw() {
        // Set background to black
        background(0);
        fill(255);

        // Checks the current menu state and displays the correct screen accordingly
        switch (currentGameState) {
            case START_MENU:
                startMenu();
                break;
            case GAME:
                game();
                // Once the game is playing always check to see if the game has finished
                checkGameOver();
                break;
            case GAME_OVER:
                gameOver();
                break;
        }
    }

    /**
     * Called if a key is pressed down.
     */
    public void keyPressed() {
        if (key == 'w') {
            wPressed = true;
        } else if (key == 's') {
            sPressed = true;
        } else if (keyCode == UP) {
            upPressed = true;
        } else if (keyCode == DOWN) {
            downPressed = true;
        }
    }

    /**
     * Called if a key is released.
     */
    public void keyReleased() {
        if (key == 'w') {
            wPressed = false;
        } else if (key == 's') {
            sPressed = false;
        } else if (keyCode == UP) {
            upPressed = false;
        } else if (keyCode == DOWN) {
            downPressed = false;
        }
    }

    /**
     * Called every time the mouse is pressed.
     */
    public void mousePressed() {
        // Only want to check for mouse click if the game state is START_MENU or GAME_OVER
        if (currentGameState == GameState.GAME) return;

        if (isMouseOver(MenuButton.START)) {
            currentGameState = GameState.GAME;
        } else if (isMouseOver(MenuButton.RESTART)) {
            puck.reset();
            leftPaddle.reset();
            rightPaddle.reset();
        } else if (isMouseOver(MenuButton.EXIT)) {
            exit();
        }
    }

    /**
     * Called to check whether the mouse if over a certain menu button when the game is over.
     *
     * @param button Tells the function which button was pressed and where to look for it.
     * @return Return true if the mouse if over the certain button and false if it isn't.
     */
    private boolean isMouseOver(MenuButton button) {
        switch (button) {
            case START:
            case RESTART:
                return (mouseX <= width / 2 + 80 && mouseX >= width / 2 - 80) && (mouseY <= height / 2 + 150 && mouseY >= height / 2 + 115);
            case EXIT:
                return (mouseX <= width / 2 + 50 && mouseX >= width / 2 - 50) && (mouseY <= height / 2 + 225 && mouseY >= height / 2 + 185);
            default:
                return false;
        }
    }

    /**
     * Displays the start menu.
     */
    private void startMenu() {
        // Sets the text to be white and and a font size of 30

        textSize(50);
        textAlign(CENTER);

        text("Pong", width / 2, 100);


        textSize(40);
        text("First to 10 points wins!", width / 2, height / 2);

        // If the mouse is over then we want the text to be larger (for UI effect)
        textSize(isMouseOver(MenuButton.START) ? 55 : 50);
        text("Start", width / 2, height / 2 + 150);

        // If the mouse is over then we want the text to be larger (for UI effect)
        textSize(isMouseOver(MenuButton.EXIT) ? 55 : 50);
        text("Exit", width / 2, height / 2 + 225);
    }

    /**
     * Displays the game.
     */
    private void game() {
        // Set the left paddle (player) score on the left and the right paddle (player) score on the right
        textSize(40);
        text(leftPaddle.getScore(), 40, 50);
        text(rightPaddle.getScore(), width - 40, 50);

        // Sets the text to be white and and a font size of 30
        textSize(30);

        // Display the paddles
        leftPaddle.show();
        rightPaddle.show();

        // Check so see if any keys are pressed and if so move the correct paddles
        movePaddlesOnPress();

        // Show and start moving the puck
        puck.show();
        puck.move();

        // Check if the paddle has hit an side, if so give the player (opposite to which edge was hit) a point
        // That can be found by giving the player a point opposite to the direction of the paddle
        if (puck.hasHitSide()) {
            playSound(Sound.MISS_BALL);

            // Give a point to the paddle (player) at the opposite end (i.e. the paddle in the opposite direction in which the puck is moving)
            (puck.isMovingRight() ? leftPaddle : rightPaddle).givePoint();
            puck.reset();
        }

        // Check if the puck has hit either a paddle or the top or bottom, if so reverse its direction
        // Depending on the direction of the puck decides which paddle to check for a collision with
        if (puck.hasHitPaddle(puck.isMovingRight() ? rightPaddle : leftPaddle)) {
            playSound(Sound.HIT_PADDLE);

            // This is the angle in which the puck will face when it returns
            int angle = (puck.isMovingRight() ? rightPaddle : leftPaddle).getReturnAngle(puck);
            puck.bounceOfPaddle(angle);
        } else if (puck.hasHitTopOrBottom()) {
            playSound(Sound.HIT_WALL);
            puck.bounceOfTopOrBottom();
        }
    }

    /**
     * Moves the paddles if the correct key is pressed. <br>
     * If statements are separate to allow multiple paddles to move at once.
     */
    private void movePaddlesOnPress() {
        if (wPressed) {
            // Move left paddle up
            leftPaddle.move(true);
        }

        if (sPressed) {
            // Move left paddle down
            leftPaddle.move(false);
        }

        if (upPressed) {
            // Move right paddle up
            rightPaddle.move(true);
        }

        if (downPressed) {
            // Move right paddle down
            rightPaddle.move(false);
        }
    }

    /**
     * Plays a sound from a audio file.
     *
     * @param sound Sound to play (will be one of the 3 ENUM values)
     */
    private void playSound(Sound sound) {
        try {
            InputStream in;
            switch (sound) {
                case HIT_PADDLE:
                    in = new FileInputStream(System.getProperty("user.dir") + "\\src\\audio\\hit_paddle.wav");
                    break;
                case HIT_WALL:
                    in = new FileInputStream(System.getProperty("user.dir") + "\\src\\audio\\hit_wall.wav");
                    break;
                case MISS_BALL:
                    in = new FileInputStream(System.getProperty("user.dir") + "\\src\\audio\\miss_ball.wav");
                    break;
                default:
                    throw new Exception("That is an invalid sound type");

            }

            AudioPlayer.player.start(new AudioStream(in));
        } catch (Exception e) {
            System.out.println("Error playing sound file: " + e.getMessage());
        }
    }

    /**
     * This displays the game over screen.
     */
    private void gameOver() {
        // Sets the text to be white and and a font size of 30
        textSize(50);
        textAlign(CENTER);

        text("Game Over!!!", width / 2, 100);

        textSize(40);
        text(leftPaddle.getScore() >= MAX_SCORE ? "Player 1 (Left) has won" : "Player 2 (Right) has won", width / 2, height / 2);


        // If the mouse is over then we want the text to be larger (for UI effect)
        textSize(isMouseOver(MenuButton.RESTART) ? 55 : 50);
        text("Restart", width / 2, height / 2 + 150);

        // If the mouse is over then we want the text to be larger (for UI effect)
        textSize(isMouseOver(MenuButton.EXIT) ? 55 : 50);
        text("Exit", width / 2, height / 2 + 225);
    }

    /**
     * Checks to see if either player has won and if so set the game state to <strong>GAME_OVER</strong>.
     * <br>
     * A play will have won if their score is equal or greater than the <strong>MAX_SCORE</strong>.
     */
    private void checkGameOver() {
        if (leftPaddle.getScore() >= MAX_SCORE || rightPaddle.getScore() >= MAX_SCORE) {
            currentGameState = GameState.GAME_OVER;
        }
    }

    /**
     * This represents all the different sounds that can be played.
     */
    private enum Sound {
        HIT_PADDLE, HIT_WALL, MISS_BALL
    }

    /**
     * This represents all the different game states.
     */
    private enum GameState {
        START_MENU, GAME, GAME_OVER
    }

    /**
     * This represents all the different menu items which are in the game. <br>
     * This are only visible on either the start or game over menu.
     */
    private enum MenuButton {
        START, RESTART, EXIT
    }

}
