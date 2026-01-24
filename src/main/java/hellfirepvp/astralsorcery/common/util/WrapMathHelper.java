package hellfirepvp.astralsorcery.common.util;

import java.util.Random;

/**
 * Wrapper class for MathHelper providing 1.12.2 API compatibility on 1.7.10.
 * This class bridges the API differences between Minecraft versions.
 */
public class WrapMathHelper {

    /** A table of sin values computed from 0 (inclusive) to 2*pi (exclusive), with steps of 2*PI / 65536. */
    private static float[] SIN_TABLE = new float[65536];
    /**
     * Though it looks like an array, this is really more like a mapping. Key (index of this array) is the upper 5 bits
     * of the result of multiplying a 32-bit unsigned integer by the B(2, 5) De Bruijn sequence 0x077CB531. Value
     * (value stored in the array) is the unique index (from the right) of the leftmost one-bit in a 32-bit unsigned
     * integer that can cause the upper 5 bits to get that value. Used for highly optimized "find the log-base-2 of
     * this number" calculations.
     */
    private static final int[] multiplyDeBruijnBitPosition;

    // ========== 1.12.2 API Methods ==========

    /**
     * Clamp an integer value between a minimum and maximum value.
     * 1.12.2 API: clamp(int, int, int)
     */
    public static int clamp(int value, int min, int max) {
        return value < min ? min : (value > max ? max : value);
    }

    /**
     * Clamp a float value between a minimum and maximum value.
     * 1.12.2 API: clamp(float, float, float)
     */
    public static float clamp(float value, float min, float max) {
        return value < min ? min : (value > max ? max : value);
    }

    /**
     * Clamp a double value between a minimum and maximum value.
     * 1.12.2 API: clamp(double, double, double)
     */
    public static double clamp(double value, double min, double max) {
        return value < min ? min : (value > max ? max : value);
    }

    /**
     * Clamp a long value between a minimum and maximum value.
     * 1.12.2 API: clamp(long, long, long)
     */
    public static long clamp(long value, long min, long max) {
        return value < min ? min : (value > max ? max : value);
    }

    /**
     * Returns the greatest integer less than or equal to the float argument.
     * 1.12.2 API: floor(float)
     */
    public static int floor(float value) {
        int i = (int) value;
        return value < (float) i ? i - 1 : i;
    }

    /**
     * Returns the greatest integer less than or equal to the double argument.
     * 1.12.2 API: floor(double)
     */
    public static int floor(double value) {
        int i = (int) value;
        return value < (double) i ? i - 1 : i;
    }

    /**
     * Alias for floor(float) - 1.7.10 naming.
     */
    public static int floor_float(float value) {
        return floor(value);
    }

    /**
     * Alias for floor(double) - 1.7.10 naming.
     */
    public static int floor_double(double value) {
        return floor(value);
    }

    /**
     * sin looked up in a table
     */
    public static float sin(float value) {
        return SIN_TABLE[(int) (value * 10430.378F) & 65535];
    }

    /**
     * cos looked up in the sin table with the appropriate offset
     */
    public static float cos(float value) {
        return SIN_TABLE[(int) (value * 10430.378F + 16384.0F) & 65535];
    }

    /**
     * Square root of a float.
     */
    public static float sqrt(float value) {
        return (float) Math.sqrt((double) value);
    }

    /**
     * Square root of a double.
     */
    public static float sqrt_double(double value) {
        return (float) Math.sqrt(value);
    }

    /**
     * Returns the smallest integer greater than or equal to the float argument.
     * 1.12.2 API: ceil(float)
     */
    public static int ceil(float value) {
        int i = (int) value;
        return value > (float) i ? i + 1 : i;
    }

    /**
     * Returns the smallest integer greater than or equal to the double argument.
     * 1.12.2 API: ceil(double)
     */
    public static int ceil(double value) {
        int i = (int) value;
        return value > (double) i ? i + 1 : i;
    }

    /**
     * Returns absolute value of a float.
     */
    public static float abs(float value) {
        return value >= 0.0F ? value : -value;
    }

    /**
     * Returns absolute value of an int.
     */
    public static int abs_int(int value) {
        return value >= 0 ? value : -value;
    }

    /**
     * Maximum of two integers.
     */
    public static int max(int a, int b) {
        return a > b ? a : b;
    }

    /**
     * Maximum of two floats.
     */
    public static float max(float a, float b) {
        return a > b ? a : b;
    }

    /**
     * Maximum of two doubles.
     */
    public static double max(double a, double b) {
        return a > b ? a : b;
    }

    /**
     * Maximum of two longs.
     */
    public static long max(long a, long b) {
        return a > b ? a : b;
    }

    /**
     * Minimum of two integers.
     */
    public static int min(int a, int b) {
        return a < b ? a : b;
    }

    /**
     * Minimum of two floats.
     */
    public static float min(float a, float b) {
        return a < b ? a : b;
    }

    /**
     * Minimum of two doubles.
     */
    public static double min(double a, double b) {
        return a < b ? a : b;
    }

    /**
     * Minimum of two longs.
     */
    public static long min(long a, long b) {
        return a < b ? a : b;
    }

    /**
     * Returns the value of the first parameter, clamped to be within the lower and upper limits.
     * 1.7.10 naming - delegates to clamp().
     */
    public static int clamp_int(int value, int min, int max) {
        return clamp(value, min, max);
    }

    /**
     * Returns the value of the first parameter, clamped to be within the lower and upper limits.
     * 1.7.10 naming - delegates to clamp().
     */
    public static float clamp_float(float value, float min, float max) {
        return clamp(value, min, max);
    }

    /**
     * Returns the value of the first parameter, clamped to be within the lower and upper limits.
     * 1.7.10 naming - delegates to clamp().
     */
    public static double clamp_double(double value, double min, double max) {
        return clamp(value, min, max);
    }

    /**
     * Wraps an angle to be between -180 and 180 degrees.
     */
    public static float wrapDegrees(float value) {
        value %= 360.0F;
        if (value >= 180.0F) {
            value -= 360.0F;
        }
        if (value < -180.0F) {
            value += 360.0F;
        }
        return value;
    }

    /**
     * Wraps an angle to be between -180 and 180 degrees (double version).
     */
    public static double wrapDegrees(double value) {
        value %= 360.0D;
        if (value >= 180.0D) {
            value -= 360.0D;
        }
        if (value < -180.0D) {
            value += 360.0D;
        }
        return value;
    }

    /**
     * Gets a random integer from the specified range.
     */
    public static int getRandomIntegerInRange(Random rand, int min, int max) {
        return min >= max ? min : rand.nextInt(max - min + 1) + min;
    }

    /**
     * Gets a random float from the specified range.
     */
    public static float randomFloatClamp(Random rand, float min, float max) {
        return min >= max ? min : rand.nextFloat() * (max - min) + min;
    }

    /**
     * Gets a random double from the specified range.
     */
    public static double getRandomDoubleInRange(Random rand, double min, double max) {
        return min >= max ? min : rand.nextDouble() * (max - min) + min;
    }

    /**
     * Returns the input value rounded up to the next highest power of two.
     */
    public static int smallestEncompassingPowerOfTwo(int value) {
        int i = value - 1;
        i |= i >> 1;
        i |= i >> 2;
        i |= i >> 4;
        i |= i >> 8;
        i |= i >> 16;
        return i + 1;
    }

    /**
     * Is the given value a power of two?
     */
    public static boolean isPowerOfTwo(int value) {
        return value != 0 && (value & value - 1) == 0;
    }

    /**
     * Calculates the log base 2 of an integer.
     */
    public static int calculateLogBaseTwo(int value) {
        return calculateLogBaseTwoDeBruijn(value) - (isPowerOfTwo(value) ? 0 : 1);
    }

    private static int calculateLogBaseTwoDeBruijn(int value) {
        value = isPowerOfTwo(value) ? value : smallestEncompassingPowerOfTwo(value);
        return multiplyDeBruijnBitPosition[(int) ((long) value * 125613361L >> 27) & 31];
    }

    /**
     * Average of an array of longs.
     */
    public static double average(long[] values) {
        long sum = 0L;
        for (long value : values) {
            sum += value;
        }
        return (double) sum / (double) values.length;
    }

    /**
     * Parses integer with default.
     */
    public static int parseIntWithDefault(String str, int defaultValue) {
        int result = defaultValue;
        try {
            result = Integer.parseInt(str);
        } catch (Throwable ignored) {}
        return result;
    }

    /**
     * Parses integer with default and max.
     */
    public static int parseIntWithDefaultAndMax(String str, int defaultValue, int maxValue) {
        int result = defaultValue;
        try {
            result = Integer.parseInt(str);
        } catch (Throwable ignored) {}
        if (result < maxValue) {
            result = maxValue;
        }
        return result;
    }

    /**
     * Parses double with default.
     */
    public static double parseDoubleWithDefault(String str, double defaultValue) {
        double result = defaultValue;
        try {
            result = Double.parseDouble(str);
        } catch (Throwable ignored) {}
        return result;
    }

    /**
     * Parses double with default and max.
     */
    public static double parseDoubleWithDefaultAndMax(String str, double defaultValue, double maxValue) {
        double result = defaultValue;
        try {
            result = Double.parseDouble(str);
        } catch (Throwable ignored) {}
        if (result < maxValue) {
            result = maxValue;
        }
        return result;
    }

    static {
        for (int i = 0; i < 65536; ++i) {
            SIN_TABLE[i] = (float) Math.sin((double) i * Math.PI * 2.0D / 65536.0D);
        }
        multiplyDeBruijnBitPosition = new int[] { 0, 1, 28, 2, 29, 14, 24, 3, 30, 22, 20, 15, 25, 17, 4, 8, 31, 27, 13,
            23, 21, 19, 16, 7, 26, 12, 18, 6, 11, 5, 10, 9 };
    }
}
