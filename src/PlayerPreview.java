import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PlayerPreview extends JButton {
    private static boolean blue;

    public PlayerPreview(JPanel panel){
        blue = true;
        this.setIcon(Constants.FIRST_PLAYER.get(Constants.STAND));
        this.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                blue = !blue;
                update();
                repaint();
            }
        });
    }
    public boolean getIfBlue(){
        return blue;
    }
    private void update(){
        if (blue){
            this.setIcon(Constants.FIRST_PLAYER.get(Constants.STAND));
        }else {
            this.setIcon(Constants.SECOND_PLAYER.get(Constants.STAND));
        }
    }
}