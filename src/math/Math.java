package math;

public class Math {
    public static float clamp(float val, float min, float max) {
        return java.lang.Math.min(max, java.lang.Math.max(min, val));
    }
}
