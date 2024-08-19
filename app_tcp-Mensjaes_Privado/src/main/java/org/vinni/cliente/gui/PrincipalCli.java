package org.vinni.cliente.gui;

import java.awt.Color;
import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.UUID;



public class PrincipalCli extends javax.swing.JFrame {

    private final int PORT = 12345;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String nombreCliente;
    
     public PrincipalCli() {
        generarNombreCliente();
        initComponents();
         
    }

    private void generarNombreCliente() {
        // Genera un nombre único para el cliente usando un UUID.
        this.nombreCliente =UUID.randomUUID().toString().substring(0, 2);
        this.setTitle("Cliente: " + nombreCliente); 
    }
    
 private void initComponents() {
        this.setTitle("Cliente");
        bConectar = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        mensajesTxt = new javax.swing.JTextArea();
        mensajeTxt = new javax.swing.JTextField();
        destinatarioTxt = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel(); 
        btEnviar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(null);

        bConectar.setFont(new java.awt.Font("Segoe UI", 0, 14)); 
        bConectar.setText("CONECTAR CON SERVIDOR");
        bConectar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bConectarActionPerformed(evt);
            }
        });
        getContentPane().add(bConectar);
        bConectar.setBounds(240, 40, 210, 40);

        mensajesTxt.setColumns(20);
        mensajesTxt.setRows(5);
        mensajesTxt.setEnabled(false);
        jScrollPane1.setViewportView(mensajesTxt);

        getContentPane().add(jScrollPane1);
        jScrollPane1.setBounds(30, 230, 410, 110);

        mensajeTxt.setFont(new java.awt.Font("Verdana", 0, 14)); 
        getContentPane().add(mensajeTxt);
        mensajeTxt.setBounds(40, 120, 350, 30);

        jLabel2.setFont(new java.awt.Font("Verdana", 0, 14));
        jLabel2.setText("Mensaje:");
        getContentPane().add(jLabel2);
        jLabel2.setBounds(20, 90, 120, 30);

        jLabel3.setFont(new java.awt.Font("Verdana", 0, 14));
        jLabel3.setText("Destinatario:");
        getContentPane().add(jLabel3);
        jLabel3.setBounds(20, 160, 120, 30);

        destinatarioTxt.setFont(new java.awt.Font("Verdana", 0, 14)); 
        getContentPane().add(destinatarioTxt);
        destinatarioTxt.setBounds(140, 160, 250, 30);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 18)); 
        jLabel1.setForeground(new java.awt.Color(64, 64, 64));
        jLabel1.setText("CLIENTE TCP : " + nombreCliente);
        getContentPane().add(jLabel1);
        jLabel1.setBounds(110, 10, 250, 17);
        
        btEnviar.setFont(new java.awt.Font("Verdana", 1, 14)); 
        btEnviar.setText("Enviar");
        btEnviar.setBackground(new Color (173, 255, 47));
        btEnviar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btEnviarActionPerformed(evt);
            }
        });
        getContentPane().add(btEnviar);
        btEnviar.setBounds(327, 200, 120, 27);

        setSize(new java.awt.Dimension(491, 400));
        getContentPane().setBackground(new Color(173, 216, 230)); 
        setLocationRelativeTo(null);
    }

    private void bConectarActionPerformed(java.awt.event.ActionEvent evt) {
        conectar();
    }

    private void btEnviarActionPerformed(java.awt.event.ActionEvent evt) {
        enviarMensaje();
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PrincipalCli().setVisible(true);
            }
        });
    }

    private javax.swing.JButton bConectar;
    private javax.swing.JButton btEnviar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3; 
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea mensajesTxt;
    private javax.swing.JTextField mensajeTxt;
    private javax.swing.JTextField destinatarioTxt; 


   //Genera la conexion entre el servidor y el cliente
    private void conectar() {
        JOptionPane.showMessageDialog(this, "Conectando con servidor");
        try {
            if (socket == null || socket.isClosed()) {
                socket = new Socket("localhost", PORT);
                out = new PrintWriter(socket.getOutputStream(), true);
                out.println(nombreCliente); // Enviar el nombre del cliente al servidor
            }
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            new Thread(new Runnable() {
                public void run() {
                    try {
                        String fromServer;
                        while ((fromServer = in.readLine()) != null) {
                            mensajesTxt.append(fromServer + "\n");
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al conectar con el servidor: " + e.getMessage());
        }
    }
   // Envia el mensaje 
    private void enviarMensaje() {
        if (out != null) {
            String destinatario = destinatarioTxt.getText().trim();
            String mensaje = mensajeTxt.getText().trim();
            if (!destinatario.isEmpty()) {
                out.println("@" + destinatario + " " + mensaje);
            } else {
                out.println(mensaje);
            }
            mensajeTxt.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "No estás conectado al servidor.");
        }
    }
}
