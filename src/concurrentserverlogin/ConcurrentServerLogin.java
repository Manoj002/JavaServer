package concurrentserverlogin;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ConcurrentServerLogin {

    public static PrintWriter fos;
    public static ArrayList<PrintWriter> al = new ArrayList();
    public static ArrayList<String> alt = new ArrayList();

    public static void main(String[] args) {
        try {
            ServerSocket ss = new ServerSocket(9081);
            while (true) {
                Socket soc = ss.accept();
                Conversation c = new Conversation(soc);
                c.start();
            }
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }
}
