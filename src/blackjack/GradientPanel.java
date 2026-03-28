package blackjack;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

/**
 * A {@link JPanel} that fills itself with a vertical two-colour gradient.
 * Used for the header bar and any other surface that needs a simple gradient
 * background without custom painting code at the call site.
 */
public class GradientPanel extends JPanel {

    private final Color top;
    private final Color bottom;

    /**
     * @param top    colour at the top edge
     * @param bottom colour at the bottom edge
     */
    public GradientPanel(Color top, Color bottom) {
        this.top    = top;
        this.bottom = bottom;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setPaint(new GradientPaint(0, 0, top, 0, getHeight(), bottom));
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
    }
}
