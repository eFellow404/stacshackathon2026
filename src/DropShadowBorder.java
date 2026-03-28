
import javax.swing.border.AbstractBorder;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;

/**
 * A soft drop-shadow {@link javax.swing.border.Border} applied to each card
 * label so the cards appear to float above the felt surface.
 *
 * <p>The shadow is built from several semi-transparent filled rounds,
 * growing progressively more opaque toward the outer edge to simulate
 * a diffuse light source from above.
 */
public class DropShadowBorder extends AbstractBorder {

    /** Pixel depth of the shadow on the right and bottom edges. */
    private static final int SHADOW_SIZE = 4;

    @Override
    public void paintBorder(Component c, Graphics g,
                            int x, int y, int w, int h) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        for (int i = SHADOW_SIZE; i > 0; i--) {
            int alpha = 20 + (SHADOW_SIZE - i) * 12;
            g2.setColor(new Color(0, 0, 0, alpha));
            g2.fillRoundRect(x + i, y + i, w - i, h - i, 4, 4);
        }
        g2.dispose();
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(0, 0, SHADOW_SIZE, SHADOW_SIZE);
    }

    @Override
    public Insets getBorderInsets(Component c, Insets insets) {
        insets.set(0, 0, SHADOW_SIZE, SHADOW_SIZE);
        return insets;
    }
}
