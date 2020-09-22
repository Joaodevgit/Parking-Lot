import java.util.concurrent.Semaphore;
import javax.swing.*;
import java.awt.*;

/**
 * @author João Pereira Número: 8170202 Turma:LEI2T1
 * @author Francisco Spínola Número:8180140 Turma:LSIRC2T1
 */
public class Light extends Thread {

    private Semaphore sem;
    private Park park;

    /**
     * Método Construtor da classe Light
     *
     * @param park Objeto que irá ser partilhado por todos
     * @param sem Semáforo que irá ser partilhado por todos
     */
    public Light(Park park, Semaphore sem) {
        this.park = park;
        this.sem = sem;
    }

    /**
     * Método run() que irá ficar responsável pela abertura da janela "Semáforo"
     * que irá conter uma imagem de um semáforo e irá ficar encarregue de
     * atualizar a cor do semáforo
     */
    @Override
    public void run() {
        JFrame lightF = new JFrame("Semáforo");
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
        lightF.setLocation(1100,50);
        lightF.setVisible(true);
        while (true) {
            /**
             * Caso o valor da flag ,definido na main,seja igual a 3, irá entrar
             * neste "if" onde o semáforo irá dar acquire (remoção do nº de
             * autorizações) e irá atribuir o valor 0 à variável flag
             */
            if (this.park.getFlag() == this.park.COMUNICA_SEMAFORO) {
                try {
                    sem.acquire();
                    this.park.setFlag(0);
                } catch (InterruptedException ex) {
                    System.out.println(ex.getMessage());
                }
                /**
                 * Alteração da cor do semáforo em função do seu estado: true
                 * (semáforo com cor verde) ou false (semáforo com cor vermelha)
                 */
                if (this.park.getLight()) {
                    lightL.setIcon(new ImageIcon("Green_Light.jpg"));
                } else {
                    lightL.setIcon(new ImageIcon("Red_Light.jpg"));
                }
            } else {
                /**
                 * Deixa a thread em suspensão até que uma outra thread invoque
                 * o método notify()(neste caso acorda()) ou notifyAll() (neste
                 * caso acordaTodas()) desse mesmo objeto (quando um botão é
                 * pressionado na classe Keycard)
                 */
                this.park.espera();
            }
        }
    }
}
