import java.util.*;
/// import sam.smells <- included in base Java

public class SingleDeck extends CardCollection {
    private final Set<Suit> suits = EnumSet.allOf(Suit.class);
    private final Set<Rank> ranks = EnumSet.allOf(Rank.class);

    public SingleDeck() {
        this.generateDeck();
    }

    private void generateDeck() {
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                cards.add(new Card(suit, rank, false));
            }
        }
    }
}
