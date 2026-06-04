import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.net.ServerSocket;
import java.net.Socket;

public class SistemaEstoque {
    public static void main(String[] args) {
        // 1. Render exige que seu app abra um Socket na porta que ele fornecer
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
        System.out.println("Iniciando servidor de estoque na porta: " + port);
        
        // Conexão com o banco Neon usando Variável de Ambiente
        String dbUrl = System.getenv("DATABASE_URL"); 

        try {
            // Cria a tabela no Neon se ela não existir
            Connection conn = DriverManager.getConnection(dbUrl);
            String sql = "CREATE TABLE IF NOT EXISTS produtos (id SERIAL PRIMARY KEY, nome VARCHAR(100), quantidade INT, preco NUMERIC)";
            conn.createStatement().execute(sql);
            System.out.println("Conexão com o Neon.tech realizada com sucesso!");
            
            // Loop para manter o servidor do Render ativo ("Web Service")
            ServerSocket serverSocket = new ServerSocket(port);
            while (true) {
                Socket socket = serverSocket.accept();
                // O Render fará "pings" aqui para verificar se a aplicação está viva
                socket.getOutputStream().write("HTTP/1.1 200 OK\r\n\r\nEstoque Online".getBytes());
                socket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
