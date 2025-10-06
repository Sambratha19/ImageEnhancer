import java.awt.image.*;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            // Load image
            BufferedImage img = ImageIO.read(new File("images/face-8-DSC_6181.JPG"));

            float scaleFactor = 1.2f;
            float offset = 30f;
            RescaleOp rescale = new RescaleOp(scaleFactor, offset, null);
            BufferedImage brightImage = rescale.filter(img, null);


            float[] sharpenKernel = {
                    0.f, -1.f,  0.f,
                    -1.f,  5.f, -1.f,
                    0.f, -1.f,  0.f
            };
            Kernel kernel = new Kernel(3, 3, sharpenKernel);
            ConvolveOp sharpen = new ConvolveOp(kernel);
            BufferedImage finalImage = sharpen.filter(brightImage, null);

            // Save enhanced image
            ImageIO.write(finalImage, "jpg", new File("images/enhanced_output.jpg"));
            System.out.println("Enhanced image saved as enhanced_output.jpg");
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }
    }
}
