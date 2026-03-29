import java.util.*;

public class Hand extends CardCollection{

    public Hand(){
        super();
    }

    public Hand(List<Card> h1, List<Card> h2, Map<String, Hand> h3){
        getCards().addAll(h1);
        getCards().addAll(h2);

        for (Hand h : h3.values()){
            getCards().addAll(h.getCards());
        }
    }

    public Card getCard(int index) {
        return getCards().get(index);
    }

    public void addCard(Card card) {
        getCards().add(card);
    }
}
