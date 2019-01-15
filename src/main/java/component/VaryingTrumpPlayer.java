package component;

import java.util.Stack;
import java.util.Random;
import java.util.ArrayList;

//Similar to Max Player, but if no leadSuit cards exist in its hand and it has some trumps, only play the trump 70% of the time
public class VaryingTrumpPlayer extends Player {
  // Trump Chance Percentage:
  double TRUMP_PLAY = 0.7;

  public VaryingTrumpPlayer(String name){
    super(name);
    // Allows the option to set a trump variance percentage
    // TRUMP_PLAY = trumpPerc;
  }

  public Card makeTurn(int leadSuit, int trumpSuit, Stack playedCards){
    // Valid cards being an array list of legal cards following the lead suit
    ArrayList<Card> validCards = new ArrayList<Card>();
    ArrayList<Card> trumps = new ArrayList<Card>();
    // Initialises the return Card as an unreachable value, but this will always be overwritten
    Card returnCard = new Card(9,9);
    int highestValue = 0;

    // Random object
    double randCall = Math.random();
    Boolean chooseTrump = (randCall<=TRUMP_PLAY);

    for(int i =0; i<pHand.size(); i++){
      int currentSuit;
      Card c = (Card) pHand.get(i);
      currentSuit = c.getSuit();
      if(currentSuit==leadSuit){
        validCards.add(c);
      }

      if(currentSuit==trumpSuit){
        trumps.add(c);
      }

      // Tracks the full set of cards in the first loop in case no lead or trump cards are possessed.
      if(c.getValue() > highestValue && currentSuit != trumpSuit){
        highestValue = c.getValue();
        returnCard = c;
      }
    }
    highestValue = 0;

    if(validCards.size()!=0){

      for(int i=0; i<validCards.size(); i++){
        Card c = (Card) validCards.get(i);

        if(c.getValue() > highestValue){
          highestValue = c.getValue();
          returnCard = c;
        }
      }
    } else if (trumps.size()!=0 && chooseTrump){
      // Play the highest trump if any exist
      for(int i=0; i<trumps.size(); i++){
        Card c = (Card) trumps.get(i);

        if(c.getValue() > highestValue){
          highestValue = c.getValue();
          returnCard = c;
        }
      }
    } else if (returnCard.getSuit()==9) {
      // If it gets to this point with the default value still stored in c then only trump cards remain
      // No other option exists, so choose one of the remaining trumps
      for(int i=0; i<trumps.size(); i++){
        Card c = (Card) trumps.get(i);

        if(c.getValue() > highestValue){
          highestValue = c.getValue();
          returnCard = c;
        }
      }
    }

    //Removes the selected card from the players hand
    pHand.remove(returnCard);
    return returnCard;
  }

}
