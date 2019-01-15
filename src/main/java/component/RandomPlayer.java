package component;

import java.util.Stack;
import java.util.Random;
import java.util.ArrayList;

// Plays a random legal card from their hand
public class RandomPlayer extends Player {
  public RandomPlayer(String name){
    super(name);
  }

  public Card makeTurn(int leadSuit, int trumpSuit, Stack playedCards){
    ArrayList<Card> validCards = new ArrayList<Card>();
    Random rand = new Random();
    int randomIndex =0;
    Card returnCard;

    for(int i =0; i<pHand.size(); i++){
      int currentSuit;
      Card c = (Card) pHand.get(i);
      currentSuit = c.getSuit();
      if(currentSuit==leadSuit){
        validCards.add(c);
      }
    }

    if(validCards.size()==0){
      randomIndex = rand.nextInt(pHand.size());
      returnCard = (Card) pHand.get(randomIndex);
    } else {
      randomIndex = rand.nextInt(validCards.size());
      returnCard = (Card) validCards.get(randomIndex);
    }

    //Removes the selected card from the players hand
    pHand.remove(returnCard);
    return returnCard;
  }
}
