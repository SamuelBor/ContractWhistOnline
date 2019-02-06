import component.Player;
import net.sf.javaml.classification.Classifier;
import net.sf.javaml.classification.KNearestNeighbors;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.tools.data.FileHandler;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

public class Predictor {
    static int newPrediction(Player player, int TIME_DELAY) throws InterruptedException, IOException {
        int prediction;
        Classifier knn = new KNearestNeighbors(7);

        String filename = "src/main/resources/ml/training" + player.getAgentType() + ".csv";

        Dataset trainingData;
        try{
            trainingData = FileHandler.loadDataset(new File(filename), 12, ",");
        } catch(Exception e) {
            trainingData = new DefaultDataset();
        }

        System.out.println("    Set trainingData");

        if ( trainingData.size() < 10 ) {
            prediction = ThreadLocalRandom.current().nextInt(0, player.getHand().size());
        } else {
            prediction = Math.min(2, player.getHand().size());
        }

        System.out.println("    Made prediction: " + prediction);
        return prediction;
    }
}
