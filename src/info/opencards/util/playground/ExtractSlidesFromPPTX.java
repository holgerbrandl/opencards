package info.opencards.util.playground;

import org.apache.poi.hslf.model.Slide;
import org.apache.poi.hslf.usermodel.SlideShow;

import java.io.FileInputStream;
import java.io.IOException;


/**
 * Document me!
 *
 * @author Holger Brandl
 */
public class ExtractSlidesFromPPTX {


    public static void main(String[] args) throws IOException {
//        XMLSlideShow ppt = new XMLSlideShow();
        FileInputStream is = new FileInputStream("/Users/brandl/Dropbox/private/oc2/testdata/experimental design.ppt");
        SlideShow ppt = new SlideShow(is);

        for (Slide xslfSlide : ppt.getSlides()) {
            System.out.println(xslfSlide.getTitle());
        }

//        XSLFSlide slide getTitle= ppt.getSlides()[0];0

//         new org.apache.poi.hslf.extractor.PowerPointExtractor("xslf-demo.pptx").getSlides
    }
}
