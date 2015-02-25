package info.opencards.util.playground;

import org.apache.poi.hslf.model.Slide;
import org.apache.poi.hslf.usermodel.SlideShow;

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

        SlideShow ppt = new SlideShow(is);


        is.close();

        Dimension pgsize = ppt.getPageSize();

        Slide[] slide = ppt.getSlides();

        for (int i = 0; i < slide.length; i++) {

            BufferedImage img = new BufferedImage(pgsize.width, pgsize.height,
                    BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = img.createGraphics();
            //clear the drawing area
            graphics.setPaint(Color.white);
            graphics.fill(new Rectangle2D.Float(0, 0, pgsize.width, pgsize.height));

            //render
            Slide slide1 = slide[i];

            slide1.draw(graphics);

            //save the output
            FileOutputStream out = new FileOutputStream("slide-" + (i + 1) + slide1.getTitle() + ".png");
            javax.imageio.ImageIO.write(img, "png", out);
            out.close();
        }
    }

}
