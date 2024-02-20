import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TCPServer {
    private static final int PORT = 12345;
    private static final int MAX_CLIENTS = 10;

    public static void main(String[] args) {
        // Création d'un pool de threads pour gérer les clients
        ExecutorService executor = Executors.newFixedThreadPool(MAX_CLIENTS);
        ServerSocket serverSocket = null;

        try {
            // Création d'une socket serveur
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server started. Listening on port " + PORT);

            // Boucle infinie pour accepter les connexions des clients
            while (true) {
                // Attente d'une connexion client
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket);

                // Création d'un gestionnaire de client dans un thread séparé
                Runnable clientHandler = new ClientHandler(clientSocket);
                executor.execute(clientHandler);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Fermeture de la socket serveur et arrêt du pool de threads
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            executor.shutdown();
        }
    }

    // Classe interne pour gérer chaque client dans un thread séparé
    static class ClientHandler implements Runnable {
        private Socket clientSocket;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try {
                // Création de flux d'entrée et de sortie pour communiquer avec le client
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

                String inputLine;
                // Boucle pour lire les données envoyées par le client
                while ((inputLine = in.readLine()) != null) {
                    System.out.println("Received from client: " + inputLine);

                    // Simuler un temps de traitement en bloquant le thread
                    try {
                        Thread.sleep(2000); // 2 secondes
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // Inverser la chaîne de caractères
                    String reversedString = new StringBuilder(inputLine).reverse().toString();

                    // Envoyer la chaîne inversée au client
                    out.println(reversedString);
                }

                // Fermeture des flux et de la socket client
                in.close();
                out.close();
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
