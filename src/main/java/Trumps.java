import component.*;
import java.lang.Math;

///CHaDS
import java.util.*;

class Trumps {
  private static int HAND_SIZE;
  private static int PLAYER_COUNT = 3;
  private int turn;
  private Stack<Card> playedCards;
  private ArrayList<Card> allPlayedCards;
  private Stack<Integer> playersToGo = new Stack<>();
  private ArrayList<Player> players = new ArrayList<>();
  private int trumpSuit;

  Player runTrumps(int handSize, ArrayList<Player> p, int trump) throws InterruptedException{
     HAND_SIZE = handSize;
     this.trumpSuit = trump;
     this.players = p;

     PLAYER_COUNT = players.size();

    //Adds players into concrete player stack for tracking turn
    if(players.size() > 0){
      for(int i = PLAYER_COUNT - 1; i>-1; i--){
        playersToGo.push(i);
        players.get(i).resetPoints();
      }

      return restartGame();
    } else {
      System.out.println("Error - No Players are Playing!");
      return new RandomPlayer("", -1); //Null player
    }
  }


  private Player restartGame() throws InterruptedException{
    playedCards = new Stack<>();
    allPlayedCards = new ArrayList<Card>();
    turn = 1;

    for(int i = PLAYER_COUNT - 1; i>-1; i--){
      playersToGo.push(i);
    }

    //Outputs all the players along with their newly dealt cards
    for(int i = 0; i<PLAYER_COUNT; i++){
      Player p;
      ArrayList h; //Stores the players' hand
      Card c;
      // String hand;
      //players[i].setPoints(0);
      p = players.get(i);
      // hand = "";
      System.out.println(p.getName() + ":");
      h = p.getHand();

      for(int j=0; j<h.size();j++){
        c = (Card) h.get(j);
        // hand += c.toMiniString() + " ";
      }

       // System.out.println(hand);
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
      ContractWhistOnline.phase1Update(playerID, trumpSuit, Integer.toString(getCardsLeft()));
      System.out.println(players.get(playerID).getName() + "'s Turn.");
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
      ContractWhistOnline.phase2Update(playerID, cardIndex);
      System.out.println(players.get(playerID).getName() + " plays " + cardInPlay.toMiniString() + " for " + cardInPlay.getScore() + " points.");
      allPlayedCards.add(cardInPlay);
      Thread.sleep(ContractWhistRunner.TIME_DELAY);

      if(cardInPlay.getScore() > topScore){
        topScore = cardInPlay.getScore();
        topScorer = playerID;
      }

      playedCards.push(cardInPlay);
      // Phase 3 Delay - Play Card
      ContractWhistOnline.updateGame(cardInPlay.getFilename(), playerID, players.get(playerID).getHand());
      Thread.sleep(ContractWhistRunner.TIME_DELAY/2);
    }

    ContractWhistOnline.showWinner(topScorer);
    Thread.sleep(ContractWhistRunner.TIME_DELAY);
    System.out.println(players.get(topScorer).getName() + " has won this hand.");
    System.out.println();

    players.get(topScorer).incrementPoints();
    ContractWhistOnline.updateCurrentHands(topScorer, players.get(topScorer).getPoints());

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

  int getCardsLeft(){
    return Math.min((HAND_SIZE - turn) + 1, HAND_SIZE);
  }

  void changeSpeed(int level){
    switch(level){
      case 1:
        ContractWhistRunner.TIME_DELAY = 15000;
        break;
      case 2:
        ContractWhistRunner.TIME_DELAY = 3500;
        break;
      case 3:
        ContractWhistRunner.TIME_DELAY = 1250;
        break;
      case 4:
        ContractWhistRunner.TIME_DELAY = 300;
        break;
      case 5:
        ContractWhistRunner.TIME_DELAY = 0;
        break;
      default:
        System.out.println("Error Changing Speed. Level " + level + " not recognised.");
    }
  }

  ArrayList<Card> getAllPlayedCards(){
    return allPlayedCards;
  }
}
