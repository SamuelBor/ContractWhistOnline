import component.*;
import org.eclipse.jetty.websocket.api.Session;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

@SuppressWarnings("Duplicates")
class ContractWhistRunner {
    private static Deck d = new Deck();
    final private static int PLAYER_COUNT = 3;
    private static int HAND_SIZE = 7;
    final private static int ZERO_LIMIT = 5;
    private static ArrayList<Player> players = new ArrayList<>();
    static int TIME_DELAY = 1250;
    private static Trumps t = new Trumps();
    public static Session s; // Stores the session of the user using the instance of the runner

    void addAgents(String agentString, Session session) throws InterruptedException {
        s = session;
        players = new ArrayList<>();
        String[] parts = agentString.split(",");

        for(int i = 0; i<6 ; i=i+2){
            String agentType = parts[i];
            String agentName = parts[i+1];

            switch(agentType){
                case "1":
                    players.add(new MaxPlayer(agentName, (i/2)));
                    break;
                case "2":
                    players.add(new MiniWinPlayer(agentName, (i/2)));
                    break;
                case "3":
                    players.add(new RandomPlayer(agentName, (i/2)));
                    break;
                case "4":
                    players.add(new VaryingTrumpPlayer(agentName, (i/2)));
                    break;
                case "5":
                    players.add(new MonteCarloPlayer(agentName, (i/2)));
                    break;
            }
        }

        playContractWhist();
    }

    private static void playContractWhist() throws InterruptedException {
        System.out.println(ContractWhistOnline.userUsernameMap.get(s) + ": Playing Contract whist");
        int fullGames;
        Player firstCall;
        Ring<Player> playerRing = new Ring<>(players);
        Iterator playerIt = playerRing.iterator();

        for(fullGames = 0; fullGames<1000; fullGames++){
            // Ensures that the players can call the ZERO_LIMIT on each game
            for ( Player player : players ) {
                player.resetZeroesCalled();
            }

            System.out.println(ContractWhistOnline.userUsernameMap.get(s) + ": Starting Game: " + fullGames);

            for(int handSize = 7; handSize>0; handSize--){
                firstCall = (Player) playerIt.next();
                singleRound(handSize, t, firstCall);
            }

            for(int handSize = 1; handSize<8; handSize++){
                firstCall = (Player) playerIt.next();
                singleRound(handSize, t, firstCall);
            }
        }
    }

    private static void singleRound(int handSize, Trumps t, Player firstCall) throws InterruptedException {
        int trumpCallMin;
        int highCall = -1;
        int chosenTrumpSuit;
        String trumpStr = "";
        Player highCallPlayer = null;
        Ring<Player> playerRing = new Ring<>(players);
        Iterator playerIt = playerRing.iterator();
        Player queryPlayer = (Player) playerIt.next();
        ArrayList<Player> playerCalls = new ArrayList<>();

        for ( int i = 0; i < PLAYER_COUNT; i++){
            if(queryPlayer != firstCall){
                queryPlayer = (Player) playerIt.next();
            }
        }

        playerCalls.add(firstCall);
        playerCalls.add((Player) playerIt.next());
        playerCalls.add((Player) playerIt.next());

        HAND_SIZE = handSize;
        d = new Deck();
        dealCards(d);

        // Loops through players in a default order as order is irrelevant here
        for ( Player player : players ) {
            ContractWhistOnline.newHand(player, s);
            player.setPrediction(-1);
            player.setP1Confidence(-1.00);
            player.setP1Confidence(-1.00);
            player.analyseHand();
        }

        // Make sure this sum variable is not equal to HAND_SIZE
        int predSum = 0;
        int playerCount = 0;

        // Here loops through the players in an order as the first person to call their prediction has an advantage
        for ( Player player : playerCalls ) {
            playerCount++;
            trumpCallMin = Math.min(handSize, highCall+1);
            player.setTrumpCallMin( trumpCallMin );
            for ( Player subplayer : players ){
                if ( !subplayer.equals(player) ){
                    double confidence = (double) subplayer.getPrediction() / handSize;
                    if ( confidence < 0 ){
                        confidence = 0;
                    }

                    if( player.getP1Confidence() < 0 ){
                        player.setP1Confidence( confidence );
                    } else {
                        player.setP2Confidence( confidence );
                    }
                }
            }

            // Prediction
            // Once a prediction is made check that it obeys the required constraints
            // If not then try to deviate by as little as possible, either towards the trump control or below with a pessimistic view
            // System.out.println("Going into prediction phase.");

            //TODO Swap basic assignment back to predictor call
            //int pred = Predictor.newPrediction(player, PLAYER_COUNT);
            int pred = 2; // Used for demonstration purposes while classifier trains elsewhere

            if ( pred == 0 && player.getZeroesCalled()==ZERO_LIMIT ){
                pred = 1;
            }

            predSum += pred;

            if ( playerCount== PLAYER_COUNT && predSum == HAND_SIZE) {
                if( (pred+1)==player.getTrumpCallMin() ){
                    pred = pred + 1;
                } else {
                    pred = pred - 1;
                }
            }

            if (pred==0) {
                player.incrementZeroesCalled();
            }

            player.setPrediction(pred);
            ContractWhistOnline.makePrediction(player.getID(), player.getPrediction(), s);
            Thread.sleep(TIME_DELAY);

            if ( highCall < player.getPrediction() ) {
                highCall = player.getPrediction();
                highCallPlayer = player;
            }
        }

        // High call player will never be null as all players will predict at least 0
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
        
        System.out.println(ContractWhistOnline.userUsernameMap.get(s) + ": " + highCallPlayer.getName() + " set the trump as " + trumpStr);
        
        // Game Stage
        t.runTrumps(handSize, players, chosenTrumpSuit, s);

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
                    player.getPoints()
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

    private static void scorePlayers(){
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

            System.out.println(ContractWhistOnline.userUsernameMap.get(s) + ": " + player.getName() + " scored " + incrementAmount + " points.");
            System.out.println(ContractWhistOnline.userUsernameMap.get(s) + ": Giving them a total of " + player.getScore() + " points.");
            ContractWhistOnline.updateScore(player.getID(), player.getScore(), s);

            // Updates the on screen current hand winnings to 0 at the end of each game round
            ContractWhistOnline.updateCurrentHands(player.getID(),0, s);
        }

    }

    private static void outputToTrainingSet(String agentType, int cardsInHand, int sameSuitSum, int handPointSum, int aceCount, int kingCount, int queenCount, int jackCount, int cardsInPlay, int trumpCallMin, double p1Confidence, double p2Confidence, int handsWon) {
        BufferedWriter writer = null;
        String filename;
        String outRow = "";

        filename = "src/main/resources/ml/training" + agentType + ".csv";

        outRow += cardsInHand + ",";
        outRow += sameSuitSum + ",";
        outRow += handPointSum + ",";
        outRow += aceCount + ",";
        outRow += kingCount + ",";
        outRow += queenCount + ",";
        outRow += jackCount + ",";
        outRow += cardsInPlay + ",";
        outRow += trumpCallMin + ",";
        outRow += p1Confidence + ",";
        outRow += p2Confidence + ",";
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

    void changeSpeed(int level){
        switch(level){
            case 1:
                TIME_DELAY = 15000;
                break;
            case 2:
                TIME_DELAY = 3500;
                break;
            case 3:
                TIME_DELAY = 1250;
                break;
            case 4:
                TIME_DELAY = 300;
                break;
            case 5:
                TIME_DELAY = 0;
                break;
            default:
                System.out.println(ContractWhistOnline.userUsernameMap.get(s) + ": Error Changing Speed. Level " + level + " not recognised.");
        }
    }

}
