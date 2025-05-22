public class Location {
    private int x;
    private int y;
    public Location(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public int getX(){return x;}
    public int getY(){return y;}

    @Override
    public String toString() {
        return x + " " + y;
    }
    public static Location random(){
        int width = 5000;
        int height = 5000;
        int x = (int)(Math.random()*(width-40)) + 20;
        int y = (int)(Math.random()*(height-40)) + 20;
        return new Location(x,y);
    }
}
