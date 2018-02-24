package servidor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;

public class ServidorRecebe implements Runnable {
    private Socket socket;
    private ArrayList<Socket> listaUsuario;
    private Vector<String> nomeUsuario;
    private Map<String, Socket> map;


    public ServidorRecebe(Socket s, ArrayList<Socket> listaUsuario, Vector<String> nomeUsuario, Map<String, Socket> map) {
        this.socket = s;
        this.listaUsuario = listaUsuario;
        this.nomeUsuario = nomeUsuario;
        this.map = map;
    }

    public void run() {
        try {
            BufferedReader brIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (true) {
                String s = brIn.readLine();
                String[] strs = s.split(",,");
                String info = strs[0];  //judge the kind of info
                String line = strs[1];
                //System.out.println(line);
                String name = "";
                if (strs.length == 3)
                    name = strs[2];

                if (info.equals("1")) {   // 1 para Nova requisição de mensagem
                    TelaServidor.console.append("Nova mensagem ---->> " + line + "\r\n");
                    TelaServidor.console.setCaretPosition(TelaServidor.console.getText().length());
                    new ServidorEnvia(listaUsuario, line, "1", "");
                } else if (info.equals("2")) {  // 2 para login
                    if (!nomeUsuario.contains(line)) {
                        TelaServidor.console.append("Novo login requisitado ---->> " + line + "\r\n");
                        TelaServidor.console.setCaretPosition(TelaServidor.console.getText().length());
                        nomeUsuario.add(line);
                        map.put(line, socket);
                        TelaServidor.user.setListData(nomeUsuario);
                        new ServidorEnvia(listaUsuario, nomeUsuario, "2", line);
                    } else {
                        TelaServidor.console.append("Login duplicado ---->> " + line + "\r\n");
                        TelaServidor.console.setCaretPosition(TelaServidor.console.getText().length());
                        listaUsuario.remove(socket);
                        new ServidorEnvia(socket, "", "4");
                    }
                } else if (info.equals("3")) {  // 3 para sair
                    TelaServidor.console.append("Saiu ---->> " + line + "\r\n");
                    TelaServidor.console.setCaretPosition(TelaServidor.console.getText().length());
                    nomeUsuario.remove(line);
                    listaUsuario.remove(socket);
                    map.remove(line);
                    TelaServidor.user.setListData(nomeUsuario);
                    new ServidorEnvia(listaUsuario, nomeUsuario, "3", line);
                    socket.close();
                    break;  // quebra de info
                } else if (info.equals("4")) {   // 4 para msg privada
                    TelaServidor.console.append("Nova mensagem privada ---->> " + line + "\r\n");
                    TelaServidor.console.setCaretPosition(TelaServidor.console.getText().length());
                    if (map.containsKey(name))
                        new ServidorEnvia(map.get(name), line, "6");
                    else
                        new ServidorEnvia(socket, "", "7");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
