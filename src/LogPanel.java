
import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;

/**
 * The left-hand game-log panel that records every dealt card and action taken
 * during a round.
 *
 * <p>Use {@link #appendLog(String)} to add entries; the panel auto-scrolls to
 * keep the latest entry visible.
 */
public class LogPanel extends JPanel {

    private final JTextArea logArea;

    public LogPanel() {
        super(new BorderLayout());
        setBackground(Theme.LOG_BG);
        setBorder(new MatteBorder(0, 0, 0, 1, Theme.GOLD_DIM));

        add(buildHeader(), BorderLayout.NORTH);

        logArea = buildLogArea();
        JScrollPane scroll = buildScrollPane(logArea);
        add(scroll, BorderLayout.CENTER);
    }

    // ── Public API ────────────────────────────────────────────────────────────

    /**
     * Appends a line of text to the log and scrolls to the bottom.
     * Safe to call from any thread.
     *
     * @param message the log line to append (a newline is added automatically)
     */
    public void appendLog(String message) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    // ── Private builders ──────────────────────────────────────────────────────

    private JLabel buildHeader() {
        JLabel header = new JLabel("  G A M E   L O G", SwingConstants.LEFT);
        header.setFont(Theme.serif(11f).deriveFont(Font.BOLD));
        header.setForeground(Theme.GOLD_DIM);
        header.setBackground(new Color(0x060F09));
        header.setOpaque(true);
        header.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 0));
        return header;
    }

    private JTextArea buildLogArea() {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setBackground(Theme.LOG_BG);
        area.setForeground(Theme.TEXT_CREAM);
        area.setFont(Theme.mono(12f));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setMargin(new Insets(10, 14, 10, 10));
        area.setCaretColor(Theme.GOLD);
        return area;
    }

    private JScrollPane buildScrollPane(JTextArea area) {
        JScrollPane scroll = new JScrollPane(area);
        scroll.setBorder(null);
        scroll.setBackground(Theme.LOG_BG);
        scroll.getViewport().setBackground(Theme.LOG_BG);
        scroll.getVerticalScrollBar().setBackground(Theme.LOG_BG);
        return scroll;
    }
}
