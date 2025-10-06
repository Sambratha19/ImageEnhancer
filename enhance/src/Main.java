import java.awt.image.*;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        File inputFolder = new File("images");
        File outputFolder = new File("Output");

        if (!outputFolder.exists()) outputFolder.mkdirs();

        File[] files = inputFolder.listFiles((dir, name) ->
                name.toLowerCase().endsWith(".jpg") ||
                        name.toLowerCase().endsWith(".jpeg") ||
                        name.toLowerCase().endsWith(".png")
        );

        if (files == null || files.length == 0) {
            System.out.println("⚠️ No images found!");
            return;
        }

        for (File file : files) {
            try {
                System.out.println("Processing: " + file.getName());
                BufferedImage img = ImageIO.read(file);

                // Step 1️⃣: Brightness & Contrast
                float scaleFactor = 1.15f;
                float offset = 25f;
                RescaleOp rescale = new RescaleOp(scaleFactor, offset, null);
                BufferedImage brightImage = rescale.filter(img, null);

                // Step 2️⃣: Apply Gaussian blur (simulate with ConvolveOp)
                float[] blurKernel = {
                        1f/16f, 2f/16f, 1f/16f,
                        2f/16f, 4f/16f, 2f/16f,
                        1f/16f, 2f/16f, 1f/16f
                };
                Kernel gaussianKernel = new Kernel(3, 3, blurKernel);
                ConvolveOp blur = new ConvolveOp(gaussianKernel, ConvolveOp.EDGE_NO_OP, null);
                BufferedImage blurred = blur.filter(brightImage, null);

                // Step 3️⃣: Unsharp Mask = Original + α*(Original - Blurred)
                float alpha = 1.5f; // Sharpen strength (1.0 to 2.0)
                BufferedImage sharpened = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());

                for (int y = 0; y < img.getHeight(); y++) {
                    for (int x = 0; x < img.getWidth(); x++) {
                        int rgbOrig = brightImage.getRGB(x, y);
                        int rgbBlur = blurred.getRGB(x, y);

                        int r = clamp((int)(((rgbOrig >> 16 & 0xFF) - (rgbBlur >> 16 & 0xFF)) * alpha + (rgbOrig >> 16 & 0xFF)));
                        int g = clamp((int)(((rgbOrig >> 8 & 0xFF) - (rgbBlur >> 8 & 0xFF)) * alpha + (rgbOrig >> 8 & 0xFF)));
                        int b = clamp((int)(((rgbOrig & 0xFF) - (rgbBlur & 0xFF)) * alpha + (rgbOrig & 0xFF)));

                        int newRGB = (0xFF << 24) | (r << 16) | (g << 8) | b;
                        sharpened.setRGB(x, y, newRGB);
                    }
                }

                // Step 4️⃣: Save output
                File outputFile = new File(outputFolder, file.getName());
                ImageIO.write(sharpened, "jpg", outputFile);

                System.out.println("Saved: " + outputFile.getPath());
            } catch (IOException e) {
                System.out.println("Error processing " + file.getName() + ": " + e.getMessage());
            }
        }

        System.out.println("\n🎯 All images enhanced successfully!");
    }

    private static int clamp(int value) {
        return Math.max(0, Math.min(255, value));
    }
}
