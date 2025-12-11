import Texture.TextureReader;
import java.awt.event.*;
import java.io.IOException;
import javax.media.opengl.*;
import java.util.BitSet;
import java.util.Random;
import javax.media.opengl.glu.GLU;
import com.sun.opengl.util.GLUT;
import javax.sound.sampled.*;
import java.io.File;
public class Chicken extends AnimListener {
    GLUT glut = new GLUT();
    String highScoreFile = "highscore.txt";
    int maxWidth = 100;
    int maxHeight = 100;
    double xPlayer = 50, yPlayer = 18;

    Random rand = new Random();
    int chickenCount =5;
    int chickenX[] = {10, 30, 50, 70, 90};
    int chickenY = 90;

    boolean eggActive = false;
    int[] eggX = new int[chickenCount];
    int[] eggY = new int[chickenCount];
    boolean[] chickenHasEgg = new boolean[chickenCount];
    int activeChicken = -1;
    int eggSpeed = selectedEggSpeed;

    int score = 0;
    int highScore = 0;

    int lives = 3;
    boolean gameOver = false;

    int timer = 60;
    long lastTime = System.currentTimeMillis();
    boolean gameWon = false;
    boolean paused = false;

    public static int selectedEggSpeed = 1;

    boolean returnToMenu = false;

    String textureNames[] = {"EggsBasket.png","Egg.png","Chicken.png","","backk.png"};
    TextureReader.Texture texture[] = new TextureReader.Texture[textureNames.length];
    int textures[] = new int[textureNames.length];

    public void init(GLAutoDrawable gld) {

        GL gl = gld.getGL();
        gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);

        gl.glEnable(GL.GL_TEXTURE_2D);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        gl.glGenTextures(textureNames.length, textures, 0);
        loadHighScore();
        for(int i = 0; i < textureNames.length; i++){
            try {
                texture[i] = TextureReader.readTexture(assetsFolderName + "//" + textureNames[i] , true);
                gl.glBindTexture(GL.GL_TEXTURE_2D, textures[i]);


                new GLU().gluBuild2DMipmaps(
                        GL.GL_TEXTURE_2D,
                        GL.GL_RGBA,
                        texture[i].getWidth(), texture[i].getHeight(),
                        GL.GL_RGBA,
                        GL.GL_UNSIGNED_BYTE,
                        texture[i].getPixels()
                );
            } catch( IOException e ) {
                System.out.println(e);
                e.printStackTrace();
            }
        }
    }

    public void display(GLAutoDrawable gld) {
        GL gl = gld.getGL();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        gl.glLoadIdentity();

        long current = System.currentTimeMillis();
        DrawBackground(gl);
        handleKeyPress();

        DrawSprite(gl, xPlayer,yPlayer, 0, 1);

        if (!eggActive) {
            spawnEggFromRandomChicken();
        }
        if (eggActive && activeChicken != -1) {

            DrawSprite(gl, eggX[activeChicken], eggY[activeChicken], 1, 0.4f);
            updateEgg(activeChicken);
        }
        for(int i=0; i<chickenCount; i++){
            DrawSprite(gl, chickenX[i], chickenY, 2, 1.4f);
        }

        drawScore(gl);

        if (current - lastTime >= 1000 && !gameOver&& !gameWon &&!paused) {
            timer--;
            lastTime = current;

            if (timer <= 0) {
                if (score >= highScore) {
                    gameWon = true;
                } else {
                    gameOver = true;
                }
            }
        }

        if (gameOver) {
            drawGameOver(gl);
        }
        if(gameWon){
            drawWin(gl);
        }
        if (paused) {
            drawPause(gl);
        }
        if (returnToMenu) {
            openMenu();
        }
    }
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
    }

    public void DrawSprite(GL gl,double x, double y, int index, float scale){
        gl.glEnable(GL.GL_BLEND);
        gl.glBindTexture(GL.GL_TEXTURE_2D, textures[index]);
        gl.glPushMatrix();
       gl.glTranslated(x/(maxWidth/2)-1, y/(maxHeight/2.0) - 1, 0);

        gl.glScaled(0.1*scale, 0.1*scale, 1);

        gl.glBegin(GL.GL_QUADS);
        gl.glTexCoord2f(0.0f, 0.0f);
        gl.glVertex3f(-1.0f, -1.0f, -1.0f);
        gl.glTexCoord2f(1.0f, 0.0f);
        gl.glVertex3f(1.0f, -1.0f, -1.0f);
        gl.glTexCoord2f(1.0f, 1.0f);
        gl.glVertex3f(1.0f, 1.0f, -1.0f);
        gl.glTexCoord2f(0.0f, 1.0f);
        gl.glVertex3f(-1.0f, 1.0f, -1.0f);
        gl.glEnd();
        gl.glPopMatrix();

        gl.glDisable(GL.GL_BLEND);
    }

    public void DrawBackground(GL gl){
        gl.glEnable(GL.GL_BLEND);
        gl.glBindTexture(GL.GL_TEXTURE_2D, textures[textureNames.length-1]);

        gl.glPushMatrix();
        gl.glBegin(GL.GL_QUADS);

        gl.glTexCoord2f(0.0f, 0.0f);
        gl.glVertex3f(-1.0f, -1.0f, -1.0f);
        gl.glTexCoord2f(1.0f, 0.0f);
        gl.glVertex3f(1.0f, -1.0f, -1.0f);
        gl.glTexCoord2f(1.0f, 1.0f);
        gl.glVertex3f(1.0f, 1.0f, -1.0f);
        gl.glTexCoord2f(0.0f, 1.0f);
        gl.glVertex3f(-1.0f, 1.0f, -1.0f);
        gl.glEnd();
        gl.glPopMatrix();

        gl.glDisable(GL.GL_BLEND);
    }

    public void updateEgg(int index) {
        if (gameOver||gameWon||paused) return;
        eggY[index] -= eggSpeed;
        if (eggY[index] <= yPlayer + 2 && eggY[index] >= yPlayer) {
            if (Math.abs(eggX[index] - xPlayer) <10) {

                score++;
                if (score > highScore) {
                    highScore = score;
                    saveHighScore();
                }

                chickenHasEgg[index] = false;
                eggActive = false;
                activeChicken = -1;
                playSound("assets/CollectSound.wav");
                return;
            }
        }
        if (eggY[index] <= 0) {
            chickenHasEgg[index] = false;
            eggActive = false;
            activeChicken = -1;

            lives--;
            if (lives <= 0) {
                if (score >= highScore) {
                    gameWon = true;
                } else {
                    gameOver = true;
                }
            }
        }
    }
    public void spawnEggFromRandomChicken() {
        activeChicken = rand.nextInt(chickenCount);
        eggX[activeChicken] = chickenX[activeChicken];
        eggY[activeChicken] = chickenY;
        chickenHasEgg[activeChicken] = true;
        eggActive = true;
        playSound("assets/ChickenSound.wav");
    }

    public void drawScore(GL gl) {
        gl.glColor3f(1.0f, 1.0f, 1.0f);
        gl.glRasterPos2f(-0.95f, -0.8f);
        String scoreText = "Score: " + score;
        for (int i = 0; i < scoreText.length(); i++) {
            glut.glutBitmapCharacter(GLUT.BITMAP_TIMES_ROMAN_24, scoreText.charAt(i));
        }
        String highScoreText = "Highest Score: " + highScore;
        gl.glRasterPos2f(-0.95f, -0.9f);
        for (int i = 0; i < highScoreText.length(); i++) {
            glut.glutBitmapCharacter(GLUT.BITMAP_TIMES_ROMAN_24, highScoreText.charAt(i));
        }

        String livesText = "Lives: " + lives;
        gl.glRasterPos2f(0.7f, -0.9f);
        for (int i = 0; i < livesText.length(); i++) {
            glut.glutBitmapCharacter(GLUT.BITMAP_TIMES_ROMAN_24, livesText.charAt(i));
        }

        String timerText = "Time: " + timer;
        gl.glRasterPos2f(0.7f, -0.8f);
        for (int i = 0; i < timerText.length(); i++) {
            glut.glutBitmapCharacter(GLUT.BITMAP_TIMES_ROMAN_24, timerText.charAt(i));
        }

        gl.glColor3f(1.0f, 1.0f, 1.0f);
    }

    public void loadHighScore() {
        try {
            java.io.File file = new java.io.File(highScoreFile);
            if (!file.exists()) return;

            java.util.Scanner sc = new java.util.Scanner(file);
            if (sc.hasNextInt()) {
                highScore = sc.nextInt();
            }
            sc.close();
        } catch (Exception e) {
            System.out.println("Error reading high score: " + e.getMessage());
        }
    }

    public void saveHighScore() {
        try {
            java.io.PrintWriter writer = new java.io.PrintWriter(highScoreFile);
            writer.println(highScore);
            writer.close();
        } catch (Exception e) {
            System.out.println("Error saving high score: " + e.getMessage());
        }
    }

    public void drawGameOver(GL gl){
        gl.glColor3f(1, 0, 0);
        gl.glRasterPos2f(-0.2f, 0);
        String text = "GAME OVER";
        for (int i = 0; i < text.length(); i++) {
            glut.glutBitmapCharacter(GLUT.BITMAP_TIMES_ROMAN_24, text.charAt(i));
        }
        gl.glColor3f(1, 1, 1);
    }

    public void drawWin(GL gl){
        gl.glColor3f(0, 1, 0);
        gl.glRasterPos2f(-0.2f, 0);
        String text = "YOU WIN!";
        for (int i = 0; i < text.length(); i++) {
            glut.glutBitmapCharacter(GLUT.BITMAP_TIMES_ROMAN_24, text.charAt(i));
        }
        gl.glColor3f(1,1,1);
    }
    public void drawPause(GL gl) {
        gl.glColor3f(0, 0, 0);
        gl.glRasterPos2f(-0.2f, 0);
        String text = "GAME PAUSED";
        for (int i = 0; i < text.length(); i++) {
            glut.glutBitmapCharacter(GLUT.BITMAP_TIMES_ROMAN_24, text.charAt(i));
        }
        gl.glColor3f(1, 1, 1);
    }

    public void playSound(String filename) {
        try {
            File soundFile = new File(filename);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();
        } catch (Exception e) {
            System.out.println("Error playing sound: " + e.getMessage());
        }
    }
    public void handleKeyPress() {
        if (gameOver||gameWon||paused) return;
        if (isKeyPressed(KeyEvent.VK_LEFT)) {
            if (xPlayer > 5) {
                xPlayer-=2;
            }
        }
        if (isKeyPressed(KeyEvent.VK_RIGHT)) {
            if (xPlayer < maxWidth-5) {
                xPlayer+=2;
            }
        }
    }
    public void openMenu() {
        new GameMenu();
        test.animator.stop();
    }

    public BitSet keyBits = new BitSet(256);

    @Override
    public void keyPressed(final KeyEvent event) {
        int keyCode = event.getKeyCode();
        keyBits.set(keyCode);
        if(isKeyPressed((KeyEvent.VK_P))){
            paused=!paused;
        }
        if (keyCode == KeyEvent.VK_M) {
            returnToMenu = true;
        }
    }

    @Override
    public void keyReleased(final KeyEvent event) {
        int keyCode = event.getKeyCode();
        keyBits.clear(keyCode);
    }

    @Override
    public void keyTyped(final KeyEvent event) {

    }

    public boolean isKeyPressed(final int keyCode) {
        return keyBits.get(keyCode);
    }
}