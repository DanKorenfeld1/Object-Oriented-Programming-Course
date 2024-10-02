package image_char_matching;

import java.util.*;

/**
 * The SubImgCharMatcher class is designed for matching characters based on image brightness levels. It allows
 * for the conversion of image sections to characters by comparing brightness levels, supporting operations
 * like adding or removing characters from the matching set, and performing histogram equalization on the
 * character set based on their brightness.
 */
public class SubImgCharMatcher {
    /**
     * The maximum ASCII value considered for character matching.
     */
    public static final int LAST_CHAR_RANGE = 126;

    /**
     * The minimum ASCII value considered for character matching.
     */
    public static final int FIRST_CHAR_RANGE = 32;

    /**
     * The range of brightness values considered, typically 256 to represent the grayscale brightness from
     * black to white.
     */
    private static final int RANGE_BRIGHTNESS = 16 * 16;

    /**
     * A list of characters considered for matching based on image brightness.
     */
    private ArrayList<Character> charset;

    /**
     * A mapping of characters to their associated brightness values.
     */
    private HashMap<Character, Double> dictBrightness;

    /**
     * A mapping of characters to their overall brightness values before normalization.
     */
    private HashMap<Character, Double> allBrightness;

    /**
     * A cache for quick access to previously calculated character matches based on brightness.
     */
    private HashMap<Double, Character> cache;

    /**
     * A priority queue for maintaining the minimum brightness values encountered.
     */
    private PriorityQueue<Double> minBrightHeap;

    /**
     * A priority queue for maintaining the maximum brightness values encountered.
     */
    private PriorityQueue<Double> maxBrightHeap;

    /**
     * A mapping of characters to their normalized brightness values after performing histogram equalization.
     */
    private HashMap<Character, Double> normalizedBrightness;

    /**
     * Constructs a SubImgCharMatcher with a specific set of characters. Initializes internal structures for
     * brightness calculation and character matching.
     *
     * @param charset An array of characters to be included in the matching set.
     */
    public SubImgCharMatcher(char[] charset) {
        this.charset = new ArrayList<Character>();
        this.cache = new HashMap<>();
        normalizedBrightness = new HashMap<>();

        allBrightness = new HashMap<>();
        for (char c = FIRST_CHAR_RANGE; c <= LAST_CHAR_RANGE; c++) {
            allBrightness.put(c, calculateBrightness(c));
        }
        maxBrightHeap = new PriorityQueue<>(Comparator.reverseOrder());
        minBrightHeap = new PriorityQueue<>();

        dictBrightness = new HashMap<>();
        for (char c : charset) {
            addCharUnSafe(c);
        }
    }

    /**
     * Determines and returns the best matching character for a specified image brightness level, utilizing
     * caching for efficiency.
     *
     * @param brightness The target brightness level, normalized between 0 and 1.
     * @return The character that best matches the given brightness level.
     */
    public char getCharByImageBrightness(double brightness) {
        if (cache.containsKey(brightness)) {
            return cache.get(brightness);
        }
        double minDistance = Double.POSITIVE_INFINITY;
        char resultChar = Character.MAX_VALUE;
        for (char c : charset) {
            if (getDistance(brightness, c) < minDistance) {
                minDistance = getDistance(brightness, c);
                resultChar = c;
            } else if ((getDistance(brightness, c) <= minDistance) && (c < resultChar)) {
                minDistance = getDistance(brightness, c);
                resultChar = c;
            }
        }
        cache.put(brightness, resultChar);
        return resultChar;
    }

    /**
     * Adds a character to the matching set if it is not already present, updating related brightness
     * mappings and caches.
     *
     * @param c The character to add.
     */
    public void addChar(char c) {
        if (!charset.contains(c)) {
            addCharUnSafe(c);
        }
    }

    /**
     * Removes a character from the matching set if it exists, updating related brightness mappings and
     * caches.
     *
     * @param c The character to remove.
     */
    public void removeChar(char c) {
        if (charset.contains(c)) {
            removeUnSafeChar(c);
        }
    }

    /**
     * Returns the current set of characters in the matcher, sorted by their ASCII values.
     *
     * @return An array of characters currently used in the matching set.
     */
    public char[] getCharSet() {
        char[] set = new char[charset.size()];

        for (int i = 0; i < set.length; i++) {
            set[i] = charset.get(i);
        }

        Arrays.sort(set);
        return set;
    }

    /**
     * Performs histogram equalization on the characters' brightness values to enhance contrast by
     * normalizing brightness across the available range.
     */
    public void histogramEqualization() {
        double maxBrightness = maxBrightHeap.peek();
        double minBrightness = minBrightHeap.peek();
        normalizedBrightness.clear();
        for (char c : charset) {
            double newCharBrightness = (dictBrightness.get(c) - minBrightness) /
                    (maxBrightness - minBrightness);
            normalizedBrightness.put(c, newCharBrightness);
        }
    }

    /**
     * Adds a character to the matching set without checking for its existence, directly updating the
     * brightness mappings and queues.
     *
     * @param c The character to add.
     */
    private void addCharUnSafe(char c) {
        cache.clear();
        charset.add(c);
        double brightness = 0;
        if (!allBrightness.containsKey(c)) {
            //update the global (un-normalized) brightness cache
            brightness = calculateBrightness(c);
            allBrightness.put(c, brightness);
        } else {
            brightness = allBrightness.get(c);
        }
        dictBrightness.put(c, brightness);
        maxBrightHeap.add(brightness);
        minBrightHeap.add(brightness);
    }

    /**
     * Removes a character from the matching set without checking for its existence, directly updating the
     * brightness mappings and queues.
     *
     * @param c The character to remove.
     */
    private void removeUnSafeChar(char c) {
        cache.clear();
        charset.remove((Object) c); // without the casting, java converts c to an index.
        double brightness = dictBrightness.get(c);
        dictBrightness.remove(c);
        maxBrightHeap.remove(brightness);
        minBrightHeap.remove(brightness);
    }

    /**
     * Calculates the distance between a given brightness level and the normalized brightness of a character.
     *
     * @param brightness The target brightness level.
     * @param c          The character whose brightness distance is to be calculated.
     * @return The absolute difference between the normalized brightness of the character and the target
     * brightness.
     */
    private double getDistance(double brightness, char c) {
        return Math.abs(normalizedBrightness.get(c) - brightness);
    }

    /**
     * Calculates the brightness of a character representation based on the proportion of "white" (true)
     * pixels in a boolean array representation.
     *
     * @param boolArrayChar A 2D boolean array representing the character in a binary (black and white)
     *                      format.
     * @return The normalized brightness value of the character.
     */
    private double calculateBrightness(boolean[][] boolArrayChar) {
        int countWhite = 0;
        for (int row = 0; row < boolArrayChar.length; row++) {
            for (int col = 0; col < boolArrayChar[row].length; col++) {
                if (boolArrayChar[row][col]) {
                    countWhite++;
                }
            }
        }
        return ((double) countWhite) / RANGE_BRIGHTNESS;
    }

    /**
     * Converts a character into a boolean array and calculates its brightness based on the proportion of
     * white pixels.
     *
     * @param c The character to be converted and analyzed for brightness.
     * @return The brightness of the character, normalized between 0 and 1.
     */
    private double calculateBrightness(char c) {
        boolean[][] boolArrayChar = CharConverter.convertToBoolArray(c);
        return calculateBrightness(boolArrayChar);
    }
}
