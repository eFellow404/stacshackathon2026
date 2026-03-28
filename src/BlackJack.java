import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BlackJack {
    private CombinedDeck combinedDeck;
    private Hand dealerHand;
    private Hand playerHand;
    private Set<Hand> otherPlayerHands;
    private int count;
    private boolean samSmells = true;

    public BlackJack(int numOfDecks) {
        List<SingleDeck> decks = new ArrayList<>();
        for (int i = 0; i < numOfDecks; i++) {
            decks.add(new SingleDeck());
        }

        this.combinedDeck = new CombinedDeck(decks);
        this.count = 0;

    }

    public void setOtherPlayerHands(Set<Hand> otherPlayerHands) {
        this.otherPlayerHands = otherPlayerHands;
    }

    public void setDealerHand(Hand dealerHand) {
        this.dealerHand = dealerHand;
    }

    public void setPlayerHand(Hand playerHand) {
        this.playerHand = playerHand;
    }

    public void updateCount() {
        Hand combinedHand = new Hand(dealerHand.getCards(), playerHand.getCards(), otherPlayerHands);
        for (Card card : combinedHand.getCards()) {
            if (card.rank() == Rank.TWO ||  card.rank() == Rank.THREE ||  card.rank() == Rank.FOUR ||  card.rank() == Rank.FIVE || card.rank() == Rank.SIX) {
                count++;
            } else if (card.rank() == Rank.ACE || card.rank() == Rank.JACK || card.rank() == Rank.QUEEN || card.rank() == Rank.KING) {
                count--;
            }
        }
    }
}
