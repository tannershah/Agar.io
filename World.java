import java.awt.*;

public class World {
    public static void main(String[] args) {
        run();
    }

    public static void run(){
        Display display = new Display(new Client("localhost"));
        try{
            Thread.sleep(500);
        }
        catch(Exception e){}
        display.run();
    }

    private final int width;
    private final int height;
    public Agar agar;
    public World(final int width, final int height){
        this.width = width;
        this.height = height;
        agar = new Agar("agar", 20, new Location(300,300));
    }

    public void paintComponent(Graphics g){
        g.setColor(Color.black);
        Location loc = agar.getLocation();
        int radius = agar.getRadius();
        int x = loc.getX();
        int y = loc.getY();
        //System.out.println("draw: " + x + " , " + y);
        g.fillOval(x - radius, y - radius, radius*2,radius*2);

    }

    public String getTitle()
    {
        return "Agar.io";
    }

}
