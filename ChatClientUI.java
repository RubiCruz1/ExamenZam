/////////////////// Chat clliente///////////////////////
package chat;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;

public class ChatClientUI extends JFrame {
    private JTextArea chatArea;
    private JTextField chatInput;
    private JButton sendButton;
    private PrintWriter out;
    private BufferedReader in;
    private String nombreUsuario;

    public ChatClientUI(String serverAddress, String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;

        setTitle("Cliente de Chat - " + nombreUsuario);
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane chatScroll = new JScrollPane(chatArea);
        add(chatScroll, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());
        chatInput = new JTextField();
        sendButton = new JButton("Enviar");
        inputPanel.add(chatInput, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        add(inputPanel, BorderLayout.SOUTH);

        try {
            Socket socket = new Socket(serverAddress, 7000);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Enviar el nombre de usuario al servidor
            out.println(nombreUsuario);

            // Hilo para leer mensajes del servidor
            new Thread(() -> {
                try {
                    String message;
                    while ((message = in.readLine()) != null) {
                        chatArea.append(message + "\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "No se pudo conectar al servidor.");
        }

        sendButton.addActionListener(e -> sendMessage());
        chatInput.addActionListener(e -> sendMessage());
    }

    private void sendMessage() {
        String message = chatInput.getText();
        if (!message.trim().isEmpty()) {
            // Enviar mensaje en texto plano sin color
            out.println(message);
            chatArea.append("Yo: " + message + "\n");
            chatInput.setText("");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            String serverAddress = JOptionPane.showInputDialog("Introduce la dirección IP del servidor:");
            String nombreUsuario = JOptionPane.showInputDialog("Introduce tu nombre de usuario:");
            
            if (serverAddress != null && !serverAddress.trim().isEmpty() &&
                nombreUsuario != null && !nombreUsuario.trim().isEmpty()) {
                
                ChatClientUI client = new ChatClientUI(serverAddress, nombreUsuario);
                client.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(null, "No se ingresaron datos El cliente se cerrará.");
                System.exit(0);
            }
        });
    }
}
