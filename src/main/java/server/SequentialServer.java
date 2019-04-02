package server;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import service.LoginService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by IntelliJ IDEA.
 * User: grigo
 * Date: Mar 18, 2009
 * Time: 11:49:21 AM
 */
public class SequentialServer extends AbstractServer {
    private LoginService loginService;

    public SequentialServer(int port) {
        super(port);
        System.out.println("SequentialServer");

        ApplicationContext context = new ClassPathXmlApplicationContext("TurismApp.xml");
        loginService = context.getBean(LoginService.class);
    }

    protected void processRequest(Socket client) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
             PrintWriter writer = new PrintWriter(client.getOutputStream())) {

            //read message from client
            String user = br.readLine();
            String pass = br.readLine();
            String result;
            if (loginService.LoginUser(user, pass) != null)
                result = "Autentificat cu succes!";
            else
                result = "Credentiale incorecte!";

            //send result back to client
            writer.println(result);
            writer.flush();

        } catch (IOException e) {
            System.err.println("Communication error " + e);
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}