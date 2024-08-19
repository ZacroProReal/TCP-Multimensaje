package org.vinni.servidor.gui;

import java.awt.Color;
import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class PrincipalSrv extends javax.swing.JFrame {
    private final int PORT = 12345;
    private ServerSocket serverSocket;
    private Map<String, PrintWriter> clientes;

    public PrincipalSrv() {
        initComponents();
        clientes = new HashMap<>();
    }

    private void initComponents() {
        this.setTitle("Servidor ...");

        bIniciar = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        mensajesTxt = new JTextArea();
        jScrollPane1 = new javax.swing.JScrollPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(null);

        bIniciar.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        bIniciar.setText("INICIAR SERVIDOR");
        bIniciar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bIniciarActionPerformed(evt);
            }
        });
        getContentPane().add(bIniciar);
        bIniciar.setBounds(100, 90, 250, 40);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(204, 0, 0));
        jLabel1.setText("SERVIDOR TCP : HOEL");
        getContentPane().add(jLabel1);
        jLabel1.setBounds(150, 10, 160, 17);

        mensajesTxt.setColumns(25);
        mensajesTxt.setRows(5);
        jScrollPane1.setViewportView(mensajesTxt);

        getContentPane().add(jScrollPane1);
        jScrollPane1.setBounds(20, 160, 410, 70);

        setSize(new java.awt.Dimension(491, 290));
        getContentPane().setBackground(new Color(173, 216, 230)); 
        setLocationRelativeTo(null);
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PrincipalSrv().setVisible(true);
            }
        });
    }

    private void bIniciarActionPerformed(java.awt.event.ActionEvent evt) {
        iniciarServidor();
    }

    private void iniciarServidor() {
        JOptionPane.showMessageDialog(this, "Iniciando servidor");
        new Thread(new Runnable() {
            public void run() {
                try {
                    serverSocket = new ServerSocket(PORT);
                    mensajesTxt.append("Servidor TCP en ejecución en puerto: " + PORT + "\n");
                    while (true) {
                        Socket clientSocket = serverSocket.accept();
                        new Thread(new ClienteConectado(clientSocket)).start();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                    mensajesTxt.append("Error en el servidor: " + ex.getMessage() + "\n");
                }
            }
        }).start();
    }

    private class ClienteConectado implements Runnable {
        private Socket clientSocket;
        private BufferedReader in;
        private PrintWriter out;
        private String nombreCliente;

        public ClienteConectado(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream(), true);

                // Leer el nombre del cliente en la primera línea
                nombreCliente = in.readLine();
                synchronized (clientes) {
                    clientes.put(nombreCliente, out);
                }
                mensajesTxt.append(nombreCliente + " se ha conectado.\n");

                String mensaje;
                while ((mensaje = in.readLine()) != null) {
                    mensajesTxt.append(nombreCliente + ": " + mensaje + "\n");

                    if (mensaje.startsWith("@")) {
                        String[] partes = mensaje.split(" ", 2);
                        String destinatario = partes[0].substring(1);
                        String mensajePrivado = partes.length > 1 ? partes[1] : "";

                        PrintWriter clienteDestinatario = clientes.get(destinatario);
                        if (clienteDestinatario != null) {
                            clienteDestinatario.println("(Privado de " + nombreCliente + "): " + mensajePrivado);
                        } else {
                            out.println("Cliente " + destinatario + " no encontrado.");
                        }
                    } else {
                        for (PrintWriter cliente : clientes.values()) {
                            cliente.println(nombreCliente + ": " + mensaje);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                mensajesTxt.append("Error al comunicarse con " + nombreCliente + ": " + e.getMessage() + "\n");
            } finally {
                try {
                    clientSocket.close();
                    synchronized (clientes) {
                        clientes.remove(nombreCliente);
                    }
                    mensajesTxt.append(nombreCliente + " se ha desconectado.\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private javax.swing.JButton bIniciar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JTextArea mensajesTxt;
    private javax.swing.JScrollPane jScrollPane1;
}
