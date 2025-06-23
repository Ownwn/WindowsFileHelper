import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;

public class ChooserDialog {
    private static Frame frame = null;
    private static Map<String, Runnable> runnables = new HashMap<>();

    private static JButton createButton(String text, Runnable action) {
        JButton button = new JButton(text);
        button.addActionListener(_ -> action.run());
        return button;
    }

    public static Panel getButtonsPanel() {
        Panel panel = new Panel();
        for (var runnableEntry : runnables.entrySet()) {
            panel.add(createButton(runnableEntry.getKey(), runnableEntry.getValue()));
        }

        return panel;
    }

    public static void open(Map<String, Runnable> map) {
        runnables = map;
        frame = new Frame("File Helper");
        frame.setSize(300, 200);
        frame.setLayout(new FlowLayout());
        Point mouse = MouseInfo.getPointerInfo().getLocation();
        frame.setLocation((int) (mouse.getX() - 50), (int) (mouse.getY() - 50));

        frame.setAlwaysOnTop(true);


        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                frame.dispose();
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
                frame.dispose();
            }
        });

        frame.add(getButtonsPanel());
        frame.pack();
        frame.setVisible(true);
    }
}
