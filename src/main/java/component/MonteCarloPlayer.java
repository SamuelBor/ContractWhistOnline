package component;
import java.util.*;

// Builds upon the minimax agent but adds probability theory on top, keeping track of past cards and calculating probability that a played card will beat unseen cards.
@SuppressWarnings("Duplicates")
public class MonteCarloPlayer extends Player {
    private ArrayList<Card> myPlayedLeadCards = new ArrayList<Card>();
    private ArrayList<Card> seenCards;
    private ArrayList<Card> allPlayedCards;
    private int handSize;

    double PROB_THRESHOLD = 0.7;

    public MonteCarloPlayer(String name, int id){
        super(name, id, "MONTE");
    }

    public Card makeTurn(int leadSuit, int trumpSuit, Stack<Card> playedCards, ArrayList<Card> allPlayedCards, int handSize){
        Card returnCard = new Card(-1,-1); // Error case, shouldn't ever hit this point
        this.handSize = handSize;

        if(allPlayedCards.size() < 3) {
            myPlayedLeadCards = new ArrayList<Card>();
        }

        this.allPlayedCards = new ArrayList<Card>(allPlayedCards);

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

        for (Card c : myPlayedLeadCards){
            int indexOfC = allPlayedCards.indexOf(c);
            int cSuit = c.getSuit();

            // Checks the 2 cards after c in the playedCards arraylist to see if the suits match
            // If not then the other players do not possess any of the leadSuit, so the entire suit can be added to seenCards
            if (allPlayedCards.get(indexOfC+1).getSuit() != cSuit && allPlayedCards.get(indexOfC+2).getSuit() != cSuit){
                addSuitToSeen(cSuit, first, trumpSuit, leadSuit);
            }
        }

        if(!win){
            // System.out.println(getName() + " is now trying to lose!");
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

        // First case is if the agent has lead suit cards in its hand to play with
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
                    /*
                    ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                    Calculate probability of each winning card winning against future played cards, using cards seen and chance of better cards being used
                    First split the winning cards into 2 bands
                    Band1: 0%-50% , Band2: 75%-100%

                    If the distance to the agent's prediction is greater than 1 then play the card most likely to win in order to raise the agent's score
                    When the agent is only one away from the prediction start playing lower percentage cards in order to avoid overshooting the prediction
                    */

                    HashMap<Card, Double> band1 = new HashMap<>();
                    HashMap<Card, Double> band2 = new HashMap<>();

                    for (Card c : winningCards) {
                        double winChance = getWinChance(getBetterCards(c, trumpSuit, leadSuit), getPlayersLeft());
                        winChance = winChance * 100;
                        // System.out.println("Chance of " + c.toMiniString() + " winning is " + String.format("%.2f", winChance) + "%");

                        if ( winChance >= 0.00 && winChance < 50.00 ) {
                            band1.put(c, winChance);
                        } else if ( winChance >= 50.00 && winChance <=100.00 ) {
                            band2.put(c, winChance);
                        } else {
                            System.out.println("WinChance is not between 0 and 100.");
                        }
                    }

                    if ( getPrediction()-getScore() > 1 ) {
                        if ( band2.size() > 0 ){
                            returnCard = getHighestFromHashMap(band2);
                        } else {
                            returnCard = getHighestFromHashMap(band1);
                        }
                    } else {
                        if ( band1.size() > 0 ){
                            returnCard = getLowestFromHashMap(band1);
                        } else {
                            returnCard = getLowestFromHashMap(band2);
                        }
                    }
                } else {
                    if( winningCards.size() < pHand.size() ) {
                        ArrayList<Card> losingCards = new ArrayList<Card>(pHand);
                        losingCards.removeAll(winningCards);
                        returnCard = getHighest(losingCards);
                    } else {
                        // A win here is guaranteed so even though the agent has met their trick they might as well
                        // Aim for as many hands as they can, so pick the lowest of the winning cards in order to minimax it
                        returnCard = getLowest(winningCards);
                    }
                }
            }
        }

        if(first){
            myPlayedLeadCards.add(returnCard);
        }

        // Error checking
        if ( returnCard.getValue() > 14 || returnCard.getValue() < 2) {
            System.out.println("Invalid return card.");
        }

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
        int turnsTaken = handSize - pHand.size();
        int playersPlayed = allPlayedCards.size() - (3*turnsTaken);

        playersLeft = 2 - playersPlayed;

        if(playersLeft != 0 && playersLeft != 1 && playersLeft != 2){
            System.out.println("Erroneous scenario");
        }

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

        if((winChance*100) < 0.01){
            System.out.println("Almost impossible to win");
        }

        return winChance;
    }

    private void addSuitToSeen(int suit, boolean first, int trumpSuit, int leadSuit){
        ArrayList<Card> suitSet = new ArrayList<Card>();

        for ( int i = 2; i<15; i++){
            suitSet.add(new Card(i, suit));
        }

        assignCardScore(suitSet, first, trumpSuit, leadSuit);

        seenCards.removeAll(suitSet);
        seenCards.addAll(suitSet);
    }

    private Card getLowestFromHashMap (HashMap<Card, Double> cardBand){
        Set set = cardBand.entrySet();
        Iterator iterator = set.iterator();
        Double winChanceLow = 999.00;
        Card cardLow = new Card(-1,-1);
        while(iterator.hasNext()) {
            Map.Entry mentry = (Map.Entry)iterator.next();
            if((Double) mentry.getValue() < winChanceLow){
                winChanceLow = (Double) mentry.getValue();
                cardLow = (Card) mentry.getKey();
            }
        }

        return cardLow;
    }

    private Card getHighestFromHashMap (HashMap<Card, Double> cardBand){
        Set set = cardBand.entrySet();
        Iterator iterator = set.iterator();
        Double winChanceHigh = -999.00;
        Card cardHigh = new Card(-1,-1);
        while(iterator.hasNext()) {
            Map.Entry mentry = (Map.Entry)iterator.next();
            if((Double) mentry.getValue() > winChanceHigh){
                winChanceHigh = (Double) mentry.getValue();
                cardHigh = (Card) mentry.getKey();
            }
        }

        return cardHigh;
    }
}