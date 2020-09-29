package ParkingLot;

import java.util.concurrent.Semaphore;
import javax.swing.*;
import java.awt.*;

/**
 * @author João Pereira
 * @author Francisco Spínola
 */
public class Light extends Thread {

    private Semaphore sem;
    private Park park;

    /**
     * Constructor Method of Light Class
     *
     * @param park Object that will be shared by all classes
     * @param sem Semaphore that will be shared by all classes
     */
    public Light(Park park, Semaphore sem) {
        this.park = park;
        this.sem = sem;
    }

    /**
     * Method that will be responsible for opening the "Traffic Light" window
     * that will contain an image of a traffic light and will be in charge of
     * updating the traffic light color
     */
    @Override
    public void run() {
        JFrame lightF = new JFrame("Traffic Light");
        lightF.setUndecorated(true);
        JPanel panel1 = new JPanel();
        JLabel lightL;
        if (this.park.getLight()) {
            lightL = new JLabel(new ImageIcon("Green_Light.jpg"));
        } else {
            lightL = new JLabel(new ImageIcon("Red_Light.jpg"));
        }
        panel1.add(lightL);
        lightF.getContentPane().add(panel1, BorderLayout.CENTER);
        lightF.pack();
        lightF.setLocation(1100, 50);
        lightF.setVisible(true);
        while (true) {
            /**
             * If the value of the flag (defined in the Main class) is equal to 3, it 
             * will enter on this "if" where the variable sem will use the method
             * acquire() (removal of the number of authorizations) and will 
             * assign the value 0 to the flag variable
             */
            if (this.park.getFlag() == this.park.COMUNICA_SEMAFORO) {
                try {
                    sem.acquire();
                    this.park.setFlag(0);
                } catch (InterruptedException ex) {
                    System.out.println(ex.getMessage());
                }
                /**
                 * Traffic light color change depending on its status: true 
                 * (traffic light with green color) or false (traffic light 
                 * with red color)
                 */
                if (this.park.getLight()) {
                    lightL.setIcon(new ImageIcon("Green_Light.jpg"));
                } else {
                    lightL.setIcon(new ImageIcon("Red_Light.jpg"));
                }
            } else {
                /**
                 * Leave the thread in suspension until another thread invokes 
                 * the notify() (in this case acorda()) or notifyAll() method 
                 * (in this case acordaTodas()) of that same object (when a button
                 * is pressed in the Keycard class)
                 */
                this.park.espera();
            }
        }
    }
}
