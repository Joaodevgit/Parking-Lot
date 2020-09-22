import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.concurrent.Semaphore;

/**
 * @author João Pereira Número: 8170202 Turma:LEI2T1
 * @author Francisco Spínola Número:8180140 Turma:LSIRC2T1
 */
public class KeyCardButtons extends Thread implements ActionListener {

    private Park park;
    private Semaphore sem;
    /**
     * Variáveis para os botões
     */
    private JButton card;
    private JButton in;
    private JButton out;
    private JButton gate;
    private JButton reset;
    private JButton stop;
    /**
     * Variáveis para os painéis
     */
    private JPanel primary;
    private JPanel secondary;
    /**
     * Variável para a frame
     */
    private JFrame frame;

    /**
     * Método construtor da classe KeyCardButtons (Cartão/Chave)
     *
     * @param park Objeto que irá ser partilhado por todos
     * @param sem Semáforo que irá ser partilhado por todos
     */
    public KeyCardButtons(Park park, Semaphore sem) {
        this.park = park;
        this.sem = sem;
        /**
         * Paineis
         */
        this.primary = new JPanel(new GridLayout(0, 1));
        this.secondary = new JPanel(new GridLayout(0, 1));
        /**
         * Frame
         */
        this.frame = new JFrame("Botões");
        this.frame.setUndecorated(true);
        /**
         * Instanciação/Criação dos botões do parque de estacionamento
         */
        this.card = new JButton("Cartão/Chave (C)");
        this.in = new JButton("Entrada do veículo (E)");
        this.out = new JButton("Saída do veículo (S)");
        this.gate = new JButton("Abrir/Fechar Cancela (A/F)");
        this.reset = new JButton("Reinício do sistema (R)");
        this.stop = new JButton("Paragem do sistema (P)");
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
         * Quando o utilizador carrega no botão "C" para introduzir os dados do
         * cartão para acesso ao parque de estacionamento
         */
        if (ev.getSource() == this.card) {
            this.park.setFlag(this.park.COMUNICA_MAIN);
            this.park.setAction(1);
            this.sem.release(); // Aumenta o nº de autorizações do semáforo
            this.park.acordaTodas(); // Desperta todas as threads suspensas no objeto
            /**
             * Quando o utilizador carrega no botão "E" de modo a simular a
             * entrada do carro no parque de estacionamento
             */
        } else if (ev.getSource() == this.in) {
            this.park.setFlag(this.park.COMUNICA_MAIN);
            this.park.setAction(2);
            this.sem.release(); // Aumenta o nº de autorizações do semáforo
            this.park.acordaTodas(); // Desperta todas as threads suspensas no objeto
            /**
             * Quando o utilizador carrega no botão "S" de modo a simular a
             * saída do carro no parque de estacionamento
             */
        } else if (ev.getSource() == this.out) {
            this.park.setFlag(this.park.COMUNICA_MAIN);
            this.park.setAction(3);
            this.sem.release(); // Aumenta o nº de autorizações do semáforo
            this.park.acordaTodas(); // Desperta todas as threads suspensas no objeto;
            /**
             * Quando o utilizador carrega no botão "A/F" de modo a interagir
             * com a cancela do parque de estacionamento
             */
        } else if (ev.getSource() == this.gate) {
            this.park.setFlag(this.park.COMUNICA_MAIN);
            this.park.setAction(4);
            this.sem.release(); // Aumenta o nº de autorizações do semáforo
            this.park.acordaTodas(); // Desperta todas as threads suspensas no objeto
            /**
             * Quando o utilizador carrega no botão "R" de modo a simular o
             * reínicio do sistema do parque de estacionamento
             */
        } else if (ev.getSource() == this.reset) {
            this.park.setFlag(this.park.COMUNICA_MAIN);
            this.park.setAction(5);
            this.sem.release(); // Aumenta o nº de autorizações do semáforo
            this.park.acordaTodas(); // Desperta todas as threads suspensas no objeto
            /**
             * Quando o utilizador carrega no botão "P" de modo a simular o
             * paragem imediata do funcionamento do sistema do parque de
             * estacionamento
             */
        } else if (ev.getSource() == this.stop) {
            this.park.setFlag(this.park.COMUNICA_MAIN);
            this.park.setAction(6);
            this.sem.release(); // Aumenta o nº de autorizações do semáforo
            this.park.acordaTodas(); // Desperta todas as threads suspensas no objeto
        } else {
            /**
             * Suspende a thread até que uma outra thread invoque o método
             * notify()(neste caso acorda()) ou notifyAll() (neste caso
             * acordaTodas()) desse mesmo objeto (quando um botão é pressionado
             * nesta classe)
             */
            this.park.espera();
        }

    }

    /**
     * Método responsável por permitir que o utilizador não consiga interagir
     * com o sistema , lançando uma caixa de diálogo ("Sistema bloqueado!") cada
     * vez que o utilizador tente interagir com o sistema
     */
    public void blocked() {
        JOptionPane.showMessageDialog(null, "Sistema bloqueado!", "Aviso",
                JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Método por permitir ao utilizador introduzir um número de 4 dígitos (abre
     * uma janela onde o utilizador introduz o código)
     *
     * @throws java.io.IOException exceção em caso de erro na escrita para o
     * ficheiro "config"
     */
    public void C() throws IOException {
        if (this.park.isBlock() == false) {
            
            String keyInput = JOptionPane.showInputDialog(null, "Chave (4 Dígitos):");
            if (keyInput == null) {
                keyInput = "";
            }
            this.park.setCodeKey(keyInput);
            this.park.log("Introdução da chave/cartão: " + keyInput);
        }
    }

    /**
     * Método que permite ao utilizador simular duas posições da chave, para
     * abrir ou fechar a cancela, respetivamente (se aberta → fecha; se fechada
     * → abre)
     *
     * @throws IOException exceção em caso de erro na escrita para o ficheiro
     * "log"
     */
    public void AF() throws IOException {
        if (!this.park.isBlock()) {
            this.park.changeMode();
            this.park.log("Modo alterado para: " + this.park.getMode());
        }
    }

    /**
     * Método que permite ao utilizador simular o reinício do sistema do parque
     * de estacionamento (coloca os lugares todos livres, semáforo a verde e
     * cancela fechada).
     *
     * @throws IOException exceção em caso de erro na escrita para o ficheiro
     */
    public void R() throws IOException {
        if (this.park.isBlock() == false) {
            this.park.setSlots(0);
            this.park.setLight(true);
            this.park.setGate(false);
            this.park.getSlotsL().setText("Lugares ocupados: " + this.park.getSlots() + "/" + this.park.MAX_SLOTS);
            JOptionPane.showMessageDialog(null, "Sistema resetado com sucesso!", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            this.park.log("Sistema resetado.");
        }
    }

    /**
     * Método que permite ao utilizador simular a paragem imediata do sistema do
     * parque de estacionamento (não há movimento da cancela e o semáforo
     * transita para vermelho)
     */
    public void P() {
        if (this.park.isBlock() == true) {
            this.park.setBlock(false);
            if (this.park.MAX_SLOTS == this.park.getSlots()) {
                this.park.setLight(false);
            } else {
                this.park.setLight(true);
            }
            JOptionPane.showMessageDialog(null, "Sistema desbloqueado com sucesso!", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
        } else {
            this.park.setBlock(true);
            this.park.setLight(false);
            JOptionPane.showMessageDialog(null, "Sistema bloqueado com sucesso!", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
        }
    }
}
