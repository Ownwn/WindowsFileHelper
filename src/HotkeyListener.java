import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

public class HotkeyListener implements NativeKeyListener {
    final int modifierKey = NativeKeyEvent.VC_CONTROL;
    final int modifierKey2 = NativeKeyEvent.VC_SHIFT;
    final int finisherKey = NativeKeyEvent.VC_QUOTE;
    private final Runnable action;

    boolean modifierDown = false;
    boolean modifierDown2 = false;

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

        switch (key) {
            case modifierKey -> modifierDown = true;
            case modifierKey2 -> modifierDown2 = true;
            case finisherKey -> {
                if (modifierDown && modifierDown2) {
                    action.run();
                }
            }
        }
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {
        if (e.getKeyCode() == modifierKey) {
            modifierDown = false;
        } else if (e.getKeyCode() == modifierKey2) {
            modifierDown2 = false;
        }
    }
}