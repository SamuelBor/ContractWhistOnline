import component.Player;
import java.util.ArrayList;

public class Predictor {
    static void newPrediction(ArrayList<Player> players, int TIME_DELAY) throws InterruptedException{
        for (Player player : players) {
            // Set prediction as 2 for now to observe change in behaviour from trying to win to trying to lose
            player.setPrediction(2);
            ContractWhistOnline.makePrediction(player.getID(), player.getPrediction());
            Thread.sleep(TIME_DELAY);
        }
    }
}
