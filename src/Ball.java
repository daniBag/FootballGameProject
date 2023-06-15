import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.random.RandomGenerator;
public class Ball extends JComponent implements Runnable, CanCollide{
    private int startingXPosition;
    private int startingYPosition;
    private final int SIZE;
    private boolean isTouched;
    private boolean toShoot;
    private final SynchronizedState<Player> PLAYER;
    private final SynchronizedState<Rectangle> COLLIDER;
    private boolean controlledShot;
    private boolean outOfBounds;
    private final SynchronizedState<Boolean> BALL_MOVED;
    private final SynchronizedState<Boolean> GOAL_SCORED;
    private boolean sleepPause;
    private final Rectangle LAST_LOCATION;
    private final MainScene MAIN_SCENE;
    private boolean stopBallMovement;
    private final ConcurrentLinkedQueue<Integer> LAST_KEYS;
    private long shotPower;
    public Ball(Player player, MainScene mainScene){
        this.LAST_KEYS = new ConcurrentLinkedQueue<>();
        this.shotPower = Constants.MIN_SHOT_POWER;
        this.LAST_LOCATION = new Rectangle();
        this.stopBallMovement = false;
        this.MAIN_SCENE = mainScene;
        this.sleepPause = false;
        this.startingXPosition = Constants.BALL_START.x;
        this.startingYPosition = Constants.BALL_START.y;
        this.SIZE = Constants.BALL_SIZE;
        this.GOAL_SCORED = new SynchronizedState<>(false);
        this.isTouched = false;
        this.toShoot = false;
        this.controlledShot = false;
        this.outOfBounds = false;
        this.BALL_MOVED = new SynchronizedState<>(false);
        this.PLAYER = new SynchronizedState<>(player);
        this.COLLIDER = new SynchronizedState<>(new Rectangle(this.startingXPosition,this.startingYPosition,this.SIZE,this.SIZE));
        this.setBounds(this.COLLIDER.getState());
    }
    public void shoot(boolean controlled) {
        if (!this.stopBallMovement){
            this.isTouched = false;
            int range = Constants.SHOT_RANGE;
            Point kickTarget = this.getLocation();
            int modifier = Constants.SHOT_MODIFIER;
            if (controlled){
                kickTarget.setLocation(controlledShotTarget(kickTarget, range));
                if (this.shotPower > Constants.MAX_SHOT_POWER){
                    modifier = modifier * Constants.MAX_SHOT_POWER;
                }else {
                    modifier = Math.toIntExact(modifier * this.shotPower);
                }
            }else {
                kickTarget.setLocation(((GoalKeeper) this.PLAYER.getState()).nextLocation(kickTarget, false));
                modifier = modifier * RandomGenerator.getDefault().nextInt((int) Constants.MIN_SHOT_POWER, Constants.MAX_SHOT_POWER);
            }
            while (modifier > 0){
                if (this.GOAL_SCORED.getState() || this.outOfBounds(this.COLLIDER.getState())){
                    break;
                }
                Utils.sleep(20);
                for (int i = 0; i < modifier; i++){
                    if (this.GOAL_SCORED.getState() || this.outOfBounds(this.COLLIDER.getState())){
                        break;
                    }
                    this.startingXPosition = Utils.posParManagement(this.startingXPosition, kickTarget.x);
                    this.startingYPosition = Utils.posParManagement(this.startingYPosition, kickTarget.y);
                }
                modifier--;
            }
            Keyboard.shotManage.setState(false);
            this.controlledShot = false;
            this.toShoot=false;
        }
    }
    private Point controlledShotTarget(Point kickTarget, int range) {
        Random random = new Random();
        kickTarget.move(MainScene.LEFT_GOAL.x, MainScene.LEFT_GOAL.y +
                random.nextInt((Constants.RANDOM_TARGET_RANGE * -1), Constants.GOAL_HEIGHT - Constants.BALL_SIZE + Constants.RANDOM_TARGET_RANGE));
        if (!this.LAST_KEYS.isEmpty()){
            if (this.LAST_KEYS.contains(KeyEvent.VK_SHIFT)){
                range = Constants.RUN_SHOT_RANGE;
            }
            for (Integer lastKey: this.LAST_KEYS){
                switch (lastKey) {
                    case KeyEvent.VK_UP -> {
                        if (this.startingYPosition - (range / Constants.KICK_DIR_FIXATION) > Constants.GOAL_TOP){
                            kickTarget.move(kickTarget.x, kickTarget.y - (range / Constants.KICK_DIR_FIXATION));
                        }
                    }
                    case KeyEvent.VK_DOWN -> {
                        if (this.startingYPosition + (range / Constants.PRE_KICK) < Constants.GOAL_BOTTOM){
                            kickTarget.move(kickTarget.x, kickTarget.y + (range / Constants.KICK_DIR_FIXATION));
                        }
                    }
                    case KeyEvent.VK_LEFT -> kickTarget.move(kickTarget.x - range, kickTarget.y);
                }
            }
        }
        return kickTarget;
    }
    private boolean outOfBounds(Rectangle rectangle) {
        return rectangle.x <= MainScene.LEFT_BORDER_TOP.x || rectangle.x >= MainScene.RIGHT_BORDER_TOP.x
                || rectangle.y <= MainScene.TOP_BORDER.y || rectangle.y >= MainScene.BOTTOM_BORDER.y;
    }
    public synchronized boolean goalScored() {
        boolean goal = this.GOAL_SCORED.getState();
        if (goal){
            this.GOAL_SCORED.setState(false);
        }
        return goal;
    }
    private void checkForGoal(){
        this.GOAL_SCORED.setState(Utils.collision(MainScene.LEFT_GOAL, this));
    }
    public void move(){
        if (this.isTouched){
            if (!this.toShoot){
                Point currentPoint = this.getCollider().getLocation();
                Point nextLocation = this.MAIN_SCENE.getKeyboard().nextLocation(currentPoint);
                Rectangle moveCheck = new Rectangle(nextLocation.x, nextLocation.y, this.COLLIDER.getState().width, this.COLLIDER.getState().height);
                Rectangle playerLoc = this.PLAYER.getState().getCollider();
                switch (Keyboard.getLastFromKeyStroke()) {
                    case KeyEvent.VK_UP -> {
                        if ((playerLoc.y + playerLoc.height) - (moveCheck.y + moveCheck.height) < 0) {
                            moveCheck.setBounds(moveCheck.x, moveCheck.y - (Constants.PLAYER_HEIGHT + moveCheck.height), moveCheck.width, moveCheck.height);
                        }
                    }
                    case KeyEvent.VK_DOWN -> {
                        if ((playerLoc.y + playerLoc.height) - (moveCheck.y + moveCheck.height) > 0) {
                            moveCheck.setBounds(moveCheck.x, moveCheck.y + playerLoc.height, moveCheck.width, moveCheck.height);
                        }
                    }
                    case KeyEvent.VK_LEFT -> {
                        if ((playerLoc.x + playerLoc.width) - (moveCheck.x + moveCheck.width) < 0){
                            moveCheck.setBounds(moveCheck.x - (Constants.PLAYER_WIDTH + moveCheck.width), moveCheck.y, moveCheck.width, moveCheck.height);
                        }
                    }
                    case KeyEvent.VK_RIGHT -> {
                        if ((playerLoc.x + playerLoc.width) - (moveCheck.x + moveCheck.width) > 0){
                            moveCheck.setBounds(moveCheck.x + Constants.PLAYER_WIDTH + moveCheck.width, moveCheck.y, moveCheck.width, moveCheck.height);
                        }
                    }
                }
                if (!this.outOfBounds(moveCheck)){
                    this.startingXPosition = moveCheck.x;
                    this.startingYPosition = moveCheck.y;
                }else{
                    this.outOfBounds = true;
                }
                this.isTouched = false;
                this.BALL_MOVED.setState(true);
                this.PLAYER.getState().hasCollided();
            }
        }
    }
    public void update(){
        this.checkForGoal();
        if (this.GOAL_SCORED.getState()){
            this.stopBallMovement = true;
            ballRelocate();
        } else if (this.outOfBounds){
            this.stopBallMovement = true;
            ballRelocate();
            this.outOfBounds = false;
        }
        this.COLLIDER.getState().setBounds(this.startingXPosition, this.startingYPosition, this.SIZE, this.SIZE);
        this.stopBallMovement = false;
    }
    public void draw(Graphics2D graphics2D){
        graphics2D.setColor(Constants.BALL_BASE_COLOR);
        graphics2D.fillOval(this.COLLIDER.getState().x, this.COLLIDER.getState().y, this.COLLIDER.getState().width, this.COLLIDER.getState().height);
        graphics2D.setColor(Constants.BALL_MID_COLOR);
        graphics2D.fillOval(this.COLLIDER.getState().x + Constants.BALL_MID_X_FIX, this.COLLIDER.getState().y + Constants.BALL_MID_Y_FIX, (this.COLLIDER.getState().width / 4) * 3, (this.COLLIDER.getState().height / 4) * 3);
        graphics2D.setColor(Constants.BALL_DARK_COLOR);
        graphics2D.fillOval(this.COLLIDER.getState().x + Constants.BALL_DARK_X_FIX, this.COLLIDER.getState().y + Constants.BALL_DARK_Y_FIX, (this.COLLIDER.getState().width / 3), (this.COLLIDER.getState().height / 3));
    }
    @Override
    public void run() {
        while (this.MAIN_SCENE.isGameRunning()) {
            if (this.sleepPause){
                this.sleepPause();
                this.sleepPause = false;
            }else {
                Utils.sleep(20);
            }
            if (this.toShoot){
                this.toShoot = false;
                shoot(this.controlledShot);
            }else if (this.isTouched){
                move();
            }
        }
    }
    private void ballRelocate() {
        this.startingXPosition = Constants.BALL_START.x;
        this.startingYPosition = Constants.BALL_START.y;
    }
    public synchronized Rectangle getCollider() {
        return this.COLLIDER.getState();
    }
    public synchronized void setTouched(Player player) {
        this.PLAYER.setState(player);
        this.isTouched = true;
    }
    public void setControlledShot(ConcurrentLinkedQueue<Integer> lastKeysFromPlayer, long shotPower){
        this.shotPower = (Math.max(shotPower, Constants.MIN_SHOT_POWER));
        synchronized (this.LAST_KEYS){
            this.LAST_KEYS.clear();
            this.LAST_KEYS.addAll(lastKeysFromPlayer);
        }
        this.controlledShot = true;
        this.toShoot = true;
    }
    public boolean isTouched(){
        return isTouched;
    }
    public synchronized void setToShoot(){
        toShoot = true;
    }
    public synchronized boolean thatsMe(Player player) {
        return this.PLAYER.getState().isSame(player);
    }
    public boolean ballMoved() {
        return this.BALL_MOVED.getState();
    }
    public void resetBallMoved() {
        this.BALL_MOVED.setState(false);
    }
    public void sleepPause() {
        Utils.sleep(200);
    }
    public void locationBackUp(){
        this.LAST_LOCATION.setBounds(this.startingXPosition, this.startingYPosition, this.SIZE, this.SIZE);
    }
    public void locationLoad(){
        this.startingXPosition = this.LAST_LOCATION.x;
        this.startingYPosition = this.LAST_LOCATION.y;
        update();
    }
    public void sleepPauseSet() {
        this.sleepPause = true;
    }
}
