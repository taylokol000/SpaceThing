import com.sun.org.apache.bcel.internal.generic.ACONST_NULL;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class Board extends JPanel implements ActionListener {

    Player player;
    Enemy enemy;
    ArrayList<Bullet> bullets = new ArrayList<Bullet>();
    Enemy[][] enemies = new Enemy[5][10];
    Timer timer;
    Long timeDelay, bulletDelay;
    Game game;

    public Board(Game game){
        this.game = game;
        setPreferredSize(new Dimension(1024,800));
        setBackground(Color.BLACK);
        timer = new Timer(1000/60, this);
        timer.start();
    }

    //Gives objects starting positions
    public void setup(){
        player = new Player(this);
        for(int row = 0; row < 5; row++){
            for(int col = 0; col < 10; col++){
                enemies[row][col] = new Enemy(getWidth()/4 + (col*50), row*50);
            }
        }
        timeDelay = System.currentTimeMillis();
        bulletDelay = System.currentTimeMillis();
    }

    public void checkCollisions(){

        for(int i = bullets.size()-1; i >= 0; i--){
            for(int j = 0; j < enemies.length; j++){
                for(int k = 0; k < enemies[0].length; k++){
                    if(enemies[j][k] != null){
                        if(bullets.get(i).getBounds().intersects(enemies[j][k].getBounds())){
                            bullets.get(i).setRemove();
                            enemies[j][k] = null;
                            break;
                        }
                    }
                }
            }

            if(bullets.get(i).getRemove()){
                bullets.remove(bullets.get(i));
            }

        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        long currentTime = System.currentTimeMillis();

        if(GameStates.isPLAY() && !GameStates.isPAUSE()) {

            checkCollisions();

            if (game.isSpacePressed() && currentTime - bulletDelay >= 250) {
                bullets.add(new Bullet(player));
                bulletDelay = System.currentTimeMillis();
            }

            for (int i = bullets.size() - 1; i >= 0; i--) {
                if (bullets.get(i).getY() < 0) {
                    bullets.remove(bullets.get(i));
                } else
                    bullets.get(i).move();
            }

            if (currentTime - timeDelay >= 1000) {
                for (int row = 0; row < 5; row++) {
                    for (int col = 0; col < 10; col++) {
                        if (enemies[row][col] != null) {
                            enemies[row][col].move();
                        }
                    }
                }
                timeDelay = System.currentTimeMillis();
            }

            if (game.isLeftPressed() && player.getX() > 0) {
                player.moveLeft();
            }

            if (game.isRightPressed() && (player.getX() + player.getWIDTH()) < getWidth()) {
                player.moveRight();
            }
        }
        if(game.isEnterPressed()){
            GameStates.setPLAY(true);
            GameStates.setMENU(false);
        }

        if (game.isPPressed()){
            if(GameStates.isPAUSE()) {
                GameStates.setPAUSE(false);
            }else{
                GameStates.setPAUSE(true);
            }
        }

        repaint();
    }

    public void paintComponent(Graphics g){

        super.paintComponent(g);

        if(GameStates.isMENU()){
            //menu paint
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial",Font.PLAIN,72));
            printSimpleString("INVADED SPACE",getWidth(),0,100,g);
            g.setFont(new Font("Arial",Font.PLAIN,36));
            printSimpleString("Press ENTER to play!",getWidth(),0,300,g);
        }

        if(GameStates.isPAUSE()){
            //menu paint
            g.setColor(Color.GRAY);
            g.setFont(new Font("Arial",Font.PLAIN,72));
            printSimpleString("PAUSE",getWidth(),0,getWidth()/2-72/2,g);
        }

        if(GameStates.isPLAY()) {
            player.paint(g);
            for (int row = 0; row < 5; row++) {
                for (int col = 0; col < 10; col++) {
                    if (enemies[row][col] != null) {
                        enemies[row][col].paint(g);
                    }
                }
            }
            for (Bullet bullet : bullets) {
                bullet.paint(g);
            }
        }
    }

    private void printSimpleString(String s,int width, int XPos,int YPos,Graphics g){
        int stringLen=(int)g.getFontMetrics().getStringBounds(s,g).getWidth();
        int start=width/2-stringLen/2;
        g.drawString(s,start+XPos,YPos);
    }

}