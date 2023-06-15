import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ScreenOverlay extends JPanel {
    private final boolean START;
    private JLabel label;
    private JButton startButton;
    private final Window WINDOW;
    private PlayerPreview customizePlayer;

    public ScreenOverlay(boolean start, Window window){
        this.WINDOW = window;
        this.START = start;
        this.setLayout(new BorderLayout());
        if (this.START){
            this.startPanelSetUp();
        }else {
            this.pausePanelSetup();
        }
        this.setBackground(new Color(0, 0, 0, 97));
    }

    private void startPanelSetUp() {
        this.label = new JLabel(new ImageIcon("res/instructions.png"));
        this.add(this.label, BorderLayout.CENTER);
        this.startButton = new JButton(new ImageIcon("res/Start_Button.png"));
        this.startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                WINDOW.setToStart();
            }
        });
        this.startButton.setVisible(true);
        this.add(this.startButton, BorderLayout.SOUTH);
        this.customizePlayer = new PlayerPreview(this);
        this.customizePlayer.setText("click to change color!");
        this.customizePlayer.setFont(this.customizePlayer.getFont().deriveFont(Constants.START_SCREEN_FONT));
        this.customizePlayer.setBackground(Constants.PLAYER_CUSTOMIZE_COLOR);
        this.customizePlayer.setVisible(true);
        this.add(this.customizePlayer, BorderLayout.NORTH);
    }
    public boolean playerBlue(){
        return this.customizePlayer.getIfBlue();
    }
    private void pausePanelSetup() {
        this.label = new JLabel(Constants.PAUSE_ICON);
        this.add(this.label);
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Constants.SCREEN_OVERLAY_BACKGROUND);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        Graphics2D graphics2D = (Graphics2D) g;
        if (this.START){
            this.startButton.getIcon().paintIcon(this.startButton, graphics2D, this.startButton.getX(), this.startButton.getY());
            this.customizePlayer.getIcon().paintIcon(this.customizePlayer, graphics2D, this.customizePlayer.getX(), this.customizePlayer.getY());
        }else {
            this.label.getIcon().paintIcon(this.label, graphics2D, Constants.CENTER.x - Constants.PAUSE_ICON_FIX, Constants.CENTER.y - Constants.PAUSE_ICON_FIX);
        }
    }
}
