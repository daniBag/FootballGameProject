import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameEndScreen extends JPanel {
    private JLabel gameEnd;
    private ImageIcon imageIcon;
    public GameEndScreen(boolean won, Window window){
        this.gameEnd = new JLabel();
        this.gameEnd.setHorizontalAlignment(SwingConstants.CENTER);
        this.gameEnd.setFont(gameEnd.getFont().deriveFont(72.0f));
        this.gameEnd.setLocation(new Point(Constants.CENTER.x - this.gameEnd.getWidth() / 2, Constants.CENTER.y - this.gameEnd.getHeight() / 2));
        if (won) {
            this.gameEnd.setText("YOU WIN!!!");
            this.imageIcon = new ImageIcon("res/win.png");
        } else {
            this.gameEnd.setText("YOU LOST!!");
            this.imageIcon = new ImageIcon("res/lose.png");
        }
        this.gameEnd.setVisible(true);
        this.add(this.gameEnd);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(new Color(255, 255, 255, 75));
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        Graphics2D graphics2D = (Graphics2D) g;
        graphics2D.drawImage(this.imageIcon.getImage(), Constants.CENTER.x - (Constants.PAUSE_ICON_FIX * 2), Constants.CENTER.y - (Constants.PAUSE_ICON_FIX * 2), this);
    }
}
