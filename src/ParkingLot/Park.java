package ParkingLot;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.Timer;

/**
 * @author João Pereira
 * @author Francisco Spínola
 */
public class Park implements ActionListener {

    /**
     * Terminology of variables
     *
     * VAR: Constant, var: normal variable, varL: Label, varF: Frame varB:
     * Button
     */
    public final int MAX_SLOTS = 50;
    public final int COMUNICA_MAIN = 1;
    public final int COMUNICA_CANCELA = 2;
    public final int COMUNICA_SEMAFORO = 3;

    /**
     * true = O (Gate always open) false = C (Gate opens and / or closes
     * depending on the functionality of each method)
     */
    private boolean mode;

    /* Regular variables */
    private int slots;
    private int action;
    private boolean insertedKey;
    private boolean block;
    private boolean gate;
    private boolean light;
    private String[] keys;
    private int flag;
    private String codeKey;

    /* Graphical variables */
    private JFrame dialogF;
    private JLabel slotsL;
    private JLabel modeL;
    private JButton exitB;
    private JButton okB;

    /**
     * Park constructor method
     */
    public Park() {
        this.insertedKey = false;
        this.block = false;
        this.slots = 0;
        this.action = 0;
        this.gate = false;
        this.flag = 0;
        this.light = true;
        this.mode = false;
        this.okB = new JButton("OK");
    }

    /**
     * Method responsible for changing the Label that represents the Gate Mode
     *
     * @param label representing the Gate Mode
     */
    public void setModeL(JLabel label) {
        this.modeL = label;
    }

    /**
     * Method responsible for returning the Label that represents the Gate Mode
     *
     * @return Label representing the Gate Mode
     */
    public JLabel getModeL() {
        return this.modeL;
    }

    /**
     * Method responsible for returning the gate mode
     *
     * @return Gate mode
     */
    public synchronized boolean getMode() {
        return this.mode;
    }

    /**
     * Method responsible for changing the Gate Mode
     */
    public synchronized void changeMode() {
        this.mode = !this.mode;
        if (this.mode && !this.gate) {
            this.gate = true;
        } else if (!this.mode && this.gate) {
            this.gate = false;
        }
    }

    /**
     * Method responsible for returning the exit button
     *
     * @return the exit button
     */
    public JButton getExitB() {
        return this.exitB;
    }

    /**
     * Method responsible for defining the exit button
     *
     * @param exitB the exit button
     */
    public void setExitB(JButton exitB) {
        this.exitB = exitB;
    }

    /**
     * Method responsible for returning the ok button
     *
     * @return the button ok
     */
    public JButton getOkB() {
        return this.okB;
    }

    /**
     * Method responsible for returning the dialog frame
     *
     * @return a dialog frame
     */
    public JFrame getDialogF() {
        return this.dialogF;
    }

    /**
     * Method responsible for defining the dialog frame
     *
     * @param dialogF a dialog frame
     */
    public void setDialogF(JFrame dialogF) {
        this.dialogF = dialogF;
    }

    /**
     * Method responsible for returning the slot label
     *
     * @return the slots label
     */
    public JLabel getSlotsL() {
        return this.slotsL;
    }

    /**
     * Method responsible for defining the slot label
     *
     * @param slotsL the slots label
     */
    public void setSlotsL(JLabel slotsL) {
        this.slotsL = slotsL;
    }

    /**
     * Method responsible for returning the key code entered by the user
     *
     * @return the key code
     */
    public String getCodeKey() {
        return codeKey;
    }

    /**
     * Method responsible for setting the key code entered by the user
     *
     * @param codeKey the key code
     */
    public void setCodeKey(String codeKey) {
        this.codeKey = codeKey;
    }

    /**
     * Method responsible for returning the flag number
     *
     * @return the flag number
     */
    public synchronized int getFlag() {
        return flag;
    }

    /**
     * Method responsible for setting the flag number
     *
     * @param flag the flag number
     */
    public synchronized void setFlag(int flag) {
        this.flag = flag;
    }

    /**
     * Method responsible for defining the action number, that is, each button
     * corresponds to a specific action number
     *
     * @param action the action number
     */
    public synchronized void setAction(int action) {
        this.action = action;
    }

    /**
     * Method responsible for returning the action number, that is, each button
     * corresponds to a specific action number
     *
     * @return the action number
     */
    public synchronized int getAction() {
        return this.action;
    }

    /**
     * Method responsible for returning the status of the key introduction true
     * - was introduced; false - not entered
     *
     * @return key introduction status
     */
    public synchronized boolean isInsertedKey() {
        return insertedKey;
    }

    /**
     * Method responsible for defining whether the key was introduced or not
     * true - it was introduced; false - not entered Método responsável por
     *
     * @param insertedKey key introduction status
     */
    public synchronized void setInsertedKey(boolean insertedKey) {
        this.insertedKey = insertedKey;
    }

    /**
     * Method responsible for returning the system state true - locked; false -
     * unlocked
     *
     * @return system state
     */
    public synchronized boolean isBlock() {
        return block;
    }

    /**
     * Method responsible for setting the system state true - blocked; false -
     * unlocked
     *
     * @param block system state
     */
    public synchronized void setBlock(boolean block) {
        this.block = block;
    }

    /**
     * Method responsible for returning the number of occupied places in the car
     * park
     *
     * @return number of occupied places in the car park
     */
    public synchronized int getSlots() {
        return this.slots;
    }

    /**
     * Method responsible for defining the number of occupied places in the car
     * park
     *
     * @param slots number of occupied spaces in the car park
     */
    public synchronized void setSlots(int slots) {
        this.slots = slots;
    }

    /**
     * Method responsible for returning the status of the gate true - open;
     * false- closed
     *
     * @return gate status
     */
    public synchronized boolean getGate() {
        return this.gate;
    }

    /**
     * Method responsible for setting the status of the gate true - open; false
     * -close
     *
     * @param gate gate status
     */
    public synchronized void setGate(boolean gate) {
        this.gate = gate;
    }

    /**
     * Method responsible for returning the state of the traffic light true -
     * green; false- red
     *
     * @return traffic light state
     */
    public synchronized boolean getLight() {
        return this.light;
    }

    /**
     * Method responsible for defining the traffic light state
     *
     * @param light traffic light state
     */
    public synchronized void setLight(boolean light) {
        this.light = light;
    }

    /**
     * Method responsible for returning an array of valid keys
     *
     * @return array of valid keys
     */
    public synchronized String[] getKeys() {
        return this.keys;
    }

    /**
     * Method responsible for defining the array of valid keys
     *
     * @param keys array of valid keys
     */
    public synchronized void setKeys(String[] keys) {
        this.keys = keys;
    }

    /**
     * Method responsible for returning the key in the array of valid keys
     *
     * @param index index of the valid key to be obtained
     * @return valid key in the pretended position
     */
    public synchronized String getKey(int index) {
        return this.keys[index];
    }

    /**
     * Method responsible for defining the key in the key array
     *
     * @param key key to be defined
     * @param index key index to be defined
     */
    public synchronized void setKey(String key, int index) {
        this.keys[index] = key;
    }

    /**
     * Method responsible for waking up multiple waiting threads
     */
    public synchronized void acordaTodas() {
        this.notifyAll();
    }

    /**
     * Method responsible for waking up a waiting thread
     */
    public synchronized void acorda() {
        this.notify();
    }

    /**
     * Method responsible for suspending a thread
     */
    public synchronized void espera() {
        try {
            this.wait();
        } catch (InterruptedException ie) {
            System.out.println(ie.getMessage());
        }
    }

    /**
     * Method responsible for validating the key entered by the user
     *
     * @param key key to be validated
     */
    public void verifyKey(String key) {
        if (key.length() == 4 && key.matches("[0-9]+")) {
            try {
                /**
                 * Information message that will be displayed for 1.5s telling
                 * the user that the key is in the process of validation
                 */
                JOptionPane optionPane = new JOptionPane("Checking key...", JOptionPane.INFORMATION_MESSAGE,
                        JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
                JDialog dialog = new JDialog();
                dialog.setTitle("Key verification");
                dialog.setModal(true);
                dialog.setContentPane(optionPane);
                dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
                dialog.setLocationRelativeTo(null);
                dialog.pack();
                /**
                 * 1.5s duration timer for dialog
                 */
                Timer timer = new Timer(1500, new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent ae) {
                        dialog.dispose();
                    }
                });
                timer.setRepeats(false);
                /**
                 * Dialog timer starts
                 */
                timer.start();
                dialog.setVisible(true);
                int i = 0;
                while (i < this.keys.length && !this.keys[i].equals(key)) {
                    i++;
                }
                if (i == this.keys.length) {
                    /**
                     * Error message telling the user that the key he entered
                     * does not exist in the "config" file
                     */
                    JOptionPane.showMessageDialog(null, "Key not found", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    this.log("Inserted key not found.");
                } else {
                    if (this.slots < this.MAX_SLOTS) {
                        /**
                         * Mode C
                         */
                        if (!this.mode) {
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException ex) {
                                System.out.println(ex.getMessage());
                            }
                            this.gate = true;
                        }
                        /**
                         * Information message telling the user that the key he
                         * entered is valid and that he can access the car park
                         */
                        JOptionPane.showMessageDialog(null, "Valid key access granted!", "Information",
                                JOptionPane.INFORMATION_MESSAGE);
                        this.log("Valid key. Access granted!");
                    } else {
                        JOptionPane.showMessageDialog(null, "Park full!", "Information",
                                JOptionPane.INFORMATION_MESSAGE);
                        this.log("Valid key. Park full!");
                    }

                    /**
                     * If the key entered by the user is valid
                     */
                    this.insertedKey = true;
                }
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        } else {
            /**
             * Error message telling the user that the key he entered has an
             * invalid format
             */
            JOptionPane.showMessageDialog(null, "Invalid key format access denied!",
                    "Invalid Key", JOptionPane.ERROR_MESSAGE);
            try {
                this.log("Invalid key format access denied!");
            } catch (IOException ex) {
            }
        }
        try {
            this.log("Verified key");
        } catch (IOException ex) {
        }
    }

    /**
     * Method responsible for saving program activity records to file
     *
     * @param message activity log message to be saved
     * @throws IOException exception thrown if writing to file fails
     */
    public void log(String message) throws IOException {
        File file = new File("log.txt");
        FileWriter fr = new FileWriter(file, true);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        fr.write("[" + dtf.format(now) + "] - " + message + "\n");
        fr.close();
    }

    /**
     * Method responsible for closing the dialog boxes that appear and that have
     * the "ok" button to close only that window and the "close" button to close
     * the program
     *
     * @param ev event received
     */
    @Override
    public void actionPerformed(ActionEvent ev) {
        /**
         * If the system is not locked
         */
        if (!this.isBlock()) {
            if (ev.getSource() == this.exitB) {
                try {
                    this.log("Program closed.\n");
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
                System.exit(0);
            } else if (ev.getSource() == this.okB) {
                this.dialogF.dispose();
                this.dialogF.setUndecorated(false);
            }
        } else {
            /**
             * Warning message telling the user that the system is locked
             */
            JOptionPane.showMessageDialog(null, "System locked!", "Warning",
                    JOptionPane.WARNING_MESSAGE);
            try {
                this.log("System locked. Key entered canceled.");
            } catch (IOException ex) {
                System.out.println("ERROR");
            }
        }
    }
}
