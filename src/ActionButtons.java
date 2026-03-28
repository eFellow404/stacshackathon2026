
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * The row of action buttons (HIT, STAND, DOUBLE DOWN, SURRENDER) displayed
 * at the bottom of the player section.
 *
 * <p>Construct with an {@link ActionListener} for each action so the calling
 * code (i.e. {@link GUI}) can wire up game logic without touching button
 * layout concerns.
 */
public class ActionButtons extends JPanel {

    /**
     * @param onHit       action fired when the player clicks HIT
     * @param onStand     action fired when the player clicks STAND
     * @param onDouble    action fired when the player clicks DOUBLE DOWN
     * @param onSurrender action fired when the player clicks SURRENDER
     */
    public ActionButtons(ActionListener onHit,
                         ActionListener onStand,
                         ActionListener onDouble,
                         ActionListener onSurrender) {
        super(new FlowLayout(FlowLayout.CENTER, 14, 12));
        setOpaque(false);

        add(buildActionButton("HIT",        Theme.BTN_HIT,       onHit));
        add(buildActionButton("STAND",      Theme.BTN_STAND,     onStand));
        add(buildActionButton("DOUBLE DOWN",Theme.BTN_DOUBLE,    onDouble));
        add(buildActionButton("SURRENDER",  Theme.BTN_SURRENDER, onSurrender));
    }

    // ── Private builder ───────────────────────────────────────────────────────

    /**
     * Creates a bold, coloured pill-shaped action button.
     *
     * @param text   button label
     * @param accent base fill colour; brightened on hover, darkened at rest
     * @param al     click listener
     */
    private JButton buildActionButton(String text, Color accent, ActionListener al) {
        JButton btn = new JButton(text) {
            private boolean hovered = false;

            {
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
                    public void mouseExited (MouseEvent e) { hovered = false; repaint(); }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                Color fill = hovered ? accent.brighter() : accent.darker();

                // Drop shadow
                g2.setColor(new Color(0, 0, 0, 100));
                g2.fillRoundRect(2, 4, getWidth() - 2, getHeight() - 4, 12, 12);

                // Button fill
                g2.setColor(fill);
                g2.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 4, 12, 12);

                // Top shine
                g2.setColor(new Color(255, 255, 255, 30));
                g2.fillRoundRect(0, 0, getWidth() - 2, (getHeight() - 4) / 2, 12, 12);

                // Label
                g2.setColor(Color.WHITE);
                g2.setFont(Theme.serif(11f).deriveFont(Font.BOLD));
                FontMetrics fm = g2.getFontMetrics();
                int tx = (getWidth()  - fm.stringWidth(getText())) / 2;
                int ty = (getHeight() - fm.getHeight()) / 2 + fm.getAscent() - 2;
                g2.drawString(getText(), tx, ty);

                g2.dispose();
            }

            @Override protected void paintBorder(Graphics g) {}
        };

        btn.setPreferredSize(new Dimension(130, 42));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(al);
        return btn;
    }
}
