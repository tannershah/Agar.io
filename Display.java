import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.*;
import javax.swing.JComponent;
import javax.swing.JFrame;

public class Display extends JComponent implements KeyListener, MouseListener, MouseMotionListener, Runnable {
    private double imageXovr;
    private double imageYovr;
    private int mouseX;
    private int mouseY;
    private int drawY;
    private int drawX;
    private int screenSize = 5000;
    private Client client;
    private JFrame frame;

    public Display(Client client){
        this.client = client;
            frame = new JFrame();
            frame.setTitle("Agar.io");
            frame.setDefaultCloseOperation(3);
            this.setPreferredSize(new Dimension(1440, 900));
            this.setFocusable(true);
            this.addKeyListener(this);
            this.addMouseMotionListener(this);
            this.addMouseListener(this);
            frame.getContentPane().add(this);
            frame.pack();
            frame.setVisible(false);
    }

    public void paintComponent(Graphics g) {
        drawGame(g);
        g.setColor(Color.BLACK);
        drawLeaderboard(g);
    }
    private void drawGame(Graphics g){
        this.drawY = 0;
        this.drawX = 0;
        int width = this.getWidth();
        int height = this.getHeight();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);
        g.setColor(Color.GRAY);
        double fovConst = getFOVConst();
        Agar agar = client.getAgar();

        while(this.drawY-35 <= this.screenSize){
            g.drawLine(0, (int)(((double)this.drawY - this.imageYovr)*fovConst + height/2.0), this.screenSize, (int)(((double)this.drawY - this.imageYovr)*fovConst + height/2.0));
            this.drawY += 35;
        }

        while(this.drawX-35 <= this.screenSize){
            g.drawLine((int)(((double)this.drawX - this.imageXovr)*fovConst + width/2.0), 0, (int)(((double)this.drawX - this.imageXovr)*fovConst + width/2.0), this.screenSize);
            this.drawX += 35;
        }

        Cell[] cells = client.getPellets();
        if(cells != null) {
            for (Cell cell : cells) {
                if (cell != null) {//del later
                    double radius = 10;
                    double nRadius = radius * fovConst;
                    g.setColor(cell.getColor());
                    g.fillOval((int) ((cell.getLocation().getX() - imageXovr - radius)*fovConst + width / 2.0), (int) ((cell.getLocation().getY() - imageYovr - radius)*fovConst + height / 2.0), (int) (2 * nRadius), (int) (2 * nRadius));
                }
            }
        }

        TreeMap<String, Agar> agars = client.getAgars();
        ArrayList<String> rankings = client.getRankings();
        if(agars != null && rankings != null) {
            for (int i = rankings.size() - 1; i >= 0; i--) {
                String s = rankings.get(i);
                if(agar != null) {
                    String name = agar.getName();
                    if (!name.equals(s)) {
                        Agar a = agars.get(s);
                        if (a != null) {
                            Color fill = a.getColor();
                            Color outline = getOutline(fill);
                            Location loc = a.getLocation();
                            double radius = a.getRadius();
                            double nRadius = radius * fovConst;
                            double zRadius = (radius-10)*fovConst;
                            g.setColor(outline);
                            g.fillOval((int) ((loc.getX() - imageXovr - radius) * fovConst + width / 2.0), (int) ((loc.getY() - imageYovr - radius) * fovConst + height / 2.0), (int) (2 * nRadius), (int) (2 * nRadius));
                            g.setColor(fill);
                            g.fillOval((int) ((loc.getX() - imageXovr - (radius-10)) * fovConst + width / 2.0), (int) ((loc.getY() - imageYovr - (radius-10)) * fovConst + height / 2.0), (int) (2 * zRadius), (int) (2 * zRadius));
                        }
                    }
                    else{
                        Color fill = agar.getColor();
                        Color outline = getOutline(fill);
                        double radius = agar.getRadius();
                        double nRadius = radius*fovConst;
                        double zRadius = (radius-10)*fovConst;
                        g.setColor(outline);
                        g.fillOval((int)(this.getWidth() / 2.0 - nRadius), (int)(this.getHeight() / 2.0 - nRadius), (int)(2*nRadius), (int)(2*nRadius));
                        g.setColor(fill);
                        g.fillOval((int)(this.getWidth() / 2.0 - zRadius), (int)(this.getHeight() / 2.0 - nRadius + 10*fovConst), (int)(2*zRadius), (int)(2*zRadius));
                    }
                }
            }
        }
        this.repaint();
    }
    private void drawLeaderboard(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setPaint(Color.black);
        float alpha = 0.5f;
        AlphaComposite alcom = AlphaComposite.getInstance(
                AlphaComposite.SRC_OVER, alpha);
        g2d.setComposite(alcom);
        int width = getWidth();

        TreeMap<String, Agar> agars = client.getAgars();
        ArrayList<String> rankings = client.getRankings();
        if (agars != null && rankings != null) {
            g2d.fillRect(width - 200, 0, 200, 50 + 25 * rankings.size());
            g2d.dispose();
            g.setColor(Color.white);
            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
            g.drawString("Leaderboard", width - 165, 25);
            g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 18));
            for (int i = 0; i < rankings.size(); i++) {
                String name = rankings.get(i);
                Agar a = agars.get(name);
                if (a != null) {
                    if(name.equals(client.getAgar().getName())) g.setColor(new Color(250, 160, 160, 255));
                    int mass = (int) a.getMass();
                    int nameWidth = g.getFontMetrics().stringWidth(name);
                    int massWidth = g.getFontMetrics().stringWidth("" + mass) + 5;
                    if(nameWidth + massWidth > 175) {
                        while (nameWidth + massWidth + 18 > 175) {
                            name = name.substring(0,name.length()-1);
                            nameWidth = g.getFontMetrics().stringWidth(name);
                        }
                        name += "...";
                    }
                    g.drawString(name + " " + mass, width - 175, 25 * (i + 2));
                    g.setColor(Color.white);
                }
            }
        }
    }

    public void keyPressed(KeyEvent e) {
        if(client.isDead()) {
            Character key = e.getKeyChar();
            System.out.println(key);
        }
    }
    public void keyReleased(KeyEvent e) {
    }
    public void keyTyped(KeyEvent e) {
    }
    public void mouseDragged(MouseEvent e) {
        this.mouseX = e.getX();
        this.mouseY = e.getY();
        this.repaint();
    }
    public void mouseMoved(MouseEvent e) {
        this.mouseX = e.getX();
        this.mouseY = e.getY();
        this.repaint();
    }
    public void mousePressed(MouseEvent e) {
        this.repaint();
    }
    public void mouseReleased(MouseEvent e) {
    }
    public void mouseClicked(MouseEvent e) {
    }
    public void mouseEntered(MouseEvent e) {
    }
    public void mouseExited(MouseEvent e) {
    }
    public void run() {
        while(true) {
            if (!client.isDead()) {
                adjPos();
                double radius = client.getAgar().getRadius();
                if (this.imageXovr + radius >= (double) this.screenSize) {
                    this.imageXovr = (double) this.screenSize - radius;
                }
                if (this.imageXovr - radius <= 0.0) {
                    this.imageXovr = radius;
                }
                if (this.imageYovr + radius >= (double) this.screenSize) {
                    this.imageYovr = (double) this.screenSize - radius;
                }
                if (this.imageYovr - radius <= 0.0) {
                    this.imageYovr = radius;
                }
                this.repaint();
                try {
                    Thread.sleep(100L);
                } catch (Exception var2) {
                }
            }
        }
    }
    public void adjPos(){
        double xDiff = this.mouseX - this.getWidth() / 2;
        double yDiff = this.mouseY - this.getHeight() / 2;
        double d = Math.sqrt(Math.pow(xDiff,2) + Math.pow(yDiff,2));
        double dx = xDiff;
        double dy = yDiff;
        Agar agar = client.getAgar();
        double radius = agar.getRadius();
        double speed = getSpeed();
        if(d > radius){
            dx *= speed/d;
            dy *= speed/d;
        }
        else{
            dx *= speed/radius;
            dy *= speed/radius;
        }
        this.imageXovr += dx;
        this.imageYovr += dy;


    }
    public Location getLoc(){
        return new Location((int)imageXovr, (int)imageYovr);
    }
    public void setLoc(Location loc){
        imageXovr = loc.getX();
        imageYovr = loc.getY();
    }
    public void hide(){frame.setVisible(false);}
    public void show(){frame.setVisible(true);}
    public double getSpeed(){
        double mass = client.getAgar().getMass();
        return 35*Math.pow(mass,-0.215);
    }
    public double getFOVConst(){
        double radius = client.getAgar().getRadius();
        return Math.pow(Math.min(64/radius , 1), .4);
    }
    public Color getOutline(Color fill){
        int red = fill.getRed();
        int green = fill.getGreen();
        int blue = fill.getBlue();
        if(red > 15) red = Math.abs(red-30);
        if(green > 15) green = Math.abs(green-30);
        if(blue > 15) blue = Math.abs(blue-30);
        return new Color(red, green, blue);
    }


}