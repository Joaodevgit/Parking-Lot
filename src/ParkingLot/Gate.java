package ParkingLot;

import java.util.concurrent.Semaphore;
import javax.swing.*;
import java.awt.*;

/**
 * @author João Pereira 
 * @author Francisco Spínola 
 */
public class Gate extends Thread {

    private Park park;
    private Semaphore sem;

    /**
     * Constructor Method of Gate Class
     *
     * @param park Object that will be shared by all classes
     * @param sem Semaphore that will be shared by all classes
     */
    public Gate(Park park, Semaphore sem) {
        this.park = park;
        this.sem = sem;
    }

    /**
     * Method that will be responsible for opening the window that will 
     * contain an image of a gate and will be in charge of updating the gate
     */
    @Override
    public void run() {
        JPanel panel1 = new JPanel();
        JLabel gateL;
        JFrame gateF = new JFrame();

        if (this.park.getGate()) {
            gateL = new JLabel(new ImageIcon("Gate_Opened.jpg"));
            gateF.getContentPane().add(panel1, BorderLayout.WEST);
        } else {
            gateL = new JLabel(new ImageIcon("Gate_Closed.jpg"));
            gateF.getContentPane().add(panel1, BorderLayout.SOUTH);
        }
        panel1.add(gateL);
        gateF.setSize(481, 400);
        gateF.setUndecorated(true);
        gateF.setLocation(530, 100);
        gateF.setVisible(true);

        while (true) {
            /**
             * If the value of the variable "flag"(defined in the Main class) is
             * equal to 2, it will enter on this "if" where the variable sem will use the 
             * method acquire() (removal of the number of authorizations) and will assign 
             * the value 0 to the flag variable
             */
            if (this.park.getFlag() == this.park.COMUNICA_CANCELA) {
                try {
                    sem.acquire();
                    this.park.setFlag(0);
                } catch (InterruptedException ex) {
                    System.out.println(ex.getMessage());
                }
                /**
                 * Change of the gate according to its status: true (the gate 
                 * will open) or false (the gate will close)
                 */
                if (this.park.getGate()) {
                    gateL.setIcon(new ImageIcon("Gate_Opened.jpg"));
                } else {
                    gateL.setIcon(new ImageIcon("Gate_Closed.jpg"));
                }
            } else {
                /**
                 * Suspend the thread until another thread invokes the notify()
                 * method (in this case acorda()) or notifyAll() method (in this case 
                 * acordaTodas()) of that same object (when a button is pressed 
                 * in the Keycard class)
                 */
                this.park.espera();
            }
        }
    }
}
