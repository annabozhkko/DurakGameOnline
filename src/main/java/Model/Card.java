package Model;

import java.io.Serializable;

public class Card implements Serializable {
    private CardSuit suit;
    private CardValue value;
    private CardStatus cardStatus = CardStatus.remove;

    public CardSuit getCardSuit(){
        return suit;
    }

    public CardValue getCardValue(){
        return value;
    }

    public void setSuitAndValue(CardSuit suit, CardValue value){
        this.suit = suit;
        this.value = value;
        this.cardStatus = CardStatus.inDeck;
    }

    public CardStatus getCardStatus(){
        return cardStatus;
    }

    public void setCardStatus(CardStatus cardStatus){
        this.cardStatus = cardStatus;
    }

    public enum CardStatus{
        inDeck,
        inGamerDeck,
        attack,
        fightBack,
        remove
    }

    public enum CardSuit{
        DIAMONDS,
        HEARTS,
        CLUBS,
        SPADES
    }

    public enum CardValue{
        SIX,
        SEVEN,
        EIGHT,
        NIGHT,
        TEN,
        JACK,
        QUEEN,
        KING,
        ACE
    }
}
