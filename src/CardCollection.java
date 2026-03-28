import java.util.ArrayList;
import java.util.List;

public abstract class CardCollection {
    protected final List<Card> cards = new ArrayList<>();

    public List<Card> getCards() {
        return cards;
    }
}