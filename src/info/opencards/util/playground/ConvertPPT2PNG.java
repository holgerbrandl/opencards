package info.opencards.util.playground;

import org.apache.poi.hslf.usermodel.HSLFSlide;
import org.apache.poi.hslf.usermodel.HSLFSlideShow;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * Document me!
 *
 * @author Holger Brandl
 */
public class ConvertPPT2PNG {


    public static void main(String[] args) throws IOException {
        FileInputStream is = new FileInputStream("/Users/brandl/Dropbox/private/oc2/testdata/experimental design.ppt");
//        FileInputStream is = new FileInputStream("/Users/brandl/Dropbox/private/oc2/testdata/Presentation5.ppt");

        HSLFSlideShow ppt = new HSLFSlideShow(is);


        is.close();

        Dimension pgsize = ppt.getPageSize();

        java.util.List<HSLFSlide> slides = ppt.getSlides();

        for (int i = 0; i < slides.size(); i++) {

            BufferedImage img = new BufferedImage(pgsize.width, pgsize.height,
                    BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = img.createGraphics();
            //clear the drawing area
            graphics.setPaint(Color.white);
            graphics.fill(new Rectangle2D.Float(0, 0, pgsize.width, pgsize.height));

            //render
            HSLFSlide slide1 = slides.get(i);

            slide1.draw(graphics);

            //save the output
            FileOutputStream out = new FileOutputStream("slide-" + (i + 1) + slide1.getTitle() + ".png");
            javax.imageio.ImageIO.write(img, "png", out);
            out.close();
        }
    }

}
