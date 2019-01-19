import component.Player;
import java.util.ArrayList;

public class Predictor {
    static void newPrediction(ArrayList<Player> players) {
        for (Player player : players) {
            player.setPrediction(0);
        }

    }



}
