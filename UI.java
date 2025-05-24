import javax.swing.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class UI {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Bambu");
        JButton printButton = new JButton("Print");

        printButton.addActionListener(e -> sendRequest());

        frame.add(printButton);
        frame.setSize(300, 100);
        frame.setLayout(null);
        printButton.setBounds(80, 20, 140, 30);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    // Sends the request to be completed by the server
    private static void sendRequest() {
        try {
            // Sets the url to send request to and opens connection
            String route = "print-file";
            URL url = new URL("http://localhost:5000/" + route);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.connect();

            // Sends request
            OutputStream os = conn.getOutputStream();
            os.write("".getBytes());
            os.flush();
            os.close();

            // Read server response
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            StringBuilder content = new StringBuilder();
            while ((line = in.readLine()) != null) {
                content.append(line);
            }

            in.close();
            conn.disconnect();

            // Show response
            JOptionPane.showMessageDialog(null, content.toString());

        } catch (Exception e) {
            System.out.println("Could not complete request");
        }
    }
}