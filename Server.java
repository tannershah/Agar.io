import java.awt.*;
import java.util.*;
import java.io.*;
import java.net.*;

public class Server {
  private ArrayList<ServerThread> threads;
  private ArrayList<ServerThread> deadThreads;
  private TreeMap<String, Integer> scores;
  private Cell[] pellets;
  Long millis;
  public Server(){ //change number of threads to include more players
    try {
      ServerSocket server = new ServerSocket(9000);
      threads = new ArrayList<>();
      deadThreads = new ArrayList<>();
      scores = new TreeMap<>();
      pellets = new Cell[1000];
      for(int i = 0; i < pellets.length; i++){
        pellets[i] = Cell.generate();
      }
      System.out.println("waiting for connections...");
      ServerThread thread1 = new ServerThread(server.accept(), this, "thread1");
      System.out.println("1 connected");
      ServerThread thread2 = new ServerThread(server.accept(), this, "thread2");
      System.out.println("2 connected");
      deadThreads.add(thread1);
      deadThreads.add(thread2);
    }
    catch(IOException e) {
      throw new RuntimeException(e);
    }
  }
  public void sendToAll(String message) {
    //System.out.println("allSending: " + message);
    for(ServerThread thread : threads) thread.send(message);
  }
  public void updateScores(){
    for(int i = 0; i < threads.size(); i++){
//      ServerThread thread = threads.get(i);
//      String ID = thread.getID();
      Agar agar = threads.get(i).getAgar();
      scores.put(agar.getName(),(int)agar.getMass());
    }
  }
  public synchronized void consume(){
    for(int i = 0; i < threads.size(); i++){
      Agar a1 = threads.get(i).getAgar();
      for(int k = 0; k < pellets.length; k++){
        Cell pellet = pellets[k];
        if(a1.canSwallow(pellet)){
          a1.setMass(a1.getMass()+pellet.getMass());
          pellets[k] = Cell.generate();
        }
      }
      for(int j = i+1; j < threads.size(); j++){
        Agar a2 = threads.get(j).getAgar();
        if(a1.canSwallow(a2)) {
          a1.eat(a2);
          sendToAll("killed " + a2.getName());
          ServerThread thread = threads.get(j);
          thread.kill();
          scores.put(thread.getName(), 0);
          deadThreads.add(threads.remove(j));
          j--;
        }
        else if(a2.canSwallow(a1)) {
          a2.eat(a1);
          sendToAll("killed " + a1.getName());
          ServerThread thread = threads.get(i);
          thread.kill();
          scores.put(thread.getName(), 0);
          deadThreads.add(threads.remove(i));
          i--;
          break;
        }
      }
    }
  }
  public void revive(ServerThread thread){
    deadThreads.remove(thread);
    threads.add(thread);
    String name = thread.getAgar().getName();
    Agar agar;
    if(name.equals("_JOHN_HU_")){
      agar = new Agar(name,10000, new Location(2500, 2500));
    }
    else {
      agar = new Agar(name);
      scores.put(name, 10);
    }
    thread.setAgar(agar);
    thread.setIsDead(false);
    updateScores();
    String s = "start " + agar + " " + pelletsToString() + " " + cellsToString(agar);
    thread.send(s);
  }
  public String pelletsToString(){
    String s = "pellets ";
    for(Cell c : pellets){
      s += c + " ";
    }
    s = s.substring(0,s.length()-1);
    return s;
  }
  public String cellsToString(Agar agar){
    String s = "cells ";
    for(ServerThread thread : threads){
      Agar a = thread.getAgar();
      if(a != null && !agar.equals(a)) s += a + " ";
    }
    s = s.substring(0,s.length()-1);
    return s;
  }
  public boolean nickAvailable(String name){
    //System.out.println(name);
    for(ServerThread thread : threads){
      String s = thread.getAgar().getName();
      //System.out.println(s);
      if(s.equals(name) && scores.get(s) != 0) return false;
    }
    return true;
  }

}