import java.net.*;
import java.io.*;
import java.util.*;
import java.text.*;
 
public class ClockServer {
    private Map<InetAddress, Date> clientTimes = new HashMap<>();
 
    public static void main(String[] args) {
        new ClockServer().startServer(9999);
    }
 
    public void startServer(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Clock server started...");
            while (true) {
                Socket socket = serverSocket.accept();
                new ServerThread(socket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
 
    class ServerThread extends Thread {
        private Socket socket;
 
        public ServerThread(Socket socket) {
            this.socket = socket;
        }
 
        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
 
                while (true) {
                    String clientTimeStr = in.readLine();
                    if (clientTimeStr == null) break;
 
                    InetAddress clientAddress = socket.getInetAddress();
                    Date clientTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(clientTimeStr);
 
                    synchronized (clientTimes) {
                        clientTimes.put(clientAddress, clientTime);
                    }
 
                    // Send synchronized time back to client
                    Date synchronizedTime = calculateSynchronizedTime();
                    out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(synchronizedTime));
                }
 
                socket.close();
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
        }
 
        private Date calculateSynchronizedTime() {
            synchronized (clientTimes) {
                if (clientTimes.isEmpty()) {
                    return new Date();
                }
 
                long sum = 0;
                for (Date time : clientTimes.values()) {
                    sum += time.getTime();
                }
 
                return new Date(sum / clientTimes.size());
            }
        }
    }
}
 



/*OUTPUT:
Server Clock   = 03:00
Client Clock 1 = 03:25
Client Clock 2 = 02:50
t1 - s = 25
t2 - s = -10
(st1 + st2 + 0)/3 = 5
t1 adjustment = -20
t2 adjustment = 15
Synchronized Server Clock  = 03:05
Synchronized Client1 Clock = 03:05
Synchronized Client2 Clock = 03:05
*/