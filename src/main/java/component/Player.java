package component;

import libsvm.SelfOptimizingLinearLibSVM;
import net.sf.javaml.classification.Classifier;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Stack;
import java.util.ArrayList;

public abstract class Player {
  ArrayList<Card> pHand = new ArrayList<>();
  private String name;
  private int prediction = 0;
  private int points = 0; // Points earned round by round in the gameplay stage
  private int score = 0; // Score based on the accuracy of the agent's prediction
  private int id;
  private String agentType;
  private int sameSuitSum;
  private int handPointsSum;
  private int aceCount;
  private int kingCount;
  private int queenCount;
  private int jackCount;
  private int trumpCallMin;
  private int chosenTrump = 0;
  private double p1Confidence;
  private double p2Confidence;
  private int zeroesCalled = 0;
  private boolean classifierSet = false;
  private Classifier classifier = new SelfOptimizingLinearLibSVM();

  // Initialises agent with name, id and agent type, then loads its classifier
  Player(String name, int id, String type){
    this.name = name;
    this.id = id;
    this.agentType = type;

    loadClassifier();
  }

  public String getName(){
    return name;
  }

  public int getPoints(){
    return points;
  }

  public ArrayList<Card> getHand(){
    return pHand;
  }

  public int getPrediction(){
    return prediction;
  }

  public int getScore(){
    return score;
  }

  public String getAgentType() {
    return agentType;
  }

  public int getID(){
    return id;
  }

  public int getSameSuitSum() {
    return sameSuitSum;
  }

  public int getHandPointsSum() {
    return handPointsSum;
  }

  public int getAceCount() {
    return aceCount;
  }

  public int getKingCount() {
    return kingCount;
  }

  public int getQueenCount() {
    return queenCount;
  }

  public int getJackCount() {
    return jackCount;
  }

  public int getTrumpCallMin() {
    return trumpCallMin;
  }

  public int getChosenTrump() {
    return chosenTrump;
  }

  public int getZeroesCalled(){
    return zeroesCalled;
  }

  public double getP1Confidence() {
    return p1Confidence;
  }

  public double getP2Confidence() {
    return p2Confidence;
  }

  ArrayList[] getValidCards(int leadSuit, boolean first){
    ArrayList[] cardSets = new ArrayList[2];

    cardSets[0] = new ArrayList<Card>();
    cardSets[1] = new ArrayList<Card>();

    // Iterates over the player's hand
    for(Card card : pHand){
      int currentSuit;

      currentSuit = card.getSuit();
      if(currentSuit==leadSuit || first){
        // Leadsuit Cards
        cardSets[0].add(card);
      } else {
        // Non-lead Cards
        cardSets[1].add(card);
      }
    }

    return cardSets;
  }

  Card getLowest(ArrayList<Card> cardSet){
    Card lowest = cardSet.get(0);

    for(Card card : cardSet){
      if(card.getScore()<lowest.getScore()){
        lowest = card;
      }
    }

    return lowest;
  }

  Card getHighest(ArrayList<Card> cardSet){
    Card highest = cardSet.get(0);

    for(Card card : cardSet){
      if(card.getScore()>highest.getScore()){
        highest = card;
      }
    }

    return highest;
  }
  
  boolean getWinIntention(){
    return getPrediction() > getPoints();
  }

  public Classifier getClassifier() {
    if(classifierSet){
      return classifier;
    } else {
      return null;
    }
  }

  public void increaseScore(int amount){
    score += amount;
  }

  public void incrementZeroesCalled(){
    zeroesCalled++;
  }

  private void loadClassifier() {
    String filenameClass = "src/main/resources/ml/classifier" + getAgentType() + ".ser";

    File f = new File(filenameClass);
    if(f.exists()) {
      try {
        FileInputStream fileIn = new FileInputStream(filenameClass);
        ObjectInputStream in = new ObjectInputStream(fileIn);
        this.classifier = (SelfOptimizingLinearLibSVM) in.readObject();
        classifierSet = true;
        in.close();
        fileIn.close();
      } catch (IOException i) {
        i.printStackTrace();
      } catch (ClassNotFoundException c) {
        System.out.println("Classifier class not found");
        c.printStackTrace();
      }
    } else {
        System.out.println("Serialized Classifier not found");
    }
  }

  public void setClassifier(Classifier c) {
    classifier = c;
    classifierSet = true;
  }

  public void setPrediction(int p) {
    prediction = p;
  }

  public void setTrumpCallMin(int t) {
    this.trumpCallMin = t;
  }

  public void setP1Confidence(double c){
    p1Confidence = c;
  }

  public void setP2Confidence(double c){
    p2Confidence = c;
  }

  public void addToHand(Card c){
    pHand.add(c);
  }

  public void incrementPoints(){
    points++;
  }

  public void resetPoints(){
    points = 0;
  }

  public void resetZeroesCalled(){
    zeroesCalled = 0;
  }

  public void setPoints(int points){
    this.points = points;
  }

  void assignCardScore(ArrayList<Card> hand, boolean first, int trumpSuit, int leadSuit){
    for (Card card : hand) {
      card.setScore(card.getValue());

      // Adds 13 points to a lead card's score
      // If the player is first to lay a card then they are automatically the lead suit
      if(first || card.getSuit() == leadSuit){
        // Lead Card so extra points for setting suit
        card.setScore(card.getScore()+13);
      }
      // Adds 26 points to a trump card's score
      if(card.getSuit()==trumpSuit){
        card.setScore(card.getScore()+26);
      }
    }
  }

  // Analyses Hand for creation of training set for classifier
  public void analyseHand(){
    int cSum = 0;
    int hSum = 0;
    int dSum = 0;
    int sSum = 0;
    int maxSum;

    this.handPointsSum = 0;
    this.aceCount = 0;
    this.kingCount = 0;
    this.queenCount = 0;
    this.jackCount = 0;

    for(Card card : pHand){
      switch(card.getSuit()){
        case 1:
          cSum += card.getValue();
          break;
        case 2:
          hSum += card.getValue();
          break;
        case 3:
          dSum += card.getValue();
          break;
        case 4:
          sSum += card.getValue();
          break;
      }

      switch(card.getValue()){
        case 14:
          this.aceCount++;
          break;
        case 13:
          this.kingCount++;
          break;
        case 12:
          this.queenCount++;
          break;
        case 11:
          this.jackCount++;
          break;
      }

      this.handPointsSum += card.getValue();
    }

    maxSum = Math.max(cSum, hSum);
    maxSum = Math.max(maxSum, dSum);
    maxSum = Math.max(maxSum, sSum);

    if ( maxSum == cSum) {
      this.chosenTrump = 1;
    } else if ( maxSum == hSum) {
      this.chosenTrump = 2;
    } else if ( maxSum == dSum) {
      this.chosenTrump = 3;
    } else {
      this.chosenTrump = 4;
    }

      this.sameSuitSum = maxSum;
  }

  @Override
  public boolean equals(Object anObject) {
    if (!(anObject instanceof Player)) {
      return false;
    }

    boolean isEqual = true;
    Player testPlayer = (Player) anObject;

    if (this.name != testPlayer.getName() || !this.getAgentType().equals(testPlayer.getAgentType())) {
      isEqual = false;
    }

    return isEqual;
  }

  // Abstract method for the agents to implement in order to use their respective strategy
  public abstract Card makeTurn(int leadSuit, int trumpSuit, Stack<Card> playedCards, ArrayList<Card> allPlayedCards, int handSize);
}
