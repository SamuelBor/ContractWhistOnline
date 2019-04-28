import component.*;
import org.eclipse.jetty.websocket.api.Session;

import java.lang.Math;

///CHaDS - Order of suit enumeration
import java.util.*;

class Trumps {
  private String user;
  private int HAND_SIZE = 7;
  private int PLAYER_COUNT = 3;
  private int turn = 1;
  private Stack<Card> playedCards;
  private ArrayList<Card> allPlayedCards;
  private Stack<Integer> playersToGo = new Stack<>();
  private ArrayList<Player> players = new ArrayList<>();
  private int trumpSuit;

  void runTrumps(int handSize, ArrayList<Player> p, int trump, String u) throws InterruptedException{
     HAND_SIZE = handSize;
     this.trumpSuit = trump;
     this.players = p;
     user = u;

     PLAYER_COUNT = players.size();

    //Adds players into concrete player stack for tracking turn
    if(players.size() > 0){
      for(int i = PLAYER_COUNT - 1; i>-1; i--){
        playersToGo.push(i);
        players.get(i).resetPoints();
      }

      restartGame();
    } else {
      System.out.println(user + ": Error - No Players are Playing!");
    }
  }

  // Restarts game by reinitialising relevant variables and lists
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

      // Phase 1 Delay - Select Player
      ContractWhistOnline.phase1Update(playerID, trumpSuit, Integer.toString(getCardsLeft()), ContractWhistOnline.getSession(user));
      Thread.sleep(getTimeDelay()/2);

      // Get the player's hand before the chosen card is removed
      ArrayList preTurnHand = new ArrayList<>(players.get(playerID).getHand());
      Card cardInPlay = players.get(playerID).makeTurn(leadSuit, trumpSuit, playedCards, getAllPlayedCards(), HAND_SIZE);

      cardInPlay.setScore(cardInPlay.getValue());

      // If no lead suit has been selected, this card sets the new lead suit
      if(leadSuit==0){
        leadSuit = cardInPlay.getSuit();
      }

      // Add point bonuses for being in the lead suit or trump suit
      if(cardInPlay.getSuit()==leadSuit){
        cardInPlay.setScore(cardInPlay.getScore()+14);
      }

      if(cardInPlay.getSuit()==trumpSuit){
        cardInPlay.setScore(cardInPlay.getScore()+28);
      }

      // Phase 2 Delay - Pick Card
      int cardIndex = preTurnHand.indexOf(cardInPlay);
      ContractWhistOnline.phase2Update(playerID, cardIndex, ContractWhistOnline.getSession(user));
      System.out.println(user + ": " + players.get(playerID).getName() + " plays " + cardInPlay.toMiniString() + " for " + cardInPlay.getScore() + " points.");
      allPlayedCards.add(cardInPlay);

      Thread.sleep(getTimeDelay());

      if(cardInPlay.getScore() > topScore){
        topScore = cardInPlay.getScore();
        topScorer = playerID;
      }

      playedCards.push(cardInPlay);
      // Phase 3 Delay - Play Card
      ContractWhistOnline.updateGame(cardInPlay.getFilename(), playerID, players.get(playerID).getHand(), ContractWhistOnline.getSession(user));
      Thread.sleep(getTimeDelay()/2);
    }

    // Show the winner on the interface
    ContractWhistOnline.showWinner(topScorer, ContractWhistOnline.getSession(user));
    Thread.sleep(getTimeDelay()/2);
    System.out.println(user + ": " + players.get(topScorer).getName() + " has won this hand.");
    System.out.println();

    players.get(topScorer).incrementPoints();
    ContractWhistOnline.updateCurrentHands(topScorer, players.get(topScorer).getPoints(), ContractWhistOnline.getSession(user));

    return endTurn(topScorer);
  }

  // End the turn
  private Player endTurn(int turnWinner) throws InterruptedException{
    turn++;

    // Play the next turn if needed
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

    // Otherwise end the game
    return endGame();
  }

  // endGame method contains no spoilers for Avengers EndGame
  // Although that movie was great
  // Any similarity to movies living or dead is purely coincidental
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

  private int getTimeDelay() {
    Session s = ContractWhistOnline.getSession(user);
    ContractWhistRunner c = ContractWhistOnline.sessionGames.get(s);
    return c.getTimeDelay();
  }

  private ArrayList<Card> getAllPlayedCards(){
    return allPlayedCards;
  }
}
