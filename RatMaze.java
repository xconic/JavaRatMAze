/**
 *
 *  
 * @version 1.0.0.0
 */
// Assignment:G2
// Date:10/4/2012
// File: RatMaze.java
// References: http://docs.oracle.com/javase/tutorial/uiswing/events/keylistener.html-keylistener
// http://slabode.exofire.net/circle_draw.shtml -efficient way to draw a circle
import java.awt.*;
import java.awt.event.*;
import com.jogamp.opengl.*;


/**
 *
 *
 * @extends Frame
 * @implements GLEventListener, KeyListener
 */

public class RatMaze extends Frame  implements GLEventListener, KeyListener {

    public int WIDTH = 500, HEIGHT = 500; // Width x Height
    private static int style[] = { 0, 1, 2, 4, 8}; // predefined bit patterns: 0001,0010,0100,1000
    private int maze[][] = new int[WIDTH / 25][HEIGHT / 25]; // maze[20][20]

    /**
     * 	Sets up RatMaze constructor (sets up canvas)
     */
    public RatMaze() {
        super("RatMaze");
        GLCapabilities caps = new GLCapabilities();
        GLCanvas canvas = new GLCanvas(caps);

        canvas.addGLEventListener(this);
        canvas.addKeyListener(this);
        add("Center", canvas);
        setSize(WIDTH, HEIGHT);
        setVisible(true);
    }

    private final static long serialVersionUID = 0; // / BWP

    /**
     * Main method sets a new Frame and deals with Exiting events
     * @param args
     */
    public static void main(String[] args) {
        RatMaze frame = new RatMaze();

        // exit if frame's close box is clicked
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosed(WindowEvent e) {
                System.exit(0);
            }

            public void windowClosing(WindowEvent e) {
                windowClosed(e);
            }
        });

    } // end main

    /* The functions below are required because we are a GLEventListener.
     We could also have put them in another class and put that class in the
     addGLEventListener method above.
     */

    /**
     * Executed exactly once to initialize the associated GLDrawable
     */
    public void init(GLAutoDrawable drawable) {
        // print every openGL call for debugging purposes
        drawable.setGL(new TraceGL(drawable.getGL(), System.err));
        GL gl = drawable.getGL();

        /**
         * Set the background color when the GLDrawable is cleared
         */
        gl.glClearColor(0.75f, 0.75f, 1.0f, 1.0f); // blue-gray background
        gl.glColor3f(0.0f, 0.0f, 1.0f); // blue drawing color
        gl.glPointSize(4.0f); // a 'dot' is 4 by 4 pixels
    }

    /**
     * Executed if the associated GLDrawable is resized
     */
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL gl = drawable.getGL();

        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrtho(0, WIDTH, 0, HEIGHT, -1, 1);
    }

    /**
     * This method handles the painting of the GLDrawable
     */

    public void display(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();

        /** Clear the color buffer */
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        gl.glColor3i(1, 1, 1);
        loadMaze(maze); // sets the 2d array with bit patterns
        for (int i = 0; i < WIDTH / 25; i++) {
            for (int j = 0; j < HEIGHT / 25; j++) {
                drawCell(i, j, gl, drawable); // executes this line 400 times if 500X500 form settings
            }
        }
        float y = (float)(350*Math.random()+53);
        gl.glColor3f(0.0f, 0.0f, 1.0f);
        DrawCircle(15.0f,y,10.0f,5,gl,drawable );
    }

    /** This method handles things if display depth changes */
    public void displayChanged(GLAutoDrawable drawable,
            boolean modeChanged,
            boolean deviceChanged) {}

    /**
     * This method generates random patterns for maze sides : _ ,|,|__, __| etc.
     * @return ans is an int with a bit pattern, for example 0110 or 1100, etc
     */
    private static int makeStyle() {
        int numStyles = (int) (4 * Math.random());
        int ans = 0;

        System.out.println("#" + numStyles);
        for (int i = 0; i < numStyles; i++) {
            int r = (int) (5 * Math.random());

            System.out.print("r: " + r + "    ");
            ans |= style[r];
            System.out.printf("0X%02x    ", ans);
            System.out.println("Pattern: " + ans);
        }
        return ans;
    }

    /**
     * This method loads a 2d array with style(bit pattern)
     * @param maze
     */
    public void loadMaze(int[][] maze) {
        for (int i = 0; i < HEIGHT / 25; i++) {
            for (int j = 0; j < WIDTH / 25; j++) {
                maze[i][j] = makeStyle();
            }
        }
        System.out.println("Maze[][]:" + maze);
    }

    /**
     * This method draws a cell with a predefined bit parameters. X, Y value multiplied by 25, because passed parameters are 1,2,3..for 2d array
     * and we need X,Y coordinates multiples of 25
     *  0001		1000		0100		0010
     *  ---			 ____		 ___		 ___
     * |   |		!	 |		|	|		|	!
     * |___|		!____|		|---|		|___!
     * @param x
     * @param y
     * @param gl
     * @param drawable
     */
    public void drawCell(int x, int y, GL gl, GLAutoDrawable drawable) {
        int style1 = maze[x][y];

        gl.glBegin(GL.GL_LINES);
        if ((style1 & 1) == 1) { // 0001 LSB
            System.out.println("truth:" + (style1 & 1));
            gl.glVertex2i(x * 25, y * 25);
            gl.glVertex2i(x * 25, y * 25 + 25);
        }
        if ((style1 & 8) == 8) { // 1000MSB
            gl.glVertex2i(x * 25, y * 25);
            gl.glVertex2i(x * 25 + 25, y * 25);
        }
        if ((style1 & 4) == 4) { // 0100
            gl.glVertex2i(x * 25, y * 25 + 25);
            gl.glVertex2i(x * 25 + 25, y * 25 + 25);
        }
        if ((style1 & 2) == 2) { // 0010
            gl.glVertex2i(x * 25, y * 25);
            gl.glVertex2i(x * 25, y * 25 + 25);
        }
        gl.glEnd();

    }

    /**
     * This method checks if UP,DOWN,LEFT,RIGHT,SPACE keys were pressed
     * @param e
     */
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();

        if (keyCode == KeyEvent.VK_DOWN) {
            down();
        }
        if (keyCode == KeyEvent.VK_UP) {
            up();
        }
        if (keyCode == KeyEvent.VK_RIGHT) {
            right();
        }
        if (keyCode == KeyEvent.VK_LEFT) {
            left();
        }
        if (keyCode == KeyEvent.VK_SPACE) {
            move();
        }
    }

    public void keyReleased(KeyEvent e) {}

    public void keyTyped(KeyEvent e) {}

    public void down() {
        System.out.println("down");
    }

    public void up() {
        System.out.println("UP");
    }

    public void left() {
        System.out.println("left");
    }

    public void right() {
        System.out.println("Right");
    }

    public void move() {
        System.out.println("Move");
    }

    public void DrawCircle(float cx, float cy, float r, int num_segments, GL gl, GLAutoDrawable drawable) {

        float theta = (float) (2 * 3.1415926 / (float) (num_segments));
        float c = (float) (Math.cos((double) (theta))); // precalculate the sine and cosine
        float s = (float) (Math.sin((double) (theta)));
        float t;
        float x = r; // we start at angle = 0
        float y = 0;
        gl.glBegin(GL.GL_LINE_LOOP);
        for (int ii = 0; ii < num_segments; ii++) {
            gl.glVertex2f(x + cx, y + cy); // output vertex
            // apply the rotation matrix
            t = x;
            x = c * x - s * y;
            y = s * t + c * y;
        }
        gl.glEnd();
    }
} // end class FirstAttempt
