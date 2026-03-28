

import java.awt.Color;
import java.awt.Font;

/**
 * Central repository for all visual constants used across the blackjack UI.
 * Import this wherever colours or fonts are needed — avoids duplication and
 * makes reskinning the application a single-file change.
 */
public final class Theme {

    // ── Construction blocked ──────────────────────────────────────────────────
    private Theme() {}

    // ── Felt / table colours ──────────────────────────────────────────────────
    public static final Color FELT_DARK  = new Color(0x0D2B1A);
    public static final Color FELT_MID   = new Color(0x14402A);
    public static final Color FELT_LIGHT = new Color(0x1A5235);

    // ── Gold accents ──────────────────────────────────────────────────────────
    public static final Color GOLD       = new Color(0xD4AF37);
    public static final Color GOLD_LIGHT = new Color(0xF0D060);
    public static final Color GOLD_DIM   = new Color(0x8B7020);

    // ── Background / surface colours ─────────────────────────────────────────
    public static final Color PANEL_BG   = new Color(0x0A1A10);
    public static final Color LOG_BG     = new Color(0x08120C);

    // ── Text ──────────────────────────────────────────────────────────────────
    public static final Color TEXT_CREAM = new Color(0xF5EED8);
    public static final Color TEXT_MUTED = new Color(0xA89060);

    // ── Chip colours ──────────────────────────────────────────────────────────
    public static final Color CHIP_RED   = new Color(0xC0392B);
    public static final Color CHIP_BLUE  = new Color(0x2471A3);

    // ── Button accent colours ─────────────────────────────────────────────────
    public static final Color BTN_HIT        = new Color(0x27AE60);
    public static final Color BTN_STAND      = new Color(0xC0392B);
    public static final Color BTN_DOUBLE     = new Color(0xD4AC0D);
    public static final Color BTN_SURRENDER  = new Color(0x717D7E);

    // ── Typography ────────────────────────────────────────────────────────────

    /**
     * Returns a Georgia (or generic serif fallback) font at the requested size.
     * Use {@link Font#deriveFont(int)} or {@link Font#deriveFont(float)} to
     * apply bold/italic after the fact.
     */
    public static Font serif(float size) {
        return new Font("Georgia", Font.PLAIN, (int) size);
    }

    /** Monospace font used in the game log. */
    public static Font mono(float size) {
        return new Font("Courier New", Font.PLAIN, (int) size);
    }
}
