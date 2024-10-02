package src.pepse.util;

import java.awt.*;
import java.util.Random;

/**
 * Provides procedurally-generated colors around a pivot.
 *
 * @author Dan Nirel
 */
public final class ColorSupplier {
    /**
     * The default maximum difference allowed between the original color and the generated color for each
     * RGB channel. This constant defines how much the red, green, and blue values of the base color can
     * change during color approximation.
     */
    private static final int DEFAULT_COLOR_DELTA = 10;
    /**
     * A shared instance of Random used to generate random numbers within the class. This field is utilized
     * for creating random variations of colors by adjusting their RGB channels within specified ranges.
     */
    private final static Random random = new Random();

    /**
     * The maximum values for a color channel.
     */
    private static final int MAX_RANGE = 255;

    /**
     * The minimum values for a color channel.
     */
    private static final int MIN_RANGE = 0;

    /**
     * Returns a color similar to baseColor, with a default delta.
     *
     * @param baseColor A color that we wish to approximate.
     * @return A color similar to baseColor.
     */
    public static Color approximateColor(Color baseColor) {
        return approximateColor(baseColor, DEFAULT_COLOR_DELTA);
    }

    /**
     * Returns a color similar to baseColor, with a difference of at most colorDelta.
     *
     * @param baseColor  A color that we wish to approximate.
     * @param colorDelta The maximal difference (per channel) between the sampled color and the base color.
     * @return A color similar to baseColor.
     */
    public static Color approximateColor(Color baseColor, int colorDelta) {

        return new Color(
                randomChannelInRange(baseColor.getRed() - colorDelta, baseColor.getRed() + colorDelta),
                randomChannelInRange(baseColor.getGreen() - colorDelta, baseColor.getGreen() + colorDelta),
                randomChannelInRange(baseColor.getBlue() - colorDelta, baseColor.getBlue() + colorDelta));
    }

    /**
     * This method generates a random value for a color channel within the given range [min, max].
     *
     * @param min The lower bound of the given range.
     * @param max The upper bound of the given range.
     * @return A random number in the range [min, max], clipped to [0,255].
     */
    private static int randomChannelInRange(int min, int max) {
        int channel = random.nextInt(max - min + 1) + min;
        return Math.min(MAX_RANGE, Math.max(channel, MIN_RANGE));
    }
}
