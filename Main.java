public class Main
{
    public static void main(String[] args) {
        play(2);
    }

    public static void play(int players){
        startServer();
        for(int i = 0; i < players; i++) {
            try { Thread.sleep(2000);} catch (Exception e) {}
            startClient();
        }
    }
    public static void startServer(){
        new Thread(() -> new Server()).start();
    }
    public static void startClient(){
        new Thread(() -> new Client("127.0.0.1")).start();
    }
}