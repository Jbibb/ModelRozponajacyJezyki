import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Window extends JFrame implements Runnable {
    private JTextField textField;
    @Override
    public void run() {
        this.getContentPane().setLayout(new BorderLayout());
        textField = new JTextField();
        textField.setEditable(true);
        this.getContentPane().add(textField, BorderLayout.CENTER);
        JButton button = new JButton("Start");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Main.classify(textField.getText());
            }
        });
        this.getContentPane().add(button, BorderLayout.PAGE_END);

        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(800, 600);
        setVisible(true);
    }
}
