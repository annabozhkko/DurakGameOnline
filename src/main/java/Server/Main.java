package Server;

public class Main {
    public static void main(String[] args){
        try {
            Server server = new Server();
        }catch (Exception exp){
            System.err.println(exp.getMessage());
        }
    }
}
