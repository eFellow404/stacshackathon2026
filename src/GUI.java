import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.util.*;

public class GUI {
    private static final int MAX_PLAYER_CARDS = 5;

    private final GUICards guiCards = new GUICards();
    private final BlackJack blackJack = new BlackJack(8);
    
    private int playerCardCount = 0;
    private int dealerCardCount = 0;
    private Map<String, Integer> otherPlayerCardCount = new LinkedHashMap<>();
    
    private CardTray dealerTray;
    private CardTray playerTray;
    private Map<String, CardTray> otherPlayerTrays = new LinkedHashMap<>();
    
    private double balance;
    private LogPanel logPanel;
    private ChipsPanel chipsPanel;
    private DefaultListModel<String> playersModel;
    private JList<String> playersList;
    private JPanel otherPlayersPanel;
    private JFrame frame;
    public Map<String, String> peopleCards = new HashMap<>();

    public GUI(){
        buildFrame();
    }

    private void buildFrame() {
        frame = new JFrame("♠ Blackjack ♠");
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
                "♠  B L A C K J A C K  ♣",
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

    private JPanel buildOtherPlayersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        // Top: label + Add Player button
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        JLabel lbl = new JLabel("Other Players", SwingConstants.CENTER);
        lbl.setFont(Theme.serif(12f));
        lbl.setForeground(Theme.GOLD_DIM);
        top.add(lbl, BorderLayout.CENTER);

        JButton addPlayerBtn = buildGhostButton("+ Add Player");
        addPlayerBtn.addActionListener(e -> handleAddPlayer());
        top.add(centreRow(addPlayerBtn, 0, 6, 0, 6), BorderLayout.EAST);

        panel.add(top, BorderLayout.NORTH);

        // Middle: list of player names (select to add cards)
        playersModel = new DefaultListModel<>();
        playersList = new JList<>(playersModel);
        playersList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        playersList.setOpaque(false);
        playersList.setBackground(Theme.FELT_DARK);
        playersList.setForeground(Theme.TEXT_CREAM);

        JScrollPane listScroll = new JScrollPane(playersList);
        listScroll.setBorder(null);
        listScroll.setOpaque(false);
        listScroll.getViewport().setOpaque(false);
        listScroll.setPreferredSize(new Dimension(160, 120));
        panel.add(listScroll, BorderLayout.CENTER);

        // Bottom: Add Card to Selected (optional)
        JButton addCardBtn = buildGhostButton("+ Add Card to Selected");
        addCardBtn.addActionListener(e -> handleAddCardToSelectedPlayer());
        panel.add(centreRow(addCardBtn, 4, 4, 8, 4), BorderLayout.SOUTH);

        return panel;
    }

    private void handleAddPlayer() {
        String name = JOptionPane.showInputDialog(frame, "Player name:", "Add Player", JOptionPane.PLAIN_MESSAGE);
        if (name == null) return;
        name = name.trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Name cannot be empty", "Invalid", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (otherPlayerTrays.containsKey(name) || "Dealer".equalsIgnoreCase(name) || "Player".equalsIgnoreCase(name)) {
            JOptionPane.showMessageDialog(frame, "A player with that name already exists.", "Duplicate", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Add to list model so the JList shows it
        playersModel.addElement(name);

        // Create a CardTray to visually show cards for this player
        CardTray tray = new CardTray(true);
        tray.setPreferredSize(new Dimension(0, GUICards.CARD_HEIGHT + 40));
        otherPlayerTrays.put(name, tray);
        otherPlayerCardCount.put(name, 0);

        // Make a small titled panel for the player: name + tray
        JPanel single = new JPanel(new BorderLayout());
        single.setOpaque(false);
        JLabel nameLbl = new JLabel(name, SwingConstants.CENTER);
        nameLbl.setForeground(Theme.GOLD_DIM);
        single.add(nameLbl, BorderLayout.NORTH);
        single.add(tray.inScrollPane(), BorderLayout.CENTER);
        single.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));

        // Ensure otherPlayersPanel is created in buildPlayerCentre (see next step)
        otherPlayersPanel.add(single);
        otherPlayersPanel.revalidate();
        otherPlayersPanel.repaint();
    }

    private void handleAddCardToSelectedPlayer() {
        String selected = playersList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(frame, "Select a player first.", "No player selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String chosen = pickCard("Add Card — " + selected);
        if (chosen == null) return;

        CardTray tray = otherPlayerTrays.get(selected);
        if (tray == null) return;

        // Add the image to the tray and update counts
        tray.addCard(guiCards.getCard(chosen));
        otherPlayerCardCount.put(selected, otherPlayerCardCount.getOrDefault(selected, 0) + 1);

        // Update backend model — the BlackJack method you added
        // Translate.translateCard(chosen) returns a Card instance from the string name
        blackJack.addOtherPlayerCard(selected, Translate.translateCard(chosen));

        logPanel.appendLog("⬦ " + selected + "  ← " + chosen.replace("_", " "));
        printLogs();
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
            balance = chipsPanel.getBalance();
            dealerTray.addCard(guiCards.getCard(chosen));
            dealerCardCount++;
            blackJack.addDealerCard(Translate.translateCard(chosen));
            logPanel.appendLog("⬦ Dealer  ← " + formatCard(chosen));

            printLogs();
        }
    }

    private JPanel buildPlayerSection(Dimension screen) {
        logPanel = new LogPanel();
        chipsPanel = new ChipsPanel();

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
            balance = chipsPanel.getBalance();
            playerTray.addCard(guiCards.getCard(chosen));
            blackJack.addPlayerCard(Translate.translateCard(chosen));
            playerCardCount++;

            printLogs();
        }
    }

    private void printLogs() {
        if (playerCardCount >= 2 && dealerCardCount >= 1) {
            logPanel.appendLog(Translate.convertPlay(blackJack.calculatePlay()));
        }

        if (playerCardCount == 2 && dealerCardCount == 1) {
            logPanel.appendLog("The true count is: " + blackJack.getTrueCount());
            if (blackJack.calculateOptimalBet() * balance > 5) {
                logPanel.appendLog("The optimal bet is: $" + blackJack.calculateOptimalBet() * balance);
            } else {
                logPanel.appendLog("Bet the $5 minimum");
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
