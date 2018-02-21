import pkg.ServerClass;

public class Server {
    public static void main(String[] args) {
        System.out.println("Starting server...");
        try {
            ServerClass server = new ServerClass(Integer.parseInt(args[0]));
        } catch (NumberFormatException e) {
            System.out.println("Incorrect argument format.");
        } catch (NullPointerException e) {
            System.out.println("Incorrect argument format.");
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Not enough arguments.");
        }
    }
}