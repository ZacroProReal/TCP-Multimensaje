package org.vinni.servidor.gui;

import java.awt.Color;
import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class PrincipalSrv extends javax.swing.JFrame {
    private final int PORT = 12345; // Puerto en el que el servidor escuchará las conexiones entrantes
    private ServerSocket serverSocket; // Socket del servidor para aceptar conexiones
    private Map<String, PrintWriter> clientes; // Mapa para almacenar los escritores de los clientes
    private Map<String, Socket> clienteSockets; // Mapa para almacenar los sockets de los clientes
   
    private javax.swing.JButton bIniciar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JTextArea mensajesTxt;
    private javax.swing.JScrollPane jScrollPane1;
    /**
     * Constructor de la clase PrincipalSrv.
     * Inicializa la interfaz gráfica y los mapas de clientes.
     */
    public PrincipalSrv() {
        initComponents();
        clientes = new HashMap<>();
        clienteSockets = new HashMap<>();
    }

    /**
     * Método para inicializar los componentes de la interfaz gráfica.
     */
    private void initComponents() {
        this.setTitle("Servidor ...");

        bIniciar = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        mensajesTxt = new JTextArea();
        jScrollPane1 = new javax.swing.JScrollPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(null);

        
        bIniciar.setFont(new java.awt.Font("Segoe UI", 0, 18)); 
        bIniciar.setText("INICIAR SERVIDOR");
        bIniciar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bIniciarActionPerformed(evt);
            }
        });
        getContentPane().add(bIniciar);
        bIniciar.setBounds(100, 90, 250, 40);

        
        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); 
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
    /**
     * Método para iniciar el servidor.
     * Escucha conexiones entrantes y maneja cada cliente en un hilo separado.
     */
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

    /**
     * Clase interna para manejar la conexión de un cliente.
     * Lee mensajes del cliente y los envía a otros clientes o maneja mensajes privados.
     */
    private class ClienteConectado implements Runnable {
        private Socket clientSocket; // Socket del cliente
        private BufferedReader in; // Buffer para leer mensajes del cliente
        private PrintWriter out; // Writer para enviar mensajes al cliente
        private String nombreCliente; // Nombre del cliente

        /**
         * Constructor para ClienteConectado.
         * @param socket Socket del cliente
         */
        public ClienteConectado(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream(), true);

                // Leer el nombre del cliente
                nombreCliente = in.readLine();
                synchronized (clientes) {
                    clientes.put(nombreCliente, out);
                    clienteSockets.put(nombreCliente, clientSocket); // Almacenar el socket del cliente
                }
                mensajesTxt.append(nombreCliente + " se ha conectado.\n");

                String mensaje;
                while ((mensaje = in.readLine()) != null) {
                    mensajesTxt.append(nombreCliente + ": " + mensaje + "\n");

                    // Manejar mensajes privados
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
                    } 
                    // Manejar transferencia de archivos
                    else if (mensaje.startsWith("archivo@")) {
                        String[] partes = mensaje.split(" ", 2);
                        String destinatario = partes[0].substring(8);
                        String nombreArchivo = partes.length > 1 ? partes[1] : "";

                        Socket socketDestinatario = clienteSockets.get(destinatario);
                        if (socketDestinatario != null) {
                            enviarArchivo(socketDestinatario, in, nombreArchivo);
                        } else {
                            out.println("Cliente " + destinatario + " no encontrado.");
                        }
                    } 
                    // Mensaje global para todos los clientes
                    else {
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
                        clienteSockets.remove(nombreCliente); // Eliminar el socket del cliente
                    }
                    mensajesTxt.append(nombreCliente + " se ha desconectado.\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        /**
         * Método para enviar un archivo a un cliente.
         * @param destinatarioSocket Socket del cliente que recibe el archivo
         * @param in BufferedReader  es para leer el archivo del cliente emisor
         * @param nombreArchivo Nombre del archivo a recibir
         */
        private void enviarArchivo(Socket destinatarioSocket, BufferedReader in, String nombreArchivo) {
            try {
                OutputStream os = destinatarioSocket.getOutputStream();
                PrintWriter outDestinatario = new PrintWriter(os, true);

                outDestinatario.println("Recibiendo archivo: " + nombreArchivo);
                byte[] buffer = new byte[4096];
                int bytesRead;
                InputStream is = clientSocket.getInputStream();
                while ((bytesRead = is.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
                os.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

   
}
