package mx.ipn.escom.clocks.client.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
// import java.io.DataInputStream;
import java.io.IOException;
// import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
// import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import mx.ipn.escom.clocks.common.model.Libro;
import mx.ipn.escom.clocks.common.rmi.Libreria;

public class Container extends JFrame {

  // Socket cliente;
  private DatagramSocket cliente;
  private TimerLabel timerLabel;
  private JButton requestButton;
  private Libreria libreria;
  private JPanel bookPanel;

  private JLabel bookNISBNLabelValue;
  private JLabel bookNameLabelValue;
  private JLabel bookAuthorLabelValue;
  private JLabel bookEditorialLabelValue;
  private JLabel bookPriceLabelValue;

  public Container() {
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setTitle("LECTOR");
    this.setLayout(new BorderLayout());
    this.setSize(400, 300);
    this.setResizable(false);
    this.setVisible(true);

    init();
  }

  private void init() {
    JPanel timerPanel = new JPanel();
    this.timerLabel = new TimerLabel();
    timerPanel.add(timerLabel);
    Thread th = new Thread(timerLabel);
    this.add(timerPanel, BorderLayout.NORTH);

    this.bookPanel = new JPanel();
    this.bookPanel.setSize(200, 200);
    this.bookPanel.setLayout(new GridLayout(5, 2));

    JLabel bookISBNLabel = new JLabel("ISBN: ");
    bookISBNLabel.setHorizontalAlignment(JLabel.RIGHT);
    bookNISBNLabelValue = new JLabel("");
    bookNISBNLabelValue.setHorizontalAlignment(JLabel.LEFT);

    JLabel bookNameLabel = new JLabel("Nombre del libro: ");
    bookNameLabel.setHorizontalAlignment(JLabel.RIGHT);
    bookNameLabelValue = new JLabel("");
    bookNameLabelValue.setHorizontalAlignment(JLabel.LEFT);

    JLabel bookAuthorLabel = new JLabel("Autor: ");
    bookAuthorLabel.setHorizontalAlignment(JLabel.RIGHT);
    bookAuthorLabelValue = new JLabel("");
    bookAuthorLabelValue.setHorizontalAlignment(JLabel.LEFT);

    JLabel bookEditorialLabel = new JLabel("Editorial: ");
    bookEditorialLabel.setHorizontalAlignment(JLabel.RIGHT);
    bookEditorialLabelValue = new JLabel("");
    bookEditorialLabelValue.setHorizontalAlignment(JLabel.LEFT);

    JLabel bookPriceLabel = new JLabel("Precio: ");
    bookPriceLabel.setHorizontalAlignment(JLabel.RIGHT);
    bookPriceLabelValue = new JLabel("");
    bookPriceLabelValue.setHorizontalAlignment(JLabel.LEFT);

    this.bookPanel.add(bookNameLabel);
    this.bookPanel.add(bookNameLabelValue);
    this.bookPanel.add(bookAuthorLabel);
    this.bookPanel.add(bookAuthorLabelValue);
    this.bookPanel.add(bookEditorialLabel);
    this.bookPanel.add(bookEditorialLabelValue);
    this.bookPanel.add(bookPriceLabel);
    this.bookPanel.add(bookPriceLabelValue);
    add(bookPanel, BorderLayout.CENTER);

    JPanel requestPanel = new JPanel();
    this.requestButton = new JButton("SOLICITAR");
    this.requestButton.setPreferredSize(new Dimension(350,50));
    this.requestButton.setFont(new Font("Arial", Font.PLAIN, 50));
    this.requestButton.setForeground(Color.WHITE);
    this.requestButton.setBackground(Color.GREEN);
    this.requestButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        try {
          setLibroData(libreria.solicitarLibro(timerLabel.getTime()));
        } catch (RemoteException e1) {
          e1.printStackTrace();
        }
      }
    });
    requestPanel.add(this.requestButton);
    this.add(requestPanel, BorderLayout.SOUTH);

    th.start();
    String HOST = "localhost";
    int PORT = 2405;
    // int PORT_RMI = 2406;

    try {
      Registry reg = LocateRegistry.getRegistry(HOST, PORT);
      libreria = (Libreria) reg.lookup("libreria");

      cliente = new DatagramSocket();
      // String dato = "LIBRO";
      // byte[] datoByte = dato.getBytes();
      // DatagramPacket datoDatagram = new DatagramPacket(datoByte, datoByte.length,
      // InetAddress.getByName(HOST), PORT);
      // cliente.send(datoDatagram);

      while (true) {
        DatagramPacket horaDG = new DatagramPacket(new byte[100], 100);
        cliente.receive(horaDG);
        String time = new String(horaDG.getData()).trim();
        System.out.println("Recibido: " + time + " de " + horaDG.getAddress() + ":" + horaDG.getPort());
        String[] dataTime = time.split(":");

        timerLabel.setTime(Integer.parseInt(dataTime[0]), Integer.parseInt(dataTime[1]), Integer.parseInt(dataTime[2]));
      }

    } catch (IOException | NotBoundException ex) {
      Logger.getLogger(Container.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  public void setLibroData(Libro libro) {
    if (libro == null) {
      JOptionPane.showMessageDialog(null, "SE TERMINARON LOS LIBROS");
      return;
    }
    this.bookNISBNLabelValue.setText(libro.getISBN() + "");
    this.bookNameLabelValue.setText(libro.getNombre());
    this.bookAuthorLabelValue.setText(libro.getAutor());
    this.bookEditorialLabelValue.setText(libro.getEditorial());
    this.bookPriceLabelValue.setText(libro.getPrecio() + "");
  }
}
