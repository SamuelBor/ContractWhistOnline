import component.*;

import java.io.*;
import java.util.ArrayList;

class ContractWhistRunner {
    private static Deck d = new Deck();
    private static int PLAYER_COUNT = 3;
    private static int HAND_SIZE = 7;
    private static ArrayList<Player> players;
    static int TIME_DELAY = 1250;

    static void playContractWhist(Trumps t, ArrayList p) throws InterruptedException, IOException {
        System.out.println("Playing Contract whist");
        int fullGames;
        players = p;

        for(fullGames = 0; fullGames<3; fullGames++){
            for(int handSize = 7; handSize>0; handSize--){
                singleRound(handSize, t);
            }

            for(int handSize = 1; handSize<8; handSize++){
                singleRound(handSize, t);
            }
        }
    }

    static void singleRound(int handSize, Trumps t) throws InterruptedException, IOException {
        int trumpCallMin;
        int highCall = -1;
        int chosenTrumpSuit;
        String trumpStr = "";
        Player highCallPlayer = null;

        HAND_SIZE = handSize;
        d = new Deck();
        dealCards(d);

        ContractWhistOnline.newHand();

        for(Player player : players) {
            player.setPrediction(-1);
            player.setP1Confidence(-1.00);
            player.setP1Confidence(-1.00);
            player.analyseHand();
            System.out.println("Hand Analysed.");
        }

        for (Player player : players) {
            trumpCallMin = Math.min(handSize, highCall+1);
            player.setTrumpCallMin(trumpCallMin);
            for (Player subplayer : players){
                if(!subplayer.equals(player)){
                    double confidence = (double) subplayer.getPrediction() / handSize;
                    if(confidence < 0){
                        confidence = 0;
                    }

                    if(player.getP1Confidence()<0){
                        player.setP1Confidence(confidence);
                    } else {
                        player.setP2Confidence(confidence);
                    }
                }
            }

            // Prediction
            System.out.println("Going into prediction phase.");
            Predictor.newPrediction(player, TIME_DELAY);
            System.out.println("Got out of prediction phase.");
            if ( highCall < player.getPrediction() ) {
                highCall = player.getPrediction();
                highCallPlayer = player;
            }
        }

        // High call player will never be null as all players will predict at least 0
        assert highCallPlayer != null;
        chosenTrumpSuit = highCallPlayer.getChosenTrump();

        switch(chosenTrumpSuit){
            case 1:  trumpStr = "♣";
                break;
            case 2:  trumpStr = "♥";
                break;
            case 3:  trumpStr = "♦";
                break;
            case 4:  trumpStr = "♠";
                break;
        }
        
        System.out.println(highCallPlayer.getName() + " set the trump as " + trumpStr);
        
        // Game Stage
        t.runTrumps(handSize, players, chosenTrumpSuit);

        for (Player player : players) {

            outputToTrainingSet(
                    player.getAgentType(),
                    handSize,
                    player.getSameSuitSum(),
                    player.getHandPointsSum(),
                    player.getAceCount(),
                    player.getKingCount(),
                    player.getQueenCount(),
                    player.getJackCount(),
                    (handSize*PLAYER_COUNT),
                    player.getTrumpCallMin(),
                    player.getP1Confidence(),
                    player.getP2Confidence(),
                    player.getPoints(),
                    player.getPrediction()
                    );
        }

        // Scoring
        scorePlayers();
    }

    static Deck getDeck() {
        return d;
    }

    private static void dealCards(Deck d){
        Card c;

        for(int i = 0; i<HAND_SIZE; i++){
            for(int j=0; j<PLAYER_COUNT; j++){
                c = d.getTopCard();
                players.get(j).addToHand(c);
            }
        }
    }

    static void scorePlayers(){
        int incrementAmount;

        for (Player player : players) {
            incrementAmount = 0;

            if(player.getPoints() == player.getPrediction()){
                // System.out.println("Adding 10 for matching prediction");
                incrementAmount += 10;
            } else if(player.getPoints() < player.getPrediction()) {
                // System.out.println("Removing " + (player.getPrediction() - player.getPoints()) + " for not making prediction");
                incrementAmount -= (player.getPrediction() - player.getPoints());
            }

            incrementAmount += (2* player.getPoints());
            // System.out.println("Adding " + (2* player.getPoints()) + " for 2* number of hands.");

            player.increaseScore(incrementAmount);

            System.out.println(player.getName() + " scored " + incrementAmount + " points.");
            System.out.println("Giving them a total of " + player.getScore() + " points.");
            ContractWhistOnline.updateScore(player.getID(), player.getScore());

            // Updates the on screen current hand winnings to 0 at the end of each game round
            ContractWhistOnline.updateCurrentHands(player.getID(),0);
        }

    }

    static void outputToTrainingSet(String agentType, int cardsInHand, int sameSuitSum, int handPointSum, int aceCount, int kingCount, int queenCount, int jackCount, int cardsInPlay, int trumpCallMin, double p1Confidence, double p2Confidence, int handsWon, int prediction) {
        BufferedWriter writer = null;
        String filename;
        String outRow = "";

        filename = "src/main/resources/ml/training" + agentType + ".csv";

        outRow += cardsInHand;
        outRow += ",";
        outRow += sameSuitSum;
        outRow += ",";
        outRow += handPointSum;
        outRow += ",";
        outRow += aceCount;
        outRow += ",";
        outRow += kingCount;
        outRow += ",";
        outRow += queenCount;
        outRow += ",";
        outRow += jackCount;
        outRow += ",";
        outRow += cardsInPlay;
        outRow += ",";
        outRow += trumpCallMin;
        outRow += ",";
        outRow += p1Confidence;
        outRow += ",";
        outRow += p2Confidence;
        outRow += ",";
        outRow += prediction;
        outRow += ",";
        outRow += handsWon;
        outRow += "\n";

        try {
            File trainingFile = new File(filename);
            writer = new BufferedWriter(new FileWriter(trainingFile, true));
            writer.write(outRow);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // Close the writer regardless of success
                writer.close();
            } catch (Exception e) {
            }
        }
    }

}
