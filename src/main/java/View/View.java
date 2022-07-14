package View;

import Model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class View extends JPanel implements ActionListener {
    final private GameModel gameModel;

    public View(GameModel gameModel){
        this.gameModel = gameModel;
        JFrame frame = new JFrame();
        frame.setTitle("Durak");
        frame.setSize(new Dimension(800, 600));
        frame.setBackground(Color.gray);
        frame.getContentPane().add(this);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
        setBackground(Color.gray);
        setFocusable(true);
    }

    private void printAttackCard(Card card, Graphics2D graphics2D){
        Image cardImage;
        ImageIcon imageIcon = new ImageIcon("src/main/resources/cards/" + card.getCardSuit() + "/" + card.getCardValue() + ".JPG");
        cardImage = imageIcon.getImage();
        graphics2D.drawImage(cardImage, 370, 236, 57,88,this);
    }

    private void printFightBackCard(Card card, Graphics2D graphics2D){
        Image cardImage;
        ImageIcon imageIcon = new ImageIcon("src/main/resources/cards/" + card.getCardSuit() + "/" + card.getCardValue() + ".JPG");
        cardImage = imageIcon.getImage();
        graphics2D.drawImage(cardImage, 370, 256, 57,88,this);
    }

    private void printCardInDeck(Card card, Graphics2D graphics2D, int x){
        Image cardImage;
        ImageIcon imageIcon = new ImageIcon("src/main/resources/cards/" + card.getCardSuit() + "/" + card.getCardValue() + ".JPG");
        cardImage = imageIcon.getImage();
        graphics2D.drawImage(cardImage, x, 430, 57,88,this);
    }

    private void printBotCard(Graphics2D graphics2D, int x){
        Image cardImage;
        ImageIcon imageIcon = new ImageIcon("src/main/resources/cards/shirt.png");
        cardImage = imageIcon.getImage();
        graphics2D.drawImage(cardImage, x, 50, 57,88,this);
    }

    private void printDeckBot(Graphics2D graphics2D){
        Card[] cards = gameModel.getGamerOpponent().getCards();
        for(int i = 0; i < gameModel.getGamerOpponent().getCountCards(); ++i){
            if(cards[i].getCardStatus() == Card.CardStatus.attack)
                printAttackCard(cards[i], graphics2D);
            if(cards[i].getCardStatus() == Card.CardStatus.fightBack)
                printFightBackCard(cards[i], graphics2D);
            if(cards[i].getCardStatus() == Card.CardStatus.inGamerDeck)
                printBotCard(graphics2D, (100 - 10 * (gameModel.getGamerOpponent().getCountCards() - 6)) * (1 + i));
        }
    }

    private void printDeckPanel(Graphics2D graphics2D){
        Card[] cards = gameModel.getGamer().getCards();
        for(int i = 0; i < gameModel.getGamer().getCountCards(); ++i){
            if(cards[i].getCardStatus() == Card.CardStatus.attack)
                printAttackCard(cards[i], graphics2D);
            if(cards[i].getCardStatus() == Card.CardStatus.fightBack)
                printFightBackCard(cards[i], graphics2D);
            if(cards[i].getCardStatus() == Card.CardStatus.inGamerDeck)
                printCardInDeck(cards[i], graphics2D, (100 - 10 * (gameModel.getGamer().getCountCards() - 6)) * (1 + i));

            graphics2D.drawString(Integer.toString(i + 1), (100 - 10 * (gameModel.getGamer().getCountCards() - 6)) * (1 + i) + 22, 540);
        }
    }

    @Override
    public void paint(Graphics g){
        Graphics2D graphics2D = (Graphics2D) g;

        if(gameModel.getAttackingGamer() == gameModel.getGamer()){
            printDeckPanel(graphics2D);
            printDeckBot(graphics2D);
        }
        else {
            printDeckBot(graphics2D);
            printDeckPanel(graphics2D);
        }

        if(gameModel.getCountDeckCards() > 0) {
            ImageIcon imageIconShirt = new ImageIcon("src/main/resources/cards/shirt.png");
            Image deck = imageIconShirt.getImage();
            graphics2D.drawImage(deck, 40, 248, 57, 88, this);
            graphics2D.drawString(Integer.toString(gameModel.getCountDeckCards()), 62, 358);
        }

        ImageIcon imageIconTrump = new ImageIcon("src/main/resources/cards/" + gameModel.getTrump() + ".png");
        Image trumpCard = imageIconTrump.getImage();
        graphics2D.drawImage(trumpCard,48, 272, 40,40,this);

        if(gameModel.isGameOver()){
            int fontSize = 20;
            Font f = new Font("Comic Sans MS", Font.BOLD, fontSize);
            graphics2D.setFont(f);
            graphics2D.drawString("Game is over", 330, 300);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e){}
}
