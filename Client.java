import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;

public class Client extends JComponent implements Runnable{
  private BufferedReader in;
  private PrintWriter out;
  private TreeMap<String,Agar> agars;
  private ArrayList<String> rankings;
  private Cell[] pellets;
  private Agar agar;
  private boolean isDead;
  private GUI gui;
  private Display display;
  public Client(String ipAddress){
    try {
      Socket socket = new Socket(ipAddress, 9000);
      in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      out = new PrintWriter(socket.getOutputStream(), true);
    }
    catch(IOException e) {
      throw new RuntimeException(e);
    }
    agars = new TreeMap<>();
    display = new Display(this);
    gui = new GUI(this);
    isDead = true;
    new Thread(this).start();
  }

  public void run() {
    while (true) {
      try {
        String message = in.readLine();
        //System.out.println("Client received: " + message);
        String[] tokens = message.split(" ");
        if(message.indexOf("killed") == 0 ){
          agars.put(message.substring(7), null);
        }
        else if(parseMessage(tokens) == true) {
          Location loc = display.getLoc();
          double radius = agar.getRadius();
          int x = (int) (loc.getX() - radius);
          int y = (int) (loc.getY() - radius);
          send(x + " " + y);
        }
      }
      catch(IOException e) {
        throw new RuntimeException(e);
      }
    }
  }
  public void send(String message) {
    out.println(message);
    //System.out.println("Client sent: " + message);
  }
  public boolean parseMessage(String[] tokens){
    //for(String s : tokens) System.out.print(s + " , ");
    //System.out.println();
      if(tokens[0].equals("invalid")){
        gui.nameTaken();
        return false;
      }
      else if (tokens[0].equals("start")) {
//        System.out.println("starting");
//        System.out.println("tokens: " + Arrays.toString(tokens));
        gui.nameAccepted();
        String name = tokens[1];
        int i = 2;
        while(!tokens[i].equals("stats")){
          name += " " + tokens[i];
          i++;
        }
        double mass = Double.parseDouble(tokens[i+1]);
        agar = new Agar(name, mass, new Color(Integer.parseInt(tokens[i+2]), Integer.parseInt(tokens[i+3]), Integer.parseInt(tokens[i+4])), new Location(Integer.parseInt(tokens[i+5]), Integer.parseInt(tokens[i+6])));
        //System.out.println("me " + agar);
        agars.put(name, agar);
        pellets = new Cell[2000];
        update(tokens, i+7);
        display.setLoc(agar.getLocation());
        isDead = false;
        display.show();
        new Thread(display).start();
      } else if (tokens[0].equals("kill")) {
        //System.out.println("killing");
        isDead = true;
        display.hide();
        gui.show();
      }  else if(agar.getRadius() > 0){
        agar.setMass(Double.parseDouble(tokens[0]));
        update(tokens, 1);
      }
      return true;
  }
  public void update(String[] tokens, int startInd){
    int i = 1 + startInd;
    int k = 0;
    while(!tokens[i].equals("cells")){
      double m = Double.parseDouble(tokens[i]);
      int r = Integer.parseInt(tokens[i+1]);
      int g = Integer.parseInt(tokens[i+2]);
      int b = Integer.parseInt(tokens[i+3]);
      int x = Integer.parseInt(tokens[i+4]);
      int y = Integer.parseInt(tokens[i+5]);
      i += 6;
      pellets[k] = new Cell(m, new Color(r,g,b), new Location(x,y));
      k++;
    }
    i++;
    while(i < tokens.length){
      String name = tokens[i];
      i++;
      while(!tokens[i].equals("stats")){
        name += " " + tokens[i];
        i++;
      }
      double m = Double.parseDouble(tokens[i+1]);
      int r = Integer.parseInt(tokens[i+2]);
      int g = Integer.parseInt(tokens[i+3]);
      int b = Integer.parseInt(tokens[i+4]);
      int x = Integer.parseInt(tokens[i+5]);
      int y = Integer.parseInt(tokens[i+6]);
      i += 7;
      Location loc = new Location(x,y);
      Agar a = agars.get(name);
      if(a == null) agars.put(name, new Agar(name, m, new Color(r,g,b), loc));
      else{
        a.setMass(m);
        //System.out.println(agar.getName() + " " + loc);
        a.setLocation(loc);
      }
    }
    updateRankings();
  }
  public void updateRankings(){
    Set<String> names = agars.keySet();
    rankings = new ArrayList<>();
    for(String name : names){
      if(agars.get(name) != null) {
        rankings.add(name);
      }
    }
    if(rankings.size() > 1){
      quickSort(rankings, 0, rankings.size()-1);
    }
  }
public void quickSort(ArrayList<String> arr, int low, int high) {
  if (low < high) {
    int ind = partition(arr, low, high);
    quickSort(arr, low, ind - 1);
    quickSort(arr, ind + 1, high);
  }
}

  public int partition(ArrayList<String> arr, int low, int high) {
    int pivot = (int) agars.get(arr.get(high)).getMass();
    int i = low - 1;

    for (int j = low; j < high; j++) {
      if ((int) agars.get(arr.get(j)).getMass() >= pivot) {
        i++;
        swap(arr, i, j);
      }
    }
    swap(arr, i + 1, high);
    return i + 1;
  }

  private void swap(ArrayList<String> arr, int i, int j) {
    String temp = arr.get(i);
    arr.set(i, arr.get(j));
    arr.set(j, temp);
  }

  public ArrayList<String> getRankings(){
    return rankings;
  }
  public Cell[] getPellets(){
    if(pellets == null || pellets[0] == null) return null;
    return pellets;
  }
  public Agar getAgar(){
    return agar;
  }
  public TreeMap<String, Agar> getAgars(){return agars;}
  public boolean isDead(){return isDead;}

}