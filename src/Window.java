import javax.swing.*;
import java.awt.*;
public class Window extends JFrame {
    private JPanel mainPanel;
    private ScreenOverlay pauseScreen;
    private boolean startGame;
    public Window(){
        this.initializeGame();
    }
    public void initializeGame() {
        this.startGame = false;
        this.setSize(Constants.SCREEN_WIDTH + Constants.WINDOW_FIX_WIDTH,Constants.SCREEN_HEIGHT + Constants.WINDOW_FIX_HEIGHT);
        this.setLayout(null);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setPauseScreen();
        this.startScreen();
    }
    private void screenRefresh() {
        this.setVisible(false);
        this.setVisible(true);
    }
    public void setToStart(){
        this.startGame = true;
    }
    private void startScreen(){
        ScreenOverlay startScreen = new ScreenOverlay(true, this);
        startScreen.setBounds(0, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
        startScreen.setVisible(true);
        this.add(startScreen);
        this.setVisible(true);
        while (!this.startGame){
            Utils.sleep(100);
        }
        this.gameStart(startScreen);
    }
    private void setPauseScreen(){
        this.pauseScreen = new ScreenOverlay(false, this);
        this.pauseScreen.setBounds(0, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
        this.pauseScreen.setVisible(false);
        this.add(this.pauseScreen);
    }
    public void pause(){
        this.pauseScreen.setVisible(true);
        while (((MainScene)this.mainPanel).getKeyboard().isPause()){
            Utils.sleep(150);
        }
    }
    public void pauseScreenRepaint(Graphics g){
        this.pauseScreen.paintComponent(g);
    }
    public void resume(){
        this.pauseScreen.setVisible(false);
    }
    public void gameStart(ScreenOverlay startScreen){
        boolean playerBlue = startScreen.playerBlue();
        this.remove(startScreen);
        MainScene mainScene = new MainScene(this, playerBlue);
        this.mainPanel = mainScene;
        Thread mainSceneThread = new Thread(mainScene);
        this.add(this.mainPanel);
        mainSceneThread.start();
        this.screenRefresh();
    }
    public void gameOver(boolean won) {
        this.remove(this.mainPanel);
        this.mainPanel = new GameEndScreen(won, this);
        this.mainPanel.setBounds(0, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
        this.mainPanel.setVisible(true);
        this.add(this.mainPanel);
        this.screenRefresh();
    }
}
