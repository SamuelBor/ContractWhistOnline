package component;

import java.util.Stack;
import java.util.ArrayList;

public abstract class Player {
  ArrayList<Card> pHand = new ArrayList<>();
  protected String name;
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

  public abstract Card makeTurn(int leadSuit, int trumpSuit, Stack playedCards);
}
