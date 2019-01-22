package component;

import java.util.Stack;
import java.util.ArrayList;

// Plays the highest legal card from their hand, winning or otherwise
public class MaxPlayer extends Player {
  public MaxPlayer(String name, int id){
    super(name, id, "MAX");
  }

  public Card makeTurn(int leadSuit, int trumpSuit, Stack playedCards){
    // Valid cards being an array list of legal cards following the lead suit
    ArrayList<Card> validCards = new ArrayList<Card>();
    ArrayList<Card> trumps = new ArrayList<Card>();
    // Initialises the return Card as the Two of Clubs, but this will always be overwritten
    Card returnCard;
    // Flag showing whether or not the agent should aim to win or not, based on a comparison between current score and predicted winning
    boolean win = getPrediction() > getPoints();
    boolean first = playedCards.empty();

    if(!win){
      System.out.println(getName() + " is now trying to lose!");
    }

    //First Assign Card Scores
    assignCardScore(pHand, first, trumpSuit);

    // New loop with card scores in place to assign into sets of valid cards and trumps
    for (Card card : pHand) {
      int currentSuit;

      currentSuit = card.getSuit();
      if (currentSuit == leadSuit || first) {
        validCards.add(card);
      }

      if (currentSuit == trumpSuit) {
        trumps.add(card);
      }
    }

    Card highestCard = getHighest(pHand);
    Card lowestCard = getLowest(pHand);

    if(validCards.size()!=0){
      // Ternery statement to either assign the highest or lowest card to the return card
      returnCard = (win) ? getHighest(validCards) : getLowest(validCards);
    } else if (trumps.size()!=0){
      // Play the highest trump if any exist if trying to win
      // If trying to lose play the lowest card in the hand
      returnCard = (win) ? getHighest(trumps) : lowestCard;
    } else {
      // Play either the highest non-trump, non-lead card in the hand if winning or lowest if not
      returnCard = (win) ? highestCard : lowestCard;
    }

    //Removes the selected card from the players hand
    pHand.remove(returnCard);
    return returnCard;
  }
}
