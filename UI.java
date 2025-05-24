import javax.swing.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class UI {

    static JLabel nozzleTemp = new JLabel("Null");
    static JLabel bedTemp = new JLabel("Null");
    static JFrame frame = new JFrame("Bambu");
    static JButton printButton = new JButton("print");
    static JButton statusButton = new JButton("status");
    public static void main(String[] args) {
        createItems();
        eventListners();
    }
    

    // Sets event listeners
    private static void eventListners(){
        printButton.addActionListener(e -> sendRequest("print-file"));
        statusButton.addActionListener(e -> sendRequest("status"));
    }

    // Adds and positions jframe elements
    private static void createItems(){
        frame.add(nozzleTemp);
        frame.add(printButton);
        frame.add(statusButton);
        frame.add(bedTemp);

        frame.setSize(300, 100);
        frame.setLayout(null);

        printButton.setBounds(80, 20, 140, 30);
        statusButton.setBounds(80, 60, 140, 30);
        nozzleTemp.setBounds(80, 90, 140, 30);
        bedTemp.setBounds(80, 120, 140, 30);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
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
        // Displays nozzle temp
        int startNozzle = data.indexOf("nozzle_nozzleTemper");
        String nozzleTemp_value = data.substring(startNozzle + 15, startNozzle + 18);
        nozzleTemp.setText("nozzle nozzleTemp: " + nozzleTemp_value +"C");

        //Displays bed temp
        int startBed = data.indexOf("nozzle_nozzleTemper");
        String bedTemp_value = data.substring(startBed + 15, startBed + 18);
        nozzleTemp.setText("nozzle nozzleTemp: " + bedTemp_value +"C");
    }
}