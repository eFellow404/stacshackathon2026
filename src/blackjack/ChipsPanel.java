package blackjack;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;

/**
 * The right-hand chips and balance panel.
 *
 * <p>Displays a decorative stack of coloured chip icons with their
 * denominations and a running balance figure at the bottom.
 * The balance label is exposed via {@link #setBalance(String)} so game
 * logic can update it without coupling to layout details.
 */
public class ChipsPanel extends JPanel {

    private final JLabel balanceLabel;

    public ChipsPanel() {
        super();
        setBackground(Theme.PANEL_BG);
        setBorder(new MatteBorder(0, 1, 0, 0, Theme.GOLD_DIM));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        add(Box.createVerticalStrut(20));
        add(buildSectionLabel());
        add(Box.createVerticalStrut(24));
        addChipRows();
        add(Box.createVerticalGlue());

        JLabel balCaption = buildCaption("BALANCE");
        add(balCaption);

        balanceLabel = buildBalanceLabel("$2,500");
        add(balanceLabel);

        add(Box.createVerticalStrut(24));
    }

    // ── Public API ────────────────────────────────────────────────────────────

    /**
     * Updates the balance figure displayed at the bottom of the panel.
     *
     * @param formatted already-formatted string, e.g. {@code "$3,750"}
     */
    public void setBalance(String formatted) {
        balanceLabel.setText(formatted);
    }

    // ── Private builders ──────────────────────────────────────────────────────

    private JLabel buildSectionLabel() {
        JLabel lbl = new JLabel("C H I P S", SwingConstants.CENTER);
        lbl.setFont(Theme.serif(11f).deriveFont(Font.BOLD));
        lbl.setForeground(Theme.GOLD_DIM);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        return lbl;
    }

    private JLabel buildCaption(String text) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setFont(Theme.serif(10f));
        lbl.setForeground(Theme.TEXT_MUTED);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        return lbl;
    }

    private JLabel buildBalanceLabel(String initial) {
        JLabel lbl = new JLabel(initial, SwingConstants.CENTER);
        lbl.setFont(Theme.serif(26f).deriveFont(Font.BOLD));
        lbl.setForeground(Theme.GOLD);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        return lbl;
    }

    private void addChipRows() {
        int[]   amounts = { 500,        100,             25,                     5                    };
        Color[] colors  = { Theme.CHIP_RED, Theme.CHIP_BLUE, new Color(0x27AE60), new Color(0x8E44AD) };

        for (int i = 0; i < amounts.length; i++) {
            add(buildChipRow(amounts[i], colors[i]));
            add(Box.createVerticalStrut(14));
        }
    }

    private JPanel buildChipRow(int amount, Color color) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        row.setOpaque(false);
        row.add(new ChipIcon(amount, color));

        JLabel lbl = new JLabel("$" + String.format("%,d", amount));
        lbl.setFont(Theme.serif(13f));
        lbl.setForeground(Theme.TEXT_CREAM);
        row.add(lbl);

        return row;
    }

    // ── Inner chip icon ───────────────────────────────────────────────────────

    /**
     * A small self-painting circular chip icon with a denomination label.
     */
    private static class ChipIcon extends JPanel {

        private final int   amount;
        private final Color color;

        ChipIcon(int amount, Color color) {
            this.amount = amount;
            this.color  = color;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            int s = Math.min(getWidth(), getHeight());

            // Drop shadow
            g2.setColor(new Color(0, 0, 0, 80));
            g2.fillOval(2, 3, s - 2, s - 2);

            // Chip fill
            g2.setColor(color);
            g2.fillOval(0, 0, s, s);

            // Highlight ring
            g2.setColor(color.brighter());
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawOval(3, 3, s - 6, s - 6);

            // Amount text
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Georgia", Font.BOLD, 9));
            FontMetrics fm = g2.getFontMetrics();
            String t = "$" + amount;
            g2.drawString(t,
                    (s - fm.stringWidth(t)) / 2,
                    (s + fm.getAscent() - fm.getDescent()) / 2);

            g2.dispose();
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(40, 40);
        }
    }
}
