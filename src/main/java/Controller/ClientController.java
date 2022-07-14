package Controller;

import Model.Card;
import Model.GameModel;
import Model.Gamer;
import View.View;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.Socket;

public class ClientController extends KeyAdapter implements ActionListener{
    final private Timer timer = new Timer(1000, this);
    private final View gamePanel;
    final private GameModel gameModel;
    private Gamer gamerSever;
    private Gamer gamerClient;
    private Gamer attackingGamer;
    private Gamer fightBackGamer;
    final private Socket socket;
    final private ObjectInputStream serverModelInput;
    final private PrintWriter serverOutput;
    final private BufferedReader serverInput;
    private Card[] deckCards;
    private Card.CardSuit trump;
    private Card cardAttack;
    private boolean isAttack = true;
    private boolean isFightBack = false;
    private boolean cardIsTaken = false;
    private int countDeckCards;
    private int currentIndexCard = 12;

    public ClientController(GameModel gameModel, View gamePanel, Socket socket) throws Exception{
        this.socket = socket;
        this.gameModel = gameModel;
        this.gamePanel = gamePanel;
        serverModelInput = new ObjectInputStream(socket.getInputStream());
        serverInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        serverOutput = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
        getGameModelFromServer();
        attackingGamer = gamerSever;
        fightBackGamer = gamerClient;
        gamePanel.addKeyListener(this);
        timer.start();
    }

    private void getGameModelFromServer() throws Exception{
        GameModel gameModelServer = (GameModel)serverModelInput.readObject();
        gameModel.gamer = gameModelServer.getGamerOpponent();
        gameModel.gamerOpponent = gameModelServer.getGamer();
        gameModel.attackingGamer = gameModel.getAttackingGamer();
        gameModel.trump = gameModelServer.getTrump();
        gameModel.countDeckCards = gameModelServer.getCountDeckCards();

        gamerClient = gameModel.getGamer();
        gamerSever = gameModel.getGamerOpponent();
        deckCards = gameModelServer.getDeckCards();
        countDeckCards = gameModel.getCountDeckCards();
        trump = gameModel.trump;
    }

    private void updateDeckCards(Gamer gamer) {
        gamer.updateCards();
        if(countDeckCards == 0)
            return;

        for(int i = gamer.getCountCards(); i < 6; ++i){
            gamer.takeCard(deckCards[currentIndexCard]);
            currentIndexCard++;
            countDeckCards--;
        }

        gameModel.setCountDeckCards(countDeckCards);
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        gamerClient.setIndexSelectedCard(key - 49);
        gamerClient.setSelection(true);
        serverOutput.println(key);
    }

    private void getCardFromServer() throws Exception{
        int key = Integer.parseInt(serverInput.readLine());
        gamerSever.setIndexSelectedCard(key - 49);
        gamerSever.setSelection(true);
    }

    private void attack(Gamer gamer){
        gamer.setSelection(false);
        try {
            cardAttack = attackingGamer.attack(gamer.getIndexSelectedCard());
        }catch (NullPointerException exp){
            System.out.println("Wrong number of card");
            return;
        }
        isAttack = false;
        isFightBack = true;
    }

    private void attackPlay() throws Exception{
        if(attackingGamer == gamerClient){
            if(gamerClient.isCardSelected()){
                attack(gamerClient);
            }
        }
        else{
            getCardFromServer();
            attack(gamerSever);
        }
    }

    private void fightBack(Gamer gamer){
        if(gamer.getIndexSelectedCard() == -39){
            cardIsTaken = true;
            gamer.setSelection(false);
            isFightBack = false;
            return;
        }
        try {
            if(fightBackGamer.fightBack(gamer.getIndexSelectedCard(), cardAttack, trump)) {
                cardIsTaken = false;
                gamer.setSelection(false);
                isFightBack = false;
            }
        }catch (NullPointerException exp){
            System.out.println("Wrong number of card");
        }
    }

    private void fightBackPlay() throws Exception{
        if(fightBackGamer == gamerClient){
            //как то оптимизировать постоянную проверку
            if(gamerClient.isFightBackCard(cardAttack, trump)){
                if(gamerClient.isCardSelected()){
                    fightBack(gamerClient);
                }
            }
            else {
                cardIsTaken = true;
                isFightBack = false;
            }
        }
        else{
            if(gamerSever.isFightBackCard(cardAttack, trump)){
                getCardFromServer();
                fightBack(gamerSever);
            }
            else {
                cardIsTaken = true;
                isFightBack = false;
            }
        }
    }

    private void updateGame(){
        updateDeckCards(attackingGamer);
        if(!cardIsTaken) {
            updateDeckCards(fightBackGamer);
            attackingGamer = fightBackGamer;
            fightBackGamer = (fightBackGamer == gamerSever) ? gamerClient : gamerSever;
        }
        else{
            fightBackGamer.takeCard(cardAttack);
        }

        isAttack = true;
        if(attackingGamer.getCountCards() == 0 || fightBackGamer.getCountCards() == 0)
            gameModel.gameOver();
    }

    public void actionPerformed(ActionEvent e){
        gamePanel.repaint();
        if(gameModel.isGameOver()) {
            timer.stop();
            try {
                socket.close();
            }catch (Exception exp){
                System.err.println(exp.getMessage());
            }
            return;
        }

        gameModel.setAttackingGamer(attackingGamer);

        try {
            if (isAttack)
                attackPlay();
            else if (isFightBack)
                fightBackPlay();
            else
                updateGame();
        }catch (Exception exp){
            System.err.println(exp.getMessage());
            timer.stop();
        }
    }
}
