package com.songoda.ultimateclaims.utils.spigotmaps.util;

import com.songoda.ultimateclaims.utils.spigotmaps.rendering.SimpleTextRenderer;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A utility class containing several methods to adjust and get images fitting the Minecraft map format.
 *
 * @author Johnny_JayJay (https://www.github.com/JohnnyJayJay)
 */
public final class ImageTools {

    /**
     * A {@link Dimension} representing the proportions of a Minecraft map.
     * Do not mutate this value as it might break this library.
     */
    public static final Dimension MINECRAFT_MAP_SIZE = new Dimension(128, 128);

    private ImageTools() {
    }

    /**
     * Tries to read an image from a URL using an explicit user-agent.
     * This might be useful to avoid 401 - Unauthorized responses.
     *
     * @param url a non-{@code null} URL to fetch the image from.
     * @return the image or {@code null} if no image could be created.
     * @throws IOException see {@link ImageIO#read(URL)}.
     */
    public static BufferedImage loadWithUserAgentFrom(URL url) throws IOException {
        URLConnection connection = url.openConnection();
        connection.addRequestProperty("User-Agent", "Mozilla/5.0");
        try (InputStream inputStream = connection.getInputStream()) {
            return ImageIO.read(inputStream);
        }
    }

    /**
     * Creates a {@link BufferedImage} with the size of {@link #MINECRAFT_MAP_SIZE}.
     * The whole image will have one color. This can be used as a background for {@link SimpleTextRenderer}s, for example.
     *
     * @param color the non-{@code null} {@link Color} this image will have.
     * @return a never-null image.
     */
    public static BufferedImage createSingleColoredImage(Color color) {
        BufferedImage image = new BufferedImage(MINECRAFT_MAP_SIZE.width, MINECRAFT_MAP_SIZE.height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setPaint(color);
        graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
        graphics.dispose();
        return image;
    }

    /**
     * Resizes an image to the size specified in {@link #MINECRAFT_MAP_SIZE}.
     *
     * @param image the non-{@code null} image to resize.
     * @return a new image with the according size.
     */
    public static BufferedImage resizeToMapSize(BufferedImage image) {
        Dimension size = MINECRAFT_MAP_SIZE;
        BufferedImage resized = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = resized.createGraphics();
        graphics.drawImage(image, 0, 0, size.width, size.height, null);
        graphics.dispose();
        return resized;
    }

    /**
     * Takes an image and resizes it in a way that the parts returned by  this method can be put together
     * to form the whole image.
     * The result will then be a square image and the parts will all be of the size specified in
     * {@link #MINECRAFT_MAP_SIZE}.
     * <p>
     * The algorithm will make a square version of the image argument first and then divide it into parts.
     *
     * @param image the non-{@code null} image to be divided.
     * @param crop  true, if the image should be cropped to a square part in the middle (i.e. the image will not be
     *              resized) or false, if the image should be resized (i.e. the whole image will be visible,
     *              but compressed to 1:1). Note that it still might be resized if it is too small.
     * @return a never-null List containing the parts.
     */
    public static List<BufferedImage> divideIntoMapSizedParts(BufferedImage image, boolean crop) {
        return Arrays.asList(divideIntoParts(crop ? cropToMapDividableSquare(image) : scaleToMapDividableSquare(image)));
    }

    private static BufferedImage[] divideIntoParts(BufferedImage image) {
        Dimension partSize = MINECRAFT_MAP_SIZE;
        int linearParts = image.getWidth() / partSize.width;
        List<BufferedImage> result = new ArrayList<>(linearParts * linearParts);
        for (int i = 0; i < linearParts; i++) {
            for (int j = 0; j < linearParts; j++) {
                result.add(image.getSubimage(partSize.width * i, partSize.height * i, partSize.width, partSize.height));
            }
        }
        return result.toArray(new BufferedImage[0]);
    }

    private static BufferedImage scaleToMapDividableSquare(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int measure = width > height ? width + (width % MINECRAFT_MAP_SIZE.width) : height + (height % MINECRAFT_MAP_SIZE.height);
        BufferedImage squared = new BufferedImage(measure, measure, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = squared.createGraphics();
        graphics.drawImage(image, 0, 0, squared.getWidth(), squared.getHeight(), null);
        graphics.dispose();
        return squared;
    }

    private static BufferedImage cropToMapDividableSquare(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        Dimension size = MINECRAFT_MAP_SIZE;
        if (width < size.width && height < size.height) {
            return resizeToMapSize(image);
        } else if (width < size.width) {
            return resizeToMapSize(image.getSubimage(0, (height - size.height) / 2, size.width, size.height));
        } else if (height < size.height) {
            return resizeToMapSize(image.getSubimage((width - size.width) / 2, 0, size.width, size.height));
        } else {
            int measure = width < height
                    ? size.width * (width / size.width)
                    : size.height * (height / size.height);
            return copyOf(image).getSubimage((width - measure) / 2, (height - measure) / 2, measure, measure);
        }
    }

    /**
     * Creates a copy of a given {@link BufferedImage} by creating a new one and populating
     * it with the content of the old one.
     *
     * @param image the non-{@code null} image to make a copy of.
     * @return a copy of the image.
     */
    public static BufferedImage copyOf(BufferedImage image) {
        BufferedImage copy = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        Graphics2D graphics = copy.createGraphics();
        graphics.drawImage(image, 0, 0, null);
        graphics.dispose();
        return copy;
    }


}
