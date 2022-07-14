package Client;

public class Main {
    public static void main(String[] args){
        try {
            Client client = new Client();
        }catch (Exception exp){
            System.err.println(exp.getMessage());
        }
    }
}
