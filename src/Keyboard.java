import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Keyboard implements KeyListener {
    private final Set<Integer> PRESSED_KEYS;
    private final ConcurrentLinkedQueue<Integer> LAST_KEY_STROKE;
    private static int lastKey;
    public static SynchronizedState<Boolean> shotManage = new SynchronizedState<>(false);
    private boolean pause;
    private final Player PLAYER;
    private long shotPower;
    private boolean powerSet;
    public Keyboard(Player player){
        this.PLAYER = player;
        this.shotPower = 0;
        this.powerSet = false;
        this.LAST_KEY_STROKE = new ConcurrentLinkedQueue<>();
        this.PRESSED_KEYS = new HashSet<>();
    }
    public static int getLastFromKeyStroke() {
        return lastKey;
    }
    @Override
    public void keyTyped(KeyEvent e) {

    }
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE){
            if (!this.powerSet){
                this.shotPower = System.currentTimeMillis();
                this.powerSet = true;
            }
        }
        if (!PRESSED_KEYS.contains(e.getKeyCode())){
            if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN ||
                    e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT
                    || e.getKeyCode() == KeyEvent.VK_SHIFT){
                PRESSED_KEYS.add(e.getKeyCode());
                if(e.getKeyCode() != KeyEvent.VK_SPACE){
                    addToLastKeys(e);
                    lastKey = e.getKeyCode();
                }
            }
        }
        if (e.getKeyCode() == KeyEvent.VK_P){
            this.pause = !this.pause;
        }
    }
    public boolean isPause(){
        return this.pause;
    }
    public void lastKeysReset(){
        this.LAST_KEY_STROKE.clear();
    }
    private void addToLastKeys(KeyEvent e){
        if (e.getKeyCode() != KeyEvent.VK_RIGHT){
            ConcurrentLinkedQueue<Integer> lastKeys = this.getLastKeyStroke();
            if (!lastKeys.contains(e.getKeyCode())) {
                if (lastKeys.size() < Constants.LAST_KEYS_MAX) {
                    lastKeys.add(e.getKeyCode());
                } else {
                    lastKeys.poll();
                    lastKeys.add(e.getKeyCode());
                }
            }
        }
    }
    public Point nextLocation(Point movePoint){
        List<Integer> pressedKeys = List.copyOf(this.PRESSED_KEYS);
        int move = Constants.MOVE_STEP;
        if (!pressedKeys.isEmpty()){
            for (Integer integer: pressedKeys){
                switch (integer){
                    case KeyEvent.VK_SHIFT -> {
                        move = Constants.RUN_STEP;
                    }
                    case KeyEvent.VK_UP -> {
                        movePoint.setLocation(movePoint.x, movePoint.y - move);
                    }
                    case KeyEvent.VK_DOWN -> {
                        movePoint.setLocation(movePoint.x, movePoint.y + move);
                    }
                    case KeyEvent.VK_LEFT -> {
                        movePoint.setLocation(movePoint.x - move, movePoint.y);
                    }
                    case KeyEvent.VK_RIGHT -> {
                        movePoint.setLocation(movePoint.x + move, movePoint.y);
                    }
                }
            }
        }
        return movePoint;
    }
    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE){
            this.shotPower = System.currentTimeMillis() - this.shotPower;
            this.PLAYER.setToShoot(this.shotPower);
            this.powerSet = false;
        }else{
            this.PRESSED_KEYS.remove(e.getKeyCode());
        }
    }
    public synchronized ConcurrentLinkedQueue<Integer> getLastKeyStroke(){
        return this.LAST_KEY_STROKE;
    }
}
