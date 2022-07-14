package Model;

import java.io.Serializable;

public class Gamer implements Serializable {
    private int countCards = 0;
    final private Card[] cards = new Card[36];;

    private boolean isCardSelected = false;
    private int indexSelectedCard;

    public void updateCards(){
        for(int i = 0; i < countCards; ++i){
            if(cards[i].getCardStatus() == Card.CardStatus.attack || cards[i].getCardStatus() == Card.CardStatus.fightBack){
                cards[i].setCardStatus(Card.CardStatus.remove);
                if (countCards - i >= 0) System.arraycopy(cards, i + 1, cards, i, countCards - i);
                countCards--;
                break;
            }
        }
    }

    public void takeCard(Card card){
        card.setCardStatus(Card.CardStatus.inGamerDeck);
        cards[countCards] = card;
        countCards++;
    }

    public int getCountCards() {
        return countCards;
    }

    public Card[] getCards(){
        return cards;
    }

    public boolean isFightBackCard(Card card, Card.CardSuit trump){
        for(int i = 0; i < countCards; ++i){
            if(fightBack(i, card, trump)){
                cards[i].setCardStatus(Card.CardStatus.inGamerDeck);
                return true;
            }
        }
        return false;
    }

    public boolean fightBack(int indexCard, Card card, Card.CardSuit trump) throws NullPointerException{
        if(card.getCardSuit() == cards[indexCard].getCardSuit()){
            if(card.getCardValue().ordinal() < cards[indexCard].getCardValue().ordinal()){
                cards[indexCard].setCardStatus(Card.CardStatus.fightBack);
                return true;
            }
            return false;
        }
        if(cards[indexCard].getCardSuit() == trump){
            cards[indexCard].setCardStatus(Card.CardStatus.fightBack);
            return true;
        }
        return false;
    }

    public Card attack(int indexCard) throws NullPointerException{
        cards[indexCard].setCardStatus(Card.CardStatus.attack);
        return cards[indexCard];
    }

    public boolean isCardSelected(){
        return isCardSelected;
    }

    public void setIndexSelectedCard(int indexSelectedCard){
        this.indexSelectedCard = indexSelectedCard;
    }

    public int getIndexSelectedCard(){
        return indexSelectedCard;
    }

    public void setSelection(boolean isCardSelected){
        this.isCardSelected = isCardSelected;
    }
}
