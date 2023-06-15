import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class MainScene extends JPanel implements Runnable{
    public static final Rectangle TOP_BORDER = new Rectangle(Constants.SHOULDERS, Constants.SHOULDERS, Constants.SCREEN_WIDTH - (2 * Constants.SHOULDERS), Constants.LINE_WIDTH);
    public static final Rectangle BOTTOM_BORDER = new Rectangle(Constants.SHOULDERS, Constants.SCREEN_HEIGHT - Constants.SHOULDERS, Constants.SCREEN_WIDTH - (2 * Constants.SHOULDERS), Constants.LINE_WIDTH);
    public static final Rectangle LEFT_BORDER_TOP = new Rectangle(Constants.SHOULDERS, Constants.SHOULDERS, Constants.LINE_WIDTH, (((Constants.SCREEN_HEIGHT - (2 * Constants.SHOULDERS)) - Constants.GOAL_HEIGHT) / 2));
    public static final Rectangle LEFT_GOAL = new Rectangle(Constants.SHOULDERS - Constants.GOAL_WIDTH, Constants.SHOULDERS + Constants.GOAL_TOP, Constants.GOAL_WIDTH, Constants.GOAL_HEIGHT);
    public static final Rectangle LEFT_BORDER_BOTTOM = new Rectangle(Constants.SHOULDERS, LEFT_GOAL.y + Constants.GOAL_HEIGHT, Constants.LINE_WIDTH, LEFT_BORDER_TOP.height);
    public static final Rectangle RIGHT_BORDER_TOP = new Rectangle(Constants.SCREEN_WIDTH - Constants.SHOULDERS, Constants.SHOULDERS, Constants.LINE_WIDTH, LEFT_BORDER_TOP.height);
    public static final Rectangle RIGHT_GOAL = new Rectangle(Constants.SCREEN_WIDTH - (Constants.SHOULDERS), Constants.SHOULDERS + Constants.GOAL_TOP, Constants.GOAL_WIDTH, Constants.GOAL_HEIGHT);
    public static final Rectangle RIGHT_BORDER_BOTTOM = new Rectangle(Constants.SCREEN_WIDTH - Constants.SHOULDERS, LEFT_BORDER_BOTTOM.height, Constants.LINE_WIDTH, LEFT_BORDER_BOTTOM.height);
    public static final Rectangle LEFT_BOX = new Rectangle(LEFT_BORDER_TOP.x, Constants.SHOULDERS + Constants.PEN_BOX_TOP, Constants.PENALTY_BOX_WIDTH, Constants.PENALTY_BOX_HEIGHT);
    public static final Rectangle RIGHT_BOX = new Rectangle(Constants.SCREEN_WIDTH - (Constants.SHOULDERS + Constants.PENALTY_BOX_WIDTH), Constants.SHOULDERS + Constants.PEN_BOX_TOP, Constants.PENALTY_BOX_WIDTH, Constants.PENALTY_BOX_HEIGHT);
    public static final Rectangle TIME_BAR = new Rectangle(LEFT_BOX.x + LEFT_BOX.width, BOTTOM_BORDER.y - BOTTOM_BORDER.height, Constants.TIME_BAR_WIDTH, Constants.TIME_BAR_HEIGHT);
    private Rectangle remainingTime;
    private int difficulty;
    private final Thread PLAYER_THREAD;
    private final Thread BALL_THREAD;
    private final Thread GOALKEEPER_THREAD;
    private final Player PLAYER;
    private final SynchronizedState<Ball> BALL;
    private static int playerId = 1;
    private final GoalKeeper GOALKEEPER;
    private final SynchronizedState<Boolean> GOAL;
    private final JLabel ANNOUNCEMENT;
    private final Keyboard KEYBOARD;
    private final Window WINDOW;
    private boolean pause;
    private final SynchronizedState<Integer> SCORED;
    private int timeDifficulty;
    private boolean timerDeduct;
    private boolean gameOver;
    public MainScene(Window window, boolean playerBlue){
        this.gameOver = false;
        this.timerDeduct = false;
        this.SCORED = new SynchronizedState<>(0);
        this.WINDOW = window;
        this.pause = false;
        this.setDoubleBuffered(true);
        this.setSize(Constants.SCREEN_WIDTH,Constants.SCREEN_HEIGHT);
        this.setFocusable(true);
        this.requestFocus(true);
        this.PLAYER = new Player(playerBlue, playerId, Constants.SCREEN_WIDTH/2 + 20, Constants.SCREEN_HEIGHT/2, this, true);
        playerId++;
        this.KEYBOARD = this.PLAYER.getController();
        this.addKeyListener(this.KEYBOARD);
        this.setBackground(Constants.PITCH_COLOR);
        this.ANNOUNCEMENT = new JLabel();
        this.ANNOUNCEMENT.setHorizontalAlignment(SwingConstants.CENTER);
        this.ANNOUNCEMENT.setFont(this.ANNOUNCEMENT.getFont().deriveFont(72.0f));
        this.add(this.ANNOUNCEMENT);
        this.difficulty = Constants.LEVEL_ONE;
        this.timeDifficulty = Constants.LEVELS.get(this.difficulty);
        this.remainingTime = new Rectangle(TIME_BAR.x + Constants.TIME_BAR_MARGAIN, TIME_BAR.y + Constants.TIME_BAR_MARGAIN,
                TIME_BAR.width - (Constants.TIME_BAR_MARGAIN * 2), TIME_BAR.height - (Constants.TIME_BAR_MARGAIN * 2));
        this.GOAL = new SynchronizedState<>(false);
        this.GOALKEEPER = new GoalKeeper(!playerBlue, playerId, LEFT_BOX.x + (LEFT_BOX.width / 2), LEFT_GOAL.y + (LEFT_GOAL.height / 2), this);
        playerId++;
        this.PLAYER_THREAD = new Thread(this.PLAYER);
        this.BALL = new SynchronizedState<>(new Ball(this.PLAYER, this));
        this.BALL_THREAD = new Thread(this.BALL.getState());
        this.GOALKEEPER_THREAD = new Thread(this.GOALKEEPER);
    }
    private void goalScored(){
        this.PLAYER.goalEvent();
        this.GOALKEEPER.goalEvent();
        String announcement = "GOALLLL  " + this.SCORED.getState();
        if (this.SCORED.getState() >= Constants.LEVEL_UP){
            if (this.difficulty < Constants.LEVEL_FIVE){
                announcement = "LEVEL UP!";
                this.SCORED.setState(0);
                this.difficulty++;
                this.timeDifficulty = Constants.LEVELS.get(this.difficulty);
                this.GOALKEEPER.difficultyHarder();
                this.remainingTime.setBounds(this.remainingTime.x, this.remainingTime.y, TIME_BAR.width - (Constants.TIME_BAR_MARGAIN * 2), TIME_BAR.height - (Constants.TIME_BAR_MARGAIN * 2));
            }else {
                this.gameWon();
            }
        }
        this.showAnnouncement(this.ANNOUNCEMENT, announcement);
        this.GOAL.setState(false);
    }
    private void gameWon() {
        this.gameOver = true;
        this.WINDOW.gameOver(true);
    }
    public int getDifficulty(){
        int difficultyModifier;
        if (this.difficulty == Constants.LEVEL_ONE || this.difficulty == Constants.LEVEL_TWO){
            difficultyModifier = Constants.DIFFICULTY_LOW;
        } else if (this.difficulty == Constants.LEVEL_THREE || this.difficulty == Constants.LEVEL_FOUR) {
            difficultyModifier = Constants.DIFFICULTY_MEDIUM;
        }else {
            difficultyModifier = Constants.DIFFICULTY_HARD;
        }
        return difficultyModifier;
    }
    private void showAnnouncement(JLabel announcement, String text) {
        announcement.setLocation(new Point(Constants.CENTER.x - announcement.getWidth() / 2, Constants.CENTER.y - announcement.getHeight() / 2));
        announcement.setText(text);
        announcement.setVisible(true);
        Thread announce = new Thread(()->{
            Utils.sleep(Constants.ANNOUNCEMENT_TIME);
            this.ANNOUNCEMENT.setVisible(false);
        });
        announce.start();
    }
    public Keyboard getKeyboard(){
        return this.KEYBOARD;
    }
    public void paintComponent(Graphics graphics){
        super.paintComponent(graphics);
        Graphics2D graphics2D = (Graphics2D) graphics;
        this.drawPitch(graphics2D);
        this.drawDynamicElements(graphics2D);
        if (this.pause){
            this.WINDOW.pauseScreenRepaint(graphics);
        }
        this.drawTimeBar(graphics2D);
    }

    private void drawDynamicElements(Graphics2D graphics2D) {
        this.PLAYER.drawPlayer(graphics2D);
        this.GOALKEEPER.drawPlayer(graphics2D);
        this.BALL.getState().draw(graphics2D);
    }

    private void drawTimeBar(Graphics2D graphics2D) {
        graphics2D.setColor(Constants.TIME_BAR_BOUND);
        graphics2D.draw(TIME_BAR);
        graphics2D.setColor(Constants.TIME_REMAINING_COLOR);
        graphics2D.fill(this.remainingTime);
    }

    private void drawPitch(Graphics2D graphics2D) {
        graphics2D.setColor(Constants.PITCH_LINE_COLOR);
        graphics2D.fillOval(Constants.CENTER.x, Constants.CENTER.y, Constants.CENTER_DOT, Constants.CENTER_DOT);
        graphics2D.fillRect(Constants.CENTER.x + Constants.CENTER_LINE_X_FIX, Constants.SHOULDERS, Constants.LINE_WIDTH, Constants.SCREEN_HEIGHT - (2 * Constants.SHOULDERS));
        graphics2D.setStroke(new BasicStroke(Constants.LINE_WIDTH));
        graphics2D.drawOval(Constants.CENTER.x - Constants.CENTER_CIRCLE_X_FIX, Constants.CENTER.y - Constants.CENTER_CIRCLE_Y_FIX, Constants.CENTER_CIRCLE_SIZE, Constants.CENTER_CIRCLE_SIZE);
        graphics2D.draw(LEFT_GOAL);
        graphics2D.draw(RIGHT_GOAL);
        graphics2D.draw(LEFT_BOX);
        graphics2D.draw(RIGHT_BOX);
        graphics2D.fill(TOP_BORDER);
        graphics2D.fill(BOTTOM_BORDER);
        graphics2D.fill(LEFT_BORDER_TOP);
        graphics2D.fill(LEFT_BORDER_BOTTOM);
        graphics2D.fill(RIGHT_BORDER_TOP);
        graphics2D.fill(RIGHT_BORDER_BOTTOM);
    }

    public synchronized void pauseManage(){
        if (this.KEYBOARD.isPause()){
            this.pause = true;
            this.WINDOW.pause();
            this.locationsBackUp();
            while (this.KEYBOARD.isPause()){
                this.sleepPause();
                Utils.sleep(150);
            }
            this.pause = false;
            this.locationsLoad();
            this.WINDOW.resume();
        }
    }
    public boolean isGameRunning(){
        return !this.gameOver;
    }
    private void locationsBackUp() {
        this.BALL.getState().locationBackUp();
        this.PLAYER.locationBackUp();
        this.GOALKEEPER.locationBackUp();
    }
    private void locationsLoad() {
        this.BALL.getState().locationLoad();
        this.PLAYER.locationLoad();
        this.GOALKEEPER.locationLoad();
    }

    private void update(){
        if (this.remainingTime.width > 0){
            pauseManage();
            if (this.BALL.getState().goalScored() && !this.GOAL.getState()){
                this.GOAL.setState(true);
                this.SCORED.setState(this.SCORED.getState() + 1);
            }
            if (this.ANNOUNCEMENT.isVisible()){
                this.ANNOUNCEMENT.setForeground(this.randomiseColor());
            }
            this.PLAYER.update();
            this.GOALKEEPER.update();
            this.BALL.getState().update();
            if (this.GOAL.getState()){
                this.goalScored();
                this.GOAL.setState(false);
            }
            if (this.timerDeduct){
                this.remainingTime = new Rectangle(this.remainingTime.x, this.remainingTime.y, this.remainingTime.width - this.timeDifficulty, this.remainingTime.height);
            }
        }else if (this.SCORED.getState() < 2){
            this.gameOver();
        }
    }

    private void gameOver() {
        this.gameOver = true;
        this.WINDOW.gameOver(false);
    }
    private void sleepPause(){
        this.PLAYER.sleepPauseSet();
        this.BALL.getState().sleepPauseSet();
        this.GOALKEEPER.sleepPauseSet();
    }
    private Color randomiseColor() {
        Random random = new Random();
        return new Color(random.nextInt(256),random.nextInt(256),random.nextInt(256));
    }

    @Override
    public void run() {
        this.PLAYER_THREAD.start();
        this.BALL_THREAD.start();
        this.GOALKEEPER_THREAD.start();
        while(!this.gameOver){
            update();
            repaint();
            Utils.sleep(20);
            this.timerDeduct = !this.timerDeduct;
        }
    }
    public Ball getBall(){
        return this.BALL.getState();
    }
    public boolean ballInLeftBox() {
        return Utils.collision(LEFT_BOX, this.BALL.getState());
    }
    public boolean collisionCheck(Player player, Rectangle nextMove) {
        boolean collision;
        if (this.PLAYER.isSame(player)){
            collision = Utils.collision(nextMove, this.GOALKEEPER);
        }else {
            collision = Utils.collision(nextMove, this.PLAYER);
        }
        return collision;
    }
}