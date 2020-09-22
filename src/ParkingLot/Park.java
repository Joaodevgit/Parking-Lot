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

public class Park implements ActionListener {

    /**
     * Terminologia das variáveis
     *
     * VAR: Constante ,var: variável normal, varL: Label, varF: Frame varB:
     * Button
     */
    public final int MAX_SLOTS = 50;
    public final int COMUNICA_MAIN = 1;
    public final int COMUNICA_CANCELA = 2;
    public final int COMUNICA_SEMAFORO = 3;

    /**
        true = A (Cancela sempre aberta)
        false = F (Cancela abre e/ou fecha dependendo das funcionalidades de cada método)
    */
    private boolean mode;

    /* Variáveis regulares */
    private int slots;
    private int action;
    private boolean insertedKey;
    private boolean block;
    private boolean gate;
    private boolean light;
    private String[] keys;
    private int flag;
    private String codeKey;

    /* Variáveis gráficas */
    private JFrame dialogF;
    private JLabel slotsL;
    private JLabel modeL;
    private JButton exitB;
    private JButton okB;

    /**
     * Método construtor park
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
     * Método responsável por alterar a Label que representa o Modo da cancela
     *
     * @param label que representa o Modo da cancela
     */
    public void setModeL(JLabel label) {
        this.modeL = label;
    }

    /**
     * Método responsável por retornar a Label que representa o Modo da cancela
     *
     * @return Label que representa o Modo da cancela
     */
    public JLabel getModeL () {
        return this.modeL;
    }

    /**
     * Método responsável por retornar o Modo da cancela
     *
     * @return Modo da cancela
     */
    public synchronized boolean getMode() {
        return this.mode;
    }

    /**
     * Método responsável por alterar o Modo da cancela
     */
    public synchronized void changeMode() {
        this.mode = !this.mode;
        if (this.mode && !this.gate)
            this.gate = true;
        else if (!this.mode && this.gate)
            this.gate = false;
    }

    /**
     * Método responsável por retornar o botão de exit
     *
     * @return o botão de exit
     */
    public JButton getExitB() {
        return this.exitB;
    }

    /**
     * Método responsável por definir o botão de exit
     *
     * @param exitB o botão de exit
     */
    public void setExitB(JButton exitB) {
        this.exitB = exitB;
    }

    /**
     * Método responsável por retornar o botão ok
     *
     * @return o botão ok
     */
    public JButton getOkB() {
        return this.okB;
    }

    /**
     * Método responsável por retornar a frame de dialog
     *
     * @return a frame de dialog
     */
    public JFrame getDialogF() {
        return this.dialogF;
    }

    /**
     * Método responsável por definir a frame de dialog
     *
     * @param dialogF a frame de dialog
     */
    public void setDialogF(JFrame dialogF) {
        this.dialogF = dialogF;
    }

    /**
     * Método responsável por retornar a label de slots
     *
     * @return a label de slots
     */
    public JLabel getSlotsL() {
        return this.slotsL;
    }

    /**
     * Método responsável por definir a label de slots
     *
     * @param slotsL a label de slots
     */
    public void setSlotsL(JLabel slotsL) {
        this.slotsL = slotsL;
    }

    /**
     * Método responsável por retornar o código da chave introduzido pelo
     * utilizador
     *
     * @return o código da chave
     */
    public String getCodeKey() {
        return codeKey;
    }

    /**
     * Método responsável por definir o código da chave introduzido pelo
     * utilizador
     *
     * @param codeKey O código da chave
     */
    public void setCodeKey(String codeKey) {
        this.codeKey = codeKey;
    }

    /**
     * Método responsável por retornar o número da flag
     *
     * @return o número da flag
     */
    public synchronized int getFlag() { // nova variável
        return flag;
    }

    /**
     * Método responsável por definir o numero da flag
     *
     * @param flag O numero de Flag
     */
    public synchronized void setFlag(int flag) { // nova variável
        this.flag = flag;
    }

    /**
     * Método responsável por definir o número da ação , ou seja , cada botão
     * corresponde a determinado número da ação
     *
     * @param action o número da ação
     */
    public synchronized void setAction(int action) {
        this.action = action;
    }

    /**
     * Método responsável por retornar o número da ação , ou seja , cada botão
     * corresponde a determinado número da ação
     *
     * @return o número da ação
     */
    public synchronized int getAction() {
        return this.action;
    }

    /**
     * Método responsável por retornar o estado da introdução da chave true -
     * foi introduzida; false - não foi introduzida
     *
     * @return estado da introdução da chave
     */
    public synchronized boolean isInsertedKey() {
        return insertedKey;
    }

    /**
     * Método responsável por definir se a chave foi introduzida ou não true -
     * foi introduzida; false - não foi introduzida
     *
     * @param insertedKey estado da introdução da chave
     */
    public synchronized void setInsertedKey(boolean insertedKey) {
        this.insertedKey = insertedKey;
    }

    /**
     * Método responsável por retornar o estado do sistema true - bloqueado;
     * false - desbloqueado
     *
     * @return estado do sistema
     */
    public synchronized boolean isBlock() {
        return block;
    }

    /**
     * Método responsável por definir o estado do sistema true - bloqueado;
     * false - desbloqueado
     *
     * @param block estado do sistema
     */
    public synchronized void setBlock(boolean block) {
        this.block = block;
    }

    /**
     * Método responsável por retornar número de lugares ocupados no parque de
     * estacionamento
     *
     * @return número de lugares ocupados no parque de estacionamento
     */
    public synchronized int getSlots() {
        return this.slots;
    }

    /**
     * Método responsável por definir o número de lugares ocupados no parque de
     * estacionamento
     *
     * @param slots número de lugares ocupados no parque de estacionamento
     */
    public synchronized void setSlots(int slots) {
        this.slots = slots;
    }

    /**
     * Método responsável por retornar o estado da cancela true - aberta; false
     * - fechada
     *
     * @return estado da cancela
     */
    public synchronized boolean getGate() {
        return this.gate;
    }

    /**
     * Método responsável por definir o estado da cancela true - aberta; false -
     * fechada
     *
     * @param gate estado da cancela
     */
    public synchronized void setGate(boolean gate) {
        this.gate = gate;
    }

    /**
     * Método responsável por retornar o estado do semáforo true - verde; false
     * - vermelho
     *
     * @return estado do semáforo
     */
    public synchronized boolean getLight() {
        return this.light;
    }

    /**
     * Método responsável por definir o estado do semáforo
     *
     * @param light estado do semáforo
     */
    public synchronized void setLight(boolean light) {
        this.light = light;
    }

    /**
     * Método responsável por retornar um array de chaves válidas
     *
     * @return array de chaves válidas
     */
    public synchronized String[] getKeys() {
        return this.keys;
    }

    /**
     * Método responsável por definir o array de chaves válidas
     *
     * @param keys array de chaves válidas
     */
    public synchronized void setKeys(String[] keys) {
        this.keys = keys;
    }

    /**
     * Método responsável por retornar a chave no array de chaves válidas
     *
     * @param index indice da chave válida que se pretende obter
     * @return chave válida na posição pretendida
     */
    public synchronized String getKey(int index) {
        return this.keys[index];
    }

    /**
     * Método responsável por definir a chave no array de chaves
     *
     * @param key chave a ser definida
     * @param index indice da chave que se pretende definir
     */
    public synchronized void setKey(String key, int index) {
        this.keys[index] = key;
    }

    /**
     * Método responsável por acordar várias threads em espera
     */
    public synchronized void acordaTodas() {
        this.notifyAll();
    }

    /**
     * Método responsável por acordar uma thread em espera
     */
    public synchronized void acorda() {
        this.notify();
    }

    /**
     * Método responsável por suspendar uma thread
     */
    public synchronized void espera() {
        try {
            this.wait();
        } catch (InterruptedException ie) {
            System.out.println(ie.getMessage());
        }
    }

    /**
     * Método responsável por validar a chave introduzida pelo utilizador
     *
     * @param key chave a ser validada
     * @return sucesso/insucesso da operação
     */
    public void verifyKey(String key) {
        if (key.length() == 4 && key.matches("[0-9]+")) {
            try {
                /**
                 * Mensagem de informação que será exibida durante 1.5s a dizer
                 * ao utilizador que a chave se encontra em processo de
                 * validação
                 */
                JOptionPane optionPane = new JOptionPane("A verificar chave...", JOptionPane.INFORMATION_MESSAGE,
                        JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
                JDialog dialog = new JDialog();
                dialog.setTitle("Verificação da chave");
                dialog.setModal(true);
                dialog.setContentPane(optionPane);
                dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
                dialog.setLocationRelativeTo(null);
                dialog.pack();
                /**
                 * Temporizador de duração 1.5s para a caixa de diálogo
                 */
                Timer timer = new Timer(1500, new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent ae) {
                        dialog.dispose();
                    }
                });
                timer.setRepeats(false);
                /**
                 * Começa o temporizador da caixa de diálogo
                 */
                timer.start();
                dialog.setVisible(true);
                int i = 0;
                while (i < this.keys.length && !this.keys[i].equals(key)) {
                    i++;
                }
                if (i == this.keys.length) {
                    /**
                     * Mensagem de erro a dizer ao utilizador que a chave que
                     * introduziu não se encontra presente no ficheiro "config"
                     */
                    JOptionPane.showMessageDialog(null, "Chave não encontrada", "Erro",
                            JOptionPane.ERROR_MESSAGE);
                    this.log("Chave inserida não encontrada.");
                } else {
                    if (this.slots < this.MAX_SLOTS) {
                        /**
                         * Modo F
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
                         * Mensagem de informação a dizer ao utilizador que a
                         * chave que introduziu é válida e que pode aceder ao
                         * parque de estacionamento
                         */
                        JOptionPane.showMessageDialog(null, "Chave válida acesso concedido!", "Informação",
                                JOptionPane.INFORMATION_MESSAGE);
                        this.log("Chave válida. Acesso concedido!");
                    } else {
                        JOptionPane.showMessageDialog(null, "Parque cheio!", "Informação",
                                JOptionPane.INFORMATION_MESSAGE);
                        this.log("Chave válida. Parque cheio!");
                    }
                    
                    /**
                     * Caso a chave introduzida pelo utilizador seja válida
                     */
                    this.insertedKey = true;
                }
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        } else {
            /**
             * Mensagem de erro a dizer ao utilizador que a chave que introduziu
             * possui um formato inválido
             */
            JOptionPane.showMessageDialog(null, "Formato da chave inválido acesso negado!",
                    "Inválida", JOptionPane.ERROR_MESSAGE);
            try {
                this.log("Formato da chave inválido acesso negado!");
            } catch (IOException ex) {}
        }
        try {
            this.log("Chave verificada");
        } catch (IOException ex) {}
    }

    /**
     * Método responsável por guardar registos de atividade da aplicação para
     * ficheiro
     *
     * @param message mensagem de registo de atividade a ser guardada
     * @throws IOException exceção lançada caso a escrita para ficheiro falhe
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
     * Método responsável por fechar as caixas de diálogo que vão aparecendo e
     * que possuem o botão "ok" para fechar só essa janela e o botão "fechar"
     * para fechar o programa
     *
     * @param ev evento recebido
     */
    @Override
    public void actionPerformed(ActionEvent ev) {
        /**
         * Se o sistema não estiver bloqueado
         */
        if (!this.isBlock()) {
            if (ev.getSource() == this.exitB) {
                try {
                    this.log("Programa fechado.\n");
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
             * Mensagem de aviso a dizer ao utilizador que a chave que o sistema
             * se encontra bloqueado
             */
            JOptionPane.showMessageDialog(null, "Sistema bloqueado!", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            try {
                this.log("Sistema bloqueado. Introdução da chave cancelada.");
            } catch (IOException ex) {
                System.out.println("ERRO");
            }
        }
    }
}