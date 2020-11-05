package engine;

import java.util.ArrayList;
import java.util.List;

public class Debug {
    private static List<String> Log = new ArrayList<>();

    public static void log(String message) {
        addMessage(message);
    }

    private static void addMessage(String message) {
        Log.add(message);

        if (Log.size() > 20) {
            Log.remove(0);
        }
    }

    public static String log() {
        StringBuilder stringBuilder = new StringBuilder();
        Log.forEach(l -> stringBuilder.append(l).append("\n"));
        return stringBuilder.toString();
    }

    public static String lastEntry() {
        if (Log.size() == 0) {
            return "";
        }
        else {
            return Log.get(Log.size() - 1);
        }
    }
}
