
import java.awt.Dimension;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.BoxLayout;
import javax.swing.JFrame;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author axel_
 */
public class Maestro {
    public static void main(String[] args) {
        new Maestro();
    }

    Maestro() {
        //Codigo de sockets obtenido de https://www.discoduroderoer.es/ejemplo-conexion-tcp-clienteservidor-en-java/
        ServerSocket servidor = null;
        Socket sc = null;
        DataInputStream in;
        DataOutputStream out;
        
        //puerto de nuestro servidor
        final int PUERTO = 54321;
        //Establecemos la ip del servidor

        try {
            InetAddress IP = InetAddress.getByName("127.0.0.1");
            //Creamos el socket del servidor
            servidor = new ServerSocket(PUERTO, 50, IP);
            System.out.println("Servidor iniciado");

            JFrame maestro = new JFrame("Maestro");
            maestro.setLayout(new BoxLayout(maestro.getContentPane(), BoxLayout.Y_AXIS));
            maestro.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            maestro.setSize(400, 700);
            maestro.setMinimumSize(new Dimension(485, 270));
            maestro.setVisible(true);

            //Siempre estara escuchando peticiones
            while (true) {
                //Espero a que un cliente se conecte
                sc = servidor.accept();
                String cliente = sc.getInetAddress().toString();
                System.out.println("Cliente " + cliente + " conectado");
                Reloj r = new Reloj(sc);
                r.setBounds(0, 150, 485, 207);
                maestro.getContentPane().add(r);
                maestro.getContentPane().revalidate();
                maestro.getContentPane().repaint();
                
            }
            //sc.close();

        } catch (IOException ex) {

        }
    }
}
