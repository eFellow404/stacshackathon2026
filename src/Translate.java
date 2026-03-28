import blackjack.*;
import java.util.ArrayList;
import java.util.List;

public class Translate {
    public static Hand translate(List<String> cards) {
        Hand hand = new Hand();
        for (String card : cards) {
            String[] line = card.split("_");
            hand.addCard(new Card(getSuit(line[2]), getRank(line[0]), true));
        }
        return hand;
    }

    private static Suit getSuit(String suit) {
        return switch (suit) {
            case "clubs" -> Suit.CLUBS;
            case "spades" -> Suit.SPADES;
            case "hearts" -> Suit.HEARTS;
            case "diamonds" -> Suit.DIAMONDS;
            case "clubs2" -> Suit.CLUBS;
            case "spades2" -> Suit.SPADES;
            case "hearts2" -> Suit.HEARTS;
            case "diamonds2" -> Suit.DIAMONDS;
            default -> null;
        };
    }

    private static Rank getRank(String rank) {
        return switch (rank) {
            case "ace" -> Rank.ACE;
            case "2" -> Rank.TWO;
            case "3" -> Rank.THREE;
            case "4" -> Rank.FOUR;
            case "5" -> Rank.FIVE;
            case "6" -> Rank.SIX;
            case "7" -> Rank.SEVEN;
            case "8" -> Rank.EIGHT;
            case "9" -> Rank.NINE;
            case "10" -> Rank.TEN;
            case "jack" -> Rank.JACK;
            case "queen" -> Rank.QUEEN;
            case "king" -> Rank.KING;
            default -> null;
        };
    }
}
