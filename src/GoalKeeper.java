import java.awt.*;
import java.util.Random;

public class GoalKeeper extends Player{
    private int difficulty;

    public GoalKeeper(boolean first, int id, int xPos, int yPos, MainScene mainScene){
        super(first, id, xPos, yPos, mainScene, false);
        this.difficulty = mainScene.getDifficulty();
    }

    @Override
    public void run() {
        int sleepDur = 200;
        while (this.getMainScene().isGameRunning()){
            if (this.getSleepPause()){
                this.sleepPause();
                this.resetSleepPause();
            }else {
                Utils.sleep(sleepDur);
            }
            if (this.getMainScene().getBall().ballMoved() || Utils.collision(MainScene.LEFT_BOX, this.getMainScene().getBall())){
                sleepDur = 200;
                move(this.getMoveTarget());
            }else {
                sleepDur = 450;
                move(this.getMoveTarget());
            }

            hasCollided();
            shoot();
        }
    }
    @Override
    public void move(Point nextLocation) {
        super.move(nextLocation);
        this.getMainScene().getBall().resetBallMoved();
    }
    public void difficultyHarder() {
        this.difficulty = this.getMainScene().getDifficulty();
    }

    private Point getMoveTarget(){
        Point currentLocation = new Point(this.getX(),this.getY());
        return this.nextLocation(currentLocation,true);
    }

    public Point nextLocation(Point currentLocation, boolean forPlayer) {
        Random random = new Random();
        Point target = currentLocation;
        if (forPlayer){
            do{
                Rectangle ballLoc = this.getMainScene().getBall().getCollider();
                int xRandMin = currentLocation.x - this.difficulty / 2;
                int xRandMax = currentLocation.x + this.difficulty / 2;
                if (this.getMainScene().ballInLeftBox()){
                    xRandMin = MainScene.LEFT_BOX.x + Constants.LINE_WIDTH;
                    xRandMax = ballLoc.x + this.difficulty / 2;
                }
                target.setLocation(random.nextInt(xRandMin, xRandMax),
                        Math.min(random.nextInt(ballLoc.y - this.difficulty, ballLoc.y + this.difficulty), MainScene.BOTTOM_BORDER.y - Constants.PLAYER_HEIGHT));
            }while (target.x > MainScene.LEFT_BOX.x + MainScene.LEFT_BOX.width + (Constants.LINE_WIDTH * 2) || target.x < MainScene.LEFT_BOX.x ||
                    target.y > Constants.SCREEN_HEIGHT || target.y < 0);
        }else {
            target.setLocation(random.nextInt((Constants.SCREEN_WIDTH / 2) - this.difficulty, (Constants.SCREEN_WIDTH / 2) + this.difficulty),
                    random.nextInt((Constants.SCREEN_HEIGHT / 2) - this.difficulty, (Constants.SCREEN_HEIGHT / 2) + this.difficulty));
        }
        return target;
    }
    @Override
    public void shoot() {
        if (this.getMainScene().getBall().isTouched() && this.getMainScene().getBall().thatsMe(this)){
            this.getMainScene().getBall().setToShoot();
        }
    }
    @Override
    public void drawPlayer(Graphics2D graphics2D) {
        super.drawPlayer(graphics2D);
    }
}
