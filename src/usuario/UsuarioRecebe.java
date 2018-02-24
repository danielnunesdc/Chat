package usuario;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class UsuarioRecebe implements Runnable {
    private Socket s;

    public UsuarioRecebe(Socket s) {
        this.s = s;
    }

    public void run() {
        try {
            BufferedReader brIn = new BufferedReader(new InputStreamReader(s.getInputStream()));
            while (true) {
                String s = brIn.readLine();
                String[] strs = s.split("\\.");
                String info = strs[0];     
                String name = "", line = "";
                if (strs.length == 2)
                    line = strs[1];
                else if (strs.length == 3) {
                    line = strs[1];
                    name = strs[2];
                }

                if (info.equals("1")) {  // 1 para msg
                    TelaUsuario.textMessage.append(line + "\r\n");
                    TelaUsuario.textMessage.setCaretPosition(TelaUsuario.textMessage.getText().length());
                } else if (info.equals("2") || info.equals("3")) { // 2 para entrar e 3 para sair
                    if (info.equals("2")) {
                        TelaUsuario.textMessage.append("(Alerta) " + name + " entrou!" + "\r\n");
                        TelaUsuario.textMessage.setCaretPosition(TelaUsuario.textMessage.getText().length());
                    } else {
                        TelaUsuario.textMessage.append("(Alerta) " + name + " saiu!" + "\r\n");
                        TelaUsuario.textMessage.setCaretPosition(TelaUsuario.textMessage.getText().length());
                    }
                    String list = line.substring(1, line.length() - 1);
                    String[] data = list.split(",");
                    TelaUsuario.user.clearSelection();
                    TelaUsuario.user.setListData(data);
                } else if (info.equals("4")) {  // 4 para alertas
                    TelaUsuario.connect.setText("entrar");
                    TelaUsuario.sair.setText("sair");
                    TelaUsuario.socket.close();
                    TelaUsuario.socket = null;
                    JOptionPane.showMessageDialog(TelaUsuario.window, "Alguém já está usando esse nome de usuário");
                    break;
                } else if (info.equals("5")) {   // 5 para fechar o servidor
                    TelaUsuario.connect.setText("entrou");
                    TelaUsuario.sair.setText("saiu");
                    TelaUsuario.socket.close();
                    TelaUsuario.socket = null;
                    break;
                } else if (info.equals("6")) {  // 6 para msg privada
                    TelaUsuario.textMessage.append("(Mensagem privada) " + line + "\r\n");
                    TelaUsuario.textMessage.setCaretPosition(TelaUsuario.textMessage.getText().length());
                } else if (info.equals("7")) {
                    JOptionPane.showMessageDialog(TelaUsuario.window, "Esse usuário não está online");
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(TelaUsuario.window, "O usuário saiu");
        }
    }
}
