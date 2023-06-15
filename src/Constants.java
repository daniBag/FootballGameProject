import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class Constants {
    public static final int SCREEN_WIDTH = 1200;
    public static final int SCREEN_HEIGHT = 850;
    public static final int SHOULDERS = 50;
    public static final Point CENTER = new Point(593, 420);
    public static final int LINE_WIDTH = 3;

    public static final int PLAYER_HEIGHT = 60;
    public static final int PLAYER_WIDTH = 60;
    public static final int RANDOM_TARGET_RANGE = 40;
    public static final Point BALL_START = CENTER;
    public static final int BALL_SIZE = 25;
    public static final int MOVE_STEP = 3;
    public static final int SHOT_RANGE = 150;
    public static final int SHOT_MODIFIER = 7;
    public static final int PENALTY_BOX_WIDTH = 160;
    public static final int PENALTY_BOX_HEIGHT = 400;
    public static final int PEN_BOX_BOTTOM = 575;
    public static final int PEN_BOX_TOP = 175;
    public static final int GOAL_TOP = 250;
    public static final int GOAL_BOTTOM = 500;
    public static final int GOAL_WIDTH = 40;
    public static final int GOAL_HEIGHT = 250;
    public static final int LAST_KEYS_MAX = 3;
    public static final int RUN_STEP = MOVE_STEP * 2;
    public static final int RUN_SHOT_RANGE = SHOT_RANGE * 2;
    public static final List<ImageIcon> FIRST_PLAYER = Arrays.asList(new ImageIcon(
            new ImageIcon("res/player1_stand.png").getImage().getScaledInstance(PLAYER_WIDTH * 2, PLAYER_HEIGHT * 2, Image.SCALE_SMOOTH)),
            new ImageIcon(new ImageIcon("res/player1_preKick.png").getImage().getScaledInstance(PLAYER_WIDTH * 2, PLAYER_HEIGHT * 2, Image.SCALE_SMOOTH)),
            new ImageIcon(new ImageIcon("res/player1_kick.png").getImage().getScaledInstance(PLAYER_WIDTH * 2, PLAYER_HEIGHT * 2, Image.SCALE_SMOOTH)),
            new ImageIcon(new ImageIcon("res/player1_run_R_forward.png").getImage().getScaledInstance(PLAYER_WIDTH * 2, PLAYER_HEIGHT * 2, Image.SCALE_SMOOTH)),
            new ImageIcon(new ImageIcon("res/player1_run_L_forward.png").getImage().getScaledInstance(PLAYER_WIDTH * 2, PLAYER_HEIGHT * 2, Image.SCALE_SMOOTH)));
    public static final List<ImageIcon> SECOND_PLAYER = Arrays.asList(new ImageIcon(
                    new ImageIcon("res/player2_stand.png").getImage().getScaledInstance(PLAYER_WIDTH * 2, PLAYER_HEIGHT * 2, Image.SCALE_SMOOTH)),
            new ImageIcon(new ImageIcon("res/player2_preKick.png").getImage().getScaledInstance(PLAYER_WIDTH * 2, PLAYER_HEIGHT * 2, Image.SCALE_SMOOTH)),
            new ImageIcon(new ImageIcon("res/player2_kick.png").getImage().getScaledInstance(PLAYER_WIDTH * 2, PLAYER_HEIGHT * 2, Image.SCALE_SMOOTH)),
            new ImageIcon(new ImageIcon("res/player2_run_R_forward.png").getImage().getScaledInstance(PLAYER_WIDTH * 2, PLAYER_HEIGHT * 2, Image.SCALE_SMOOTH)),
            new ImageIcon(new ImageIcon("res/player2_run_L_forward.png").getImage().getScaledInstance(PLAYER_WIDTH * 2, PLAYER_HEIGHT * 2, Image.SCALE_SMOOTH)));
    public static final int STAND = 0;
    public static final int PRE_KICK = 1;
    public static final int KICK = 2;
    public static final int RUN_R = 3;
    public static final int RUN_L = 4;
    public static final int LOW = 1;
    public static final int MEDIUM = 2;
    public static final int HIGH = 3;
    public static final int DIFFICULTY_MEDIUM = 50;
    public static final int DIFFICULTY_HARD = 25;
    public static final long MIN_SHOT_POWER = 2;
    public static final int MAX_SHOT_POWER = 4;
    public static final int LEVEL_ONE = 0;
    public static final int LEVEL_TWO = 1;
    public static final int LEVEL_THREE = 2;
    public static final int LEVEL_FOUR = 3;
    public static final int LEVEL_FIVE = 4;
    public static final List<Integer> LEVELS = List.of(1, 2, 3, 4, 5);
    public static final int DIFFICULTY_LOW = 75;
    public static final int TIME_BAR_WIDTH = 800;
    public static final int TIME_BAR_HEIGHT = 30;
    public static final Color BALL_BASE_COLOR = new Color(124, 157, 196);
    public static final Color BALL_MID_COLOR = new Color(105, 130, 171);
    public static final Color BALL_DARK_COLOR = new Color(86, 113, 152);
    public static final int BALL_MID_X_FIX = 1;
    public static final int BALL_MID_Y_FIX = 6;
    public static final int BALL_DARK_Y_FIX = 14;
    public static final int BALL_DARK_X_FIX = 3;
    public static final int KICK_DIR_FIXATION = 20;
    public static final int TOUCH_RANGE = 10;
    public static final int PAUSE_SLEEP = 200;
    public static final int PLAYER_THREAD_SLEEP = 15;
    public static final Color TIME_REMAINING_COLOR = new Color(28, 89, 187);
    public static final Color TIME_BAR_BOUND = new Color(130, 143, 137);
    public static final int CENTER_CIRCLE_SIZE = 180;
    public static final int CENTER_CIRCLE_X_FIX = 80;
    public static final int CENTER_CIRCLE_Y_FIX = 85;
    public static final Color PITCH_LINE_COLOR = new Color(211, 204, 204);
    public static final int ANNOUNCEMENT_TIME = 2500;
    public static final int CENTER_DOT = 15;
    public static final int CENTER_LINE_X_FIX = 5;
    public static final int TIME_BAR_MARGAIN = 2;
    public static final Color PITCH_COLOR = new Color(23, 73, 23);
    public static final int WINDOW_FIX_WIDTH = 10;
    public static final int WINDOW_FIX_HEIGHT = 36;
    public static final float START_SCREEN_FONT = 24.0f;
    public static final Color PLAYER_CUSTOMIZE_COLOR = new Color(255, 255, 255);
    public static final ImageIcon PAUSE_ICON = new ImageIcon("res/Untitled_Artwork.png");
    public static final Color SCREEN_OVERLAY_BACKGROUND = new Color(255, 255, 255, 75);
    public static final int PAUSE_ICON_FIX = 200;
    public static final Integer LEVEL_UP = 2;
}
