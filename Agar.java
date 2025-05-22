import java.awt.*;

public class Agar extends Cell{
    private String name;
    public Agar(String name){
        super(10, Location.random());
        this.name = name;
    }
    public Agar(String name, double mass, Location loc){
        super(mass, loc);
        this.name = name;
    }
    public Agar(String name, double mass, Color color, Location loc){
        super(mass, color, loc);
        this.name = name;
    }
    public String getName(){
        return name;
    }
    public void setName(String name){this.name = name;}
    public void eat(Cell cell){
        setMass(getMass() + cell.getMass());
    }

    public String toString(){
        return name + " stats " + super.toString();
    }
    public boolean equals(Agar agar){
        return name.equals(agar.getName());
    }
    public boolean canSwallow(Agar other){
        double d = Math.sqrt(Math.pow(other.getLocation().getX()-getLocation().getX(), 2) + Math.pow(other.getLocation().getY()-getLocation().getY(),2));
        return d + (double)other.getRadius()/2 < radius && getMass() > other.getMass()*4/3;
    }


}
