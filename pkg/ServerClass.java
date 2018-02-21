package pkg;

import java.io.*;
import java.net.*;

public class ServerClass {
    private ServerSocket socket;
    private Socket client;
    private int port;
    
    private PrintWriter writer;
    private BufferedReader reader;

    private int state = 0;
    /* States
        0: INIT
        1: WAITING
        2: PROTOCOL
        3: ERROR
    */
    private Boolean eflag = false;
    private String error = "";

    public ServerClass(int p) {
        port = p;
        try {
            socket = new ServerSocket(port);
        } catch (IOException e) {
            System.out.println("Error starting server.");
        }
        start(port);
    }

    public void start(int port) {
        try {
            client = socket.accept();
            writer = new PrintWriter(client.getOutputStream(), true);
            reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            System.out.printf("Connection made on port %d\nHostname: %s\n\n", port, socket.getInetAddress().getHostName());
            state = 1;

            writer.println("Hello!");

            String toClient = "";
            String fromClient = "";

            while ((fromClient = reader.readLine()) != null) {
                System.out.println("From client: " + fromClient);
                String[] parsedIn = fromClient.split(" ");
                System.out.print("Parsed Input: ");
                for (String s : parsedIn) System.out.print("{" + s + "} ");

                // Error Checking
                System.out.printf("Command length: %d\n", parsedIn.length);
                if (
                    parsedIn[0].equals("Hello!") ||
                    parsedIn[0].equals("Waiting!") ||
                    parsedIn[0].equals("Received!")
                ) {
                    System.out.println("Waiting for request...");
                    state = 1;
                } else if (
                    parsedIn[0].equalsIgnoreCase("bye") ||
                    parsedIn[0].equalsIgnoreCase("terminate")
                ) {
                    state = 2;
                }
                else {
                    eflag = false;
                    error = "";
                    checkInput(parsedIn);
                    state = eflag ? 3 : 2;
                }
                // Error checking done

                // Request Protocol
                switch (state) {
                    case 0: 
                        toClient = "Hello!";
                        state = 1;
                        break;
                    case 1:
                        toClient = "Waiting!";
                        state = 2;
                        break;
                    case 2:
                        toClient = reqProtocol(parsedIn);
                        state = 1;
                        break;
                    case 3: 
                        toClient = this.error;
                        state = 2;
                        break;
                    default:
                        System.out.println("State error, exiting...");
                        writer.println("-5");
                        System.exit(1);
                }
                System.out.println("To client: " + toClient);
                System.out.println("");
                writer.println(toClient);

            }
        } catch(IOException e) {
            System.out.println("Error starting server.");
        }

        System.out.println("Loop skipped");
        System.exit(1);
    }

    private void checkInput(String[] in) {
        if (in.length < 3) {
            System.out.println("Error: Not enough arguments.");
            error = (error.isEmpty() || Integer.parseInt(error) < -2) ? "-2" : error;
            eflag = true;
        } else if (in.length > 5) {
            System.out.println("Error: Too many arguments.");
            error = (error.isEmpty() || Integer.parseInt(error) < -3) ? "-3" : error;
            eflag = true;          
        }

        int i = 0;
        for (String s : in) {
            if (i++ == 0) {
                if (
                    !s.equalsIgnoreCase("add") && 
                    !s.equalsIgnoreCase("subtract") && 
                    !s.equalsIgnoreCase("multiply") &&
                    !s.equalsIgnoreCase("bye") &&
                    !s.equalsIgnoreCase("terminate")
                ) {
                    System.out.println("Error: Incorrect command.");
                    error = (error.isEmpty() || Integer.parseInt(error) < -1) ? "-1" : error;
                    eflag = true;
                }
                else System.out.println("Correct command.");
            }
            else {
                try {
                    Integer.parseInt(s);
                } catch(NumberFormatException e) {
                    error = (error.isEmpty() || Integer.parseInt(error) < -4) ? "-4" : error;
                    eflag = true;
                } catch(NullPointerException e) {
                    error = (error.isEmpty() || Integer.parseInt(error) < -4) ? "-4" : error;
                    eflag = true;
                }
            }
        }
        return;
    }

    private String reqProtocol(String[] req) {
        int result = 0;
        String mes = "";
        switch (req[0].toLowerCase()) {
            case "add":
                for(int i = 1; i < req.length; i++) result += Integer.parseInt(req[i]);
                mes = Integer.toString(result);
                break;
            case "subtract":
                for(int i = 1; i < req.length; i++) {
                    if (i == 1) result = Integer.parseInt(req[i]);
                    else result -= Integer.parseInt(req[i]);
                }
                mes = Integer.toString(result);
                break;
            case "multiply":
                for(int i = 1; i < req.length; i++) {
                    if (i == 1) result = Integer.parseInt(req[i]);
                    else result *= Integer.parseInt(req[i]);
                }
                mes = Integer.toString(result);
                break;
            case "bye":
                writer.println("-5");
                try {
                    client.close();
                } catch(IOException e) {
                    System.out.println("Error closing socket.");
                }
                System.out.println("Client exit.");
                state = 0;
                start(port);
            case "terminate":
                writer.println("-5");
                try {
                    client.close();
                } catch(IOException e) {
                    System.out.println("Error closing socket.");
                }
                System.out.println("Server exit.");
                System.exit(0);
            default:
                mes = "Error!";
                break;
        }
        return mes;
    }
}