import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class GUICards {

    private static final Toolkit tk = Toolkit.getDefaultToolkit();
    private static final Dimension d = tk.getScreenSize();

    public static final int CARD_WIDTH  = d.width / 8;
    public static final int CARD_HEIGHT = d.height / 8;

    // Path to your assets folder — adjust if your working directory differs
    private static final String ASSETS_PATH = "../assets/";

    private ArrayList<Image> cardImages = new ArrayList<>();

    private HashMap<String, Image> cardMap = new HashMap<>();

    // ── File names matching your assets folder exactly ─────────────────────── 
    private static final String[] CARD_FILES = {
            // Numbered cards
            "2_of_clubs",   "2_of_diamonds",   "2_of_hearts",   "2_of_spades",
            "3_of_clubs",   "3_of_diamonds",   "3_of_hearts",   "3_of_spades",
            "4_of_clubs",   "4_of_diamonds",   "4_of_hearts",   "4_of_spades",
            "5_of_clubs",   "5_of_diamonds",   "5_of_hearts",   "5_of_spades",
            "6_of_clubs",   "6_of_diamonds",   "6_of_hearts",   "6_of_spades",
            "7_of_clubs",   "7_of_diamonds",   "7_of_hearts",   "7_of_spades",
            "8_of_clubs",   "8_of_diamonds",   "8_of_hearts",   "8_of_spades",
            "9_of_clubs",   "9_of_diamonds",   "9_of_hearts",   "9_of_spades",
            "10_of_clubs",  "10_of_diamonds",  "10_of_hearts",  "10_of_spades",
            "jack_of_clubs2",  "jack_of_diamonds2",  "jack_of_hearts2",  "jack_of_spades2",
            "queen_of_clubs2", "queen_of_diamonds2", "queen_of_hearts2", "queen_of_spades2",
            "king_of_clubs2",  "king_of_diamonds2",  "king_of_hearts2",  "king_of_spades2",
            "ace_of_clubs",  "ace_of_diamonds",  "ace_of_hearts",  "ace_of_spades2", "Uknown"
    };

    public GUICards() {
        for (String cardName : CARD_FILES) {
            String filePath = ASSETS_PATH + cardName + ".png";
            Image img = loadAndScale(filePath);

            cardImages.add(img);          // add to ordered list
            cardMap.put(cardName, img);   // add to name lookup map
        }

        System.out.println("Loaded " + cardImages.size() + " card images.");
    }

    private Image loadAndScale(String path) {
        Image img = Toolkit.getDefaultToolkit().getImage(path);
        return img.getScaledInstance(CARD_WIDTH, CARD_HEIGHT, Image.SCALE_SMOOTH);
    }

    public Image getCard(String cardName) {
        return cardMap.get(cardName);
    }

    public Image getCard(int index) {
        return cardImages.get(index);
    }

    public ArrayList<Image> getAllCards() {
        return cardImages;
    }

    public HashMap<String, Image> getCardMap() {
        return cardMap;
    }
}