package component;
import java.util.Stack;
import java.util.ArrayList;

// Builds upon the minimax agent but adds probability theory on top, keeping track of past cards and calculating probability that a played card will beat unseen cards.
@SuppressWarnings("Duplicates")
public class MonteCarloPlayer extends Player {
    ArrayList<Card> seenCards;
    double PROB_THRESHOLD = 0.7;

    public MonteCarloPlayer(String name, int id){
        super(name, id, "MONTE");
    }

    public Card makeTurn(int leadSuit, int trumpSuit, Stack<Card> playedCards, ArrayList<Card> allPlayedCards){
        Card returnCard = new Card(1,1); // Error case, shouldn't ever hit this point
        ArrayList<Card> validCards;
        ArrayList<Card> nonLeadCards;
        ArrayList<Card> winningCards = new ArrayList<Card>();
        // Flag showing whether or not the agent should aim to win or not, based on a comparison between current score and predicted winning
        boolean win = getPrediction() > getPoints();
        boolean first = playedCards.empty();
        int highestPlayedScore = 0;

        assignCardScore(pHand, first, trumpSuit, leadSuit);

        // Creates a reference of the playedCards Stack
        Stack<Card> refPlayedCards = new Stack<Card>();
        refPlayedCards.addAll(playedCards);

        // Assign all played cards and cards in hand to seenCards
        seenCards = new ArrayList<Card>(allPlayedCards);
        seenCards.addAll(pHand);


        if(!win){
            System.out.println(getName() + " is now trying to lose!");
        }

        // Compiles two array list of lead suit cards and non lead suit cards
        ArrayList[] cardSets = getValidCards(leadSuit, first);
        validCards = cardSets[0];
        nonLeadCards = cardSets[1];

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

        System.out.println("Winning Cards: " + winningCards);

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
                    ArrayList<Card> losingCards = new ArrayList<Card>(pHand);
                    losingCards.removeAll(winningCards);
                    returnCard = getHighest(losingCards);
                }
            }

            for (Card c : winningCards) {
                double winChance = getWinChance(getBetterCards(c, trumpSuit, leadSuit), getPlayersLeft());
                winChance = winChance * 100;
                System.out.println("Chance of " + c.toMiniString() + " winning is " + String.format("%.2f", winChance) + "%");
            }
        }

        /*

        if(playedCards.size()<2){
            if (first){
                boolean existsTrump = false;
                for(Card card : pHand){
                    // Checks for a card in the trump suit in the hand
                    if(card.getSuit() == trumpSuit){
                        existsTrump = true;
                    }
                }

                if(existsTrump){
                    // trumpSuit == leadSuit
                    returnCard = trumpIsLead();
                } else {
                    // trumpSuit != leadSuit
                    returnCard = trumpNotLead();
                }

            } else if (trumpSuit == leadSuit){
                returnCard = trumpIsLead();

            } else {
                // Not playing first and trumpSuit != leadSuit
                returnCard = trumpNotLead();
            }
        }


        */


        //Removes the selected card from the players hand
        pHand.remove(returnCard);
        return returnCard;
    }

    private ArrayList<Card> getBetterCards(Card c, int trumpSuit, int leadSuit){
        ArrayList<Card> betterCards = new ArrayList<Card>();
        ArrayList<Card> deckList = new ArrayList<Card>();
        Deck pointsDeck = new Deck();
        int queryScore = c.getScore();

        for (int i = 0; i<52; i++){
            Card card = pointsDeck.getTopCard();
            deckList.add(card);
        }

        // First value assigned as false as analysing cards to be played after Monte
        // Meaning they will never be first
        // If leadSuit = 0 then Monte is going first, so test the better cards using the potential lead suit value
        if (leadSuit == 0){
            assignCardScore(deckList, false, trumpSuit, c.getSuit());
        } else {
            assignCardScore(deckList, false, trumpSuit, leadSuit);
        }


        for (Card deckCard : deckList) {
            int currentCardScore = deckCard.getScore();
            // System.out.println(deckCard.toMiniString() + " : " + deckCard.getScore());

            if ( currentCardScore > queryScore ) {
                betterCards.add(deckCard);
            }
        }

        // System.out.println("The cards better than " + c.toMiniString() + " are: ");
        // System.out.println(betterCards);

        return betterCards;
    }

    private int getPlayersLeft () {
        int playersLeft;
        int otherCardsSeen = seenCards.size() - 7;
        int turnsTaken = 7 - pHand.size();
        int playersPlayed = otherCardsSeen - (2*turnsTaken);

        playersLeft = 2 - playersPlayed;

        return playersLeft;
    }

    private double getWinChance (ArrayList<Card> betterCards, int playersLeft) {
        ArrayList<Card> unseenBetterCards = new ArrayList<Card>(betterCards);
        unseenBetterCards.removeAll(seenCards);

        int unseenBetterCardsCount = unseenBetterCards.size();
        double chanceOfBetterCard = (double) unseenBetterCardsCount / (double) (52-seenCards.size());
        // System.out.println("The chance of a better card coming out is: " + chanceOfBetterCard);
        // Calculates the number of possible cards that could be played this round
        int possibleCards = pHand.size() * playersLeft;

        // 1 - losing chance
        double winChance = Math.pow((1 - (chanceOfBetterCard)),(possibleCards));

        return winChance;
    }

}