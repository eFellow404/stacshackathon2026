

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class CardTray extends JPanel {

    private static final int MIN_HEIGHT_PAD = 28;

    private final boolean greenGlow;
    // Keep an ordered list of labels to preserve insertion order and control z-ordering
    private final java.util.List<JLabel> cardLabels = new java.util.ArrayList<>();

    public CardTray(boolean greenGlow) {
        // Use absolute positioning so card JLabels can overlap like a real deck
        super(null);
        this.greenGlow = greenGlow;
        setOpaque(false);
        // Re-layout when this panel is resized so the stack remains centered
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                layoutCards();
            }
        });
    }

    public void addCard(Image cardImage) {
        JLabel label = new JLabel(new ImageIcon(cardImage));
        label.setBorder(BorderFactory.createCompoundBorder(
                new DropShadowBorder(),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)));

        // Ensure label has fixed size (card size) so we can position it absolutely
        label.setSize(GUICards.CARD_WIDTH, GUICards.CARD_HEIGHT);
        add(label);
        cardLabels.add(label);

        // Recompute positions for all cards so they overlap compactly
        layoutCards();
    }

    public void removeCards() {
        // remove Swing components and clear our bookkeeping list
        for (JLabel l : cardLabels) {
            remove(l);
        }
        cardLabels.clear();

        // reset preferred size after clearing
        setPreferredSize(new Dimension(0, getPreferredSize().height));
        revalidate();
        repaint();
    }

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
        // Base preferred size is driven by number of cards and overlap amount
        int count = cardLabels.size();
        int overlap = getOverlapX();
        int width = 0;
        if (count > 0) {
            width = overlap * (count - 1) + GUICards.CARD_WIDTH + 24; // padding
        }
        int height = GUICards.CARD_HEIGHT + MIN_HEIGHT_PAD;
        // ensure a sensible minimum width so the tray is visible when empty
        return new Dimension(Math.max(width, GUICards.CARD_WIDTH + 24), height);
    }

    // Amount of horizontal shift between successive cards (smaller = more overlap)
    private int getOverlapX() {
        // Make overlap a smaller fraction of card width so cards sit more on top of each other
        // Use a small fixed minimum to avoid completely covering cards
        return Math.max(8, GUICards.CARD_WIDTH / 8);
    }

    // Position all child components (card labels) with overlap and center vertically
    private void layoutCards() {
        int count = cardLabels.size();
        int overlap = getOverlapX();

        // inner width of stacked cards (without outer padding)
        int innerWidth = 0;
        if (count > 0) {
            innerWidth = overlap * (count - 1) + GUICards.CARD_WIDTH;
        }

        int prefWidth = innerWidth + 24; // include some padding
        int prefHeight = GUICards.CARD_HEIGHT + MIN_HEIGHT_PAD;
        setPreferredSize(new Dimension(prefWidth, prefHeight));

        // center the stack inside this component's current width
        int containerWidth = getWidth() > 0 ? getWidth() : prefWidth;
        int startX = Math.max(12, (containerWidth - innerWidth) / 2);
        int y = (prefHeight - GUICards.CARD_HEIGHT) / 2;

        // Position cards left-to-right based on insertion order (older -> left)
        for (int i = 0; i < count; i++) {
            JLabel c = cardLabels.get(i);
            int x = startX + i * overlap;
            c.setBounds(x, y, GUICards.CARD_WIDTH, GUICards.CARD_HEIGHT);
        }

        // Ensure newest card (last in cardLabels) is on top by setting z-order
        // setComponentZOrder(comp, 0) makes that component be painted last (on top)
        for (int i = 0; i < count; i++) {
            JLabel c = cardLabels.get(i);
            // we want the last element to have z-order 0
            int z = count - 1 - i;
            setComponentZOrder(c, z);
        }

        revalidate();
        repaint();
    }

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
