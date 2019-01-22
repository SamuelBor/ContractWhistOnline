import component.Player;
import java.util.ArrayList;

public class Predictor {
    static void newPrediction(ArrayList<Player> players) throws InterruptedException{
        for (Player player : players) {
            player.setPrediction(0);
            ContractWhistOnline.makePrediction(player.getID(), player.getPrediction());
            Thread.sleep(750);
        }
    }
}
