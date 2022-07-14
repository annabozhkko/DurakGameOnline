package Controller;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

import Model.*;
import View.View;

public class ServerController extends KeyAdapter implements ActionListener {
    final private Timer timer = new Timer(1000, this);
    final private View gamePanel;
    final private GameModel gameModel;
    final private Gamer gamerSever;
    final private Gamer gamerClient;
    private Gamer attackingGamer;
    private Gamer fightBackGamer;
    final private Socket socket;
    final private ServerSocket serverSocket;
    final private BufferedReader clientInput;
    final private PrintWriter clientOutput;
    final private Card[] deckCards;
    private Card.CardSuit trump;
    private Card cardAttack;
    private int countDeckCards = 36;
    private boolean isAttack = true;
    private boolean isFightBack = false;
    private boolean cardIsTaken = false;
    private int currentIndexCard = 0;

    public ServerController(GameModel gameModel, View gamePanel, ServerSocket serverSocket, Socket socket) throws Exception{
        this.socket = socket;
        this.serverSocket = serverSocket;
        this.gamePanel = gamePanel;
        this.gameModel = gameModel;
        ObjectOutputStream clientModelOutput = new ObjectOutputStream(socket.getOutputStream());
        clientOutput = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
        clientInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        gamerSever = gameModel.getGamer();
        gamerClient = gameModel.getGamerOpponent();
        deckCards = gameModel.getDeckCards();
        gamePanel.addKeyListener(this);
        timer.start();
        initGame();
        clientModelOutput.writeObject(gameModel);
    }

    private void fillDeckCards() {
        int index = new Random().nextInt(36);
        for (Card.CardSuit suit : Card.CardSuit.values()) {
            for (Card.CardValue value : Card.CardValue.values()) {
                while (deckCards[index].getCardStatus() != Card.CardStatus.remove) {
                    index = new Random().nextInt(36);
                }
                deckCards[index].setSuitAndValue(suit, value);
            }
        }
    }

    private void setTrump() {
        int indexSuit = new Random().nextInt(Card.CardSuit.values().length);
        trump = Card.CardSuit.values()[indexSuit];
        gameModel.setTrump(trump);
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

    void initGame() {
        attackingGamer = gamerSever;
        fightBackGamer = gamerClient;

        for (int i = 0; i < countDeckCards; ++i) {
            deckCards[i] = new Card();
        }

        fillDeckCards();
        setTrump();
        updateDeckCards(gamerSever);
        updateDeckCards(gamerClient);
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        gamerSever.setIndexSelectedCard(key - 49);
        gamerSever.setSelection(true);
        clientOutput.println(key);
    }

    private void getCardFromClient() throws Exception{
        int key = Integer.parseInt(clientInput.readLine());
        gamerClient.setIndexSelectedCard(key - 49);
        gamerClient.setSelection(true);
    }

    /*
    private void waitForKeyPressed() throws Exception{
        //final CountDownLatch latch = new CountDownLatch(1);
        KeyEventDispatcher dispatcher = new KeyEventDispatcher() {
            // Anonymous class invoked from EDT
            public boolean dispatchKeyEvent(KeyEvent e) {
                //
                if (e.getKeyCode() == KeyEvent.VK_SPACE)
                    latch.countDown();
                return false;

                 //
                int key = e.getKeyCode();
                gamerSever.setIndexSelectedCard(key - 49);
                gamerSever.setSelection(true);
                clientOutput.println(key);
                latch.countDown();
                return false;
            }
        };
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(dispatcher);
        latch.await();  // current thread waits here until countDown() is called
        KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(dispatcher);
    }
    */

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
        if(attackingGamer == gamerSever){
            /*
            try {
                waitForKeyPressed();
            }catch (Exception exp){
                System.err.println(exp.getMessage());
            }
            */
            if(gamerSever.isCardSelected()){
                attack(gamerSever);
            }
        }
        else{
            getCardFromClient();
            attack(gamerClient);
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
        if(fightBackGamer == gamerSever){
            if(gamerSever.isFightBackCard(cardAttack, trump)){
                /*
                try {
                    waitForKeyPressed();
                }catch (Exception exp){
                    System.err.println(exp.getMessage());
                }
                 */
                if(gamerSever.isCardSelected()){
                    fightBack(gamerSever);
                }
            }
            else {
                cardIsTaken = true;
                isFightBack = false;
            }
        }
        else{
            if(gamerClient.isFightBackCard(cardAttack, trump)){
                getCardFromClient();
                fightBack(gamerClient);
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
                serverSocket.close();
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
