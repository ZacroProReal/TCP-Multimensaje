package org.vinni.cliente.gui;

import java.awt.Color;
import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.UUID;

/**
 * Clase PrincipalCli - Representa la interfaz gráfica del cliente TCP.
 */
public class PrincipalCli extends javax.swing.JFrame {

    // Puerto del servidor al que se conectará el cliente.
    private final int PORT = 12345;

    // Socket para la comunicación con el servidor.
    private Socket socket;

    // Writer para enviar datos al servidor.
    private PrintWriter out;

    // Reader para recibir datos del servidor.
    private BufferedReader in;

    // Nombre único del cliente.
    private String nombreCliente;
    
    /**
     * Constructor de la clase PrincipalCli. Inicializa el nombre del cliente
     * y los componentes de la interfaz gráfica.
     */
    public PrincipalCli() {
        generarNombreCliente();  // Genera un nombre único para el cliente.
        initComponents();        // Inicializa los componentes de la interfaz gráfica.
    }

    /**
     * Genera un nombre único para el cliente utilizando UUID y lo establece
     * como el título de la ventana.
     */
    private void generarNombreCliente() {
        this.nombreCliente = UUID.randomUUID().toString().substring(0, 2);  // Genera un nombre único de 2 caracteres.
        this.setTitle("Cliente: " + nombreCliente);  // Establece el título de la ventana.
    }
    
    /**
     * Inicializa y configura los componentes de la interfaz gráfica.
     */
    private void initComponents() {
        // Configuración básica de la ventana.
        this.setTitle("Cliente");

        // Creación y configuración de los botones y etiquetas.
        bConectar = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        mensajesTxt = new javax.swing.JTextArea();
        mensajeTxt = new javax.swing.JTextField();
        destinatarioTxt = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        btEnviar = new javax.swing.JButton();
        btEnviarArchivo = new javax.swing.JButton();  

        // Configuración de la ventana al cerrar.
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(null);

        // Configuración del botón para conectar con el servidor.
        bConectar.setFont(new java.awt.Font("Segoe UI", 0, 14)); 
        bConectar.setText("CONECTAR CON SERVIDOR");
        bConectar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bConectarActionPerformed(evt);  // Acción al hacer clic en el botón.
            }
        });
        getContentPane().add(bConectar);
        bConectar.setBounds(240, 40, 210, 40);

        // Configuración del área de texto donde se mostrarán los mensajes.
        mensajesTxt.setColumns(20);
        mensajesTxt.setRows(5);
        mensajesTxt.setEnabled(false);
        jScrollPane1.setViewportView(mensajesTxt);

        getContentPane().add(jScrollPane1);
        jScrollPane1.setBounds(30, 230, 410, 110);

        // Configuración del campo de texto para escribir mensajes.
        mensajeTxt.setFont(new java.awt.Font("Verdana", 0, 14)); 
        getContentPane().add(mensajeTxt);
        mensajeTxt.setBounds(40, 120, 350, 30);

        // Etiqueta "Mensaje".
        jLabel2.setFont(new java.awt.Font("Verdana", 0, 14)); 
        jLabel2.setText("Mensaje:");
        getContentPane().add(jLabel2);
        jLabel2.setBounds(20, 90, 120, 30);

        // Etiqueta "Destinatario".
        jLabel3.setFont(new java.awt.Font("Verdana", 0, 14)); 
        jLabel3.setText("Destinatario:");
        getContentPane().add(jLabel3);
        jLabel3.setBounds(20, 160, 120, 30);

        // Campo de texto para ingresar el nombre del destinatario.
        destinatarioTxt.setFont(new java.awt.Font("Verdana", 0, 14)); 
        getContentPane().add(destinatarioTxt);
        destinatarioTxt.setBounds(140, 160, 250, 30);

        // Etiqueta con el nombre del cliente en la parte superior de la ventana.
        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 18)); 
        jLabel1.setForeground(new java.awt.Color(64, 64, 64));
        jLabel1.setText("CLIENTE TCP : " + nombreCliente);
        getContentPane().add(jLabel1);
        jLabel1.setBounds(110, 10, 250, 17);

        // Botón para enviar mensajes.
        btEnviar.setFont(new java.awt.Font("Verdana", 1, 14)); 
        btEnviar.setText("Enviar");
        btEnviar.setBackground(new Color(173, 255, 47));  // Color verde para el botón.
        btEnviar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btEnviarActionPerformed(evt);  // Acción al hacer clic en el botón.
            }
        });
        getContentPane().add(btEnviar);
        btEnviar.setBounds(327, 200, 120, 27);

        // Botón para enviar archivos.
        btEnviarArchivo.setFont(new java.awt.Font("Verdana", 1, 14)); 
        btEnviarArchivo.setText("Enviar Archivo");
        btEnviarArchivo.setBackground(new Color(173, 255, 47));  // Color verde para el botón.
        btEnviarArchivo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btEnviarArchivoActionPerformed(evt);  // Acción al hacer clic en el botón.
            }
        });
        getContentPane().add(btEnviarArchivo);
        btEnviarArchivo.setBounds(40, 200, 150, 27);

        // Configuración del tamaño de la ventana y su posición en pantalla.
        setSize(new java.awt.Dimension(491, 400));
        getContentPane().setBackground(new Color(173, 216, 230));  // Color de fondo de la ventana.
        setLocationRelativeTo(null);  // Centra la ventana en la pantalla.
    }

    /**
     * Acción realizada al hacer clic en el botón "CONECTAR CON SERVIDOR".
     * Establece la conexión con el servidor.
     */
    private void bConectarActionPerformed(java.awt.event.ActionEvent evt) {
        conectar();  // Llama al método conectar().
    }

    /**
     * Acción realizada al hacer clic en el botón "Enviar".
     * Envía un mensaje al servidor.
     */
    private void btEnviarActionPerformed(java.awt.event.ActionEvent evt) {
        enviarMensaje();  // Llama al método enviarMensaje().
    }

    /**
     * Acción realizada al hacer clic en el botón "Enviar Archivo".
     * Envía un archivo al servidor.
     */
    private void btEnviarArchivoActionPerformed(java.awt.event.ActionEvent evt) {
        enviarArchivo();  // Llama al método enviarArchivo().
    }

    /**
     * Método principal que inicia la interfaz gráfica.
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PrincipalCli().setVisible(true);  // Muestra la ventana principal.
            }
        });
    }

    // Variables de los componentes de la interfaz gráfica.
    private javax.swing.JButton bConectar;
    private javax.swing.JButton btEnviar;
    private javax.swing.JButton btEnviarArchivo; 
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3; 
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea mensajesTxt;
    private javax.swing.JTextField mensajeTxt;
    private javax.swing.JTextField destinatarioTxt; 

    /**
     * Conecta el cliente con el servidor. Establece el socket, el stream de
     * salida (out) y el stream de entrada (in). Además, inicia un hilo para
     * escuchar mensajes del servidor.
     */
    private void conectar() {
        JOptionPane.showMessageDialog(this, "Conectando con servidor");  // Muestra un mensaje de conexión.
        try {
            if (socket == null || socket.isClosed()) {
                socket = new Socket("localhost", PORT);  // Crea un nuevo socket para conectarse al servidor.
                //Si se desea una conexion publica puede ser usado --> socket = new Socket("IP_PUBLICA_DEL_SERVIDOR", PORT);
                out = new PrintWriter(socket.getOutputStream(), true);  // Crea un PrintWriter para enviar datos al servidor.
                out.println(nombreCliente);  // Envía el nombre del cliente al servidor.
            }
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));  // Crea un BufferedReader para recibir datos del servidor.
            
            // Inicia un nuevo hilo para escuchar mensajes del servidor.
            new Thread(new Runnable() {
                public void run() {
                    try {
                        String fromServer;
                        while ((fromServer = in.readLine()) != null) {
                            if (fromServer.startsWith("Recibiendo archivo:")) {
                                // Si el servidor envía un mensaje indicando la recepción de un archivo, lo recibe.
                                String nombreArchivo = fromServer.substring(19).trim();
                                recibirArchivo(nombreArchivo);
                            } else {
                                // Si es un mensaje normal, lo muestra en el área de texto.
                                mensajesTxt.append(fromServer + "\n");
                            }
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

    /**
     * Envía un mensaje al servidor. Si se especifica un destinatario, envía
     * un mensaje privado. Si no, envía un mensaje público.
     */
    private void enviarMensaje() {
        if (out != null) {
            String destinatario = destinatarioTxt.getText().trim();  // Obtiene el destinatario del mensaje.
            String mensaje = mensajeTxt.getText().trim();  // Obtiene el mensaje a enviar.
            if (!destinatario.isEmpty()) {
                // Si se especificó un destinatario, envía un mensaje privado.
                out.println("@" + destinatario + " " + mensaje);
            } else {
                // Si no se especificó destinatario, envía un mensaje público.
                out.println(mensaje);
            }
            mensajeTxt.setText("");  // Limpia el campo de texto del mensaje.
        } else {
            JOptionPane.showMessageDialog(this, "No estás conectado al servidor.");
        }
    }

    /**
     * Envía un archivo al servidor. El archivo se selecciona mediante un
     * JFileChooser. El archivo se envía en forma de bytes al servidor.
     */
    private void enviarArchivo() {
        if (out != null) {
            String destinatario = destinatarioTxt.getText().trim();  // Obtiene el destinatario del archivo.
            if (destinatario.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, ingresa el nombre del destinatario.");
                return;
            }

            // Muestra un JFileChooser para seleccionar el archivo a enviar.
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try {
                    // Envía al servidor el comando para indicar que se enviará un archivo.
                    out.println("archivo@" + destinatario + " " + file.getName());

                    // Envía el archivo al servidor en forma de bytes.
                    byte[] buffer = new byte[4096];
                    FileInputStream fis = new FileInputStream(file);
                    OutputStream os = socket.getOutputStream();
                    int bytesRead;
                    while ((bytesRead = fis.read(buffer)) != -1) {
                        os.write(buffer, 0, bytesRead);
                    }
                    os.flush();
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "No estás conectado al servidor.");
        }
    }

    /**
     * Recibe un archivo del servidor y lo guarda en la carpeta "ArchivosRecibidos"
     * dentro del directorio del usuario.
     * @param nombreArchivo El nombre del archivo a recibir.
     */
    private void recibirArchivo(String nombreArchivo) {
        try {
            // Crea la carpeta "ArchivosRecibidos" en el directorio del usuario si no existe.
            File directorio = new File(System.getProperty("user.home") + "/ArchivosRecibidos");
            if (!directorio.exists()) {
                directorio.mkdirs(); // Crea la carpeta si no existe.
            }

            // Crea un archivo en la carpeta "ArchivosRecibidos".
            File archivoDestino = new File(directorio, nombreArchivo);
            FileOutputStream fos = new FileOutputStream(archivoDestino);
            InputStream is = socket.getInputStream();
            byte[] buffer = new byte[4096];
            int bytesRead;
            
            // Lee los datos del archivo desde el socket y los guarda en el archivo destino.
            while ((bytesRead = is.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
            fos.close();
            mensajesTxt.append("Archivo " + nombreArchivo + " recibido y guardado en " + archivoDestino.getAbsolutePath() + "\n");
        } catch (IOException e) {
            e.printStackTrace();
            mensajesTxt.append("Error al recibir el archivo: " + e.getMessage() + "\n");
        }
    }
}
