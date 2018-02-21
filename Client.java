import pkg.ClientClass;

public class Client{
    public static void main(String[] args) {
        System.out.println("Starting client...");
        try {
            ClientClass client = new ClientClass(args[0], Integer.parseInt(args[1]));
        } catch (NumberFormatException e) {
            System.out.println("Incorrect argument format.");
        } catch (NullPointerException e) {
            System.out.println("Incorrect argument format.");
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Not enough arguments.");
        }
    }
}