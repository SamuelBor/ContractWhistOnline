package component;

import java.util.Stack;
import java.util.Random;
import java.util.ArrayList;

// Plays the lowest legal card that will win the round
// Takes the played cards into account and saves high cards if a loss is inevitable
public class MiniWinPlayer extends Player {
  public MiniWinPlayer(String name){
    super(name);
  }

  public Card makeTurn(int leadSuit, int trumpSuit, Stack playedCards){
    // Valid cards being an array list of legal cards following the lead suit
    ArrayList<Card> validCards = new ArrayList<Card>();
    ArrayList<Card> nonLeadCards = new ArrayList<Card>();
    ArrayList<Card> winningCards = new ArrayList<Card>();
    // Initialises the return Card as the Two of Clubs, but this will always be overwritten
    Card returnCard = new Card(2,1);
    int currentHighestScore =0;
    int lowestWin = 0;

    //If the player plays first then should play the highest card possible
    Boolean first = playedCards.empty();
    //Used for selecting a non winning card to throw away
    int lowestIndex = 0;
    int lowestScore = 100;

    // First finds the score of the current highest card that has been played
    while(!playedCards.empty()){
      Card c = (Card) playedCards.pop();

      c.setScore(c.getValue());

      if(c.getSuit()==leadSuit){
        c.setScore(c.getScore()+13);
      }

      if(c.getSuit()==trumpSuit){
        c.setScore(c.getScore()+26);
      }

      if(c.getScore()>currentHighestScore){
        currentHighestScore = c.getScore();
      }
    }

    // Compiles two array list of lead suit cards and non lead suit cards
    for(int i =0; i<pHand.size(); i++){
      int currentSuit;
      Card c = (Card) pHand.get(i);
      currentSuit = c.getSuit();
      if(currentSuit==leadSuit){
        validCards.add(c);
      } else {
        nonLeadCards.add(c);
      }
    }

    // First case is if the AI has lead suit cards in its hand to play with
    if(validCards.size()!=0){
      for(int i=0; i<validCards.size(); i++){
        Card c = (Card) validCards.get(i);
        c.setScore(c.getValue()+13);
        if(c.getSuit()==trumpSuit){
          c.setScore(c.getScore()+26);
        }

        if(c.getScore()>currentHighestScore){
          winningCards.add(c);
        }

        if(c.getScore() < lowestScore){
          lowestIndex = i;
          lowestScore = c.getScore();
        }
      }

      if(winningCards.size()==0){
        returnCard = (Card) validCards.get(lowestIndex);
      }
    } else {
      // Otherwise deal with the non lead set, trump or throwout
      for(int i=0; i<nonLeadCards.size(); i++){
        Card c = (Card) nonLeadCards.get(i);
        if(c.getSuit()==trumpSuit){
          c.setScore(c.getScore()+26);
        }

        if(c.getScore()>currentHighestScore){
          winningCards.add(c);
        }

        if(c.getScore() < lowestScore){
          lowestIndex = i;
          lowestScore = c.getScore();
        }
      }

      if(winningCards.size()==0){
        returnCard = (Card) nonLeadCards.get(lowestIndex);
      }
    }

    if(winningCards.size()>0){
      Card c;
      //Selects the highest card if playing first, otherwise the lowest winning card
      if(first){
        int highestScore = 0;
        int highestIndex = 0;

        for(int i=0;i<winningCards.size();i++){
          c = (Card) winningCards.get(i);
          if(c.getScore()>highestScore){
            highestScore = c.getScore();
            highestIndex = i;
          }
        }

        returnCard = (Card) winningCards.get(highestIndex);
      } else {
        lowestScore = 100;
        lowestIndex = 0;
        int i;
        for(i=0;i<winningCards.size();i++){
          c = (Card) winningCards.get(i);
          if(c.getScore()<lowestScore){
            lowestScore = c.getScore();
            lowestIndex = i;
          }
        }

        returnCard = (Card) winningCards.get(lowestIndex);
      }
    }

    //Removes the selected card from the players hand
    pHand.remove(returnCard);
    return returnCard;
  }
}
