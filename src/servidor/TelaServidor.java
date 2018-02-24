package servidor;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class TelaServidor {
    public static JFrame window;
    public static int ports;
    public static JTextArea console;
    public static JList<String> user;

    JButton inicia, sair, enviar;
    JTextField nomeServidor, portaServidor, message;

    //main
    public static void main(String[] args) {
        new TelaServidor();
    }

    public TelaServidor() {
        init();
    }

    public void init() {   // layout do servidor
        window = new JFrame("Servidor | Nota Final");
        window.setLayout(null);
        window.setBounds(200, 200, 500, 350);
        window.setResizable(false);

        JLabel labelnomeServidor = new JLabel("Servidor:");
        labelnomeServidor.setBounds(10, 8, 80, 30);
        window.add(labelnomeServidor);

        nomeServidor = new JTextField();
        nomeServidor.setBounds(80, 8, 60, 30);
        window.add(nomeServidor);

        JLabel label_porta = new JLabel("Porta:");
        label_porta.setBounds(150, 8, 60, 30);
        window.add(label_porta);

        portaServidor = new JTextField();
        portaServidor.setBounds(200, 8, 70, 30);
        window.add(portaServidor);

        inicia = new JButton("Iniciar");
        inicia.setBounds(280, 8, 90, 30);
        window.add(inicia);

        sair = new JButton("Sair");
        sair.setBounds(380, 8, 110, 30);
        window.add(sair);

        console = new JTextArea();
        console.setBounds(10, 70, 340, 320);
        console.setEditable(false);  // não pode ser editado

        console.setLineWrap(true);  // automatic content line feed
        console.setWrapStyleWord(true);

        JLabel label_text = new JLabel("Painel do Servidor");
        label_text.setBounds(100, 47, 190, 30);
        window.add(label_text);

        JScrollPane paneText = new JScrollPane(console);
        paneText.setBounds(10, 70, 340, 220);
        window.add(paneText);

        JLabel label_listaUsuario = new JLabel("Lista de Usuários");
        label_listaUsuario.setBounds(357, 47, 180, 30);
        window.add(label_listaUsuario);

        user = new JList<String>();
        JScrollPane paneUser = new JScrollPane(user);
        paneUser.setBounds(355, 70, 130, 220);
        window.add(paneUser);

        message = new JTextField();
        message.setBounds(0, 0, 0, 0);
        window.add(message);

        enviar = new JButton("Enviar");
        enviar.setBounds(0, 0, 0, 0);
        window.add(enviar);

        myEvent();  // add listeners
        window.setVisible(true);
    }

    public void myEvent() {
        window.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (Servidor.listaUsuario != null && Servidor.listaUsuario.size() != 0) {
                    try {
                        new ServidorEnvia(Servidor.listaUsuario, "", "5", "");
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
                System.exit(0); // sair da janela
            }
        });

        sair.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (Servidor.ss == null || Servidor.ss.isClosed()) {
                    JOptionPane.showMessageDialog(window, "O Servidor foi fechado!");
                } else {
                    if (Servidor.listaUsuario != null && Servidor.listaUsuario.size() != 0) {
                        try {
                            new ServidorEnvia(Servidor.listaUsuario, "", "5", "");
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                    try {
                        inicia.setText("Iniciar");
                        sair.setText("Fechar");
                        Servidor.ss.close();
                        Servidor.ss = null;
                        Servidor.listaUsuario = null;
                        Servidor.flag = false;   // importante
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });

        inicia.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (Servidor.ss != null && !Servidor.ss.isClosed()) {
                    JOptionPane.showMessageDialog(window, "O servidor foi iniciado!");
                } else {
                    ports = getPorta();
                    if (ports != 0) {
                        try {
                            Servidor.flag = true;
                            new Thread(new Servidor(ports)).start(); // inicia servidor thread
                            inicia.setText("Iniciado");
                            sair.setText("Fechar");
                        } catch (IOException e1) {
                            JOptionPane.showMessageDialog(window, "Falha ao rodar");
                        }
                    }
                }
            }
        });

        message.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    enviarMsg();
                }
            }
        });

        enviar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                enviarMsg();
            }
        });
    }

    public void enviarMsg() {
        String messages = message.getText();
        if ("".equals(messages)) {
            JOptionPane.showMessageDialog(window, "Não há nada para enviar!");
        } else if (Servidor.listaUsuario == null || Servidor.listaUsuario.size() == 0) {
            JOptionPane.showMessageDialog(window, "Não há conexão!");
        } else {
            try {
                new ServidorEnvia(Servidor.listaUsuario, getnomeServidor() + ": " + messages, "1", "");
                message.setText(null);
            } catch (IOException e1) {
                JOptionPane.showMessageDialog(window, "Falha ao enviar!");
            }
        }
    }

    public int getPorta() {
        String porta = portaServidor.getText();
        String name = nomeServidor.getText();
        if ("".equals(porta) || "".equals(name)) {
            JOptionPane.showMessageDialog(window, "Nenhuma porta ou Nome de usuário encontrada!");
            return 0;
        } else {
            return Integer.parseInt(porta);
        }
    }

    public String getnomeServidor() {
        return nomeServidor.getText();
    }
}