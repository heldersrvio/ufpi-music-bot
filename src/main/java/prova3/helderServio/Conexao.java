package prova3.helderServio;

import java.sql.Connection;
import java.sql.DriverManager;

public class Conexao {
    private static Connection[] connections = new Connection[10];
    private static boolean connected = false;
    private static int pos = 0;

    private Conexao(){

    }

    public static Connection getConnection(){
        if (pos == 10)
            pos = 0;
        if (!connected){
            try{
                Class.forName("com.mysql.cj.jdbc.Driver");
                for (int i = 0; i < 10; i++){
                    connections[i] = DriverManager.getConnection("jdbc:mysql://localhost:3306/ufpimusic?allowPublicKeyRetrieval=true&useSSL=false", "root", "12345678");
                }
            }catch (Exception e){
                e.printStackTrace(System.out);
                System.exit(1);
            }
            connected = true;
        }
        return connections[pos++];
    }
}
