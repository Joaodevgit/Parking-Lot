package ParkingLot;

import java.util.concurrent.Semaphore;
import javax.swing.*;
import java.io.*;
import java.util.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.nio.file.*;
import java.nio.charset.*;


/*
* Values that the semaphore uses to authorize (flag): Main - 1
                                                      Gate - 2
                                                      Semáforo - 3
* Action values for each button that are used in the switch case (action): Enter keycard code (C) - 1
                                                                           Car Entrance (E) - 2
                                                                           Car Exit (Ex) - 3
                                                                           Open/Close Gate (O/C) 4
                                                                           Reset System (R) - 5
                                                                           Stop System (S) - 6
 */
/**
 * @author João Pereira
 * @author Francisco Spínola
 */
public class Main extends Thread implements Runnable {

    private Park park;
    private Semaphore sem;
    private KeyCardButtons keycardButtons;
    private Gate gate;
    private Light light;

    public Main() {

        this.sem = new Semaphore(0);
        this.park = new Park();
        this.keycardButtons = new KeyCardButtons(park, sem);
        this.gate = new Gate(park, sem);
        this.light = new Light(park, sem);
        try {
            this.park.log("Program initialized.");
        } catch (IOException ex) {
            System.out.println("ERROR");
        }
        try {
            load(this.park);
        } catch (IOException ex) {
            try {
                this.park.log("Error loading configurations.");
            } catch (IOException e) {
            }
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Method that controls the parking lot (Park)
     */
    @Override
    public void run() {
        this.keycardButtons.start();
        this.gate.start();
        this.light.start();
        try {
            this.park.log("Initialized Threads.");
        } catch (IOException ex) {
            System.out.println("ERROR");
        }
        this.park.setSlotsL(new JLabel("Occupied places: " + this.park.getSlots() + "/" + this.park.MAX_SLOTS));
        if (this.park.getMode()) {
            this.park.setModeL(new JLabel("Gate Mode: O"));
        } else {
            this.park.setModeL(new JLabel("Gate Mode: C"));
        }
        JFrame frame = new JFrame("Park Manager");
        frame.setUndecorated(true);
        JPanel panel1 = new JPanel();
        JPanel panel2 = new JPanel();
        JPanel panel3 = new JPanel();
        this.park.setExitB(new JButton("Close"));
        panel1.add(new JLabel("PARK MANAGER"));
        panel1.setBackground(Color.lightGray);
        panel2.add(this.park.getSlotsL());
        panel2.add(this.park.getModeL());
        panel2.setBackground(Color.lightGray);
        panel3.add(this.park.getExitB());
        panel3.setBackground(Color.lightGray);

        frame.getContentPane().add(panel1, BorderLayout.NORTH);
        frame.getContentPane().add(panel2, BorderLayout.CENTER);
        frame.getContentPane().add(panel3, BorderLayout.SOUTH);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setSize(215, 100);
        frame.setLocation(150, 150);
        frame.setVisible(true);
        this.park.getExitB().addActionListener(this.park);
        try {
            this.park.log("Full initialization");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        while (true) {
            try {
                /**
                 * If the flag gets a value of 1 it means that the user has
                 * pressed a button on the keycard otherwise the Main class is
                 * waiting for a new user interaction
                 */
                if (this.park.getFlag() == this.park.COMUNICA_MAIN) {
                    try {
                        this.sem.acquire(); // reduces the number of semaphore authorizations
                        this.park.setFlag(0);
                    } catch (InterruptedException ex) {
                        System.out.println(ex.getMessage());
                    }
                    /**
                     * If no action was taken, that is, if no button was
                     * pressed, the main class waits for this to happen and
                     * meanwhile the thread is suspended
                     */
                    if (this.park.getAction() < 1 || this.park.getAction() > 6) {
                        this.park.espera();
                    }

                    switch (this.park.getAction()) {
                        /**
                         * When the user presses the "C" button (action = 1)
                         */
                        case 1:
                            try {
                                /**
                                 * If the system is not locked
                                 */
                                if (!this.park.isBlock()) {
                                    this.park.log("Button: Card/Key");
                                    /**
                                     * The user enters a key
                                     */
                                    this.keycardButtons.C();
                                    /**
                                     * The system checks whether the key entered
                                     * by the user is valid or not and the main
                                     * class communicates with the Gate class
                                     * (flag = 2)
                                     */
                                    this.park.verifyKey(this.park.getCodeKey());
                                    this.park.setFlag(this.park.COMUNICA_CANCELA);
                                    this.sem.release();
                                    this.park.acordaTodas();
                                } else {
                                    /**
                                     * If the system is locked
                                     */
                                    this.keycardButtons.blocked();
                                    this.park.log("Locked System (Button: Card/Key)");
                                }
                                this.park.setAction(0);
                            } catch (IOException ex) {
                                System.out.println(ex.getMessage());
                            }
                            break;
                        /**
                         *
                         * When the user presses the "E" button (action = 2)
                         */
                        case 2:
                            try {
                                /**
                                 * If the system is not locked
                                 */
                                if (!this.park.isBlock()) {
                                    /**
                                     * The user has already entered a valid key
                                     * before
                                     */
                                    this.park.log("Button: Car Entrance");
                                    this.E();
                                    /**
                                     * Write to the "config" file the new number
                                     * of occupied places
                                     */
                                    write();
                                    this.park.setAction(0);
                                } else {
                                    /**
                                     * If the system is locked
                                     */
                                    this.keycardButtons.blocked();
                                    this.park.log("Locked System (Button: Car Entrance)");
                                }
                            } catch (IOException e) {
                                System.out.println(e.getMessage());
                            }

                            break;
                        /**
                         * When the user presses the "Ex" button (action = 3)
                         */
                        case 3:
                            try {
                                /**
                                 * If the system is not locked
                                 */
                                if (!this.park.isBlock()) {
                                    this.park.log("Button: Car Exit");
                                    this.S();
                                    /**
                                     * Write to the "config" file the new number
                                     * of occupied places
                                     */
                                    write();
                                } else {
                                    /**
                                     * If the system is locked
                                     */
                                    this.keycardButtons.blocked();
                                    this.park.log("Locked System (Button: Car Exit)");
                                }
                                this.park.setAction(0);
                            } catch (IOException e) {
                                System.out.println(e.getMessage());
                            }
                            break;
                        /*
                         * When the user presses the "O/C" button (action = 4)
                         */
                        case 4:
                            try {
                                /**
                                 * If the system is not locked
                                 */
                                if (!this.park.isBlock()) {
                                    this.park.log("Button: Open/Close gate");
                                    this.keycardButtons.AF();
                                    if (this.park.getMode()) {
                                        this.park.getModeL().setText("Gate Mode: O");
                                    } else {
                                        this.park.getModeL().setText("Gate Mode: C");
                                    }
                                    /**
                                     * The thread is asleep for 500 ms
                                     */
                                    try {
                                        Thread.sleep(500);
                                    } catch (InterruptedException ex) {
                                        System.out.println(ex.getMessage());
                                    }
                                    /**
                                     * The Main class communicates with the Gate
                                     * class (flag = 2)
                                     */
                                    this.park.setFlag(this.park.COMUNICA_CANCELA);
                                    this.sem.release();
                                    this.park.acordaTodas();
                                } else {
                                    /**
                                     * If the system is locked
                                     */
                                    this.keycardButtons.blocked();
                                    this.park.log("Locked System (Button: Open/Close gate)");
                                }
                                this.park.setAction(0);
                            } catch (IOException e) {
                                System.out.println(e.getMessage());
                            }
                            break;
                        /**
                         * When the user presses the "R" button (action = 5)
                         */
                        case 5:
                            try {
                                /**
                                 * If the system is not locked
                                 */
                                if (!this.park.isBlock()) {
                                    this.park.log("Button: System Reset");
                                    this.keycardButtons.R();
                                    write();
                                    /**
                                     * The thread is asleep for 100 ms
                                     */
                                    try {
                                        Thread.sleep(100);
                                    } catch (InterruptedException ex) {
                                        System.out.println(ex.getMessage());
                                    }
                                    /**
                                     * The Main class communicates with the Gate
                                     * class (flag = 2)
                                     */
                                    this.park.setFlag(this.park.COMUNICA_CANCELA);
                                    this.sem.release();
                                    this.park.acordaTodas();
                                    /**
                                     * The thread is asleep for 100 ms
                                     */
                                    try {
                                        Thread.sleep(100);
                                    } catch (InterruptedException ex) {
                                        System.out.println();
                                    }
                                    /**
                                     * The Main class communicates with the
                                     * Light class (flag = 3)
                                     */
                                    this.park.setFlag(this.park.COMUNICA_SEMAFORO);
                                    this.sem.release();
                                    this.park.acordaTodas();
                                } else {
                                    /**
                                     * If the system is locked
                                     */
                                    this.keycardButtons.blocked();
                                    this.park.log("Locked System (Button: System Reset)");
                                }
                                this.park.setAction(0);
                            } catch (IOException e) {
                                System.out.println(e.getMessage());
                            }
                            break;
                        case 6:
                            this.park.log("Button: Stop System");
                            this.keycardButtons.P();
                            /**
                             * The thread is asleep for 500 ms
                             */
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException ex) {
                                System.out.println(ex.getMessage());
                            }
                            /**
                             * The Main class communicates with the Gate class
                             * (flag = 2)
                             */
                            this.park.setFlag(this.park.COMUNICA_CANCELA);
                            this.sem.release();
                            this.park.acordaTodas();
                            /**
                             * The thread is asleep for 100 ms
                             */
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException ex) {
                                System.out.println(ex.getMessage());
                            }
                            /**
                             * The Main class communicates with the Light class
                             * (flag = 3)
                             */
                            this.park.setFlag(this.park.COMUNICA_SEMAFORO);
                            this.sem.release();
                            this.park.acordaTodas();
                            try {
                                if (this.park.isBlock()) {
                                    this.park.log("System locked.");
                                } else {
                                    this.park.log("System unlocked.");
                                }
                            } catch (IOException e) {
                                System.out.println(e.getMessage());
                            }
                            this.park.setAction(0);
                            break;
                        default:
                            break;
                    }
                } else {
                    /**
                     * Suspends the thread until another thread invokes the
                     * notify() method (in this case acorda()) or notifyAll()
                     * method (in this case acordaTodas()) of that same object
                     */
                    this.park.espera();
                }
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    /**
     * Method that allows the user to simulate the entry of the vehicle in the
     * park, increasing the number of occupied places
     *
     * @throws IOException exception in case of error in writing to the "log"
     * file
     */
    public void E() throws IOException {
        if (this.park.isBlock() == false) {
            if (!this.park.getGate()) {
                JOptionPane.showMessageDialog(null, "Enter a valid key!", "Information",
                        JOptionPane.INFORMATION_MESSAGE);

                this.park.log("Unsuccessful car entry . It is necessary to open the gate.");
            } else {
                /**
                 * If park is not full
                 */
                if (this.park.getSlots() != this.park.MAX_SLOTS) {
                    this.park.setSlots(this.park.getSlots() + 1);
                    this.park.getSlotsL().setText("Occupied places: " + this.park.getSlots() + "/" + this.park.MAX_SLOTS);

                    JOptionPane.showMessageDialog(null, "Car entry successful!", "Information",
                            JOptionPane.INFORMATION_MESSAGE);
                    this.park.log("Car entry.");
                }
                /**
                 * Mode C
                 */
                if (!this.park.getMode()) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ex) {
                        System.out.println(ex.getMessage());
                    }
                    this.park.setGate(false);
                    this.park.setFlag(this.park.COMUNICA_CANCELA);
                    this.sem.release();
                    this.park.acordaTodas();
                    this.park.log("Gate closed");
                }
                /**
                 * Change traffic light in case the parking lot becomes full
                 * after the car entrance.
                 */
                if (this.park.MAX_SLOTS == this.park.getSlots()) {
                    this.park.setLight(false);
                    this.park.setFlag(this.park.COMUNICA_SEMAFORO);
                    this.sem.release();
                    this.park.acordaTodas();
                    this.park.log("Red traffic light");
                }
            }
        }
    }

    /**
     * Method that allows the user to simulate the car leaving the park by
     * decreasing the number of occupied places
     *
     * @throws IOException exception in case of error in writing to the "log"
     * file
     */
    public void S() throws IOException {
        /**
         * If the system is locked
         */
        if (this.park.isBlock() == false) {
            /**
             * If park is empty
             */
            if (this.park.getSlots() == 0) {
                JOptionPane.showMessageDialog(null, "Empty car park!", "Information",
                        JOptionPane.INFORMATION_MESSAGE);
                this.park.log("Unsuccessful car exit. Empty park.");
            } else {
                /**
                 * Mode O
                 */
                if (this.park.getMode()) {
                    this.park.setSlots(this.park.getSlots() - 1);
                    this.park.getSlotsL().setText("Occupied places: " + this.park.getSlots() + "/" + this.park.MAX_SLOTS);
                    /**
                     * If park was full, change traffic light
                     */
                    if (this.park.getSlots() + 1 == this.park.MAX_SLOTS) {
                        this.park.setLight(true);
                        this.park.setFlag(this.park.COMUNICA_SEMAFORO);
                        this.sem.release();
                        this.park.acordaTodas();
                    }
                    JOptionPane.showMessageDialog(null, "Successful car exit!", "Information",
                            JOptionPane.INFORMATION_MESSAGE);
                    this.park.log("Car exit.");
                } /**
                 * Mode C
                 */
                else {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ex) {
                        System.out.println(ex.getMessage());
                    }
                    this.park.setGate(true);
                    /**
                     * The Main class communicates with the Gate class (flag= 2)
                     */
                    this.park.setFlag(this.park.COMUNICA_CANCELA);
                    this.sem.release();
                    this.park.acordaTodas();
                    this.park.log("Gate opened");
                    this.park.setSlots(this.park.getSlots() - 1);
                    this.park.getSlotsL().setText("Occupied places: " + this.park.getSlots() + "/" + this.park.MAX_SLOTS);
                    if (this.park.getSlots() + 1 == this.park.MAX_SLOTS) {
                        this.park.setLight(true);
                        this.park.setFlag(this.park.COMUNICA_SEMAFORO);
                        this.sem.release();
                        this.park.acordaTodas();
                    }
                    JOptionPane.showMessageDialog(null, "Successful car exit!", "Information",
                            JOptionPane.INFORMATION_MESSAGE);
                    this.park.log("Car exit.");
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ex) {
                        System.out.println(ex.getMessage());
                    }
                    this.park.setGate(false);
                    /*
                     * The Main class communicates with the Gate class (flag= 2)
                     */
                    this.park.setFlag(this.park.COMUNICA_CANCELA);
                    this.sem.release();
                    this.park.acordaTodas();
                    this.park.log("Gate closed");
                }
            }
        }
    }

    /**
     * Method responsible for writing to the configuration file (config) the
     * number of places currently occupied and the valid keys to access the park
     */
    private void write() throws IOException {
        Charset ENCODING = StandardCharsets.UTF_8;
        Path path = Paths.get("config.cfg");
        List<String> linhas = new ArrayList<>();
        linhas.add("slots=\"" + this.park.getSlots() + "/" + this.park.MAX_SLOTS + "\"");
        for (int i = 0; i < this.park.getKeys().length; i++) {
            linhas.add("key=\"" + this.park.getKey(i) + "\"");
        }
        Files.write(path, linhas, ENCODING);
        this.park.log("Saved configurations.");
    }

    /**
     * Method responsible for loading the information from the configuration
     * file (config) to the program and also responsible for displaying a window
     * with the information loaded when the program is started
     */
    private void load(Park park) throws IOException {
        Charset ENCODING = StandardCharsets.UTF_8;
        Path path = Paths.get("config.cfg");
        List<String> lines = Files.readAllLines(path, ENCODING);
        LineNumberReader lnr = new LineNumberReader(new FileReader("config.cfg"));
        int lineCounter = 0;
        while (lnr.readLine() != null) {
            lineCounter++;
        }
        String fspots = lines.get(0);
        String occupied = "";
        park.setKeys(new String[lineCounter - 1]);

        if (fspots.contains("slots")) {
            int i = 7;
            while (fspots.charAt(i) != 47) {
                occupied += fspots.charAt(i++);
            }
            i++;
            park.setSlots(Integer.parseInt(occupied));
        }

        int i = 1;
        while (i < lines.size() && lines.get(i).contains("key")) {
            park.setKey("", i - 1);
            for (int j = 5; j < 9; j++) {
                park.setKey(park.getKey(i - 1) + lines.get(i).charAt(j), i - 1);
            }
            i++;
        }
        if (park.getSlots() == park.MAX_SLOTS) {
            park.setLight(false);
        } else {
            park.setLight(true);
        }

        park.setDialogF(new JFrame("Loading..."));
        park.getDialogF().setResizable(false);
        JPanel fields1 = new JPanel();
        JPanel fields2 = new JPanel();

        fields2.add(park.getOkB());
        fields1.setLayout(new BoxLayout(fields1, BoxLayout.Y_AXIS));
        fields1.add(new JLabel("Occupied places: " + park.getSlots() + "/" + park.MAX_SLOTS));
        fields1.add(new JLabel("Keys loaded: " + park.getKeys().length));

        park.getDialogF().getContentPane().add(fields1, BorderLayout.CENTER);
        park.getDialogF().getContentPane().add(fields2, BorderLayout.SOUTH);
        park.getDialogF().pack();
        int width = (int) park.getDialogF().getMinimumSize().getWidth() + 40;
        park.getDialogF().setSize(width, (int) park.getDialogF().getSize().getHeight());
        park.getDialogF().setLocation(400, 150);
        park.getDialogF().setVisible(true);

        park.getOkB().addActionListener(park);
        this.park.log("Configurations loaded.");
    }

    public static void main(String args[]) {
        Main main = new Main();
        main.start();
    }
}
