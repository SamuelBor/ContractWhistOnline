import component.Player;
import libsvm.SelfOptimizingLinearLibSVM;
import net.sf.javaml.classification.Classifier;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;
import net.sf.javaml.tools.data.FileHandler;

import java.io.*;
import java.util.concurrent.ThreadLocalRandom;

@SuppressWarnings("SameParameterValue")
public class Predictor {
    static int newPrediction(Player player, int PLAYER_COUNT) throws InterruptedException, IOException {
        int prediction;
        Classifier svm = new SelfOptimizingLinearLibSVM();

        String filenameTrain = "src/main/resources/ml/training" + player.getAgentType() + ".csv";
        String filenameClass = "src/main/resources/ml/classifier" + player.getAgentType() + ".ser";
        // String filename = "src/main/resources/ml/trainingTEST.csv";

        Dataset trainingData;
        try{
            // Loads the relevant CSV file as the training data
            trainingData = FileHandler.loadDataset(new File(filenameTrain), 11, ",");
        } catch(Exception e) {
            trainingData = new DefaultDataset();
        }

        // Sets up the instance as the test data with all the relevant current game information
        double[] testValues = new double[] {
                player.getHand().size(),
                player.getSameSuitSum(),
                player.getHandPointsSum(),
                player.getAceCount(),
                player.getKingCount(),
                player.getQueenCount(),
                player.getJackCount(),
                (player.getHand().size()*PLAYER_COUNT),
                player.getTrumpCallMin(),
                player.getP1Confidence(),
                player.getP2Confidence(),
                player.getPoints()
        };

        Instance instance = new DenseInstance(testValues);

        if ( trainingData.size() < 10 ) {
            // Provides some random values for the algorithm to learn from
            prediction = ThreadLocalRandom.current().nextInt(0, player.getHand().size());
        } else {
            // Once there are 10 entries in the field, the svm builds a classifier on the training data
            File f = new File(filenameClass);
            if(!f.exists()) {
                svm = new SelfOptimizingLinearLibSVM();
                System.out.println("Building Classifier");
                svm.buildClassifier(trainingData);
                System.out.println("Built Classifier");

                try {
                    FileOutputStream fileOut =
                            new FileOutputStream(filenameClass);
                    ObjectOutputStream out = new ObjectOutputStream(fileOut);
                    out.writeObject(svm);
                    out.close();
                    fileOut.close();
                    System.out.println("Serialized data is saved in " + filenameClass);
                } catch (IOException i) {
                    i.printStackTrace();
                }
            } else {
                try {
                    FileInputStream fileIn = new FileInputStream(filenameClass);
                    ObjectInputStream in = new ObjectInputStream(fileIn);
                    svm = (SelfOptimizingLinearLibSVM) in.readObject();
                    in.close();
                    fileIn.close();
                } catch (IOException i) {
                    i.printStackTrace();
                } catch (ClassNotFoundException c) {
                    System.out.println("Classifier class not found");
                    c.printStackTrace();
                }
            }

            // Then classifies based on the test data stored in the instance double
            Object classValue = svm.classify(instance);

            // Finally tries to cast into an int
            try{
                prediction = Integer.parseInt((String) classValue);
            } catch (Exception e) {
                System.out.println("Houston we have a problem");
                System.out.println(e);
                e.printStackTrace();
                prediction = (int) Math.floor((double) classValue);
            }
        }

        return prediction;
    }
}
