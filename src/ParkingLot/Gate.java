import java.util.concurrent.Semaphore;
import javax.swing.*;
import java.awt.*;

/**
 * @author João Pereira Número: 8170202 Turma:LEI2T1
 * @author Francisco Spínola Número:8180140 Turma:LSIRC2T1
 */
public class Gate extends Thread {

    private Park park;
    private Semaphore sem;

    /**
     * Método Construtor da classe Gate
     *
     * @param park Objeto que irá ser partilhado por todos
     * @param sem Semáforo que irá ser partilhado por todos
     */
    public Gate(Park park, Semaphore sem) {
        this.park = park;
        this.sem = sem;
    }

    /**
     * Método run() que irá ficar responsável pela abertura da janela que irá
     * conter uma imagem de uma cancela e irá ficar encarregue de atualizar a
     * cancela
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
             * Caso o valor da flag ,definido na main,seja igual a 2, irá entrar
             * neste "if" onde o semáforo irá dar acquire (remoção do nº de
             * autorizações) e irá atribuir o valor 0 à variável flag
             */
            if (this.park.getFlag() == this.park.COMUNICA_CANCELA) {
                try {
                    sem.acquire();
                    this.park.setFlag(0);
                } catch (InterruptedException ex) {
                    System.out.println(ex.getMessage());
                }
                /**
                 * Alteração da cancela em função do seu estado: true (cancela
                 * irá-se abrir) ou false (cancela irá-se fechar)
                 */
                if (this.park.getGate()) {
                    gateL.setIcon(new ImageIcon("Gate_Opened.jpg"));
                } else {
                    gateL.setIcon(new ImageIcon("Gate_Closed.jpg"));
                }
            } else {
                /**
                 * Suspende a thread até que uma outra thread invoque o método
                 * notify()(neste caso acorda()) ou notifyAll() (neste caso
                 * acordaTodas()) desse mesmo objeto (quando um botão é
                 * pressionado na classe Keycard)
                 */
                this.park.espera();
            }
        }
    }
}
