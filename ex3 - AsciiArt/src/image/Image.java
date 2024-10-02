package image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * A package-private class of the package image.
 *
 * @author Dan Nirel
 */
public class Image {

    /**
     * A 2D array of color objects representing the pixel data of the image.
     * Each element in the array corresponds to a pixel's color in the image.
     */
    private final Color[][] pixelArray;

    /**
     * The width of the image in pixels. This represents the number of columns in the pixel array.
     */
    private final int width;

    /**
     * The height of the image in pixels. This represents the number of rows in the pixel array.
     */
    private final int height;

    /**
     * Constructs an Image object by loading an image from the specified file.
     * The image is read into a 2D array of color objects representing the pixel data.
     *
     * @param filename The path to the image file to be loaded.
     * @throws IOException If an error occurs during reading the image file.
     */
    public Image(String filename) throws IOException {
        BufferedImage im = ImageIO.read(new File(filename));
        width = im.getWidth();
        height = im.getHeight();


        pixelArray = new Color[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                pixelArray[i][j] = new Color(im.getRGB(j, i));
            }
        }
    }

    /**
     * Constructs an Image object using a provided 2D array of color objects, width, and height.
     * This constructor allows direct creation of an Image object from pixel data without reading from a file.
     *
     * @param pixelArray A 2D array of color objects representing the image's pixel data.
     * @param width The width of the image in pixels.
     * @param height The height of the image in pixels.
     */
    public Image(Color[][] pixelArray, int width, int height) {
        this.pixelArray = pixelArray;
        this.width = width;
        this.height = height;
    }

    /**
     * Retrieves the width of the image in pixels.
     * @return The width of the image.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Retrieves the height of the image in pixels.
     *
     * @return The height of the image.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Retrieves the color of a specific pixel in the image.
     *
     * @param x The x-coordinate of the pixel (row index).
     * @param y The y-coordinate of the pixel (column index).
     * @return The color of the pixel at the specified coordinates.
     */
    public Color getPixel(int x, int y) {
        return pixelArray[x][y];
    }

    /**
     * Saves the current image to a file with the specified name. The image is saved as a JPEG file.
     *
     * @param fileName The name of the file to save the image to, without the file extension.
     * @throws RuntimeException If an error occurs during writing the file.
     */
    public void saveImage(String fileName) {
        // Initialize BufferedImage, assuming Color[][] is already properly populated.
        BufferedImage bufferedImage = new BufferedImage(pixelArray[0].length, pixelArray.length,
                BufferedImage.TYPE_INT_RGB);
        // Set each pixel of the BufferedImage to the color from the Color[][].
        for (int x = 0; x < pixelArray.length; x++) {
            for (int y = 0; y < pixelArray[x].length; y++) {
                bufferedImage.setRGB(y, x, pixelArray[x][y].getRGB());
            }
        }
        File outputfile = new File(fileName + ".jpeg");
        try {
            ImageIO.write(bufferedImage, "jpeg", outputfile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
