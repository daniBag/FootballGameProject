import javax.swing.*;
import java.awt.*;

public class Player extends JComponent implements Runnable, CanCollide{
    private final boolean FIRST;
    private int xPosition;
    private int yPosition;
    private Rectangle location;
    private ImageIcon body;
    private final Keyboard CONTROLLER;
    private final int ID;
    private final MainScene MAIN_SCENE;
    private final SynchronizedState<Boolean> GOAL_SCORED;
    private boolean sleepPause;
    private Rectangle lastLoc;
    private boolean moving;
    private int imageSwitch;
    private boolean runRFoot;
    private boolean toShoot;
    private long shotPower;

    public Player(boolean first, int id, int xPos, int yPosition, MainScene mainScene, boolean isControlled){
        this.shotPower = 0;
        this.CONTROLLER = (isControlled? new Keyboard(this) : null );
        this.FIRST = first;
        this.toShoot = false;
        this.body = (this.FIRST? Constants.FIRST_PLAYER.get(Constants.STAND): Constants.SECOND_PLAYER.get(Constants.STAND));
        this.sleepPause = false;
        this.moving = false;
        this.runRFoot = false;
        this.ID = id;
        this.xPosition = xPos;
        this.yPosition = yPosition;
        this.MAIN_SCENE = mainScene;
        this.GOAL_SCORED = new SynchronizedState<>(false);
       this.startingLocationSet();
    }
    public void setToShoot(long pressedDur){
        this.shotPower = pressedDur / 200;
        this.toShoot = true;
    }

    private void startingLocationSet() {
        this.location = new Rectangle(this.xPosition, this.yPosition, Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT);
        this.setBounds(this.location);
    }

    public void move(Point nextLocation){
        this.moving = true;
        while(this.xPosition != nextLocation.x || this.yPosition != nextLocation.y){
            Rectangle nextMove = new Rectangle(Utils.posParManagement(this.xPosition, nextLocation.x),
                    Utils.posParManagement(this.yPosition, nextLocation.y), Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT);
            if (this.MAIN_SCENE.collisionCheck(this, nextMove)){
                break;
            }
            Utils.sleep(5);
            this.xPosition = Utils.posParManagement(this.xPosition, nextLocation.x);
            this.yPosition = Utils.posParManagement(this.yPosition, nextLocation.y);
        }
        this.moving = false;
    }

    private Point getMoveTarget(){
        return this.getController().nextLocation(new Point(this.xPosition, this.yPosition));
    }

    public void shoot(){
        if (this.MAIN_SCENE.getBall().isTouched() && this.MAIN_SCENE.getBall().thatsMe(this)){
            this.MAIN_SCENE.getBall().setControlledShot(this.CONTROLLER.getLastKeyStroke(), this.shotPower);
        }
        this.toShoot = false;
    }

    public void hasCollided(){
        if (Utils.collision(this.getCollider(), this.MAIN_SCENE.getBall())){
            this.MAIN_SCENE.getBall().setTouched(this);
        }
    }

    public void drawPlayer(Graphics2D graphics2D){
        graphics2D.drawImage(this.body.getImage(), this.xPosition - 28, this.yPosition - 35, this.body.getIconWidth(), this.body.getIconHeight(), this);
    }
    public void update(){
        if (this.GOAL_SCORED.getState()){
            this.startingLocationSet();
            this.GOAL_SCORED.setState(false);
        }
        if (this.moving){
            this.imageSwitch++;
            if (imageSwitch % 4 == 0) {
                this.runImageSwitch();
            }
        }else {
            this.imageSwitch = 0;
        }
        this.location.setBounds(this.xPosition, this.yPosition, Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT);
        this.setBounds(this.location);
        hasCollided();
    }

    private void runImageSwitch() {
        if (this.runRFoot){
            this.body = (this.FIRST? Constants.FIRST_PLAYER.get(Constants.RUN_R): Constants.SECOND_PLAYER.get(Constants.RUN_R));
        }else {
            this.body = (this.FIRST? Constants.FIRST_PLAYER.get(Constants.RUN_L): Constants.SECOND_PLAYER.get(Constants.RUN_L));
        }
        this.runRFoot = ! this.runRFoot;
    }

    @Override
    public void run() {
        while (this.MAIN_SCENE.isGameRunning()) {
            if (this.sleepPause){
                this.sleepPause();
                this.resetSleepPause();
            }else {
                Utils.sleep(Constants.PLAYER_THREAD_SLEEP);
            }
            move(this.getMoveTarget());
            if (this.toShoot){
                shoot();
                this.CONTROLLER.lastKeysReset();
            }
        }
    }
    public boolean isSame(Player player) {
        return this.ID == player.ID;
    }

    protected MainScene getMainScene() {
        return this.MAIN_SCENE;
    }

    public void goalEvent() {
        this.GOAL_SCORED.setState(true);
    }

    protected void sleepPause() {
        Utils.sleep(Constants.PAUSE_SLEEP);
    }
    public void locationBackUp(){
        this.lastLoc.setBounds(this.location);
    }
    public void locationLoad(){
        this.xPosition = this.lastLoc.x;
        this.yPosition = this.lastLoc.y;
        update();
    }
    protected boolean getSleepPause() {
        return this.sleepPause;
    }

    public void sleepPauseSet() {
        this.sleepPause = true;
    }

    protected void resetSleepPause() {
        this.sleepPause = false;
    }

    public Keyboard getController() {
        return this.CONTROLLER;
    }

    @Override
    public Rectangle getCollider() {
        return new Rectangle(this.xPosition - Constants.TOUCH_RANGE, this.yPosition - Constants.TOUCH_RANGE,
                Constants.PLAYER_WIDTH + Constants.TOUCH_RANGE * 2, Constants.PLAYER_HEIGHT + Constants.TOUCH_RANGE * 2);
    }
}
