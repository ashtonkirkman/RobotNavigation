package Robot;
import javax.swing.*;
import java.awt.event.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.lang.*;
import javax.swing.JComponent;
import javax.swing.JFrame;
import java.io.*;
import java.util.Random;
import java.util.Scanner;
import java.net.*;


// This class draws the probability map and value iteration map that you create to the window
// You need only call updateProbs() and updateValues() from your theRobot class to update these maps
class mySmartMap extends JComponent implements KeyListener {
    public static final int NORTH = 0;
    public static final int SOUTH = 1;
    public static final int EAST = 2;
    public static final int WEST = 3;
    public static final int STAY = 4;

    int currentKey;

    int winWidth, winHeight;
    double sqrWdth, sqrHght;
    Color gris = new Color(170,170,170);
    Color myWhite = new Color(220, 220, 220);
    World mundo;
    
    int gameStatus;

    double[][] probs;
    double[][] vals;
    
    public mySmartMap(int w, int h, World wld) {
        mundo = wld;
        probs = new double[mundo.width][mundo.height];
        vals = new double[mundo.width][mundo.height];
        winWidth = w;
        winHeight = h;
        
        sqrWdth = (double)w / mundo.width;
        sqrHght = (double)h / mundo.height;
        currentKey = -1;
        
        addKeyListener(this);
        
        gameStatus = 0;
    }
    
    public void addNotify() {
        super.addNotify();
        requestFocus();
    }
    
    public void setWin() {
        gameStatus = 1;
        repaint();
    }
    
    public void setLoss() {
        gameStatus = 2;
        repaint();
    }
    
    public void updateProbs(double[][] _probs) {
        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                probs[x][y] = _probs[x][y];
            }
        }
        
        repaint();
    }
    
    public void updateValues(double[][] _vals) {
        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                vals[x][y] = _vals[x][y];
            }
        }
        
        repaint();
    }

    public void paint(Graphics g) {
        paintProbs(g);
        //paintValues(g);
    }

    public void paintProbs(Graphics g) {
        double maxProbs = 0.0;
        int mx = 0, my = 0;
        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                if (probs[x][y] > maxProbs) {
                    maxProbs = probs[x][y];
                    mx = x;
                    my = y;
                }
                if (mundo.grid[x][y] == 1) {
                    g.setColor(Color.black);
                    g.fillRect((int)(x * sqrWdth), (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
                else if (mundo.grid[x][y] == 0) {
                    //g.setColor(myWhite);
                    
                    int col = (int)(255 * Math.sqrt(probs[x][y]));
                    if (col > 255)
                        col = 255;
                    g.setColor(new Color(255-col, 255-col, 255));
                    g.fillRect((int)(x * sqrWdth), (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
                else if (mundo.grid[x][y] == 2) {
                    g.setColor(Color.red);
                    g.fillRect((int)(x * sqrWdth), (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
                else if (mundo.grid[x][y] == 3) {
                    g.setColor(Color.green);
                    g.fillRect((int)(x * sqrWdth), (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
            
            }
            if (y != 0) {
                g.setColor(gris);
                g.drawLine(0, (int)(y * sqrHght), (int)winWidth, (int)(y * sqrHght));
            }
        }
        for (int x = 0; x < mundo.width; x++) {
                g.setColor(gris);
                g.drawLine((int)(x * sqrWdth), 0, (int)(x * sqrWdth), (int)winHeight);
        }
        
        //System.out.println("repaint maxProb: " + maxProbs + "; " + mx + ", " + my);
        
        g.setColor(Color.green);
        g.drawOval((int)(mx * sqrWdth)+1, (int)(my * sqrHght)+1, (int)(sqrWdth-1.4), (int)(sqrHght-1.4));
        
        if (gameStatus == 1) {
            g.setColor(Color.green);
            g.drawString("You Won!", 8, 25);
        }
        else if (gameStatus == 2) {
            g.setColor(Color.red);
            g.drawString("You're a Loser!", 8, 25);
        }
    }
    
    public void paintValues(Graphics g) {
        double maxVal = -99999, minVal = 99999;
        int mx = 0, my = 0;
        
        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                if (mundo.grid[x][y] != 0)
                    continue;
                
                if (vals[x][y] > maxVal)
                    maxVal = vals[x][y];
                if (vals[x][y] < minVal)
                    minVal = vals[x][y];
            }
        }
        if (minVal == maxVal) {
            maxVal = minVal+1;
        }

        int offset = winWidth+20;
        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                if (mundo.grid[x][y] == 1) {
                    g.setColor(Color.black);
                    g.fillRect((int)(x * sqrWdth)+offset, (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
                else if (mundo.grid[x][y] == 0) {
                    //g.setColor(myWhite);
                    
                    //int col = (int)(255 * Math.sqrt((vals[x][y]-minVal)/(maxVal-minVal)));
                    int col = (int)(255 * (vals[x][y]-minVal)/(maxVal-minVal));
                    if (col > 255)
                        col = 255;
                    g.setColor(new Color(255-col, 255-col, 255));
                    g.fillRect((int)(x * sqrWdth)+offset, (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
                else if (mundo.grid[x][y] == 2) {
                    g.setColor(Color.red);
                    g.fillRect((int)(x * sqrWdth)+offset, (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
                else if (mundo.grid[x][y] == 3) {
                    g.setColor(Color.green);
                    g.fillRect((int)(x * sqrWdth)+offset, (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
            
            }
            if (y != 0) {
                g.setColor(gris);
                g.drawLine(offset, (int)(y * sqrHght), (int)winWidth+offset, (int)(y * sqrHght));
            }
        }
        for (int x = 0; x < mundo.width; x++) {
                g.setColor(gris);
                g.drawLine((int)(x * sqrWdth)+offset, 0, (int)(x * sqrWdth)+offset, (int)winHeight);
        }
    }

    
    public void keyPressed(KeyEvent e) {
        //System.out.println("keyPressed");
    }
    public void keyReleased(KeyEvent e) {
        //System.out.println("keyReleased");
    }
    public void keyTyped(KeyEvent e) {
        char key = e.getKeyChar();
        //System.out.println(key);
        
        switch (key) {
            case 'i':
                currentKey = NORTH;
                break;
            case ',':
                currentKey = SOUTH;
                break;
            case 'j':
                currentKey = WEST;
                break;
            case 'l':
                currentKey = EAST;
                break;
            case 'k':
                currentKey = STAY;
                break;
        }
    }
}


// This is the main class that you will add to in order to complete the lab
public class theRobot extends JFrame {
    // Mapping of actions to integers
    public static final int NORTH = 0;
    public static final int SOUTH = 1;
    public static final int EAST = 2;
    public static final int WEST = 3;
    public static final int STAY = 4;

    Color bkgroundColor = new Color(230,230,230);
    
    static mySmartMap myMaps; // instance of the class that draw everything to the GUI
    String mundoName;
    
    World mundo; // mundo contains all the information about the world.  See World.java
    double moveProb, sensorAccuracy;  // stores probabilies that the robot moves in the intended direction
                                      // and the probability that a sonar reading is correct, respectively
    
    // variables to communicate with the Server via sockets
    public Socket s;
	public BufferedReader sin;
	public PrintWriter sout;
    
    // variables to store information entered through the command-line about the current scenario
    boolean isManual = false; // determines whether you (manual) or the AI (automatic) controls the robots movements
    boolean knownPosition = false;
    int startX = -1, startY = -1;
    int currentX = -1, currentY = -1;
    int decisionDelay = 250;
    
    // store your probability map (for position of the robot in this array
    double[][] probs;
    
    // store your computed value of being in each state (x, y)
    double[][] Vs;

    // store your policy (i.e., the action you should take in each state)
    int[][] policy;
    
    public theRobot(String _manual, int _decisionDelay) {
        // initialize variables as specified from the command-line
        if (_manual.equals("automatic"))
            isManual = false;
        else
            isManual = true;
        decisionDelay = _decisionDelay;
        
        // get a connection to the server and get initial information about the world
        initClient();
    
        // Read in the world
        mundo = new World(mundoName);
        
        // set up the GUI that displays the information you compute
        int width = 500;
        int height = 500;
        int bar = 20;
        setSize(width,height+bar);
        getContentPane().setBackground(bkgroundColor);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(0, 0, width, height+bar);
        myMaps = new mySmartMap(width, height, mundo);
        getContentPane().add(myMaps);
        
        setVisible(true);
        setTitle("Probability and Value Maps");
        
        doStuff(); // Function to have the robot move about its world until it gets to its goal or falls in a stairwell
    }
    
    // this function establishes a connection with the server and learns
    //   1 -- which world it is in
    //   2 -- it's transition model (specified by moveProb)
    //   3 -- it's sensor model (specified by sensorAccuracy)
    //   4 -- whether it's initial position is known.  if known, its position is stored in (startX, startY)
    public void initClient() {
        int portNumber = 3333;
        String host = "localhost";
        
        try {
			s = new Socket(host, portNumber);
            sout = new PrintWriter(s.getOutputStream(), true);
			sin = new BufferedReader(new InputStreamReader(s.getInputStream()));
            
            mundoName = sin.readLine();
            moveProb = Double.parseDouble(sin.readLine());
            sensorAccuracy = Double.parseDouble(sin.readLine());
            System.out.println("Need to open the mundo: " + mundoName);
            System.out.println("moveProb: " + moveProb);
            System.out.println("sensorAccuracy: " + sensorAccuracy);
            
            // find out of the robots position is know
            String _known = sin.readLine();
            if (_known.equals("known")) {
                knownPosition = true;
                startX = Integer.parseInt(sin.readLine());
                startY = Integer.parseInt(sin.readLine());
                currentX = startX;
                currentY = startY;
                System.out.println("Robot's initial position is known: " + startX + ", " + startY);
            }
            else {
                System.out.println("Robot's initial position is unknown");
            }
        } catch (IOException e) {
            System.err.println("Caught IOException: " + e.getMessage());
        }
    }

    // function that gets human-specified actions
    // 'i' specifies the movement up
    // ',' specifies the movement down
    // 'l' specifies the movement right
    // 'j' specifies the movement left
    // 'k' specifies the movement stay
    int getHumanAction() {
        System.out.println("Reading the action selected by the user");
        while (myMaps.currentKey < 0) {
            try {
                Thread.sleep(50);
            }
            catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
        int a = myMaps.currentKey;
        myMaps.currentKey = -1;
        
        System.out.println("Action: " + a);
        
        return a;
    }
    
    // initializes the probabilities of where the AI is
    void initializeProbabilities() {
        probs = new double[mundo.width][mundo.height];
        Vs = new double[mundo.width][mundo.height];
        // if the robot's initial position is known, reflect that in the probability map
        if (knownPosition) {
            for (int y = 0; y < mundo.height; y++) {
                for (int x = 0; x < mundo.width; x++) {
                    if ((x == startX) && (y == startY))
                        probs[x][y] = 1.0;
                    else
                        probs[x][y] = 0.0;
                }
            }
        }
        else {  // otherwise, set up a uniform prior over all the positions in the world that are open spaces
            int count = 0;
            
            for (int y = 0; y < mundo.height; y++) {
                for (int x = 0; x < mundo.width; x++) {
                    if (mundo.grid[x][y] == 0)
                        count++;
                }
            }
            
            for (int y = 0; y < mundo.height; y++) {
                for (int x = 0; x < mundo.width; x++) {
                    if (mundo.grid[x][y] == 0)
                        probs[x][y] = 1.0 / count;
                    else
                        probs[x][y] = 0;
                }
            }
        }
        
        myMaps.updateProbs(probs);
    }
      // TODO: update the probabilities of where the AI thinks it is based on the action selected and the new sonar readings
    //       To do this, you should update the 2D-array "probs"
    // Note: sonars is a bit string with four characters, specifying the sonar reading in the direction of North, South, East, and West
    //       For example, the sonar string 1001, specifies that the sonars found a wall in the North and West directions, but not in the South and East directions
    void updateProbabilities(int action, String sonars) {
        double[][] newBelief = new double[mundo.width][mundo.height];

        // Motion Model (Prediction Step)
        for (int x = 0; x < mundo.width; x++) {
            for (int y = 0; y < mundo.height; y++) {
                if (mundo.grid[x][y] == 1) {
                    newBelief[x][y] = 0;
                    continue;
                }

                double sum = 0.0;
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        if (Math.abs(dx) + Math.abs(dy) == 2) continue;
                        int prevX = x + dx;
                        int prevY = y + dy;
                        if (prevX < 0 || prevX >= mundo.width || prevY < 0 || prevY >= mundo.height) continue;
                        if (mundo.grid[prevX][prevY] == 1) continue;

                        double transitionProb = getTransitionProbability(prevX, prevY, x, y, action);
                        sum += transitionProb * probs[prevX][prevY];
                    }
                }
                newBelief[x][y] = sum;
            }
        }

        // Sensor Model (Correction Step)
        for (int x = 0; x < mundo.width; x++) {
            for (int y = 0; y < mundo.height; y++) {
                if (mundo.grid[x][y] == 1) {
                    continue;
                }

                double sensorProb = getSensorProbability(x, y, sonars);
                newBelief[x][y] *= sensorProb;
            }
        }

        // Normalize the new belief
        double total = 0.0;
        for (int x = 0; x < mundo.width; x++) {
            for (int y = 0; y < mundo.height; y++) {
                total += newBelief[x][y];
            }
        }
        for (int x = 0; x < mundo.width; x++) {
            for (int y = 0; y < mundo.height; y++) {
                newBelief[x][y] /= total;
            }
        }

        probs = newBelief;
//        printBelief();
        myMaps.updateProbs(probs); // call this function after updating your probabilities so that the
                                   //  new probabilities will show up in the probability map on the GUI
    }

    void printBelief() {
        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                System.out.print(probs[x][y] + " ");
            }
            System.out.println();
        }
    }

    double getTransitionProbability(int prevX, int prevY, int x, int y, int action) {
        if (prevX == x && prevY == y) {
            if (action == STAY) {
                return moveProb;
            } else {
                return (1 - moveProb)/4.0;
            }
        }

        if (prevX == x && prevY == y - 1) {
            if (action == SOUTH) {
                return moveProb;
            } else {
                return (1 - moveProb)/4.0;
            }
        } else if (prevX == x && prevY == y + 1) {
            if (action == NORTH) {
                return moveProb;
            } else {
                return (1 - moveProb)/4.0;
            }
        } else if (prevX == x + 1 && prevY == y) {
            if (action == WEST) {
                return moveProb;
            } else {
                return (1 - moveProb)/4.0;
            }
        } else if (prevX == x - 1 && prevY == y) {
            if (action == EAST) {
                return moveProb;
            } else {
                return (1 - moveProb)/4.0;
            }
        }

        return 0.0;
    }

    // Note: sonars is a bit string with four characters, specifying the sonar reading in the direction of North, South, East, and West
    //       For example, the sonar string 1001, specifies that the sonars found a wall in the North and West directions, but not in the South and East directions
    double getSensorProbability(int x, int y, String sonars) {
        double totalProb;
        double probSensor1;
        double probSensor2;
        double probSensor3;
        double probSensor4;

        // North
        if (sonars.charAt(0) == '1') {
            if (y == 0 || mundo.grid[x][y - 1] == 1) {
                probSensor1 = sensorAccuracy;
            } else {
                probSensor1 = 1 - sensorAccuracy;
            }
        } else {
            if (y == 0 || mundo.grid[x][y - 1] == 1) {
                probSensor1 = 1 - sensorAccuracy;
            } else {
                probSensor1 = sensorAccuracy;
            }
        }

        // South
        if (sonars.charAt(1) == '1') {
            if (y == mundo.height - 1 || mundo.grid[x][y + 1] == 1) {
                probSensor2 = sensorAccuracy;
            } else {
                probSensor2 = 1 - sensorAccuracy;
            }
        } else {
            if (y == mundo.height - 1 || mundo.grid[x][y + 1] == 1) {
                probSensor2 = 1 - sensorAccuracy;
            } else {
                probSensor2 = sensorAccuracy;
            }
        }

        // East
        if (sonars.charAt(2) == '1') {
            if (x == mundo.width - 1 || mundo.grid[x + 1][y] == 1) {
                probSensor3 = sensorAccuracy;
            } else {
                probSensor3 = 1 - sensorAccuracy;
            }
        } else {
            if (x == mundo.width - 1 || mundo.grid[x + 1][y] == 1) {
                probSensor3 = 1 - sensorAccuracy;
            } else {
                probSensor3 = sensorAccuracy;
            }
        }

        // West
        if (sonars.charAt(3) == '1') {
            if (x == 0 || mundo.grid[x - 1][y] == 1) {
                probSensor4 = sensorAccuracy;
            } else {
                probSensor4 = 1 - sensorAccuracy;
            }
        } else {
            if (x == 0 || mundo.grid[x - 1][y] == 1) {
                probSensor4 = 1 - sensorAccuracy;
            } else {
                probSensor4 = sensorAccuracy;
            }
        }

        totalProb = probSensor1 * probSensor2 * probSensor3 * probSensor4;
        return totalProb;
    }

    void computeValueIteration() {
        double[][] newVs = new double[mundo.width][mundo.height];
        double[][] rewards = new double[mundo.width][mundo.height];
        double discount = 0.99;
        double epsilon = 0.00001;       // 0.01
        boolean converged = false;

        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                if (mundo.grid[x][y] == 1) {
                    rewards[x][y] = -100;
                } else if (mundo.grid[x][y] == 2) {
                    rewards[x][y] = -100;           // -10
                } else if (mundo.grid[x][y] == 3) {
                    rewards[x][y] = 200;            // 100
                } else {
                    rewards[x][y] = -1;
                }
            }
        }

        while (!converged) {
            converged = true;

            for (int y = 0; y < mundo.height; y++) {
                for (int x = 0; x < mundo.width; x++) {
                    if (mundo.grid[x][y] == 1) {
                        newVs[x][y] = -100;
                        continue;
                    } else if (mundo.grid[x][y] == 2) {
                        newVs[x][y] = -10;
                        continue;
                    } else if (mundo.grid[x][y] == 3) {
                        newVs[x][y] = 100;
                        continue;
                    }

                    double bestValue = Double.NEGATIVE_INFINITY;

                    for (int action = 0; action < 5; action++) {
                        double expectedUtility = 0.0;

                        for (int dx = -1; dx <= 1; dx++) {
                            for (int dy = -1; dy <= 1; dy++) {
                                if (Math.abs(dx) + Math.abs(dy) == 2) continue;
                                int newX = x + dx, newY = y + dy;
                                if (newX < 0 || newX >= mundo.width || newY < 0 || newY >= mundo.height) continue;

                                double transitionProb = getTransitionProbability(x, y, newX, newY, action);
                                expectedUtility += transitionProb * Vs[newX][newY];
                            }
                        }

                        double newValue = rewards[x][y] + discount * expectedUtility;
                        if (newValue > bestValue) {
                            bestValue = newValue;
                        }
                    }

                    if (Math.abs(bestValue - Vs[x][y]) > epsilon) {
                        converged = false;
                    }

                    newVs[x][y] = bestValue;
                }
            }

            for (int x = 0; x < mundo.width; x++) {
                for (int y = 0; y < mundo.height; y++) {
                    Vs[x][y] = newVs[x][y];
                }
            }
        }
    }

    void computePolicy(boolean debug_mode) {
        policy = new int[mundo.width][mundo.height];

        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                if (mundo.grid[x][y] == 1) {
                    policy[x][y] = -1;
                    continue;
                } else if (mundo.grid[x][y] == 2) {
                    policy[x][y] = -1;
                    continue;
                } else if (mundo.grid[x][y] == 3) {
                    policy[x][y] = -1;
                    continue;
                }

                double bestValue = Double.NEGATIVE_INFINITY;
                int bestAction = -1;

                for (int action = 0; action < 5; action++) {
                    double expectedUtility = 0;

                    for (int dx = -1; dx <= 1; dx++) {
                        for (int dy = -1; dy <= 1; dy++) {
                            if (Math.abs(dx) + Math.abs(dy) == 2) continue;
                            int newX = x + dx, newY = y + dy;
                            if (newX < 0 || newX >= mundo.width || newY < 0 || newY >= mundo.height) continue;
                            if (mundo.grid[newX][newY] == 1) continue;

                            double transitionProb = getTransitionProbability(x, y, newX, newY, action);
                            expectedUtility += transitionProb * Vs[newX][newY];
                        }
                    }

                    if (expectedUtility > bestValue && expectedUtility != 0.0) {
                        bestValue = expectedUtility;
                        bestAction = action;
                    }
                }

                policy[x][y] = bestAction;
            }
        }

        if (debug_mode) {
            for (int y = 0; y < mundo.height; y++) {
                for (int x = 0; x < mundo.width; x++) {
                    if (mundo.grid[x][y] == 1) {
                        System.out.print("X ");
                    } else if (mundo.grid[x][y] == 2) {
                        System.out.print("X ");
                    } else if (mundo.grid[x][y] == 3) {
                        System.out.print("G ");
                    } else {
                        if (policy[x][y] == NORTH) {
                            System.out.print("N ");
                        } else if (policy[x][y] == SOUTH) {
                            System.out.print("S ");
                        } else if (policy[x][y] == EAST) {
                            System.out.print("E ");
                        } else if (policy[x][y] == WEST) {
                            System.out.print("W ");
                        } else if (policy[x][y] == STAY) {
                            System.out.print("STAY ");
                        }
                    }
                }

                System.out.print("\t");

                for (int x = 0; x < mundo.width; x++) {
                    System.out.printf("%4d ", Math.round(Vs[x][y]));
                }
                System.out.println();
            }
        }
    }

    // This is the function you'd need to write to make the robot move using your AI;
    // You do NOT need to write this function for this lab; it can remain as is
    int automaticAction() {
        double max_probability = -1.0;
        for (int x = 0; x < mundo.width; x++) {
            for (int y = 0; y < mundo.height; y++) {
                if (probs[x][y] > max_probability) {
                    max_probability = probs[x][y];
                    currentX = x;
                    currentY = y;
                }
            }
        }

        return policy[currentX][currentY];
    }
    
    void doStuff() {
        int action;
        
        //valueIteration();  // TODO: function you will write in Part II of the lab
        initializeProbabilities();  // Initializes the location (probability) map
        computeValueIteration();
        computePolicy(true);
        
        while (true) {
            try {
                if (isManual)
                    action = getHumanAction();  // get the action selected by the user (from the keyboard)
                else
                    action = automaticAction(); // TODO: get the action selected by your AI;
                                                // you'll need to write this function for part III
                
                sout.println(action); // send the action to the Server
                
                // get sonar readings after the robot moves
                String sonars = sin.readLine();
                //System.out.println("Sonars: " + sonars);
            
                updateProbabilities(action, sonars); // TODO: this function should update the probabilities of where the AI thinks it is
                
                if (sonars.length() > 4) {  // check to see if the robot has reached its goal or fallen down stairs
                    if (sonars.charAt(4) == 'w') {
                        System.out.println("I won!");
                        myMaps.setWin();
                        break;
                    }
                    else if (sonars.charAt(4) == 'l') {
                        System.out.println("I lost!");
                        myMaps.setLoss();
                        break;
                    }
                }
                else {
                    // here, you'll want to update the position probabilities
                    // since you know that the result of the move as that the robot
                    // was not at the goal or in a stairwell
//                    updateProbabilities(action, sonars);
                }
                Thread.sleep(decisionDelay);  // delay that is useful to see what is happening when the AI selects actions
                                              // decisionDelay is specified by the send command-line argument, which is given in milliseconds
            }
            catch (IOException e) {
                System.out.println(e);
            }
            catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }

    // java theRobot [manual/automatic] [delay]
    public static void main(String[] args) {
        theRobot robot = new theRobot(args[0], Integer.parseInt(args[1]));  // starts up the robot
    }
}