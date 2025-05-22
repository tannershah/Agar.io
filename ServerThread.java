import java.io.*;
import java.net.*;
import java.util.Set;

public class ServerThread extends Thread {
  private Server server;
  private BufferedReader in;
  private PrintWriter out;
  private Agar agar;
  private Long millis;
  private boolean isDead;
  public ServerThread(Socket socket, Server server, String name) {
    this.server = server;
    agar = new Agar(name, 10, new Location(0,0));
    try {
      in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      out = new PrintWriter(socket.getOutputStream(), true);
    }
    catch(IOException e) {
      throw new RuntimeException(e);
    }
    start();
  }
  
  public void run(){
    while (true) {
      try {
        String message = in.readLine();
        //System.out.println("ServerThread " + agar.getName() + " received: " + message);
        String[] tokens = message.split(" ");
        if (message.indexOf("revive") == 0) {
          String name = message.substring(7);
          if(server.nickAvailable(name)) {
            agar.setName(name);
            server.revive(this);
            startTimer();
          }
          else send("invalid");
        }
        else{
          if(!isDead) {
            int x = Integer.parseInt(tokens[0]) + agar.getRadius();
            int y = Integer.parseInt(tokens[1]) + agar.getRadius();
            agar.setLocation(new Location(x, y));
            updateMass();
            server.consume();
            send(agar.getMass() + " " + server.pelletsToString() + " " + server.cellsToString(agar));
          }
        }
      }
      catch(IOException e) {
        throw new RuntimeException(e);
      }
    }
  }
  public void kill(){
    isDead = true;
    send("kill");
  }
  public void send(String message) {
    out.println(message);
  }
  public Agar getAgar(){return agar;}
  public void setAgar(Agar agar){
    this.agar = agar;
  }
  public void updateMass(){
    agar.setMass((agar.getMass() * Math.pow(0.998, ((double)getEndTimer() / 1000))));
    startTimer();
  }
  public void startTimer(){
    millis = System.currentTimeMillis();
  }
  public Long getEndTimer(){
    return System.currentTimeMillis()-millis;
  }
  public void setIsDead(boolean b){
    isDead = b;
  }
}