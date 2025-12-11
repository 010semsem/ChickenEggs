
import Texture.TextureReader;
import java.awt.event.*;
import java.io.IOException;
import javax.media.opengl.*;
import java.util.BitSet;
import java.util.Random;
import javax.media.opengl.glu.GLU;
import com.sun.opengl.util.GLUT;
public class Chicken extends AnimListener {
    GLUT glut = new GLUT();
    String highScoreFile = "highscore.txt";
    int maxWidth = 100;
    int maxHeight = 100;
    int xPlayer = 50, yPlayer = 20;

    Random rand = new Random();
    int chickenCount =5;
    int chickenX[] = {10, 30, 50, 70, 90};
    int chickenY = 90;

    boolean eggActive = false;
    int[] eggX = new int[chickenCount];
    int[] eggY = new int[chickenCount];
    boolean[] chickenHasEgg = new boolean[chickenCount];
    int activeChicken = -1;
    int eggSpeed = 1;

    int score = 0;
    int highScore = 0;

    int lives = 3;
    boolean gameOver = false;

    int timer = 60;
    long lastTime = System.currentTimeMillis();

    String textureNames[] = {"EggsBasket.png","Egg.png","","","Back.png"};
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
        for(int i=0; i<chickenCount; i++){
            DrawSprite(gl, chickenX[i], chickenY, 2, 1);
        }

        DrawSprite(gl, xPlayer,yPlayer, 0, 1);

        if (!eggActive) {
            spawnEggFromRandomChicken();
        }
        if (eggActive && activeChicken != -1) {

            DrawSprite(gl, eggX[activeChicken], eggY[activeChicken], 1, 0.7f);
            updateEgg(activeChicken);
        }
        drawScore(gl);

        if (current - lastTime >= 1000 && !gameOver) {
            timer--;
            lastTime = current;

            if (timer <= 0) {
                gameOver = true;
            }
        }

        if (gameOver) {
            drawGameOver(gl);
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
        if (gameOver) return;
        eggY[index] -= eggSpeed;
        if (eggY[index] <= yPlayer + 10 && eggY[index] >= yPlayer+5 ) {
            if (Math.abs(eggX[index] - xPlayer) <10) {

                score++;
                if (score > highScore) {
                    highScore = score;
                    saveHighScore();
                }

                chickenHasEgg[index] = false;
                eggActive = false;
                activeChicken = -1;
                return;
            }
        }
        if (eggY[index] <= 0) {
            chickenHasEgg[index] = false;
            eggActive = false;
            activeChicken = -1;

            lives--;
            if (lives <= 0) {
            gameOver=true;
            }
            return;
        }
    }
    public void spawnEggFromRandomChicken() {
        activeChicken = rand.nextInt(chickenCount);
        eggX[activeChicken] = chickenX[activeChicken];
        eggY[activeChicken] = chickenY;
        chickenHasEgg[activeChicken] = true;
        eggActive = true;
    }

    public void drawScore(GL gl) {
        gl.glColor3f(1.0f, 0.0f, 0.0f);
        gl.glRasterPos2f(-0.95f, -0.8f);
        String scoreText = "Score: " + score;
        for (int i = 0; i < scoreText.length(); i++) {
            glut.glutBitmapCharacter(GLUT.BITMAP_HELVETICA_18, scoreText.charAt(i));
        }
        String highScoreText = "High Score: " + highScore;
        gl.glRasterPos2f(-0.95f, -0.9f);
        for (int i = 0; i < highScoreText.length(); i++) {
            glut.glutBitmapCharacter(GLUT.BITMAP_HELVETICA_18, highScoreText.charAt(i));
        }

        String livesText = "Lives: " + lives;
        gl.glRasterPos2f(0.7f, -0.9f);
        for (int i = 0; i < livesText.length(); i++) {
            glut.glutBitmapCharacter(GLUT.BITMAP_HELVETICA_18, livesText.charAt(i));
        }

        String timerText = "Time: " + timer;
        gl.glRasterPos2f(0.7f, -0.8f);
        for (int i = 0; i < timerText.length(); i++) {
            glut.glutBitmapCharacter(GLUT.BITMAP_HELVETICA_18, timerText.charAt(i));
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
            glut.glutBitmapCharacter(GLUT.BITMAP_HELVETICA_18, text.charAt(i));
        }
        gl.glColor3f(1, 1, 1);
    }

    public void handleKeyPress() {
        if (gameOver) return;
        if (isKeyPressed(KeyEvent.VK_LEFT)) {
            if (xPlayer > 5) {
                xPlayer--;
            }
        }
        if (isKeyPressed(KeyEvent.VK_RIGHT)) {
            if (xPlayer < maxWidth-5) {
                xPlayer++;
            }

        }
    }

    public BitSet keyBits = new BitSet(256);

    @Override
    public void keyPressed(final KeyEvent event) {
        int keyCode = event.getKeyCode();
        keyBits.set(keyCode);
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