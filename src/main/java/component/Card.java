package component;

public class Card {
  // Value is between 2 and 14 when assigned
  private int value;
  // Suit is between 1 and 4 when assigned
  private int suit;
  private int score = 0;

  public Card(int value, int suit){
    this.value = value;
    this.suit = suit;
  }

  public int getValue(){
    return value;
  }

  public int getSuit(){
    return suit;
  }

  public int getScore(){
    return score;
  }

  public void setScore(int score){
    this.score = score;
  }

  public String toString(){
    String returnString = "";

    returnString = valueToString(false) + " of " + suitToString(false);
    return returnString;
  }

  public String toMiniString(){
    String returnString = "";

    returnString = "[" + valueToString(true) + suitToString(true) + "]";
    return returnString;
  }

  public String getFilename(){
      return valueToString(false).toLowerCase() + "_of_" + suitToString(false) + ".png";
  }

  public String suitToString(Boolean mini){
    String strSuit = "";

    if(mini){
      switch(suit){
        case 1:  strSuit = "♣";
            break;
        case 2:  strSuit = "♥";
            break;
        case 3:  strSuit = "♦";
            break;
        case 4:  strSuit = "♠";
            break;
      }
    } else {
      switch(suit){
        case 1:  strSuit = "clubs";
            break;
        case 2:  strSuit = "hearts";
            break;
        case 3:  strSuit = "diamonds";
            break;
        case 4:  strSuit = "spades";
            break;
      }
    }


    return strSuit;
  }

  public String valueToString(Boolean mini){
    String strValue = "";

    if(mini){
      switch(value){
        case 14:   strValue = "A";
            break;
        case 11:  strValue = "J";
            break;
        case 12:  strValue = "Q";
            break;
        case 13:  strValue = "K";
            break;
        default:  strValue = Integer.toString(value);
            break;
      }
    } else {
      switch(value){
        case 14:   strValue = "Ace";
            break;
        case 11:  strValue = "Jack";
            break;
        case 12:  strValue = "Queen";
            break;
        case 13:  strValue = "King";
            break;
        default:  strValue = Integer.toString(value);
            break;
      }
    }

    return strValue;
  }
}
