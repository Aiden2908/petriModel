package operatingsys;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author Aiden Perera
 */
public class PetriModel extends JFrame {

    private ResourceHolder lTrafficLightRed, lTrafficLightGreen, lTrafficLightOrange,
            rTrafficLightRed, rTrafficLightGreen, rTrafficLightOrange, sharedResourceHolder;
    private Transition lT1, lT2, lT3, rT1, rT2, rT3;
    private Scanner scanner;
    private DrawPanel drawPanel;

    public PetriModel() {
        this.setSize(new Dimension(600, 500));//GUI
        this.setName("Two traffic lights-Petri Model");
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        init();
        drawPanel = new DrawPanel();
        this.add(drawPanel);
        run();
        this.setVisible(true);

    }

    private void run() {//Gets userinput and starts the traffic flow accordingly.
        scanner = new Scanner(System.in);
        Thread t = new Thread(new Runnable() {//Thread that gets input from user and performes the traffic flows.
            @Override
            public void run() {
                System.out.println("Please see the GUI for graphical representation.");
                while (true) {//Infinite loop for getting users input endlessly.
                    char input = '0';
                    do {
                        System.out.print("What light do you want to go Green? (type L for left R for Right).: ");
                        input = scanner.next().charAt(0);
                    } while (input != 'r' && input != 'R' && input != 'l' && input != 'L');//Checks wether user entered a valid input, if not ask for input again.

                    Transition[] currentTransitions = new Transition[6];
                    if (input == 'l' || input == 'L') {//Order in which transistion are called.
                        currentTransitions[0] = lT1;
                        currentTransitions[1] = lT2;
                        currentTransitions[2] = lT3;
                        currentTransitions[3] = rT1;
                        currentTransitions[4] = rT2;
                        currentTransitions[5] = rT3;
                    } else {
                        currentTransitions[0] = rT1;
                        currentTransitions[1] = rT2;
                        currentTransitions[2] = rT3;
                        currentTransitions[3] = lT1;
                        currentTransitions[4] = lT2;
                        currentTransitions[5] = lT3;
                    }
                    startTraaficFlow(currentTransitions);
                }
            }

        });

        t.start();

    }

    private void startTraaficFlow(Transition[] currentTransitions ) {//Handles calling each trasistion, repainting panel and sleeping the thread.
        System.out.println("Traffic flow has started...");
        for (int i = 0; i < currentTransitions.length; i++) {
            try {
                currentTransitions[i].go();//Synchronized method
                drawPanel.repaint();
                Thread.sleep(833);//each step will take 0.83 secs.
            } catch (InterruptedException ex) {
                //Logger.getLogger(PetriModel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println("Traffic flow has ended...");
    }

    public static void main(String[] args) {//Main
        PetriModel petriModel = new PetriModel();
    }

    private void init() { //initializes resourceHolders and transitions.
        sharedResourceHolder = new ResourceHolder();//Shared holder
        sharedResourceHolder.hasResource = true;

        lTrafficLightRed = new ResourceHolder();//Left Traffic light
        lTrafficLightGreen = new ResourceHolder();
        lTrafficLightOrange = new ResourceHolder();

        lTrafficLightRed.hasResource = true;

        ResourceHolder lT1resourcesRequired[] = new ResourceHolder[2];
        lT1resourcesRequired[0] = lTrafficLightRed;
        lT1resourcesRequired[1] = sharedResourceHolder;

        ResourceHolder lT3recievingResources[] = new ResourceHolder[2];
        lT3recievingResources[0] = lTrafficLightRed;
        lT3recievingResources[1] = sharedResourceHolder;

        lT1 = new Transition(lT1resourcesRequired, lTrafficLightGreen);
        lT2 = new Transition(lTrafficLightGreen, lTrafficLightOrange);
        lT3 = new Transition(lTrafficLightOrange, lT3recievingResources);

        rTrafficLightRed = new ResourceHolder();//Right Traffic light
        rTrafficLightGreen = new ResourceHolder();
        rTrafficLightOrange = new ResourceHolder();

        rTrafficLightRed.hasResource = true;

        ResourceHolder rT1resourcesRequired[] = new ResourceHolder[2];
        rT1resourcesRequired[0] = rTrafficLightRed;
        rT1resourcesRequired[1] = sharedResourceHolder;

        ResourceHolder rT3recievingResources[] = new ResourceHolder[2];
        rT3recievingResources[0] = rTrafficLightRed;
        rT3recievingResources[1] = sharedResourceHolder;

        rT1 = new Transition(rT1resourcesRequired, rTrafficLightGreen);
        rT2 = new Transition(rTrafficLightGreen, rTrafficLightOrange);
        rT3 = new Transition(rTrafficLightOrange, rT3recievingResources);
    }

    private class DrawPanel extends JPanel {//Handles all the drawing of the petri model.

        public DrawPanel() {
            setBackground(new Color(26, 26, 26));
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            g.setColor(Color.WHITE);
            int x = 30, y = 200;
            int lineTempX, lineTempY;

            g.drawString("Red", x - 12, y + 25);//lred
            x += 15;

            g.setColor(Color.RED);
            g.drawOval(x, y, 40, 40);
            if (lTrafficLightRed.hasResource) {
                g.setColor(Color.WHITE);
                g.fillOval(x + 10, y + 10, 20, 20);

            }
            g.setColor(Color.WHITE);

            lineTempX = x;
            lineTempY = y;

            x += 100;//lt1
            y = 20;
            g.drawRect(x, y, 40, 40);
            if (lT1.isRequiredResourcesAvailable()) {
                g.fillRect(x + 5, y + 5, 30, 30);
            }
            g.drawLine(lineTempX + 15, lineTempY, x, y + 20);
            g.drawLine(lineTempX + 15, lineTempY + 40, 145, 400);

            lineTempX = x;
            lineTempY = y;
            y += 90;
            g.drawString("Green", x - 40, y + 25);//lgreen
            g.setColor(Color.green);
            g.drawOval(x, y, 40, 40);
            if (lTrafficLightGreen.hasResource) {
                g.setColor(Color.WHITE);
                g.fillOval(x + 10, y + 10, 20, 20);

            }
            g.setColor(Color.WHITE);

            g.drawLine(lineTempX + 20, lineTempY + 40, x + 20, y);
            g.drawLine(lineTempX + 40, lineTempY + 20, 280, 200);

            lineTempX = x;
            lineTempY = y;

            y += 90;//lt2
            g.drawRect(x, y, 40, 40);
            if (lT2.isRequiredResourcesAvailable()) {
                g.fillRect(x + 5, y + 5, 30, 30);
            }
            g.drawLine(lineTempX + 20, lineTempY + 40, x + 20, y);

            lineTempX = x;
            lineTempY = y;

            y += 90;
            g.drawString("Orange", x - 45, y + 25);//lorange

            g.setColor(Color.ORANGE);
            g.drawOval(x, y, 40, 40);
            if (lTrafficLightOrange.hasResource) {
                g.setColor(Color.WHITE);
                g.fillOval(x + 10, y + 10, 20, 20);

            }
            g.setColor(Color.WHITE);

            g.drawLine(lineTempX + 20, lineTempY + 40, x + 20, y);

            lineTempX = x;
            lineTempY = y;

            y += 90;//lt3
            g.drawRect(x, y, 40, 40);
            if (lT3.isRequiredResourcesAvailable()) {
                g.fillRect(x + 5, y + 5, 30, 30);
            }
            g.drawLine(lineTempX + 20, lineTempY + 40, x + 20, y);
            lineTempX = x;
            lineTempY = y;
            x += 100;
            y = 200;

            g.drawString("Red", x - 30, y + 25);//lred
            x += 15;

            g.setColor(Color.RED);
            g.drawOval(x, y, 40, 40);
            if (sharedResourceHolder.hasResource) {
                g.setColor(Color.WHITE);
                g.fillOval(x + 10, y + 10, 20, 20);
            }
            g.setColor(Color.WHITE);

            g.drawLine(lineTempX + 40, lineTempY + 20, x + 20, y + 40);

            lineTempX = x;
            lineTempY = y;

            x += 100;//rt1
            y = 20;
            g.drawRect(x, y, 40, 40);
            if (rT1.isRequiredResourcesAvailable()) {
                g.fillRect(x + 5, y + 5, 30, 30);
            }
            g.drawLine(lineTempX + 20, lineTempY, x, y + 20);

            lineTempX = x;
            lineTempY = y;

            y += 90;
            g.drawString("Green", x - 40, y + 25);//rgreen
            g.setColor(Color.green);
            g.drawOval(x, y, 40, 40);
            if (rTrafficLightGreen.hasResource) {
                g.setColor(Color.WHITE);
                g.fillOval(x + 10, y + 10, 20, 20);

            }
            g.setColor(Color.WHITE);

            g.drawLine(lineTempX + 20, lineTempY + 40, x + 20, y);
            g.drawLine(lineTempX + 40, lineTempY + 20, 490, 200);

            lineTempX = x;
            lineTempY = y;

            y += 90;//rt2
            g.drawRect(x, y, 40, 40);
            if (rT2.isRequiredResourcesAvailable()) {
                g.fillRect(x + 5, y + 5, 30, 30);
            }
            g.drawLine(lineTempX + 20, lineTempY + 40, x + 20, y);

            lineTempX = x;
            lineTempY = y;

            y += 90;
            g.drawString("Orange", x - 45, y + 25);//rorange
            g.setColor(Color.ORANGE);
            g.drawOval(x, y, 40, 40);
            if (rTrafficLightOrange.hasResource) {
                g.setColor(Color.WHITE);
                g.fillOval(x + 10, y + 10, 20, 20);

            }
            g.setColor(Color.WHITE);

            g.drawLine(lineTempX + 20, lineTempY + 40, x + 20, y);

            lineTempX = x;
            lineTempY = y;

            y += 90;//rt3
            g.drawRect(x, y, 40, 40);
            if (rT3.isRequiredResourcesAvailable()) {
                g.fillRect(x + 5, y + 5, 30, 30);
            }
            g.drawLine(lineTempX + 20, lineTempY + 40, x + 20, y);
            lineTempX = x;
            lineTempY = y;

            x += 100;
            y = 200;

            g.drawString("Red", x - 15, y + 25);//rred
            x += 15;
            g.setColor(Color.red);
            g.drawOval(x, y, 40, 40);
            if (rTrafficLightRed.hasResource) {
                g.setColor(Color.WHITE);
                g.fillOval(x + 10, y + 10, 20, 20);

            }
            g.setColor(Color.WHITE);
            g.drawLine(lineTempX + 40, lineTempY - 20 + 40, x + 20, y + 40);
            g.drawLine(lineTempX, lineTempY + 20, 280, 240);
        }
    }

    private class ResourceHolder {//A class that respresent resource holder. 

        private boolean hasResource;

        public boolean hasResource() {
            return hasResource;
        }

        public void setHasResource(boolean hasResource) {
            this.hasResource = hasResource;
        }
    }

    private class Transition { //A class that respresent a transistion.

        private final ResourceHolder[] rquiredResources;//Required resources to perform transistion.
        private final ResourceHolder[] recievingResources;//Output resources by transistion.

        //Mutiple constructors for easier initialisations.
        public Transition(ResourceHolder[] requiredResourceFrom, ResourceHolder[] recievingResourcesTo) {
            this.rquiredResources = requiredResourceFrom;
            this.recievingResources = recievingResourcesTo;
        }

        public Transition(ResourceHolder requiredResourceFrom, ResourceHolder recievingResourcesTo) {
            rquiredResources = new ResourceHolder[1];
            this.rquiredResources[0] = requiredResourceFrom;

            recievingResources = new ResourceHolder[1];
            this.recievingResources[0] = recievingResourcesTo;
        }

        public Transition(ResourceHolder[] requiredResourceFrom, ResourceHolder recievingResourcesTo) {
            this.rquiredResources = requiredResourceFrom;

            recievingResources = new ResourceHolder[1];
            this.recievingResources[0] = recievingResourcesTo;
        }

        public Transition(ResourceHolder requiredResourceFrom, ResourceHolder[] recievingResourcesTo) {
            rquiredResources = new ResourceHolder[1];
            this.rquiredResources[0] = requiredResourceFrom;

            this.recievingResources = recievingResourcesTo;
        }

        public boolean isRequiredResourcesAvailable() {//Check whether a trasition has all the required to resources to proceed.
            for (ResourceHolder rquiredResource : rquiredResources) {
                if (!rquiredResource.hasResource) {
                    return false;
                }
            }
            return true;
        }

        private void transition() {//Perfomes the transistion.
            for (ResourceHolder s : this.rquiredResources) {
                s.setHasResource(false);
            }
            for (ResourceHolder s : this.recievingResources) {
                s.setHasResource(true);
            }

        }

        public synchronized void go() {///Synchronized method to handle deadlocking.
            if (isRequiredResourcesAvailable()) {
                transition();
            }
        }
    }
}
