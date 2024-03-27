package Gui;
import org.dreambot.api.utilities.Logger;
import java.util.Map;
import javax.swing.*;
import java.awt.*;

public class GUI {
    private final int toolbarHeight = 30; // Define toolbar height


    public void paint(Graphics g){
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(0, 0, 400, toolbarHeight); // Fill the top section

        // Draw button borders (replace with actual buttons if needed)
        int buttonWidth = 100;
        int buttonSpacing = 10;
        for (int i = 0; i < 2; i++) { // Draw 2 buttons
            int x = buttonSpacing + (i * (buttonWidth + buttonSpacing));
            g.drawRect(x, 5, buttonWidth, toolbarHeight - 10); // Draw button rectangle
        }

        // Draw text labels (optional)
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 12));
        for (int i = 0; i < 2; i++) {
            int x = buttonSpacing + (i * (buttonWidth + buttonSpacing)) + buttonWidth / 2 - 20; // Center text
            g.drawString("Button " + (i + 1), x, toolbarHeight - 5); // Draw button text
        }
    }




}