public class Translate {
    public static Card translateCard(String card) {
        String[] line = card.split("_");
        return new Card(getSuit(line[2]), getRank(line[0]), true);
    }

    public static String translateHand(Hand hand) {
        int total = 0;
        String index = "";
        // pair split
        if (hand.getCards().size() == 2 && hand.getCard(0) == hand.getCard(1) && hand.getCard(0).rank() == Rank.ACE) {
            index += "a";
            index += convertRank(hand.getCard(1).rank());
        } else if (hand.getCards().size() == 2 && hand.getCard(0).rank() == Rank.ACE) { // soft total
            index += convertRank(hand.getCard(1).rank());
            index += convertRank(hand.getCard(0).rank());
        } else { // hard total
            for (Card card : hand.getCards()) {
                total += convertRank(card.rank());
            }
            index = String.valueOf(total);
        }
        return index;
    }

    private static int convertRank(Rank rank) {
        return switch (rank) {
          case ACE -> 11;
          case TWO -> 2;
          case THREE -> 3;
          case FOUR -> 4;
          case FIVE -> 5;
          case SIX -> 6;
          case SEVEN -> 7;
          case EIGHT -> 8;
          case NINE -> 9;
          case TEN, QUEEN, KING, JACK -> 10;
        };
    }

    public static String convertPlay(Play play) {
        return switch (play) {
            case INSURANCE -> "Insurance";
            case SURRENDER -> "Surrender";
            case SPLIT ->  "Split";
            case DOUBLE_DOWN ->  "Double down";
            case HIT -> "Hit";
            case STAND ->  "Stand";
            default -> "Nothing";
        };
    }


    private static Suit getSuit(String suit) {
        return switch (suit) {
            case "clubs", "clubs2" -> Suit.CLUBS;
            case "spades", "spades2" -> Suit.SPADES;
            case "hearts", "hearts2" -> Suit.HEARTS;
            case "diamonds", "diamonds2" -> Suit.DIAMONDS;
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
