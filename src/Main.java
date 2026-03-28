import static java.lang.Thread.sleep;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        GUI gui = new GUI();
        sleep(5000);
        System.out.println(gui.getDealerCards());
    }
}
