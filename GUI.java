import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
public class GUI implements ActionListener {
    private JTextField field;
    private JFrame frame;
    private Client client;
    public GUI(Client client) {
        this.client = client;
        frame = new JFrame();
        frame.setTitle("My Window");
        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.PAGE_AXIS));
        JLabel title = new JLabel("Agar.io");
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 42));
        frame.getContentPane().add(new JLabel("Agar.io"));
        frame.getContentPane().add(new JLabel(
                "This is an implementation of a multiplayer browser game in which you are an Agar in a petri dish, and you can consume" +
                        " pellets and other Agar in order to increase your mass."
        ));
        frame.getContentPane().add(new JLabel(
                "To play, simply move your mouse and your" +
                        " Agar will follow. Try to consume objects smaller than yourself by going over them with your Agar."
        ));
        frame.getContentPane().add(new JLabel(
                "This game is meant for multiple machines, but multiple players can be hosted on a single machine."
        ));
        frame.getContentPane().add(new JLabel(
                "In the main class, enter a new player amount to try it (loading screens will first appear on top of each other," +
                        " so move this one out of the way)"
        ));
        frame.getContentPane().add(new JLabel(
                "Enter a nickname to start!"
        ));

        frame.getContentPane().add(new JLabel("Nickname"));
        JLabel label = new JLabel("Name is already taken");
        label.setForeground(Color.RED);
        label.setVisible(false);
        frame.getContentPane().add(label);
        field = new JTextField(25);
        frame.getContentPane().add(field);
        JButton button = new JButton("Play");
        frame.getContentPane().add(button);
        button.addActionListener(this);
        button.setActionCommand("revive");
        frame.pack();
        frame.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("revive")) {
            if(field.getText().length() > 0) client.send("revive " + field.getText());
        }
    }
    public void hide(){
        frame.setVisible(false);
    }
    public void show(){frame.setVisible(true);}
    public void nameTaken(){
        Component[] comps = frame.getContentPane().getComponents();
        comps[0].setVisible(false);
        comps[1].setVisible(true);
    }
    public void nameAccepted(){
        hide();
        Component[] comps = frame.getContentPane().getComponents();
        comps[0].setVisible(true);
        comps[1].setVisible(false);
    }

}
