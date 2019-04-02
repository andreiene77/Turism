package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class StartAutentificateClient {
    public static void main(String[] args) {
        System.out.println("Trying to connect ...");
        try (Socket client = new Socket("localhost", 55555)) {
            System.out.println("Connection obtained.");
            try (PrintWriter writer = new PrintWriter(client.getOutputStream());
                 BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()))) {

                System.out.println("Sending message ...");
                String user = "ceva";
                String pass = "cevap";

                writer.println(user);
                writer.println(pass);
                writer.flush();

                System.out.println("Waiting for response...");
                String response = br.readLine();
                System.out.println("Response: " + response);


            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
