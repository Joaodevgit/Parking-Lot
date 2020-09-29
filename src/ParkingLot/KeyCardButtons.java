package ParkingLot;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.concurrent.Semaphore;

/**
 * @author João Pereira
 * @author Francisco Spínola
 */
public class KeyCardButtons extends Thread implements ActionListener {

    private Park park;
    private Semaphore sem;
    /**
     * Button Variables
     */
    private JButton card;
    private JButton in;
    private JButton out;
    private JButton gate;
    private JButton reset;
    private JButton stop;
    /**
     * Panel Variables
     */
    private JPanel primary;
    private JPanel secondary;
    /**
     * Frame Variables
     */
    private JFrame frame;

    /**
     * Constructor Method of KeyCardButtons Class
     *
     * @param park Object that will be shared by all classes
     * @param sem Semaphore that will be shared by all classes
     */
    public KeyCardButtons(Park park, Semaphore sem) {
        this.park = park;
        this.sem = sem;
        /**
         * Panels
         */
        this.primary = new JPanel(new GridLayout(0, 1));
        this.secondary = new JPanel(new GridLayout(0, 1));
        /**
         * Frames
         */
        this.frame = new JFrame("Botões");
        this.frame.setUndecorated(true);
        /**
         * Instantiation / Creation of parking lot buttons
         */
        this.card = new JButton("Enter Keycard Code (C)");
        this.in = new JButton("Car Entrance (E)");
        this.out = new JButton("Car Exit (Ex)");
        this.gate = new JButton("Open/Close Gate (O/C)");
        this.reset = new JButton("Reset System (R)");
        this.stop = new JButton("Stop System (S)");
    }

    @Override
    public void run() {
        this.primary.add(this.card);
        this.primary.add(this.in);
        this.primary.add(this.out);
        this.primary.add(this.gate);
        this.primary.add(new JLabel());
        this.secondary.add(this.reset);
        this.secondary.add(this.stop);

        this.frame.getContentPane().add(primary, BorderLayout.CENTER);
        this.frame.getContentPane().add(secondary, BorderLayout.SOUTH);

        this.frame.pack();
        this.frame.setResizable(false);
        this.frame.setLocation(160, 300);
        this.frame.setVisible(true);

        this.card.addActionListener(this);
        this.in.addActionListener(this);
        this.out.addActionListener(this);
        this.gate.addActionListener(this);
        this.reset.addActionListener(this);
        this.stop.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        /**
         * When the user presses the "C" button to enter the data of the card
         * for access to the car park
         */
        if (ev.getSource() == this.card) {
            this.park.setFlag(this.park.COMUNICA_MAIN);
            this.park.setAction(1);
            this.sem.release(); // Increase the number of semaphore authorizations
            this.park.acordaTodas(); // Awakens all threads suspended on the object
            /**
             * When the user presses the "E" button to simulate the car entering
             * the parking lot
             */
        } else if (ev.getSource() == this.in) {
            this.park.setFlag(this.park.COMUNICA_MAIN);
            this.park.setAction(2);
            this.sem.release(); // Increase the number of semaphore authorizations
            this.park.acordaTodas(); // Awakens all threads suspended on the object
            /**
             * When the user presses the "S" button in order to simulate the car
             * leaving the car park
             */
        } else if (ev.getSource() == this.out) {
            this.park.setFlag(this.park.COMUNICA_MAIN);
            this.park.setAction(3);
            this.sem.release(); // Increase the number of semaphore authorizations
            this.park.acordaTodas(); // Awakens all threads suspended on the object
            /**
             * When the user presses the "A / F" button in order to interact
             * with the car park gate
             */
        } else if (ev.getSource() == this.gate) {
            this.park.setFlag(this.park.COMUNICA_MAIN);
            this.park.setAction(4);
            this.sem.release(); // Increase the number of semaphore authorizations
            this.park.acordaTodas(); // Awakens all threads suspended on the object
            /**
             * When the user presses the "R" button in order to simulate the
             * restart of the car park system
             */
        } else if (ev.getSource() == this.reset) {
            this.park.setFlag(this.park.COMUNICA_MAIN);
            this.park.setAction(5);
            this.sem.release(); // Increase the number of semaphore authorizations
            this.park.acordaTodas(); // Awakens all threads suspended on the object
            /**
             * When the user presses the "P" button in order to simulate the
             * immediate stop of the car park system operation
             */
        } else if (ev.getSource() == this.stop) {
            this.park.setFlag(this.park.COMUNICA_MAIN);
            this.park.setAction(6);
            this.sem.release(); // Increase the number of semaphore authorizations
            this.park.acordaTodas(); // Awakens all threads suspended on the object
        } else {
            /**
             * Suspend the thread until another thread invokes the notify()
             * method (in this case it acorda()) or notifyAll() method (in this
             * case it acordaTodas()) of that same object (when a button is
             * pressed in this class)
             */
            this.park.espera();
        }

    }

    /**
     * Method responsible for allowing the user to not be able to interact with
     * the system, launching a dialog box ("System locked!") Every time the user
     * tries to interact with the system
     */
    public void blocked() {
        JOptionPane.showMessageDialog(null, "System locked!", "Warning",
                JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Method for allowing the user to enter a 4-digit number (opens a window
     * where the user enters the code)
     *
     * @throws java.io.IOException exception in case of error in writing to the
     * "config" file
     */
    public void C() throws IOException {
        if (this.park.isBlock() == false) {

            String keyInput = JOptionPane.showInputDialog(null, "Key (4 Digits):");
            if (keyInput == null) {
                keyInput = "";
            }
            this.park.setCodeKey(keyInput);
            this.park.log("Key/Card introduction: " + keyInput);
        }
    }

    /**
     * Method that allows the user to simulate two positions of the key, to open
     * or close the gate, respectively (if open → close; if closed → open)
     * 
     * @throws IOException exception in case of error in writing to the
     * "config" file
     */
    public void AF() throws IOException {
        if (!this.park.isBlock()) {
            this.park.changeMode();
            this.park.log("Mode changed to: " + this.park.getMode());
        }
    }

    /**
     * Method that allows the user to simulate the restart of the car park system
     * (set all spaces free, traffic light is set to green and the sets the gate
     * to closed).
     * 
     * @throws IOException exception in case of error in writing to the
     * "config" file
     */
    public void R() throws IOException {
        if (this.park.isBlock() == false) {
            this.park.setSlots(0);
            this.park.setLight(true);
            this.park.setGate(false);
            this.park.getSlotsL().setText("Occupied places: " + this.park.getSlots() + "/" + this.park.MAX_SLOTS);
            JOptionPane.showMessageDialog(null, "System reset successfully!", "Warning",
                    JOptionPane.WARNING_MESSAGE);
            this.park.log("System reset.");
        }
    }

    /**
     * Method that allows the user to simulate the immediate stop of the car park
     * system (there is no movement of the gate and the traffic light changes to red)
     */
    public void P() {
        if (this.park.isBlock() == true) {
            this.park.setBlock(false);
            if (this.park.MAX_SLOTS == this.park.getSlots()) {
                this.park.setLight(false);
            } else {
                this.park.setLight(true);
            }
            JOptionPane.showMessageDialog(null, "System successfully unlocked!", "Warning",
                    JOptionPane.WARNING_MESSAGE);
        } else {
            this.park.setBlock(true);
            this.park.setLight(false);
            JOptionPane.showMessageDialog(null, "System successfully blocked!", "Warning",
                    JOptionPane.WARNING_MESSAGE);
        }
    }
}
