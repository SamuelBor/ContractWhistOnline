import component.*;

import java.util.ArrayList;

class ContractWhistRunner {
    private static Deck d = new Deck();
    private static int PLAYER_COUNT = 3;
    private static int HAND_SIZE = 7;
    private static ArrayList<Player> players;

    static void playContractWhist(Trumps t, ArrayList p) throws InterruptedException {
        System.out.println("Playing Contract whist");
        int fullGames;
        players = p;

        for(fullGames = 0; fullGames<100; fullGames++){

            for(int handSize = 7; handSize>0; handSize--){
                HAND_SIZE = handSize;
                d = new Deck();
                dealCards(d);

                ContractWhistOnline.newHand();

                // Prediction
                Predictor.newPrediction(players);

                // Game Stage
                t.runTrumps(handSize, players);

//                for (Player player : players) {
////                    System.out.println(player.getName() + " won " + player.getPoints() + " hands.");
////                }

                // Scoring
                scorePlayers();
            }

            for(int handSize = 1; handSize<8; handSize++){
                HAND_SIZE = handSize;
                d = new Deck();
                dealCards(d);

                ContractWhistOnline.newHand();

                // Prediction
                Predictor.newPrediction(players);

                // Game Stage
                t.runTrumps(handSize, players);

//                for (Player player : players) {
////                    System.out.println(player.getName() + " won " + player.getPoints() + " hands.");
////                }

                // Scoring
                scorePlayers();
            }
        }
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
                System.out.println("Adding 10 for matching prediction");
                incrementAmount += 10;
            } else if(player.getPoints() < player.getPrediction()) {
                System.out.println("Removing " + (player.getPrediction() - player.getPoints()) + " for not making prediction");
                incrementAmount -= (player.getPrediction() - player.getPoints());
            }

            incrementAmount += (2* player.getPoints());
            System.out.println("Adding " + (2* player.getPoints()) + " for 2* number of hands.");

            player.increaseScore(incrementAmount);

            System.out.println(player.getName() + " scored " + incrementAmount + " points.");
            System.out.println("Giving them a total of " + player.getScore() + " points.");
            ContractWhistOnline.updateScore(player.getID(), player.getScore());

            // Updates the on screen current hand winnings to 0 at the end of each game round
            ContractWhistOnline.updateCurrentHands(0,0);
            ContractWhistOnline.updateCurrentHands(1,0);
            ContractWhistOnline.updateCurrentHands(2,0);
        }

    }
}
