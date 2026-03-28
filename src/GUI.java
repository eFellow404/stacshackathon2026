import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

public class GUI extends JFrame {

    // ── Palette ──────────────────────────────────────────────────────────────
    private static final Color FELT_DARK    = new Color(0x0D2B1A);
    private static final Color FELT_MID     = new Color(0x14402A);
    private static final Color FELT_LIGHT   = new Color(0x1A5235);
    private static final Color GOLD         = new Color(0xD4AF37);
    private static final Color GOLD_LIGHT   = new Color(0xF0D060);
    private static final Color GOLD_DIM     = new Color(0x8B7020);
    private static final Color PANEL_BG     = new Color(0x0A1A10);
    private static final Color LOG_BG       = new Color(0x08120C);
    private static final Color TEXT_CREAM   = new Color(0xF5EED8);
    private static final Color TEXT_MUTED   = new Color(0xA89060);
    private static final Color CHIP_RED     = new Color(0xC0392B);
    private static final Color CHIP_BLUE    = new Color(0x2471A3);

    // ── State ────────────────────────────────────────────────────────────────
    private JTextArea          logArea;
    private JPanel             cardArea;
    private JPanel             dealerCardArea;
    private GUICards           guiCards;
    private ArrayList<String>  dealerCards  = new ArrayList<>();
    private ArrayList<String>  playerCards  = new ArrayList<>();
    private static final int   MAX_CARDS    = 5;
    private int                playerCardCount = 0;

    public GUI() {
        guiCards = new GUICards();
        createGUI();
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  MAIN FRAME
    // ═════════════════════════════════════════════════════════════════════════
    private void createGUI() {
        JFrame frame = new JFrame("♠  Royal Blackjack  ♠");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension d = tk.getScreenSize();
        frame.setSize(d.width, d.height);
        frame.setLayout(new BorderLayout(0, 0));
        frame.setBackground(PANEL_BG);

        // Header
        frame.add(buildHeader(), BorderLayout.NORTH);

        // Main felt table (split top / bottom)
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                buildDealerSection(frame),
                buildPlayerSection(frame, d));
        split.setDividerSize(3);
        split.setDividerLocation((int)(d.height * 0.48));
        split.setResizeWeight(0.48);
        split.setBorder(null);
        split.setBackground(FELT_DARK);
        frame.add(split, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    // ─── Golden header bar ───────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel header = new GradientPanel(PANEL_BG, new Color(0x0D2010));
        header.setPreferredSize(new Dimension(0, 52));
        header.setLayout(new BorderLayout());

        JLabel title = new JLabel("♠  R O Y A L   B L A C K J A C K  ♣", SwingConstants.CENTER);
        title.setFont(loadSerif(22f).deriveFont(Font.BOLD));
        title.setForeground(GOLD);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        header.add(title, BorderLayout.CENTER);

        // Thin gold bottom line
        header.setBorder(new MatteBorder(0, 0, 2, 0, GOLD_DIM));
        return header;
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  DEALER SECTION (top half)
    // ═════════════════════════════════════════════════════════════════════════
    private JPanel buildDealerSection(JFrame frame) {
        JPanel panel = new FeltPanel();
        panel.setLayout(new BorderLayout(0, 8));

        // Zone label
        JLabel lbl = sectionLabel("D E A L E R");
        panel.add(lbl, BorderLayout.NORTH);

        // Card tray — scrollable so extra cards are never clipped
        dealerCardArea = buildCardTray(false);
        JScrollPane dealerScroll = buildTrayScroller(dealerCardArea);
        panel.add(dealerScroll, BorderLayout.CENTER);

        // Add-dealer-card button
        JButton addBtn = buildGhostButton("+ Add Dealer Card");
        addBtn.addActionListener(e -> showCardPickerDialog(frame));
        JPanel btnRow = transparentRow();
        btnRow.add(addBtn);
        btnRow.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
        panel.add(btnRow, BorderLayout.SOUTH);

        return panel;
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  PLAYER SECTION (bottom half)
    // ═════════════════════════════════════════════════════════════════════════
    private JPanel buildPlayerSection(JFrame frame, Dimension screen) {
        JPanel outer = new JPanel(new BorderLayout(0, 0));
        outer.setBackground(PANEL_BG);

        // ── Left log panel ────────────────────────────────────────────────
        JPanel logPanel = buildLogPanel(screen);

        // ── Centre felt ───────────────────────────────────────────────────
        JPanel centre = new FeltPanel();
        centre.setLayout(new BorderLayout(0, 8));

        JLabel lbl = sectionLabel("P L A Y E R");
        centre.add(lbl, BorderLayout.NORTH);

        cardArea = buildCardTray(true);
        JScrollPane playerScroll = buildTrayScroller(cardArea);
        centre.add(playerScroll, BorderLayout.CENTER);

        centre.add(buildButtonPanel(), BorderLayout.SOUTH);

        // ── Right chips panel ──────────────────────────────────────────────
        JPanel rightPanel = buildChipsPanel(screen);

        // Assemble
        int logW   = (int)(screen.width * 0.20);
        int rightW = (int)(screen.width * 0.20);

        outer.add(logPanel,   BorderLayout.WEST);
        outer.add(centre,     BorderLayout.CENTER);
        outer.add(rightPanel, BorderLayout.EAST);

        logPanel.setPreferredSize(new Dimension(logW,   0));
        rightPanel.setPreferredSize(new Dimension(rightW, 0));

        return outer;
    }

    // ─── Log panel ────────────────────────────────────────────────────────────
    private JPanel buildLogPanel(Dimension screen) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(LOG_BG);
        panel.setBorder(new MatteBorder(0, 0, 0, 1, GOLD_DIM));

        JLabel header = new JLabel("  G A M E   L O G", SwingConstants.LEFT);
        header.setFont(loadSerif(11f).deriveFont(Font.BOLD));
        header.setForeground(GOLD_DIM);
        header.setBackground(new Color(0x060F09));
        header.setOpaque(true);
        header.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 0));
        panel.add(header, BorderLayout.NORTH);

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setBackground(LOG_BG);
        logArea.setForeground(TEXT_CREAM);
        logArea.setFont(new Font("Courier New", Font.PLAIN, 12));
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        logArea.setMargin(new Insets(10, 14, 10, 10));
        logArea.setCaretColor(GOLD);

        JScrollPane scroll = new JScrollPane(logArea);
        scroll.setBorder(null);
        scroll.setBackground(LOG_BG);
        scroll.getViewport().setBackground(LOG_BG);
        scroll.getVerticalScrollBar().setBackground(LOG_BG);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    // ─── Chips / stats side panel ─────────────────────────────────────────────
    private JPanel buildChipsPanel(Dimension screen) {
        JPanel panel = new JPanel();
        panel.setBackground(PANEL_BG);
        panel.setBorder(new MatteBorder(0, 1, 0, 0, GOLD_DIM));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(Box.createVerticalStrut(20));

        JLabel lbl = new JLabel("C H I P S", SwingConstants.CENTER);
        lbl.setFont(loadSerif(11f).deriveFont(Font.BOLD));
        lbl.setForeground(GOLD_DIM);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lbl);

        panel.add(Box.createVerticalStrut(24));

        // Decorative chip stack icons
        int[] amounts = {500, 100, 25, 5};
        Color[] colors = {CHIP_RED, CHIP_BLUE, new Color(0x27AE60), new Color(0x8E44AD)};
        for (int i = 0; i < amounts.length; i++) {
            panel.add(buildChipRow(amounts[i], colors[i]));
            panel.add(Box.createVerticalStrut(14));
        }

        panel.add(Box.createVerticalGlue());

        // Balance display
        JLabel balLbl = new JLabel("BALANCE", SwingConstants.CENTER);
        balLbl.setFont(loadSerif(10f));
        balLbl.setForeground(TEXT_MUTED);
        balLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(balLbl);

        JLabel balance = new JLabel("$2,500", SwingConstants.CENTER);
        balance.setFont(loadSerif(26f).deriveFont(Font.BOLD));
        balance.setForeground(GOLD);
        balance.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(balance);

        panel.add(Box.createVerticalStrut(24));
        return panel;
    }

    private JPanel buildChipRow(int amount, Color color) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        row.setOpaque(false);

        // Chip circle
        JPanel chip = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int s = Math.min(getWidth(), getHeight());
                // Shadow
                g2.setColor(new Color(0, 0, 0, 80));
                g2.fillOval(2, 3, s - 2, s - 2);
                // Fill
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
                g2.drawString(t, (s - fm.stringWidth(t)) / 2, (s + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
            @Override public Dimension getPreferredSize() { return new Dimension(40, 40); }
        };
        chip.setOpaque(false);
        row.add(chip);

        JLabel lbl = new JLabel("$" + String.format("%,d", amount));
        lbl.setFont(loadSerif(13f));
        lbl.setForeground(TEXT_CREAM);
        row.add(lbl);

        return row;
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  CARD TRAY
    // ═════════════════════════════════════════════════════════════════════════
    private JPanel buildCardTray(boolean glowGreen) {
        int cardH = GUICards.CARD_HEIGHT;
        int minH  = cardH + 28;

        JPanel tray = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 10)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color base = glowGreen ? new Color(0x103820) : new Color(0x0C2818);
                g2.setColor(base);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                Color glow = glowGreen ? new Color(0x20, 0xC0, 0x60, 60) : new Color(0xD4, 0xAF, 0x37, 50);
                g2.setColor(glow);
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 16, 16);
                g2.dispose();
            }
            @Override public Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                d.height = Math.max(d.height, minH);
                return d;
            }
        };
        tray.setOpaque(false);
        return tray;
    }

    private JScrollPane buildTrayScroller(JPanel tray) {
        JScrollPane sp = new JScrollPane(tray,
                JScrollPane.VERTICAL_SCROLLBAR_NEVER,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        sp.setBorder(null);
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        sp.getHorizontalScrollBar().setBackground(FELT_DARK);
        return sp;
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  ACTION BUTTONS
    // ═════════════════════════════════════════════════════════════════════════
    private JPanel buildButtonPanel() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 12));
        row.setOpaque(false);

        row.add(buildActionButton("HIT",         new Color(0x27AE60), e -> showPlayerCardPickerDialog(null)));
        row.add(buildActionButton("STAND",        new Color(0xC0392B), e -> appendLog("Player stands.")));
        row.add(buildActionButton("DOUBLE DOWN",  new Color(0xD4AC0D), e -> appendLog("Double down!")));
        row.add(buildActionButton("SURRENDER",    new Color(0x717D7E), e -> appendLog("Player surrenders.")));

        return row;
    }

    /** Bold, coloured pill button */
    private JButton buildActionButton(String text, Color accent, ActionListener al) {
        JButton btn = new JButton(text) {
            private boolean hovered = false;
            {
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
                    public void mouseExited (MouseEvent e) { hovered = false; repaint(); }
                });
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color fill = hovered ? accent.brighter() : accent.darker();
                // Shadow
                g2.setColor(new Color(0, 0, 0, 100));
                g2.fillRoundRect(2, 4, getWidth() - 2, getHeight() - 4, 12, 12);
                // Fill
                g2.setColor(fill);
                g2.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 4, 12, 12);
                // Top shine
                g2.setColor(new Color(255, 255, 255, 30));
                g2.fillRoundRect(0, 0, getWidth() - 2, (getHeight() - 4) / 2, 12, 12);
                // Text
                g2.setColor(Color.WHITE);
                g2.setFont(loadSerif(11f).deriveFont(Font.BOLD));
                FontMetrics fm = g2.getFontMetrics();
                int tx = (getWidth() - fm.stringWidth(getText())) / 2;
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

    /** Slim, text-only gold ghost button */
    private JButton buildGhostButton(String text) {
        JButton btn = new JButton(text) {
            private boolean hovered = false;
            {
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
                    public void mouseExited (MouseEvent e) { hovered = false; repaint(); }
                });
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (hovered) {
                    g2.setColor(new Color(0xD4, 0xAF, 0x37, 20));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                }
                g2.setColor(hovered ? GOLD_LIGHT : GOLD_DIM);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.setColor(hovered ? GOLD_LIGHT : GOLD);
                g2.setFont(loadSerif(11f));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(),
                        (getWidth()  - fm.stringWidth(getText())) / 2,
                        (getHeight() - fm.getHeight()) / 2 + fm.getAscent());
                g2.dispose();
            }
            @Override protected void paintBorder(Graphics g) {}
        };
        btn.setPreferredSize(new Dimension(170, 32));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  DIALOGS
    // ═════════════════════════════════════════════════════════════════════════
    private void showCardPickerDialog(JFrame parent) {
        String chosen = pickCard(parent, "Add Dealer Card");
        if (chosen != null) {
            addCardToPanel(dealerCardArea, guiCards.getCard(chosen));
            dealerCards.add(chosen);
            appendLog("⬦ Dealer  ← " + formatCard(chosen));
        }
    }

    private void showPlayerCardPickerDialog(JFrame parent) {
        if (playerCardCount >= MAX_CARDS) {
            appendLog("✖ Maximum " + MAX_CARDS + " cards reached.");
            return;
        }
        String chosen = pickCard(parent, "Hit — Draw a Card");
        if (chosen != null) {
            addCardToPanel(cardArea, guiCards.getCard(chosen));
            playerCards.add(chosen);
            playerCardCount++;
            appendLog("⬧ Player  ← " + formatCard(chosen)
                    + "  [" + playerCardCount + "/" + MAX_CARDS + "]");
        }
    }

    private String pickCard(JFrame parent, String title) {
        ArrayList<String> names = new ArrayList<>(guiCards.getCardMap().keySet());
        names.sort(String::compareTo);
        UIManager.put("OptionPane.background",        FELT_DARK);
        UIManager.put("Panel.background",             FELT_DARK);
        UIManager.put("OptionPane.messageForeground", TEXT_CREAM);
        UIManager.put("ComboBox.background",          FELT_MID);
        UIManager.put("ComboBox.foreground",          TEXT_CREAM);
        return (String) JOptionPane.showInputDialog(
                parent, "Select a card:", title,
                JOptionPane.PLAIN_MESSAGE, null,
                names.toArray(new String[0]), names.get(0));
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  HELPERS
    // ═════════════════════════════════════════════════════════════════════════
    private void addCardToPanel(JPanel panel, Image image) {
        JLabel lbl = new JLabel(new ImageIcon(image));
        // Subtle card drop-shadow wrapper
        lbl.setBorder(BorderFactory.createCompoundBorder(
                new DropShadowBorder(),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)));
        panel.add(lbl);
        panel.revalidate();
        panel.repaint();
    }

    public void appendLog(String message) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    private String formatCard(String raw) {
        return raw.replace("_", " ");
    }

    private JLabel sectionLabel(String text) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setFont(loadSerif(11f).deriveFont(Font.BOLD | Font.ITALIC));
        lbl.setForeground(GOLD_DIM);
        lbl.setBorder(BorderFactory.createEmptyBorder(10, 0, 2, 0));
        return lbl;
    }

    private JPanel centreWrap(JComponent inner) {
        JPanel w = new JPanel(new GridBagLayout());
        w.setOpaque(false);
        w.add(inner);
        return w;
    }

    private JPanel transparentRow() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER));
        p.setOpaque(false);
        return p;
    }

    /** Georgia as a safe serif fallback */
    private Font loadSerif(float size) {
        return new Font("Georgia", Font.PLAIN, (int) size);
    }

    public ArrayList<String> getPlayerCards() { return new ArrayList<>(playerCards); }
    public ArrayList<String> getDealerCards() { return new ArrayList<>(dealerCards); }

    // ═════════════════════════════════════════════════════════════════════════
    //  INNER PAINT HELPERS
    // ═════════════════════════════════════════════════════════════════════════

    /** Panel with a diagonal felt-like gradient */
    private static class FeltPanel extends JPanel {
        FeltPanel() { setOpaque(false); setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10)); }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setPaint(new GradientPaint(0, 0, FELT_MID, getWidth(), getHeight(), FELT_DARK));
            g2.fillRect(0, 0, getWidth(), getHeight());
            // Subtle noise-like dot pattern for texture
            g2.setColor(new Color(255, 255, 255, 6));
            for (int x = 0; x < getWidth(); x += 4)
                for (int y = 0; y < getHeight(); y += 4)
                    if ((x + y) % 8 == 0) g2.fillRect(x, y, 1, 1);
            g2.dispose();
        }
    }

    /** Panel with a vertical gradient */
    private static class GradientPanel extends JPanel {
        private final Color top, bot;
        GradientPanel(Color top, Color bot) { this.top = top; this.bot = bot; setOpaque(false); }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setPaint(new GradientPaint(0, 0, top, 0, getHeight(), bot));
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
        }
    }

    /** Simple drop shadow border for cards */
    private static class DropShadowBorder extends AbstractBorder {
        private static final int SZ = 4;
        @Override public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            for (int i = SZ; i > 0; i--) {
                g2.setColor(new Color(0, 0, 0, 20 + (SZ - i) * 12));
                g2.fillRoundRect(x + i, y + i, w - i, h - i, 4, 4);
            }
            g2.dispose();
        }

        @Override public Insets getBorderInsets(Component c) { return new Insets(0, 0, SZ, SZ); }
        @Override public Insets getBorderInsets(Component c, Insets i) {
            i.set(0, 0, SZ, SZ); return i;
        }
    }
}