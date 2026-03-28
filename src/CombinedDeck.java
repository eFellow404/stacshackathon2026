import java.util.List;

public class CombinedDeck extends CardCollection {
    public CombinedDeck(List<SingleDeck> decks) {
        for (SingleDeck deck : decks) {
            this.cards.addAll(deck.getCards());
        }
    }
}
