

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;

/**
 * A {@link JPanel} that paints itself with a diagonal felt-like gradient and
 * a subtle dot-pattern texture.  Used for the dealer and player table sections.
 */
public class FeltPanel extends JPanel {

    public FeltPanel() {
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setPaint(new GradientPaint(
                0, 0, Theme.FELT_MID,
                getWidth(), getHeight(), Theme.FELT_DARK));
        g2.fillRect(0, 0, getWidth(), getHeight());

        // Subtle noise-like dot pattern for fabric texture
        g2.setColor(new Color(255, 255, 255, 6));
        for (int x = 0; x < getWidth(); x += 4)
            for (int y = 0; y < getHeight(); y += 4)
                if ((x + y) % 8 == 0)
                    g2.fillRect(x, y, 1, 1);

        g2.dispose();
    }
}
