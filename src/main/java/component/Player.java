package component;

import java.util.Stack;
import java.util.ArrayList;

public abstract class Player {
  protected ArrayList<Card> pHand = new ArrayList<Card>();
  protected String name = "Bob";
  private int points = 0;

  public Player(String name){
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

  public void addToHand(Card c){
    pHand.add(c);
  }

  public void incrementPoints(){
    points++;
  }

  public void setPoints(int points){
    this.points = points;
  }

  public abstract Card makeTurn(int leadSuit, int trumpSuit, Stack playedCards);
}
