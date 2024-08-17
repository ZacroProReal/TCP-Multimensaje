package org.vinni.cliente.gui;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.UUID;

/**
 * author: Vinni 2024
 */
public class PrincipalCli extends javax.swing.JFrame {

    private final int PORT = 12345;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String nombreCliente;

    public PrincipalCli() {
        initComponents();
        generarNombreCliente();
    }

    private void generarNombreCliente() {
        // Genera un nombre único para el cliente usando un UUID.
        this.nombreCliente = "Cliente-" + UUID.randomUUID().toString().substring(0, 2);
    }

    private void initComponents() {
        this.setTitle("Cliente ");
        bConectar = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        mensajesTxt = new javax.swing.JTextArea();
        mensajeTxt = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        btEnviar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(null);

        bConectar.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        bConectar.setText("CONECTAR CON SERVIDOR");
        bConectar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bConectarActionPerformed(evt);
            }
        });
        getContentPane().add(bConectar);
        bConectar.setBounds(260, 40, 210, 40);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(204, 0, 0));
        jLabel1.setText("CLIENTE TCP : " + nombreCliente);
        getContentPane().add(jLabel1);
        jLabel1.setBounds(110, 10, 250, 17);

        mensajesTxt.setColumns(20);
        mensajesTxt.setRows(5);
        mensajesTxt.setEnabled(false);
        jScrollPane1.setViewportView(mensajesTxt);

        getContentPane().add(jScrollPane1);
        jScrollPane1.setBounds(30, 210, 410, 110);

        mensajeTxt.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        getContentPane().add(mensajeTxt);
        mensajeTxt.setBounds(40, 120, 350, 30);

        jLabel2.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        jLabel2.setText("Mensaje:");
        getContentPane().add(jLabel2);
        jLabel2.setBounds(20, 90, 120, 30);

        btEnviar.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        btEnviar.setText("Enviar");
        btEnviar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btEnviarActionPerformed(evt);
            }
        });
        getContentPane().add(btEnviar);
        btEnviar.setBounds(327, 160, 120, 27);

        setSize(new java.awt.Dimension(491, 375));
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
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea mensajesTxt;
    private JTextField mensajeTxt;

    private void conectar() {
        JOptionPane.showMessageDialog(this, "Conectando con servidor");
        try {
            if (socket == null || socket.isClosed()) {
                socket = new Socket("localhost", PORT);
                out = new PrintWriter(socket.getOutputStream(), true);
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

    private void enviarMensaje() {
        if (out != null) {
            out.println(nombreCliente + ": " + mensajeTxt.getText());
            mensajeTxt.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "No estás conectado al servidor.");
        }
    }
}