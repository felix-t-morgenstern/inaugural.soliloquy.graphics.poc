package io;

import java.util.HashMap;

import static org.lwjgl.glfw.GLFW.*;

public class Input {
    private long _window;
    private HashMap<Integer,Boolean> _keys;

    public Input(long window) {
        _window = window;
        _keys = new HashMap<>();
        addKeysRangeToKeys(32,69);
        addKeysRangeToKeys(161,162);
        addKeysRangeToKeys(256,348);
    }

    private void addKeysRangeToKeys(int start, int end) {
        for(int i = start; i < end; i++) {
            _keys.put(i, false);
        }
    }

    public boolean isKeyDown(int key) {
        return glfwGetKey(_window, key) == GLFW_TRUE;
    }

    public boolean isKeyPressed(int key) {
        return (isKeyDown(key) && !_keys.get(key));
    }

    public boolean isKeyReleased(int key) {
        return (!isKeyDown(key) && _keys.get(key));
    }

    public boolean isMouseButtonDown(int button) {
        return glfwGetMouseButton(_window, button) == GLFW_TRUE;
    }

    public void update() {
        _keys.entrySet().forEach(entry -> entry.setValue(isKeyDown(entry.getKey())));
    }
}
