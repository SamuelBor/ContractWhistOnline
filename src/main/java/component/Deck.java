package component;

import java.util.Stack;
import java.util.Random;
import java.util.ArrayList;

public class Deck {
  private Stack<Card> deckStack;
  private Random rand = new Random();
  private ArrayList<String> deckList = new ArrayList<String>(52);

  public Deck(){
    init();
  }

  //Initialises a randomised 52 card deck
  public void init(){
    deckStack = new Stack<Card>();

    for(int i =0; i<52; i++){
      insertCard();
    }
  }

  private void insertCard(){
    Boolean unique = false;
    Card c;

    do{
      int value = rand.nextInt(13) + 2;
      int suit = rand.nextInt(4) + 1;

      c = new Card(value, suit);

      String query = Integer.toString(c.getSuit()) + Integer.toString(c.getValue());

      if(!deckList.contains(query)){
        unique = true;
      }
    } while (unique==false);

    deckStack.push(c);
    deckList.add(Integer.toString(c.getSuit()) + Integer.toString(c.getValue()));
  }

  public Card getTopCard(){
    Card c = deckStack.pop();

    return c;
  }
}
