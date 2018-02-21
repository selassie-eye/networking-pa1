package pkg;

import java.io.*;
import java.net.*;

public class ClientClass {
    private int state = 0;
    /* States
        0: INIT
        1: READING
        2: WRITING
    */

    public ClientClass(String host, int port) {
        start(host, port);
    }

    private void start(String host, int port) {
        try (
            Socket server = new Socket(host, port);
            PrintWriter writer = new PrintWriter(server.getOutputStream(), true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(server.getInputStream()));
        ) {
            String fromServer, toServer;
            BufferedReader cmd = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Connecting to server...");

            while((fromServer = reader.readLine()) != null) {
                switch (state) {
                    case 0: // INIT
                        if (fromServer.equals("Hello!")) {
                            System.out.println("Connected to server. Starting command prompt...");
                            state = 2;
                            writer.println("Hello!");
                        } else {
                            System.out.println("Server not ready.");
                            writer.println("Waiting!");
                        }
                        break;
                    case 1: // READING
                        
                        // Error handling and Response Protocol
                        switch (fromServer) {
                            case "-1":
                                System.out.println("-1: Incorrect operation command.");
                                break;
                            case "-2":
                                System.out.println("-2: Number of inputs less than 2.");
                                break;
                            case "-3":
                                System.out.println("-3: Number of inputs greater than 4.");
                                break;
                            case "-4":
                                System.out.println("-4: Non-number inputs.");
                                break;
                            case "-5":
                                System.out.println("-5: Server exit.");
                                break;
                            default:
                                System.out.println("Response: " + fromServer);
                                break;
                        }
                        state = 2;
                        writer.println("Received!");
                        break;
                    case 2: // WRITING
                        System.out.print("> ");
                        toServer = cmd.readLine();
                        if (toServer.split(" ")[0].equalsIgnoreCase("terminate")) {
                            System.out.println("Closing network...");
                            writer.println(toServer);
                            System.exit(0);
                        } else {
                            state = 1;
                            writer.println(toServer);
                        }
                        break;
                    default:
                        System.out.print("State error, exiting...");
                        System.exit(1);
                }

            }
        } catch (IOException e) {
            System.out.println("Error starting client: " + e.getMessage());
        }

    }
}