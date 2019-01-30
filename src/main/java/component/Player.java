package component;

import java.util.Stack;
import java.util.ArrayList;

public abstract class Player {
  ArrayList<Card> pHand = new ArrayList<>();
  private String name;
  private int prediction = 0;
  private int points = 0; // Points earned round by round in the gameplay stage
  private int score = 0; // Score based on the accuracy of the agent's prediction
  private int id;
  private String agentType;

  Player(String name, int id, String type){
    this.name = name;
    this.id = id;
    this.agentType = type;
  }

  public String getName(){
    return name;
  }

  public int getPoints(){
    return points;
  }

  public ArrayList getHand(){
    return pHand;
  }

  public int getPrediction(){
    return prediction;
  }

  public int getScore(){
    return score;
  }

  public String getAgentType() {
    return agentType;
  }

  public int getID(){
    return id;
  }

  public void increaseScore(int amount){
    score += amount;
  }

  public void setPrediction(int p) {
    prediction = p;
  }

  public void addToHand(Card c){
    pHand.add(c);
  }

  public void incrementPoints(){
    points++;
  }

  public void resetPoints(){
    points = 0;
  }

  public void setPoints(int points){
    this.points = points;
  }

  Card getLowest(ArrayList<Card> cardSet){
    Card lowest = cardSet.get(0);

    for(Card card : cardSet){
      if(card.getScore()<lowest.getScore()){
        lowest = card;
      }
    }

    return lowest;
  }

  Card getHighest(ArrayList<Card> cardSet){
    Card highest = cardSet.get(0);

    for(Card card : cardSet){
      if(card.getScore()>highest.getScore()){
        highest = card;
      }
    }

    return highest;
  }

  void assignCardScore(ArrayList<Card> hand, boolean first, int trumpSuit, int leadSuit){
    for (Card card : hand) {
      card.setScore(card.getValue());
      if(first || card.getSuit() == leadSuit){
        // Lead Card so extra points for setting suit
        card.setScore(card.getScore()+13);
      }
      if(card.getSuit()==trumpSuit){
        card.setScore(card.getScore()+26);
      }
    }
  }

  ArrayList[] getValidCards(int leadSuit, boolean first){
    ArrayList[] cardSets = new ArrayList[2];

    // Initialises arraylists so they can actually be used
    cardSets[0] = new ArrayList<Card>();
    cardSets[1] = new ArrayList<Card>();

    // System.out.println("Getting valid cards.");

    for(Card card : pHand){
      int currentSuit;

      // System.out.println("Looking at " + card.toMiniString());

      currentSuit = card.getSuit();
      if(currentSuit==leadSuit || first){
        // Valid Cards
        cardSets[0].add(card);
      } else {
        cardSets[1].add(card);
      }
    }

    return cardSets;
  }

  public abstract Card makeTurn(int leadSuit, int trumpSuit, Stack<Card> playedCards, ArrayList<Card> allPlayedCards);
}
