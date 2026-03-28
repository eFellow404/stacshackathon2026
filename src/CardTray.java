

import javax.swing.*;
import java.awt.*;

public class CardTray extends JPanel {

    /** Minimum visible height keeps the tray from collapsing when empty. */
    private static final int MIN_HEIGHT_PAD = 28;

    private final boolean greenGlow;

    /**
     * @param greenGlow {@code true} for the player tray (green accent),
     *                  {@code false} for the dealer tray (gold accent)
     */
    public CardTray(boolean greenGlow) {
        super(new FlowLayout(FlowLayout.CENTER, 12, 10));
        this.greenGlow = greenGlow;
        setOpaque(false);
    }

    // ── Public API ────────────────────────────────────────────────────────────

    /**
     * Appends a card image to the tray, wrapped in a drop-shadow border.
     *
     * @param cardImage rendered card {@link Image} from {@code blackjack.GUICards}
     */
    public void addCard(Image cardImage) {
        JLabel label = new JLabel(new ImageIcon(cardImage));
        label.setBorder(BorderFactory.createCompoundBorder(
                new DropShadowBorder(),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)));
        add(label);
        revalidate();
        repaint();
    }

    // ── Painting ──────────────────────────────────────────────────────────────

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        // Background fill
        Color base = greenGlow ? new Color(0x103820) : new Color(0x0C2818);
        g2.setColor(base);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);

        // Border glow
        Color glow = greenGlow
                ? new Color(0x20, 0xC0, 0x60, 60)
                : new Color(0xD4, 0xAF, 0x37, 50);
        g2.setColor(glow);
        g2.setStroke(new java.awt.BasicStroke(2f));
        g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 16, 16);

        g2.dispose();
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        int minHeight = GUICards.CARD_HEIGHT + MIN_HEIGHT_PAD;
        d.height = Math.max(d.height, minHeight);
        return d;
    }

    // ── Factory / scroll wrapper ──────────────────────────────────────────────

    /**
     * Wraps this tray in a {@link JScrollPane} configured for horizontal-only
     * scrolling with a transparent viewport (matching the felt background).
     *
     * @return a scroll pane ready to drop into a {@link BorderLayout} centre slot
     */
    public JScrollPane inScrollPane() {
        JScrollPane sp = new JScrollPane(this,
                JScrollPane.VERTICAL_SCROLLBAR_NEVER,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        sp.setBorder(null);
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        sp.getHorizontalScrollBar().setBackground(Theme.FELT_DARK);
        return sp;
    }
}
