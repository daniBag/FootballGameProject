import java.awt.*;

public class Utils {
    public static void sleep(int milli){
        try {
            Thread.sleep(milli);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public static int posParManagement(int curr, int nextLocation){
        if (curr > nextLocation){
            if (curr - Constants.MOVE_STEP > nextLocation){
                curr -= Constants.MOVE_STEP;
            }else {
                curr = nextLocation;
            }
        } else if (curr < nextLocation){
            if (curr + Constants.MOVE_STEP < nextLocation){
                curr += Constants.MOVE_STEP;
            }else {
                curr = nextLocation;
            }
        }
        return curr;
    }
    public static boolean collision(Rectangle rectangle, CanCollide toCheck){
        return rectangle.intersects(toCheck.getCollider());
    }
}
