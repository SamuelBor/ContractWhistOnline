package component;

import java.util.Stack;
import java.util.ArrayList;

public abstract class Player {
  ArrayList<Card> pHand = new ArrayList<>();
  protected String name;
  private int prediction = 0;
  private int points = 0;
  private int score = 0;

  Player(String name){
    this.name = name;
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
