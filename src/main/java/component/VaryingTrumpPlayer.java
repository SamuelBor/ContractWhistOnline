package component;

import java.util.Stack;
import java.util.ArrayList;

//Similar to Max Player, but if no leadSuit cards exist in its hand and it has some trumps, only play the trump 70% of the time
@SuppressWarnings("Duplicates")
public class VaryingTrumpPlayer extends Player {

  public VaryingTrumpPlayer(String name, int id){
    super(name, id, "VAR");
    // Allows the option to set a trump variance percentage
    // TRUMP_PLAY = trumpPerc;
  }

  public Card makeTurn(int leadSuit, int trumpSuit, Stack<Card> playedCards, ArrayList<Card> allPlayedCards, int handSize){
    // Valid cards being an array list of legal cards following the lead suit
    ArrayList<Card> validCards = new ArrayList<Card>();
    ArrayList<Card> trumps = new ArrayList<Card>();
    // Initialises the return Card as an unreachable value, but this will always be overwritten
    Card returnCard = new Card(9,9);
    int highestValue = 0;
    // Flag showing whether or not the agent should aim to win or not, based on a comparison between current score and predicted winning
    boolean win = getWinIntention();

    // Random object
    double randCall = Math.random();
    // Trump Chance Percentage:
    double TRUMP_PLAY = 0.7;
    boolean chooseTrump = (randCall <= TRUMP_PLAY);

    for (Card card : pHand) {
      int currentSuit;

      currentSuit = card.getSuit();

      if(currentSuit==leadSuit){
        validCards.add(card);
      }

      if(currentSuit==trumpSuit){
        trumps.add(card);
      }

      // Tracks the full set of cards in the first loop in case no lead or trump cards are possessed.
      if(card.getValue() > highestValue && currentSuit != trumpSuit){
        highestValue = card.getValue();
        returnCard = card;
      }
    }

    highestValue = 0;
    int lowestValue = 100;

    if(validCards.size()!=0){

      for (Card validCard : validCards) {
        if(win){
          if (validCard.getValue() > highestValue) {
            highestValue = validCard.getValue();
            returnCard = validCard;
          }
        } else {
          if (validCard.getValue() < lowestValue) {
            lowestValue = validCard.getValue();
            returnCard = validCard;
          }
        }

      }

    } else if (trumps.size() != 0 && chooseTrump){

      // Play the highest trump if any exist and trying to win, otherwise picks the lowest trump
      for (Card trump : trumps) {
        if(win){
          if(trump.getValue() > highestValue){
            highestValue = trump.getValue();
            returnCard = trump;
          }
        } else {
          if(trump.getValue() < lowestValue){
            lowestValue = trump.getValue();
            returnCard = trump;
          }
        }

      }

    } else if (returnCard.getSuit()==9) {
      // If it gets to this point with the default value still stored in c then only trump cards remain
      // No other option exists, so choose one of the remaining trumps
      for(Card trump : trumps){
        // If trying to win pick the highest trump, otherwise pick the lowest
        if(win){
          if(trump.getValue() > highestValue){
            highestValue = trump.getValue();
            returnCard = trump;
          }
        } else {
          if(trump.getValue() < lowestValue){
            lowestValue = trump.getValue();
            returnCard = trump;
          }
        }

      }
    }

    //Removes the selected card from the players hand
    pHand.remove(returnCard);
    return returnCard;
  }



}
