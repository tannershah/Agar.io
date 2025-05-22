import java.awt.*;

public class Cell extends Circle{
    private double mass;
    private final Color color;
    private Location location;
    private static final Color[] colors = {Color.BLUE, Color.CYAN, Color.GREEN, Color.PINK, Color.MAGENTA, Color.ORANGE, Color.YELLOW, Color.RED};
    public Cell(double mass, Color color, Location loc){
        super((int)(Math.sqrt(mass*100)));
        setMass(mass);
        this.color = color;
        location = loc;
    }
    public Cell(double mass, Location loc){
        super((int)(Math.sqrt(mass*100)));
        this.mass = mass;
        this.color = randomColor();
        location = loc;
    }
    public void setMass(double mass){
        this.mass = mass;
        super.radius = (int)Math.sqrt(mass*100);
    }
    public double getMass(){
        return mass;
    }
    public boolean canSwallow(Cell other){
        double d = Math.sqrt(Math.pow(other.getLocation().getX()-location.getX(), 2) + Math.pow(other.getLocation().getY()-location.getY(),2));
        return d + 1.5 < radius;
    }
    public void setLocation(Location location){
        this.location = location;
    }
    public Location getLocation(){return location;}
    public Color getColor(){ return color;}
    public static Cell generate(){
        int mass = 1;
        for(int i = 0; i < 4; i++){
            double random = Math.random();
            if(random > .75) mass++;
        }
        return new Cell(mass, randomColor(), Location.random());
    }
    public String toString(){
        return mass + " " + color.getRed() + " " +  color.getGreen() + " " + color.getBlue() + " " + location;
    }
    public static Color randomColor(){
        return colors[(int)(Math.random()*colors.length)];
    }

}
