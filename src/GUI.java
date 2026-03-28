import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GUI {

    private static final int MAX_PLAYER_CARDS = 5;

    private final GUICards guiCards = new GUICards();
    private final BlackJack blackJack = new BlackJack(8);
    private int playerCardCount = 0;
    private int dealerCardCount = 0;

    private CardTray dealerTray;
    private CardTray playerTray;
    private LogPanel logPanel;
    private JFrame frame;
    public Map<String, String> peopleCards = new HashMap<>();

    public GUI(){
        buildFrame();
    }

    private void buildFrame() {
        frame = new JFrame("♠  Royal Blackjack  ♠");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setSize(screen.width, screen.height);
        frame.setLayout(new BorderLayout(0, 0));
        frame.setBackground(Theme.PANEL_BG);

        frame.add(buildHeader(),   BorderLayout.NORTH);
        frame.add(buildSplitPane(screen), BorderLayout.CENTER);

        frame.setVisible(true);
    }

    private JPanel buildHeader() {
        JPanel header = new GradientPanel(Theme.PANEL_BG, new Color(0x0D2010));
        header.setPreferredSize(new Dimension(0, 52));
        header.setLayout(new BorderLayout());

        JLabel title = new JLabel(
                "♠  R O Y A L   B L A C K J A C K  ♣",
                SwingConstants.CENTER);
        title.setFont(Theme.serif(22f).deriveFont(Font.BOLD));
        title.setForeground(Theme.GOLD);
        header.add(title, BorderLayout.CENTER);

        header.setBorder(new MatteBorder(0, 0, 2, 0, Theme.GOLD_DIM));
        return header;
    }


    private JSplitPane buildSplitPane(Dimension screen) {
        JSplitPane split = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                buildDealerSection(),
                buildPlayerSection(screen));

        split.setDividerSize(3);
        split.setDividerLocation((int) (screen.height * 0.48));
        split.setResizeWeight(0.48);
        split.setBorder(null);
        split.setBackground(Theme.FELT_DARK);
        return split;
    }

    private JPanel buildDealerSection() {
        JPanel panel = new FeltPanel();
        panel.setLayout(new BorderLayout(0, 8));

        panel.add(sectionLabel("D E A L E R"), BorderLayout.NORTH);

        dealerTray = new CardTray(false);
        panel.add(dealerTray.inScrollPane(), BorderLayout.CENTER);

        JButton addBtn = buildGhostButton("+ Add Dealer Card");
        addBtn.addActionListener(e -> handleAddDealerCard());
        panel.add(centreRow(addBtn, 0, 0, 12, 0), BorderLayout.SOUTH);

        JButton addBtn2 = buildGhostButton("Next Hand");
        addBtn2.addActionListener(e -> handleNextHand());
        panel.add(centreRow(addBtn2, 0, 0, 12, 0), BorderLayout.NORTH);

        return panel;
    }

    private void handleNextHand() {
        playerTray.removeCards();
        dealerTray.removeCards();
        playerCardCount = 0;
        dealerCardCount = 0;
        blackJack.reset();
    }

    private void handleAddDealerCard() {
        String chosen = pickCard("Add Dealer Card");
        if (chosen != null) {
            dealerTray.addCard(guiCards.getCard(chosen));
            dealerCardCount++;
            blackJack.addDealerCard(Translate.translateCard(chosen));
            logPanel.appendLog("⬦ Dealer  ← " + formatCard(chosen));

            if (playerCardCount >= 2 && dealerCardCount >= 1) {
                logPanel.appendLog(Translate.convertPlay(blackJack.calculatePlay()));
            }

            if (playerCardCount == 2 && dealerCardCount == 1) {
                logPanel.appendLog("The true count is: " + blackJack.getTrueCount());
                logPanel.appendLog("The optimal bet is: " + blackJack.calculateOptimalBet());
            }
        }
    }

    private JPanel buildPlayerSection(Dimension screen) {
        logPanel = new LogPanel();
        ChipsPanel chipsPanel = new ChipsPanel();

        JPanel centre = buildPlayerCentre();

        int sideWidth = (int) (screen.width * 0.20);

        JPanel outer = new JPanel(new BorderLayout(0, 0));
        outer.setBackground(Theme.PANEL_BG);
        outer.add(logPanel,   BorderLayout.WEST);
        outer.add(centre,     BorderLayout.CENTER);
        outer.add(chipsPanel, BorderLayout.EAST);

        logPanel.setPreferredSize(new Dimension(sideWidth, 0));
        chipsPanel.setPreferredSize(new Dimension(sideWidth, 0));

        return outer;
    }

    private JPanel buildPlayerCentre() {
        JPanel centre = new FeltPanel();
        centre.setLayout(new BorderLayout(0, 8));

        centre.add(sectionLabel("P L A Y E R"), BorderLayout.NORTH);

        playerTray = new CardTray(true);
        centre.add(playerTray.inScrollPane(), BorderLayout.CENTER);

        ActionButtons buttons = new ActionButtons(
                e -> handleHit(),
                e -> logPanel.appendLog("Player stands."),
                e -> logPanel.appendLog("Double down!"),
                e -> logPanel.appendLog("Player surrenders."));
        centre.add(buttons, BorderLayout.SOUTH);

        return centre;
    }

    private void handleHit() {
        if (playerCardCount >= MAX_PLAYER_CARDS) {
            logPanel.appendLog("✖ Maximum " + MAX_PLAYER_CARDS + " cards reached.");
            return;
        }
        String chosen = pickCard("Hit — Draw a Card");
        if (chosen != null) {
            playerTray.addCard(guiCards.getCard(chosen));
            blackJack.addPlayerCard(Translate.translateCard(chosen));
            playerCardCount++;
            
            if (playerCardCount >= 2 && dealerCardCount >= 1) {
                logPanel.appendLog(Translate.convertPlay(blackJack.calculatePlay()));
            }

            if (playerCardCount == 2 && dealerCardCount == 1) {
                logPanel.appendLog("The true count is: " + blackJack.getTrueCount());
                logPanel.appendLog("The optimal bet is: " + blackJack.calculateOptimalBet());
            }
        }
    }

    private String pickCard(String title) {
        ArrayList<String> names = new ArrayList<>(guiCards.getCardMap().keySet());
        names.sort(String::compareTo);

        // Style the native dialog to match the dark theme
        UIManager.put("OptionPane.background",        Theme.FELT_DARK);
        UIManager.put("Panel.background",             Theme.FELT_DARK);
        UIManager.put("OptionPane.messageForeground", Theme.TEXT_CREAM);
        UIManager.put("ComboBox.background",          Theme.FELT_MID);
        UIManager.put("ComboBox.foreground",          Theme.TEXT_CREAM);

        return (String) JOptionPane.showInputDialog(
                frame,
                "Select a card:", title,
                JOptionPane.PLAIN_MESSAGE, null,
                names.toArray(new String[0]),
                names.get(0));
    }

    private JLabel sectionLabel(String text) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setFont(Theme.serif(11f).deriveFont(Font.BOLD | Font.ITALIC));
        lbl.setForeground(Theme.GOLD_DIM);
        lbl.setBorder(BorderFactory.createEmptyBorder(10, 0, 2, 0));
        return lbl;
    }

    private JPanel centreRow(JComponent inner, int top, int left, int bottom, int right) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.CENTER));
        row.setOpaque(false);
        row.add(inner);
        row.setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right));
        return row;
    }

    private String formatCard(String raw) {
        return raw.replace("_", " ");
    }


    private JButton buildGhostButton(String text) {
        JButton btn = new JButton(text) {
            private boolean hovered = false;

            {
                addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseEntered(java.awt.event.MouseEvent e) { hovered = true;  repaint(); }
                    public void mouseExited (java.awt.event.MouseEvent e) { hovered = false; repaint(); }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                if (hovered) {
                    g2.setColor(new Color(0xD4, 0xAF, 0x37, 20));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                }

                g2.setColor(hovered ? Theme.GOLD_LIGHT : Theme.GOLD_DIM);
                g2.setStroke(new java.awt.BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);

                g2.setColor(hovered ? Theme.GOLD_LIGHT : Theme.GOLD);
                g2.setFont(Theme.serif(11f));
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
}
