import java.util.concurrent.Semaphore;
import javax.swing.*;
import java.io.*;
import java.util.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.nio.file.*;
import java.nio.charset.*;


/*
* Valores aos quais o semáforo usa para autorizar (flag): Main - 1
                                                          Cancela - 2
                                                          Semáforo - 3
* Valores da ação de cada botão que são usados no switch case (action): Passar o Cartão (C) - 1
                                                                        Entrada do Carro (E) - 2
                                                                        Saída do Carro (S) - 3
                                                                        Abrir/Fechar Cancela (A/F) 4
                                                                        Resetar o sistema (R) - 5
                                                                        Parar o sistema (P) - 6
 */
/**
 * @author João Pereira Número: 8170202 Turma:LEI2T1
 * @author Francisco Spínola Número:8180140 Turma:LSIRC2T1
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
            this.park.log("Programa inicializado.");
        } catch (IOException ex) {
            System.out.println("ERRO");
        }
        try {
            load(this.park);
        } catch (IOException ex) {
            try {
                this.park.log("Erro ao carregar configurações.");
            } catch (IOException e) {}
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Método que controla o parque de estacionamento (Park)
     */
    @Override
    public void run() {
        this.keycardButtons.start();
        this.gate.start();
        this.light.start();
        try {
            this.park.log("Threads inicializadas.");
        } catch (IOException ex) {
            System.out.println("ERRO");
        }
        this.park.setSlotsL(new JLabel("Lugares ocupados: " + this.park.getSlots() + "/" + this.park.MAX_SLOTS));
        if (this.park.getMode())
            this.park.setModeL(new JLabel("Modo de Cancela: A"));
        else
            this.park.setModeL(new JLabel("Modo de Cancela: F"));
        JFrame frame = new JFrame("Gestor de Parque");
        frame.setUndecorated(true);
        JPanel panel1 = new JPanel();
        JPanel panel2 = new JPanel();
        JPanel panel3 = new JPanel();
        this.park.setExitB(new JButton("Fechar"));
        panel1.add(new JLabel("GESTOR DE PARQUE"));
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
            this.park.log("Inicialização completa");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        while (true) {
            try {
                /**
                 * Se a flag obtiver o valor 1 significa que o utilizador
                 * carregou em algum botão do keycard caso contrário a main fica
                 * à espera de uma nova interação do utilizador
                 */
                if (this.park.getFlag() == this.park.COMUNICA_MAIN) { // variável nova
                    try {
                        this.sem.acquire(); // reduz o nº de autorizações do semáforo
                        this.park.setFlag(0);
                    } catch (InterruptedException ex) {
                        System.out.println(ex.getMessage());
                    }
                    /**
                     * Se não foi efetuada nenhuma ação , ou seja, se não foi
                     * carregado nenhum botão a main fica à espera que tal
                     * aconteça e entretanto a thread é suspensa
                     */
                    if (this.park.getAction() < 1 || this.park.getAction() > 6) {
                        this.park.espera();
                    }

                    switch (this.park.getAction()) {
                        /**
                         * Quando o utilizador carrega no botão "C" (action = 1)
                         */
                        case 1:
                            try {
                                /**
                                 * Se o sistema não estiver bloqueado
                                 */
                                if (!this.park.isBlock()) {
                                    this.park.log("Botão: Cartão/Chave");
                                    /**
                                     * O utilizador introduz uma chave
                                     */
                                    this.keycardButtons.C();
                                    /**
                                     * O Sistema verifica se a chave introduzida
                                     * pelo utilizador é válida ou não e a main
                                     * comunica com a cancela (flag = 2)
                                     */
                                    this.park.verifyKey(this.park.getCodeKey());
                                    this.park.setFlag(this.park.COMUNICA_CANCELA);
                                    this.sem.release();
                                    this.park.acordaTodas();
                                } else {
                                    /**
                                     * Se o sistema estiver bloqueado
                                     */
                                    this.keycardButtons.blocked();
                                    this.park.log("Sistema bloqueado(Botão: Cartão/Chave)");
                                }
                                this.park.setAction(0);
                            } catch (IOException ex) {
                                System.out.println(ex.getMessage());
                            }
                            break;
                        /**
                         * Quando o utilizador carrega no botão "E" (action = 2)
                         */
                        case 2:
                            try {
                                /**
                                 * Se o sistema não estiver bloqueado
                                 */
                                if (!this.park.isBlock()) {
                                    /**
                                     * O utilizador já introduziu uma chave
                                     * válida antes
                                     */
                                        this.park.log("Botão: Entrada de carro");
                                        this.E();
                                        /**
                                         * Escreve para o ficheiro "config" o
                                         * novo nº de lugares ocupados
                                         */
                                        write();
                                    this.park.setAction(0);
                                } else {
                                    /**
                                     * Se o sistema estiver bloqueado
                                     */
                                    this.keycardButtons.blocked();
                                    this.park.log("Sistema bloqueado(Botão: Entrada de carro)");
                                }
                            } catch (IOException e) {
                                System.out.println(e.getMessage());
                            }

                            break;
                        /**
                         * Quando o utilizador carrega no botão "S" (action = 3)
                         */
                        case 3:
                            try {
                                /**
                                 * Se o sistema não estiver bloqueado
                                 */
                                if (!this.park.isBlock()) {
                                    this.park.log("Botão: Saída de carro");
                                    this.S();
                                    /**
                                     * Escreve para o ficheiro "config" o novo
                                     * nº de lugares ocupados
                                     */
                                    write();
                                } else {
                                    /**
                                     * Se o sistema estiver bloqueado
                                     */
                                    this.keycardButtons.blocked();
                                    this.park.log("Sistema bloqueado(Botão: Saída de carro)");
                                }
                                this.park.setAction(0);
                            } catch (IOException e) {
                                System.out.println(e.getMessage());
                            }
                            break;
                        /*
                         * Quando o utilizador carrega no botão "A/F"(action = 4)
                         */
                        case 4:
                            try {
                                /**
                                 * Se o sistema não estiver bloqueado
                                 */
                                if (!this.park.isBlock()) {
                                    this.park.log("Botão: Abrir/Fechar cancela");
                                    this.keycardButtons.AF();
                                    if (this.park.getMode())
                                        this.park.getModeL().setText("Modo de Cancela: A");
                                    else
                                        this.park.getModeL().setText("Modo de Cancela: F");
                                    /**
                                     * A thread é adormecida durante 500 ms
                                     */
                                    try {
                                        Thread.sleep(500);
                                    } catch (InterruptedException ex) {
                                        System.out.println(ex.getMessage());
                                    }
                                    /**
                                     * A main comunica com a cancela (flag = 2)
                                     */
                                    this.park.setFlag(this.park.COMUNICA_CANCELA);
                                    this.sem.release();
                                    this.park.acordaTodas();
                                } else {
                                    /**
                                     * Se o sistema estiver bloqueado
                                     */
                                    this.keycardButtons.blocked();
                                    this.park.log("Sistema bloqueado(Botão: Abrir/Fechar cancela)");
                                }
                                this.park.setAction(0);
                            } catch (IOException e) {
                                System.out.println(e.getMessage());
                            }
                            break;
                        /**
                         * Quando o utilizador carrega no botão "R" (action = 5)
                         */
                        case 5:
                            try {
                                /**
                                 * Se o sistema não estiver bloqueado
                                 */
                                if (!this.park.isBlock()) {
                                    this.park.log("Botão: Reinicio do sistema");
                                    this.keycardButtons.R();
                                    write();
                                    /**
                                     * A thread é adormecida durante 100 ms
                                     */
                                    try {
                                        Thread.sleep(100);
                                    } catch (InterruptedException ex) {
                                        System.out.println(ex.getMessage());
                                    }
                                    /*
                                     * A main comunica com a cancela (flag = 2)
                                     */
                                    this.park.setFlag(this.park.COMUNICA_CANCELA);
                                    this.sem.release();
                                    this.park.acordaTodas();
                                    /**
                                     * A thread é adormecida durante 100 ms
                                     */
                                    try {
                                        Thread.sleep(100);
                                    } catch (InterruptedException ex) {
                                        System.out.println();
                                    }
                                    /**
                                     * A main comunica com o semáforo (flag = 3)
                                     */
                                    this.park.setFlag(this.park.COMUNICA_SEMAFORO);
                                    this.sem.release();
                                    this.park.acordaTodas();
                                } else {
                                    /**
                                     * Se o sistema estiver bloqueado
                                     */
                                    this.keycardButtons.blocked();
                                    this.park.log("Sistema bloqueado(Botão: Reinicio do sistema)");
                                }
                                this.park.setAction(0);
                            } catch (IOException e) {
                                System.out.println(e.getMessage());
                            }
                            break;
                        case 6:
                            this.park.log("Botão: Paragem do sistema");
                            this.keycardButtons.P();
                            /**
                             * A thread é adormecida durante 500 ms
                             */
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException ex) {
                                System.out.println(ex.getMessage());
                            }
                            /*
                             * A main comunica com a cancela (flag = 2)
                             */
                            this.park.setFlag(this.park.COMUNICA_CANCELA);
                            this.sem.release();
                            this.park.acordaTodas();
                            /**
                             * A thread é adormecida durante 100 ms
                             */
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException ex) {
                                System.out.println(ex.getMessage());
                            }
                            /**
                             * A main comunica com o semáforo (flag = 3)
                             */
                            this.park.setFlag(this.park.COMUNICA_SEMAFORO);
                            this.sem.release();
                            this.park.acordaTodas();
                            try {
                                if (this.park.isBlock()) {
                                    this.park.log("Sistema bloqueado.");
                                } else {
                                    this.park.log("Sistema desbloqueado.");
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
                     * Suspende a thread até que uma outra thread invoque o
                     * método notify()(neste caso acorda()) ou notifyAll()
                     * (neste caso acordaTodas()) desse mesmo objeto
                     */
                    this.park.espera();
                }
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    /**
     * Método que permite ao utilizador simular a entrada do veículo no parque
     * incrementando o nº de lugares ocupados
     *
     * @throws IOException exceção em caso de erro na escrita para o ficheiro
     * "log"
     */
    public void E() throws IOException {
        if (this.park.isBlock() == false) {
            if (!this.park.getGate()) {
                JOptionPane.showMessageDialog(null, "Introduza uma chave válida!", "Informação",
                        JOptionPane.INFORMATION_MESSAGE);
                this.park.log("Entrada de carro mal sucedida. É necessário abrir a cancela.");
            } else {
                /**
                 * Se parque não está cheio
                 */
                if (this.park.getSlots() != this.park.MAX_SLOTS) {
                    this.park.setSlots(this.park.getSlots() + 1);
                    this.park.getSlotsL().setText("Lugares ocupados: " + this.park.getSlots() + "/" + this.park.MAX_SLOTS);
                    
                    JOptionPane.showMessageDialog(null, "Entrada do carro bem sucedida!", "Informação",
                            JOptionPane.INFORMATION_MESSAGE);
                    this.park.log("Entrada de carro.");
                }
                /**
                 * Modo F
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
                    this.park.log("Cancela fechada");
                } 
                /**
                 * Mudar semáforo para o caso de parque ficar cheio após entrada do carro.
                 */
                if (this.park.MAX_SLOTS == this.park.getSlots()) {
                    this.park.setLight(false);
                    this.park.setFlag(this.park.COMUNICA_SEMAFORO);
                    this.sem.release();
                    this.park.acordaTodas();
                    this.park.log("Semáforo vermelho");
                }
            }
        }
    }

    /**
     * Método que permite ao utilizador simular a saída do veículo no parque
     * decrementando o nº de lugares ocupados
     *
     * @throws IOException exceção em caso de erro na escrita para o ficheiro
     * "log"
     */
    public void S() throws IOException {
        /**
         * Se programa estiver bloqueado
         */
        if (this.park.isBlock() == false) {
            /**
             * Se parque estiver vazio
             */
            if (this.park.getSlots() == 0) {
                JOptionPane.showMessageDialog(null, "Parque vazio!", "Informação",
                        JOptionPane.INFORMATION_MESSAGE);
                this.park.log("Saída de carro mal sucedida. Parque vazio.");
            } else {
                /**
                 * Modo A
                 */
                if (this.park.getMode()) {
                    this.park.setSlots(this.park.getSlots() - 1);
                    this.park.getSlotsL().setText("Lugares ocupados: " + this.park.getSlots() + "/" + this.park.MAX_SLOTS);
                    /**
                     * Se parque estava cheio, muda semáforo
                     */
                    if (this.park.getSlots() + 1 == this.park.MAX_SLOTS) {
                        this.park.setLight(true);
                        this.park.setFlag(this.park.COMUNICA_SEMAFORO);
                        this.sem.release();
                        this.park.acordaTodas();
                    }
                    JOptionPane.showMessageDialog(null, "Saída do carro bem sucedida!", "Informação",
                            JOptionPane.INFORMATION_MESSAGE);
                    this.park.log("Saída de carro.");
                }
                /**
                 * Modo F
                 */ 
                else {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ex) {
                        System.out.println(ex.getMessage());
                    }
                    this.park.setGate(true);
                    /*
                     * A main comunica com a cancela (flag = 2)
                     */
                    this.park.setFlag(this.park.COMUNICA_CANCELA);
                    this.sem.release();
                    this.park.acordaTodas();
                    this.park.log("Cancela aberta");
                    this.park.setSlots(this.park.getSlots() - 1);
                    this.park.getSlotsL().setText("Lugares ocupados: " + this.park.getSlots() + "/" + this.park.MAX_SLOTS);
                    if (this.park.getSlots() + 1 == this.park.MAX_SLOTS) {
                        this.park.setLight(true);
                        this.park.setFlag(this.park.COMUNICA_SEMAFORO);
                        this.sem.release();
                        this.park.acordaTodas();
                    }
                    JOptionPane.showMessageDialog(null, "Saída do carro bem sucedida!", "Informação",
                            JOptionPane.INFORMATION_MESSAGE);
                    this.park.log("Saída de carro.");
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ex) {
                        System.out.println(ex.getMessage());
                    }
                    this.park.setGate(false);
                    /*
                     * A main comunica com a cancela (flag = 2)
                     */
                    this.park.setFlag(this.park.COMUNICA_CANCELA);
                    this.sem.release();
                    this.park.acordaTodas();
                    this.park.log("Cancela fechada");
                }
            }
        }
    }

    /**
     * Método responsável por escrever para o ficheiro de configuração (config)
     * o nº de lugares atualmente ocupados e as chaves válidas de acesso ao
     * parque
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
        this.park.log("Configurações guardadas.");
    }

    /**
     * Método responsável por carregar as informações do ficheiro de
     * configuração (config) para o programa e responsável também por apresentar
     * uma janela com as informações carregadas quando o programa é iniciado
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

        park.setDialogF(new JFrame("Carregar..."));
        park.getDialogF().setResizable(false);
        JPanel fields1 = new JPanel();
        JPanel fields2 = new JPanel();

        fields2.add(park.getOkB());
        fields1.setLayout(new BoxLayout(fields1, BoxLayout.Y_AXIS));
        fields1.add(new JLabel("Lugares ocupados: " + park.getSlots() + "/" + park.MAX_SLOTS));
        fields1.add(new JLabel("Chaves carregadas: " + park.getKeys().length));

        park.getDialogF().getContentPane().add(fields1, BorderLayout.CENTER);
        park.getDialogF().getContentPane().add(fields2, BorderLayout.SOUTH);
        park.getDialogF().pack();
        int width = (int) park.getDialogF().getMinimumSize().getWidth() + 40;
        park.getDialogF().setSize(width, (int) park.getDialogF().getSize().getHeight());
        park.getDialogF().setLocation(400, 150);
        park.getDialogF().setVisible(true);

        park.getOkB().addActionListener(park);
        this.park.log("Configurações carregadas.");
    }

    public static void main(String args[]) {
        Main main = new Main();
        main.start();
    }
}
