package component;

import java.util.Stack;
import java.util.Random;
import java.util.ArrayList;

// Plays the lowest legal card that will win the round
// Takes the played cards into account and saves high cards if a loss is inevitable

@SuppressWarnings("Duplicates")
public class MiniWinPlayer extends Player {
    public MiniWinPlayer(String name, int id){
        super(name, id, "MIN");
    }

    public Card makeTurn(int leadSuit, int trumpSuit, Stack<Card> playedCards, ArrayList<Card> allPlayedCards, int handSize){
        //If the player plays first then should play the highest card possible
        boolean first = playedCards.empty();
        // Valid cards being an array list of legal cards following the lead suit
        ArrayList<Card> validCards;
        ArrayList<Card> nonLeadCards;
        ArrayList<Card> winningCards = new ArrayList<Card>();
        // Initialises the return Card as the Two of Clubs, but this will always be overwritten
        Card returnCard = pHand.get(0);
        int highestPlayedScore = 0;
        // Flag showing whether or not the agent should aim to win or not, based on a comparison between current score and predicted winning
        boolean win = getPrediction() > getPoints();
        //Used for selecting a non winning card to throw away

        if(!win){
            System.out.println(getName() + " is now trying to lose!");
        }

        // Creates a reference of the playedCards Stack
        Stack<Card> refPlayedCards = new Stack<Card>();
        refPlayedCards.addAll(playedCards);

        //First Assign Card Scores
        assignCardScore(pHand, first, trumpSuit, leadSuit);

        // System.out.println("Assigned Card Scores");

        // Finds the score of the current highest card that has been played
        while(!refPlayedCards.empty()){
            Card c = refPlayedCards.pop();
            c.setScore(c.getValue());

            if(c.getSuit()==leadSuit){
                c.setScore(c.getScore()+13);
            }
            if(c.getSuit()==trumpSuit){
                c.setScore(c.getScore()+26);
            }
            if(c.getScore()>highestPlayedScore){
                highestPlayedScore = c.getScore();
            }
        }

        // System.out.println("Determined highest card played");

        // Compiles two array list of lead suit cards and non lead suit cards
        ArrayList[] cardSets = getValidCards(leadSuit, first);
        validCards = cardSets[0];
        nonLeadCards = cardSets[1];

        // System.out.println("Valid Cards: " + validCards);
        // System.out.println("Non-Valid Cards: " + nonLeadCards);

        // First case is if the AI has lead suit cards in its hand to play with
        if(validCards.size()>0){
            for(Card card : validCards){
                if(card.getScore()>highestPlayedScore){
                    winningCards.add(card);
                }
            }

            // No winning cards
            // Either set the return card to the lowest valid card if still trying to win
            // Or the highest valid card if trying to lose

            if(winningCards.size()==0){
                returnCard = (win) ? getLowest(validCards) : getHighest(validCards);
            }
        } else {
            // Otherwise deal with the non lead set, find any cards in the hand that can beat the highest played score
            for(Card card : nonLeadCards){
                if(card.getScore()>highestPlayedScore){
                    winningCards.add(card);
                }
            }

            if(winningCards.size()==0){
                returnCard = (win) ? getLowest(nonLeadCards) : getHighest(nonLeadCards);
            }
        }

        // System.out.println("Winning Cards: " + winningCards);

        if(winningCards.size()>0){
            //Selects the highest card if playing first, otherwise the lowest winning card
            if(first){
                returnCard = (win) ? getHighest(winningCards) : getLowest(winningCards);
            } else {
                // Either play the lowest winning card if trying to win
                // Or the highest losing card to destroy future chances
                if(win){
                    returnCard = getLowest(winningCards);
                } else {
                    if( winningCards.size() < pHand.size()){
                        ArrayList<Card> losingCards = new ArrayList<Card>(pHand);
                        losingCards.removeAll(winningCards);
                        returnCard = getHighest(losingCards);
                    } else {
                        // A win here is guaranteed so even though the agent has met their trick they might as well
                        // Aim for as many hands as they can, so pick the lowest of the winning cards
                        returnCard = getLowest(winningCards);
                    }
                }
            }
        }

        //Removes the selected card from the players hand
        pHand.remove(returnCard);
        return returnCard;
    }
}
