package ascii_art;

import image.Image;
import image.ImageManipulator;
import image_char_matching.SubImgCharMatcher;

/**
 * Represents the core algorithm for generating ASCII art from an image. This class takes an image,
 * applies a specified resolution, and utilizes a SubImgCharMatcher to map parts of the image to characters
 * based on their brightness levels to create ASCII art.
 */
public class AsciiArtAlgorithm {

    private Image image;
    private int resolution;
    private SubImgCharMatcher subImgCharMatcher;

    /**
     * Constructs an instance of the AsciiArtAlgorithm with a specified image, resolution, and a
     * SubImgCharMatcher.
     * The SubImgCharMatcher is used to map different brightness levels in the image to specific characters.
     *
     * @param image         The image to be converted into ASCII art.
     * @param resolution    The resolution to be used for the ASCII art, influencing how detailed the final
     *                      art will be.
     * @param subImgCharMatcher The SubImgCharMatcher instance responsible for matching image brightness
     *                          levels to
     *                      characters.
     */
    public AsciiArtAlgorithm(Image image, int resolution, SubImgCharMatcher subImgCharMatcher) {
        this.image = image;
        this.resolution = resolution;
        this.subImgCharMatcher = subImgCharMatcher;
    }

    /**
     * Executes the algorithm to convert the specified image into ASCII art. The process involves padding the
     * image,
     * splitting it according to the specified resolution, and then converting each segment into a character
     * that
     * represents its average brightness. The SubImgCharMatcher is used to find the best matching character
     * for
     * each segment.
     *
     * @return A 2D character array representing the ASCII art generated from the image.
     */
    public char[][] run() {
        Image paddedImage = ImageManipulator.padImage(image);
        Image[][] splittedImage = ImageManipulator.splitImage(paddedImage, resolution);
        char[][] art = new char[splittedImage.length][splittedImage[0].length];

        for (int row = 0; row < splittedImage.length; row++) {
            for (int col = 0; col < splittedImage[row].length; col++) {
                double subImageBrightness = ImageManipulator.getImageBrightness(splittedImage[row][col]);
                art[row][col] = subImgCharMatcher.getCharByImageBrightness(subImageBrightness);
            }
        }
        return art;
    }
}
