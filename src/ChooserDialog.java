import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Map;

public class ChooserDialog {
    private Frame frame = null;
    private final Map<String, Runnable> runnables;

    private JButton createButton(String text, Runnable action) {
        JButton button = new JButton(text);
        button.addActionListener(_ -> action.run());
        return button;
    }

    public Panel getButtonsPanel() {
        Panel panel = new Panel();
        for (var runnableEntry : runnables.entrySet()) {
            panel.add(createButton(runnableEntry.getKey(), runnableEntry.getValue()));
        }

        return panel;
    }

    public ChooserDialog(Map<String, Runnable> map) {
        runnables = map;
        open();
    }

    public void open() {
        if (frame != null) {
            frame.dispose();
        }

        frame = new Frame("");
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

            @Override
            public void windowLostFocus(WindowEvent e) {
                frame.dispose();
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
                super.windowDeiconified(e);
            }
        });

        frame.add(getButtonsPanel());
        frame.pack();
        frame.setVisible(true);
    }
}
