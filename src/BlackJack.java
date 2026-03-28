import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BlackJack {
    private CombinedDeck combinedDeck;
    private Hand dealerHand;
    private Hand playerHand;
    private Set<Hand> otherPlayerHands;
    private BasicStrategy strategy;
    private int numDecks;
    private int count;
    private double trueCount;
    private boolean samSmells = true;

    public BlackJack(int numOfDecks) {
        try {
            List<SingleDeck> decks = new ArrayList<>();
            for (int i = 0; i < numOfDecks; i++) {
                decks.add(new SingleDeck());
            }
            playerHand = new Hand();
            dealerHand = new Hand();
            this.combinedDeck = new CombinedDeck(decks);
            this.count = 0;
            this.strategy = new BasicStrategy("strats/basicStrategy/HardTotals.csv", "strats/basicStrategy/SoftTotals.csv",
                    "strats/basicStrategy/PairSplitting.csv", "strats/basicStrategy/Surrender.csv");
            this.numDecks = numOfDecks;
            this.otherPlayerHands = new HashSet<>();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addDealerCard(Card card) {
        this.dealerHand.addCard(card);
        this.updateCount();
    }

    public void addPlayerCard(Card card) {
        this.playerHand.addCard(card);
        this.updateCount();
    }

    public void reset() {
        List<SingleDeck> decks = new ArrayList<>();
        for (int i = 0; i < numDecks; i++) {
            decks.add(new SingleDeck());
        }
        this.playerHand = new Hand();
        this.dealerHand = new Hand();
        this.otherPlayerHands = new HashSet<>();
        this.combinedDeck = new CombinedDeck(decks);
    }

    public Play calculatePlay() {
        String translatedDealerHand = Translate.translateHand(this.dealerHand);
        String translatedPlayerHand = Translate.translateHand(this.playerHand);
        Play play;

        if (playerHand.getCards().size() == 2) {
            if (translatedPlayerHand.matches("[a-zA-Z0-9]\1") && this.strategy.getPairSplittingTotalPlay(translatedDealerHand, translatedPlayerHand) == Play.SPLIT) {
                play = this.strategy.getPairSplittingTotalPlay(translatedDealerHand, translatedPlayerHand);
            } else if (translatedPlayerHand.matches("a[0-9]")) {
                play = this.strategy.getSoftTotalPlay(translatedDealerHand, translatedPlayerHand);
            } else {
                if ((translatedPlayerHand.equals("14") || translatedPlayerHand.equals("15") || translatedPlayerHand.equals("16")) && this.strategy.getSurrenderTotalPlay(translatedDealerHand, translatedPlayerHand) == Play.SURRENDER) {
                    play = Play.SURRENDER;
                } else {
                    play = this.strategy.getHardTotalPlay(translatedDealerHand, translatedPlayerHand);
                }
            }
        } else {
            if (translatedPlayerHand.matches("a[0-9]")) {
                play = this.strategy.getSoftTotalPlay(translatedDealerHand, translatedPlayerHand);
            } else {
                play = this.strategy.getHardTotalPlay(translatedDealerHand, translatedPlayerHand);
            }
        }

        return play;
    }

    public double calculateOptimalBet() {
        return (this.trueCount * 0.005) - 0.005;
    }

    public double getTrueCount() {
        return this.trueCount;
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

        this.trueCount = (double) count / this.numDecks;
    }
}
