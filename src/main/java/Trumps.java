import component.*;
import org.eclipse.jetty.websocket.api.Session;

import java.lang.Math;

///CHaDS
import java.util.*;

class Trumps {
  private static int HAND_SIZE = 7;
  private static int PLAYER_COUNT = 3;
  private int turn = 1;
  private Stack<Card> playedCards;
  private ArrayList<Card> allPlayedCards;
  private Stack<Integer> playersToGo = new Stack<>();
  private ArrayList<Player> players = new ArrayList<>();
  private int trumpSuit;
  private Session s; // Stores the session of the user using the instance of trumps

  void runTrumps(int handSize, ArrayList<Player> p, int trump, Session sess) throws InterruptedException{
     HAND_SIZE = handSize;
     this.trumpSuit = trump;
     this.players = p;
     this.s = sess;

     PLAYER_COUNT = players.size();

    //Adds players into concrete player stack for tracking turn
    if(players.size() > 0){
      for(int i = PLAYER_COUNT - 1; i>-1; i--){
        playersToGo.push(i);
        players.get(i).resetPoints();
      }

      restartGame();
    } else {
      System.out.println(ContractWhistOnline.userUsernameMap.get(s) + ": Error - No Players are Playing!");
    }
  }


  private Player restartGame() throws InterruptedException{
    playedCards = new Stack<>();
    allPlayedCards = new ArrayList<>();
    turn = 1;

    for(int i = PLAYER_COUNT - 1; i>-1; i--){
      playersToGo.push(i);
    }

    return takeTurn();
  }

  private Player takeTurn() throws InterruptedException{
    int leadSuit = 0;
    int topScore = 0;
    int topScorer = -1;
    int playerID;

    playedCards = new Stack<>();

    for(int i = 0; i<PLAYER_COUNT; i++){
      playerID = playersToGo.pop();

      //Phase 1 Delay - Select Player
      ContractWhistOnline.phase1Update(playerID, trumpSuit, Integer.toString(getCardsLeft()), s);
      System.out.println(ContractWhistOnline.userUsernameMap.get(s) + ": " + players.get(playerID).getName() + "'s Turn.");
      Thread.sleep(ContractWhistRunner.TIME_DELAY/2);

      //Get the player's hand before the chosen card is removed
      ArrayList preTurnHand = new ArrayList<>(players.get(playerID).getHand());
      Card cardInPlay = players.get(playerID).makeTurn(leadSuit, trumpSuit, playedCards, getAllPlayedCards(), HAND_SIZE);

      cardInPlay.setScore(cardInPlay.getValue());

      if(leadSuit==0){
        leadSuit = cardInPlay.getSuit();
      }

      if(cardInPlay.getSuit()==leadSuit){
        cardInPlay.setScore(cardInPlay.getScore()+14);
      }

      if(cardInPlay.getSuit()==trumpSuit){
        cardInPlay.setScore(cardInPlay.getScore()+28);
      }

      // Phase 2 Delay - Pick Card
      int cardIndex = preTurnHand.indexOf(cardInPlay);
      ContractWhistOnline.phase2Update(playerID, cardIndex, s);
      System.out.println(ContractWhistOnline.userUsernameMap.get(s) + ": " + players.get(playerID).getName() + " plays " + cardInPlay.toMiniString() + " for " + cardInPlay.getScore() + " points.");
      allPlayedCards.add(cardInPlay);
      Thread.sleep(ContractWhistRunner.TIME_DELAY);

      if(cardInPlay.getScore() > topScore){
        topScore = cardInPlay.getScore();
        topScorer = playerID;
      }

      playedCards.push(cardInPlay);
      // Phase 3 Delay - Play Card
      ContractWhistOnline.updateGame(cardInPlay.getFilename(), playerID, players.get(playerID).getHand(), s);
      Thread.sleep(ContractWhistRunner.TIME_DELAY/2);
    }

    ContractWhistOnline.showWinner(topScorer, s);
    Thread.sleep(ContractWhistRunner.TIME_DELAY);
    System.out.println(ContractWhistOnline.userUsernameMap.get(s) + ": " + players.get(topScorer).getName() + " has won this hand.");
    System.out.println();

    players.get(topScorer).incrementPoints();
    ContractWhistOnline.updateCurrentHands(topScorer, players.get(topScorer).getPoints(), s);

    return endTurn(topScorer);
  }

  private Player endTurn(int turnWinner) throws InterruptedException{
    turn++;

    if(turn <= HAND_SIZE){
      for(int i = turnWinner-1; i>-1; i--){
        playersToGo.push(i);
      }

      for(int i = PLAYER_COUNT-1; i>turnWinner; i--){
        playersToGo.push(i);
      }

      playersToGo.push(turnWinner);
      takeTurn();
    }

    return endGame();
  }

  private Player endGame(){
    // Initialise with a blank random player to be overwritten
    Player winningPlayer = new RandomPlayer("", -1);
    int highestPoints = 0;

    for(int i = 0; i<PLAYER_COUNT; i++){
      if(players.get(i).getPoints()>highestPoints){
        highestPoints = players.get(i).getPoints();
        winningPlayer = players.get(i);
      }
    }

    // winnerName = winningPlayer.getName();
    // System.out.println("The Overall Winner is " + winnerName + " after winning " + highestPoints + " hands.");
    // wonHands = highestPoints;
    return winningPlayer;
  }

  private int getCardsLeft(){
    return Math.min((HAND_SIZE - turn) + 1, HAND_SIZE);
  }

  private ArrayList<Card> getAllPlayedCards(){
    return allPlayedCards;
  }
}
