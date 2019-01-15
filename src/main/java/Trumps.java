import component.*;

///CHaDS
import java.util.*;

class Trumps {
  private int PLAYER_COUNT = 3;
  private int HAND_SIZE = 7;
  private int TIME_DELAY = 1250;
  private int turn;
  private Stack<Card> playedCards;
  private Deck d;
  private Stack<Integer> playersToGo = new Stack<>();
  private int firstPlayer;
  private ArrayList<Player> players = new ArrayList<>();

  Player runTrumps(int handSize, ArrayList<Player> p) throws InterruptedException{
     this.HAND_SIZE = handSize;
     this.players = p;

     PLAYER_COUNT = players.size();

    //Adds players into concrete player stack for tracking turn
    if(players.size() > 0){
      for(int i = PLAYER_COUNT - 1; i>-1; i--){
        playersToGo.push(i);
      }

      return restartGame();
    } else {
      System.out.println("Error - No Players are Playing!");
      return new RandomPlayer(""); //Null player
    }
  }


  private Player restartGame() throws InterruptedException{
    d = new Deck();
    playedCards = new Stack<>();
    turn = 1;

    for(int i = PLAYER_COUNT - 1; i>-1; i--){
      playersToGo.push(i);
    }

    firstPlayer = 0;

    dealCards(d);

    //Outputs all the players along with their newly dealt cards
    for(int i = 0; i<PLAYER_COUNT; i++){
      Player p;
      ArrayList h; //Stores the players' hand
      Card c;
      String hand;
      //players[i].setPoints(0);
      p = players.get(i);
      hand = "";
       //System.out.println(p.getName() + ":");
      h = p.getHand();

      for(int j=0; j<h.size();j++){
        c = (Card) h.get(j);
        hand += c.toMiniString() + " ";
      }

       //System.out.println(hand);
    }

    return takeTurn();
  }

  private Player takeTurn() throws InterruptedException{
    int leadSuit = 0;
    int trumpSuit = getTrump();
    int topScore = 0;
    int topScorer = -1;
    int playerID;

    playedCards = new Stack<>();

    for(int i = 0; i<PLAYER_COUNT; i++){
      playerID = playersToGo.pop();
      Card cardInPlay = players.get(playerID).makeTurn(leadSuit, trumpSuit, playedCards);

      //Phase 1 Delay - Select User

      ContractWhistOnline.phase1Update(playerID);
      System.out.println(players.get(playerID).getName() + "'s Turn.");
      Thread.sleep(TIME_DELAY/2);

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

      System.out.println(players.get(playerID).getName() + " plays " + cardInPlay.toMiniString() + " for " + cardInPlay.getScore() + " points.");
      Thread.sleep(TIME_DELAY);


      if(cardInPlay.getScore() > topScore){
        topScore = cardInPlay.getScore();
        topScorer = playerID;
      }

      playedCards.push(cardInPlay);
      // Phase 3 Delay - Play Card
      ContractWhistOnline.updateGame(Integer.toString(getCardsLeft()), cardInPlay.getFilename());
      Thread.sleep(TIME_DELAY/2);
    }

    ContractWhistOnline.showWinner(topScorer);
    Thread.sleep(TIME_DELAY/2);
    System.out.println(players.get(topScorer).getName() + " has won this hand.");
     //System.out.println();
    players.get(topScorer).incrementPoints();

    return endTurn(topScorer);
  }


  private int getTrump(){
    // Removes a card from the top of the deck and takes its suit to be the trump card
    Card c = d.getTopCard();
    String suit;
    suit = c.suitToString(true);

    //System.out.println("The Trump is [" + suit + "]");
    return c.getSuit();
  }

  private Player endTurn(int turnWinner) throws InterruptedException{
    System.out.println("Cards Left: " + getCardsLeft());
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
    Player winningPlayer = new RandomPlayer("");
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

  private void dealCards(Deck d){
    Card c;

    for(int i = 0; i<HAND_SIZE; i++){
      for(int j=0; j<PLAYER_COUNT; j++){
        c = d.getTopCard();
        players.get(j).addToHand(c);
      }
    }
  }

  int getCardsLeft(){
    return (HAND_SIZE - turn);
  }

  public void changeSpeed(int level){
    switch(level){
      case 1:
        TIME_DELAY = 4000;
        break;
      case 2:
        TIME_DELAY = 2500;
        break;
      case 3:
        TIME_DELAY = 1250;
        break;
      case 4:
        TIME_DELAY = 300;
        break;
      case 5:
        TIME_DELAY = 10;
        break;
      default:
        System.out.println("Error Changing Speed. Level " + level + " not recognised.");
    }
  }

}
