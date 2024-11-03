package image;

import java.awt.*;

/**
 * A utility class for manipulating images.
 */
public class ImageManipulator {

    /**
     * Maximum intensity value for grayscale images.
     */
    private static final int MAX_GRAYSCALE_INTENSITY = 255;

    /**
     * Coefficient for red color to calculate grayscale value.
     */
    private static final double COEFFICIENT_RED = 0.2126;

    /**
     * Coefficient for green color to calculate grayscale value.
     */
    private static final double COEFFICIENT_GREEN = 0.7152;

    /**
     * Coefficient for blue color to calculate grayscale value.
     */
    private static final double COEFFICIENT_BLUE = 0.0722;
    private static final String START_MESSAGE_INVALID_RESOLUTION = "Invalid resolution: ";

    /**
     * Pads the given image to the nearest power of two for both width and height.
     *
     * @param image The original image to pad.
     * @return A new Image object with padding applied.
     */
    public static Image padImage(Image image) {
        int newHeight = nearestPowerOfTwo(image.getHeight());
        int newWidth = nearestPowerOfTwo(image.getWidth());

        Color[][] colorArray = new Color[newHeight][newWidth];
        for (int row = 0; row < newHeight; row++) {
            for (int col = 0; col < newWidth; col++) {
                colorArray[row][col] = Color.WHITE;
            }
        }
        
        /** 
         * This part is responsible for copying the original image to the center of the new array we created
         * Calculate how many pixels should be added to each side to center the image.
         */
            
        int padHeight = (newHeight - image.getHeight()) / 2;
        int padWidth = (newWidth - image.getWidth()) / 2;

        for (int row = 0; row < image.getHeight(); row++) {
            for (int column = 0; column < image.getWidth(); column++) {
                colorArray[row + padHeight][column + padWidth] = image.getPixel(row, column);
            }
        }

        return new Image(colorArray, newWidth, newHeight);
    }

    /**
     * Splits the given image into multiple images based on the specified resolution.
     *
     * @param image      The image to split.
     * @param resolution The number of segments to split the image into along one dimension.
     * @return A 2D array of Image objects after splitting.
     * @throws IllegalArgumentException If the resolution is not valid for the given image.
     */
    public static Image[][] splitImage(Image image, int resolution) throws IllegalArgumentException {
        if (!isValidResolution(image, resolution)) {
            //todo: check message
            throw new IllegalArgumentException(START_MESSAGE_INVALID_RESOLUTION + resolution);
        }

        int squareEdge = image.getWidth() / resolution;
        int sizeHeightSplitedImage = image.getHeight() / squareEdge;

        Image[][] splitedImage = new Image[sizeHeightSplitedImage][resolution];

        for (int rowOfSplitedImage = 0; rowOfSplitedImage < sizeHeightSplitedImage; rowOfSplitedImage++) {
            for (int columnOfSplitedImage = 0; columnOfSplitedImage < resolution; columnOfSplitedImage++) {
                splitedImage[rowOfSplitedImage][columnOfSplitedImage] = extractSubImage(image,
                        rowOfSplitedImage * squareEdge,
                        columnOfSplitedImage * squareEdge, squareEdge);
            }
        }
        return splitedImage;
    }

    /**
     * Checks if the given resolution is valid for splitting the image.
     *
     * @param image      The image to check against.
     * @param resolution The resolution to validate.
     * @return true if the resolution is valid, false otherwise.
     */
    public static boolean isValidResolution(Image image, int resolution) {
        //checked resolution is in range
        if (resolution < Math.max(1, (image.getWidth() / image.getHeight()))) {
            return false;
        }
        if (resolution > image.getWidth()) {
            return false;
        }
        int squareEdge = image.getWidth() / resolution;
        int sizeHeightSplitedImage = image.getHeight() / squareEdge;

        if (((double) image.getWidth()) / resolution > squareEdge) {
            return false;
        }
        if (((double) image.getHeight()) / squareEdge > sizeHeightSplitedImage) {
            return false;
        }

        return true;
    }

    /**
     * Calculates the brightness of the given image.
     *
     * @param image The image to calculate the brightness for.
     * @return The brightness level of the image.
     */
    public static double getImageBrightness(Image image) {
        double[][] grayImage = RGB2Gray(image);
        double sumGray = 0;
        for (int row = 0; row < image.getHeight(); row++) {
            for (int col = 0; col < image.getWidth(); col++) {
                sumGray += grayImage[row][col];
            }
        }
        return sumGray / (MAX_GRAYSCALE_INTENSITY * image.getHeight() * image.getWidth());
    }

    /**
     * Converts an image to grayscale using the RGB values and predefined coefficients.
     *
     * @param image The image to convert.
     * @return A 2D array of grayscale values.
     */
    private static double[][] RGB2Gray(Image image) {
        double[][] grayColorImage = new double[image.getHeight()][image.getWidth()];
        for (int row = 0; row < image.getHeight(); row++) {
            for (int col = 0; col < image.getWidth(); col++) {
                Color color = image.getPixel(row, col);
                double grayPixel = RGB2Gray(color);
                grayColorImage[row][col] = grayPixel;
            }
        }

        return grayColorImage;
    }

    /**
     * Converts a single pixel from RGB to grayscale using predefined coefficients.
     *
     * @param color The color of the pixel to convert.
     * @return The grayscale value of the pixel.
     */
    private static double RGB2Gray(Color color) {
        return color.getRed() * COEFFICIENT_RED + color.getGreen() * COEFFICIENT_GREEN +
                color.getBlue() * COEFFICIENT_BLUE;
    }

    /**
     * Finds the nearest power of two for a given number.
     *
     * @param number The number to find the nearest power of two for.
     * @return The nearest power of two.
     */
    private static int nearestPowerOfTwo(int number) {
        return (int) Math.pow(2, Math.ceil(log2(number)));
    }


    /**
     * Calculates the base 2 logarithm of a given number.
     *
     * @param number The number to calculate the log base 2 for.
     * @return The log base 2 of the number.
     */
    private static double log2(int number) {
        return Math.log(number) / Math.log(2);
    }

    /**
     * Extracts a sub-image from the given image.
     *
     * @param image               The original image.
     * @param topLeftCornerRow    The row index of the top-left corner of the sub-image.
     * @param topLeftCornerColumn The column index of the top-left corner of the sub-image.
     * @param squareEdge          The length of the edges of the square sub-image.
     * @return A new Image object representing the sub-image.
     */
    private static Image extractSubImage(Image image, int topLeftCornerRow, int topLeftCornerColumn,
                                         int squareEdge) {
        Color[][] colors = new Color[squareEdge][squareEdge];

        for (int row = 0; row < squareEdge; row++) {
            for (int column = 0; column < squareEdge; column++) {
                colors[row][column] = image.getPixel(topLeftCornerRow + row, topLeftCornerColumn
                        + column);
            }
        }

        return new Image(colors, squareEdge, squareEdge);
    }

}
