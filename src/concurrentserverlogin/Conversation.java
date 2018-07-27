package concurrentserverlogin;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;

public class Conversation extends Thread {

    public static Socket soc;
    HashMap<String, String> hm = new HashMap<>();

    public Conversation(Socket soc) {
        this.soc = soc;
        hm.put("maddy", "maddy123");
        hm.put("mandee", "mandee123");
        hm.put("manoj", "manoj123");
    }

    @Override
    public void run() {
        try {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(
                            soc.getInputStream()
                    )
            );
            PrintWriter pw = new PrintWriter(
                    new BufferedWriter(
                            new OutputStreamWriter(
                                    soc.getOutputStream()
                            )
                    ), true
            );
            String userName = br.readLine();
            login(userName, br, pw);
            ConcurrentServerLogin.fos = new PrintWriter(
                    new FileWriter(
                            "D:/output1.txt"
                    ), true
            );
            ConcurrentServerLogin.al.add(pw);
            for (PrintWriter nos : ConcurrentServerLogin.al) {
                if (nos != pw) {
                    nos.println(userName + " online");
                }
            }
            for (String str : ConcurrentServerLogin.alt) {
                if (!str.equals(userName)) {
                    pw.println(str + " online");
                }
            }
            String str = br.readLine();
            while (!str.equalsIgnoreCase("end")) {
                System.out.println("Server recieved : " + str);
                pw.append(str + "\n");
                ConcurrentServerLogin.fos.println(str
                        + " "
                        + new Date()
                        + " "
                        + soc.getRemoteSocketAddress()
                );
                for (PrintWriter nos : ConcurrentServerLogin.al) {
                    nos.println("broadCast : " + str);
                }
                str = br.readLine();
            }
            ConcurrentServerLogin.al.remove(pw);
            for (PrintWriter nos : ConcurrentServerLogin.al) {
                nos.println(userName + " offline");
            }
            pw.println("end");
            System.out.println("Server recieved : end");
            System.out.println(Thread.currentThread().getName()
                    + "signed off"
            );
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    public void login(String userName, BufferedReader br, PrintWriter pw) {
        try {
            String passWord = br.readLine();
            String pwd = (String) hm.get(userName);
            boolean flag = true;
            while (flag) {
                int c = 0;
                for (int i = 0; i < 3; i++) {
                    pwd = (String) hm.get(userName);
                    if (c >= 3) {
                        soc.close();
                        Thread.currentThread().stop();
                        break;
                    } else if (c < 3 && passWord.equals(pwd)) {
                        pw.println("1");
                        ConcurrentServerLogin.alt.add(userName);
                        flag = false;
                        break;
                    } else {
                        c++;
                        pw.println("0");
                    }
                    userName = br.readLine();
                    passWord = br.readLine();
                }
                if (c >= 3) {
                    soc.close();
                    Thread.currentThread().stop();
                }
            }
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }
}
