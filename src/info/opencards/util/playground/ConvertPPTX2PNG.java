package info.opencards.util.playground;

import org.apache.poi.hslf.model.Slide;
import org.apache.poi.hslf.usermodel.SlideShow;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;

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
public class ConvertPPTX2PNG {


    public static void main(String[] args) throws IOException, InvalidFormatException {
        FileInputStream is = new FileInputStream("/Users/brandl/Dropbox/private/oc2/testdata/experimental design.pptx");

        XMLSlideShow ppt2 = new XMLSlideShow(OPCPackage.open("/Users/brandl/Dropbox/private/oc2/testdata/experimental design.pptx"));
        XSLFSlide slide1 = ppt2.getSlides()[0];
//        slide1.get

        SlideShow ppt = new SlideShow(is);
//
        Slide slide2 = ppt.getSlides()[1];


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
            slide[i].draw(graphics);

            //save the output
            FileOutputStream out = new FileOutputStream("slide-" + (i + 1) + ".png");
            javax.imageio.ImageIO.write(img, "png", out);
            out.close();
        }
    }

}
