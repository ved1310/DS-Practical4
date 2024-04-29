
import java.net.*;
import java.io.*;
import java.text.*;
import java.util.Date;
 
public class ClockClient {
    public static void main(String[] args) {
        new ClockClient().startClient("127.0.0.1", 9999);
    }
 
    public void startClient(String serverAddress, int serverPort) {
        try (Socket socket = new Socket(serverAddress, serverPort);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
 
            while (true) {
                // Send local time to server
                out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
 
                // Receive synchronized time from server
                String synchronizedTimeStr = in.readLine();
                Date synchronizedTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(synchronizedTimeStr);
                System.out.println("Synchronized time at the client is: " + synchronizedTime);
 
                Thread.sleep(5000); // Sleep for 5 seconds
            }
        } catch (IOException | ParseException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}