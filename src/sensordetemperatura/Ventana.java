/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sensordetemperatura;

import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Date;
import java.util.Enumeration;
import javax.swing.ImageIcon;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class Ventana extends JFrame implements ActionListener {

    private JPanel contentPane;
    private JTextField textField;
    Enumeration puertos_libres = null;
    CommPortIdentifier port = null;
    SerialPort puerto_ser = null;
    OutputStream out = null;
    InputStream in = null;
    int temperatura = 10;
    Thread timer;
    JLabel lblNewLabel;
    JLabel lblNewLabel1;
    JLabel bajo;
    JLabel medio;
    JLabel alto;
    JLabel escala;
    JLabel logo;
    JButton btnNewButton, btnNewButton_1, Guardar;
    JFileChooser fileChooser;
    JTextArea areaDeTexto;

    public Ventana() {
        setResizable(false);
        Image icon = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/imagenes/Imagen1.png"));
        setIconImage(icon);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 636, 365);
        this.setTitle("SENSOR TEMPERATURA");

        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);
        contentPane.setBackground(Color.LIGHT_GRAY);
        fileChooser = new JFileChooser();
        areaDeTexto = new JTextArea();
        //para que el texto se ajuste al area
        areaDeTexto.setLineWrap(true);

        this.setVisible(true);
        timer = new Thread(new ImplementoRunnable());
        timer.start();
        timer.interrupt();
        btnNewButton = new JButton("Conectar Sensor");
        btnNewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                puertos_libres = CommPortIdentifier.getPortIdentifiers();
                int aux = 0;
                while (puertos_libres.hasMoreElements()) {
                    port = (CommPortIdentifier) puertos_libres.nextElement();
                    int type = port.getPortType();
                    if (port.getName().equals(textField.getText())) {
                        try {
                            puerto_ser = (SerialPort) port.open("puerto serial", 2000);
                            int baudRate = 9600; // 9600bps
                            //configuracion de arduino
                            puerto_ser.setSerialPortParams(
                                    baudRate,
                                    SerialPort.DATABITS_8,
                                    SerialPort.STOPBITS_1,
                                    SerialPort.PARITY_NONE);
                            puerto_ser.setDTR(true);

                            out = puerto_ser.getOutputStream();//salida de java
                            in = puerto_ser.getInputStream(); // entrada de java
                            btnNewButton_1.setEnabled(true);
                            btnNewButton.setEnabled(false);
                            timer.resume();//esta variable iniciará nuestro metodo ImplementoRunnable
                        } catch (IOException e1) {
                        } catch (PortInUseException e1) {
                            e1.printStackTrace();
                        } catch (UnsupportedCommOperationException e1) {
                            e1.printStackTrace();
                        }

                        break;
                    }
                }
            }
        });

        btnNewButton.setBounds(45, 140, 140, 23);
        contentPane.add(btnNewButton);

        btnNewButton_1 = new JButton("Desconectar");
        btnNewButton_1.setEnabled(false);
        btnNewButton_1.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
                timer.interrupt();
                puerto_ser.close();
                btnNewButton_1.setEnabled(false);
                btnNewButton.setEnabled(true);
            }
        });

        btnNewButton_1.setBounds(210, 140, 128, 23);
        contentPane.add(btnNewButton_1);

        Guardar = new JButton("Guardar");
        Guardar.setBounds(130, 340, 128, 23);
        contentPane.add(Guardar);
        Guardar.addActionListener(this);

        Guardar.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {

                if (ae.getSource() == Guardar) {
                    guardarArchivo();
                }
            }
        }
        );

        textField = new JTextField();
        textField.setBounds(130, 100, 128, 20);
        contentPane.add(textField);
        textField.setColumns(10);

        JLabel lblPuertoCom = new JLabel("Puerto COM");
        lblPuertoCom.setBounds(155, 80, 90, 14);
        contentPane.add(lblPuertoCom);
        lblPuertoCom.setFont(new java.awt.Font("Arial", 1, 13));
        ImageIcon titulo = new ImageIcon("C:\\Users\\Natali\\Documents\\NetBeansProjects\\SensorDeTemperatura\\Imagen2.png");
        lblNewLabel = new JLabel(titulo);
        lblNewLabel.setBounds(10, 10, 280, 60);
        lblNewLabel.setFont(new java.awt.Font("Arial", 0, 18));
        lblNewLabel.setForeground(Color.black);
        contentPane.add(lblNewLabel);

        lblNewLabel1 = new JLabel("Temperatura     Termometro        Escala");
        lblNewLabel1.setBounds(40, 180, 400, 24);
        lblNewLabel1.setFont(new java.awt.Font("Arial", 1, 14));
        lblNewLabel1.setForeground(Color.black);
        contentPane.add(lblNewLabel1);

        escala = new JLabel();
        escala.setForeground(Color.black);
        escala.setBounds(40, 250, 100, 50);
        contentPane.add(escala);
        escala.setFont(new java.awt.Font("Arial", 0, 40));
        escala.setForeground(Color.BLACK);

        bajo = new JLabel("MENOR A 20");
        bajo.setForeground(Color.blue);
        bajo.setBounds(260, 220, 120, 24);
        contentPane.add(bajo);

        medio = new JLabel("ENTRE 20 y 39");
        medio.setForeground(Color.YELLOW);
        medio.setBounds(260, 235, 120, 24);
        contentPane.add(medio);

        alto = new JLabel("MAYOR a 40");
        alto.setForeground(Color.red);
        alto.setBounds(260, 250, 120, 24);
        contentPane.add(alto);

        ImageIcon imagen = new ImageIcon("C:\\Users\\Natali\\Documents\\NetBeansProjects\\SensorDeTemperatura\\ICONO2.01.png");
        logo = new JLabel(imagen);
        logo.setBounds(270, 10, 102, 120);
        contentPane.add(logo);
        logo.setBackground(Color.yellow);

        /*areaDeTexto= new JTextArea();
        areaDeTexto.setBounds(40, 250, 100, 50);
        areaDeTexto.setFont(new java.awt.Font("Arial", 0, 40));
        
        areaDeTexto .setForeground(Color.BLACK);
        areaDeTexto .setBackground(Color.white.brighter());
        contentPane.add(areaDeTexto);*/
    }

    private void guardarArchivo() {
        java.util.Date fecha = new Date();
        try {
            String nombre = "";
            JFileChooser file = new JFileChooser();
            file.showSaveDialog(this);
            File guarda = file.getSelectedFile();

            if (guarda != null) {
                nombre = file.getSelectedFile().getName();
                /*guardamos el archivo y le damos el formato directamente,
		 			 * si queremos que se guarde en formato doc lo definimos como .doc*/
                FileWriter save = new FileWriter(guarda + ".txt");
                save.write("La temperatura recibida por el sensor es: " + escala.getText() + " Fecha: " + fecha);
                save.close();
                JOptionPane.showMessageDialog(null, "El archivo se a guardado Exitosamente", "Información", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Throwable e) {
            JOptionPane.showMessageDialog(null, "Su archivo no se ha guardado", "Advertencia", JOptionPane.WARNING_MESSAGE);
        }

    }

    public void paint(Graphics g) {

        super.paint(g);

        if (temperatura < 20) {
            g.setColor(Color.blue);
        } else if (temperatura >= 20 && temperatura < 40) {
            g.setColor(Color.YELLOW);
        } else {
            g.setColor(Color.red);
        }

        g.fillRect(160, 330 - temperatura, 80, temperatura);
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent ae) {

    }

    private class ImplementoRunnable implements Runnable {

        int aux;

        public void run() {
            while (true) {
                try {
                    out.write('T'); // enviamos el dato al puerto
                    Thread.sleep(100);
                    aux = in.read(); //el puerto nos regresa el valor los sensores
                    if (aux != 2) {
                        temperatura = aux;
                        escala.setText("" + temperatura + " ºC"); //lo imprimimos para mostrar el valor de la temperatura
                        System.out.println(aux);
                    }
                    repaint();

                } catch (Exception e1) {

                }

            }
        }
    }
}
