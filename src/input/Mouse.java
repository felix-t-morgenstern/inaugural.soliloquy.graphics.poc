package input;

import engine.Application;
import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWMouseButtonCallback;

import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;

public class Mouse extends GLFWMouseButtonCallback {
    private static boolean _anyButtonBeingHeld = false;

    public static Map<Integer,Boolean> BUTTONS_BEING_HELD = new HashMap<>();
    public static Map<Integer,Boolean> BUTTONS_PRESSED = new HashMap<>();
    public static Map<Integer,Boolean> BUTTONS_RELEASED = new HashMap<>();

    private static List<Integer> _pressedButtons = new ArrayList<>();
    private static List<Integer> _releasedButtons = new ArrayList<>();
    private static long _window;
    private static DoubleBuffer _xBuffer;
    private static DoubleBuffer _yBuffer;

    public Mouse() {
        _window = Application.window();
        _xBuffer = BufferUtils.createDoubleBuffer(1);
        _yBuffer = BufferUtils.createDoubleBuffer(1);
    }

    public static void reset() {
        BUTTONS_PRESSED.keySet().forEach(k -> BUTTONS_PRESSED.put(k, false));
        BUTTONS_RELEASED.keySet().forEach(k -> BUTTONS_RELEASED.put(k, false));

        _anyButtonBeingHeld = false;

        _pressedButtons.clear();
        _releasedButtons.clear();
    }

    @Override
    public void invoke(long window, int button, int action, int mods) {
        _anyButtonBeingHeld = true;

        BUTTONS_BEING_HELD.put(button, action != GLFW_RELEASE);

        if (action == GLFW_PRESS) {
            BUTTONS_PRESSED.put(button, true);
            _pressedButtons.add(button);
        }
        if (action == GLFW_RELEASE) {
            BUTTONS_RELEASED.put(button, true);
            _releasedButtons.add(button);
        }
    }

    public static boolean anyButtonBeingHeld() {
        return _anyButtonBeingHeld;
    }

    public static boolean buttonIsBeingHeld(int buttonCode) {
        return getValueFromMap(buttonCode, BUTTONS_BEING_HELD);
    }

    public static boolean anyButtonPressed() {
        return _pressedButtons.size() > 0;
    }

    public static boolean buttonIsPressed(int buttonCode) {
        return getValueFromMap(buttonCode, BUTTONS_PRESSED);
    }

    public static boolean anyButtonReleased() {
        return _releasedButtons.size() > 0;
    }

    public static boolean buttonIsReleased(int buttonCode) {
        return getValueFromMap(buttonCode, BUTTONS_RELEASED);
    }

    private static boolean getValueFromMap(int buttonCode, Map<Integer,Boolean> map) {
        return map.containsKey(buttonCode) && map.get(buttonCode);
    }

    public static Vector2f position() {
        glfwGetCursorPos(_window, _xBuffer, _yBuffer);
        return new Vector2f((float)_xBuffer.get(0), (float)_yBuffer.get(0));
    }

    enum MouseImage {
        Pointer,
        Hand,
        HorizontalScroll,
        VerticalScroll
    }
}
