public class User {
    private String name;
    private int cardTotal;

    public User(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public void addToCard(int card) {
        cardTotal += card;
    }
}
