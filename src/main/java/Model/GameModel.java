package Model;

import java.io.Serializable;

public class GameModel implements Serializable {
    public Gamer gamer = new Gamer();
    public Gamer gamerOpponent = new Gamer();
    public Gamer attackingGamer;
    public Card.CardSuit trump;
    public Card[] deckCards = new Card[36];
    public int countDeckCards = 36;
    public boolean isGameOver = false;

    public Gamer getGamer() {
        return gamer;
    }

    public Gamer getGamerOpponent() {
        return gamerOpponent;
    }

    public Card[] getDeckCards() {
        return deckCards;
    }

    public Card.CardSuit getTrump() {
        return trump;
    }

    public int getCountDeckCards() {
        return countDeckCards;
    }

    public void setAttackingGamer(Gamer attackingGamer){
        this.attackingGamer = attackingGamer;
    }

    public void setTrump(Card.CardSuit trump) {
        this.trump = trump;
    }

    public void setCountDeckCards(int countDeckCards) {
        this.countDeckCards = countDeckCards;
    }

    public Gamer getAttackingGamer(){
        return attackingGamer;
    }

    public void gameOver(){
        isGameOver = true;
    }

    public boolean isGameOver(){
        return isGameOver;
    }
}
