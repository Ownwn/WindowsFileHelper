import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

import javax.swing.*;

public class HotkeyListener implements NativeKeyListener {
    final int firstMask = NativeKeyEvent.CTRL_L_MASK;
    final int secondMask = NativeKeyEvent.SHIFT_L_MASK;
    final int finisherKey = NativeKeyEvent.VC_QUOTE;
    private final Runnable action;

    public HotkeyListener(Runnable action) {
        this.action = action;
    }

    public static void init(Runnable action) {
        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException ex) {
            System.err.println("There was a problem registering the native hook.");
            System.err.println(ex.getMessage());

            System.exit(1);
        }

        GlobalScreen.addNativeKeyListener(new HotkeyListener(action));
        System.out.println();
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        int key = e.getKeyCode();
        if (key == finisherKey && e.getModifiers() == (firstMask | secondMask)) {
            SwingUtilities.invokeLater(action);
        }
    }
}