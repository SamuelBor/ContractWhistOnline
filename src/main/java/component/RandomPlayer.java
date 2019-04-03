package component;

import java.util.Stack;
import java.util.Random;
import java.util.ArrayList;

// Plays a random legal card from their hand
public class RandomPlayer extends Player {
    public RandomPlayer(String name, int id){
        super(name, id, "RAN");
    }

    // No intentional losing attribute is implemented in the random agent as this would remove from its randomness, left to do what it wants
    public Card makeTurn(int leadSuit, int trumpSuit, Stack<Card> playedCards, ArrayList<Card> allPlayedCards, int handSize){
        ArrayList<Card> validCards = new ArrayList<Card>();
        Random rand = new Random();
        int randomIndex = 0;
        Card returnCard;
        boolean first = playedCards.empty();

        validCards = getValidCards(leadSuit, first)[0];

        if(validCards.size() == 0){
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
