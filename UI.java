import javax.swing.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class UI {

    static JLabel temp;
    public static void main(String[] args) {
        JFrame frame = new JFrame("Bambu");
        JButton printButton = new JButton("print");
        JButton statusButton = new JButton("status");
        temp = new JLabel("Hello, World!");
        createItems();

        printButton.addActionListener(e -> sendRequest("print-file"));
        statusButton.addActionListener(e -> sendRequest("status"));

        frame.add(temp);
        frame.add(printButton);
        frame.add(statusButton);
        frame.setSize(300, 100);
        frame.setLayout(null);
        printButton.setBounds(80, 20, 140, 30);
        statusButton.setBounds(80, 60, 140, 30);
        temp.setBounds(80, 90, 140, 30);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
    
    // Creates jframe elements
    private static void createItems(){
       
    }

    // Sends the request to be completed by the server
    private static void sendRequest(String request) {
        try {
            // Sets the url to send request to and opens connection
            String route = request;
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
                statusUpdate(line);
            }

            in.close();
            conn.disconnect();

            // Show response
            JOptionPane.showMessageDialog(null, content.toString());

        } catch (Exception e) {
            System.out.println("Could not complete request");
        }
    }

    // Updates printer status
    private static void statusUpdate(String data){
        int start = data.indexOf("nozzle_temper");
        String temp_value = data.substring(start + 15, start + 18);
        temp.setText("nozzle temp: " + temp_value +"C");
    }
}