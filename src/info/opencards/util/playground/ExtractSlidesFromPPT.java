package info.opencards.util.playground;

import org.apache.poi.hslf.usermodel.HSLFSlide;
import org.apache.poi.hslf.usermodel.HSLFSlideShow;

import java.io.FileInputStream;
import java.io.IOException;


/**
 * Document me!
 *
 * @author Holger Brandl
 */
public class ExtractSlidesFromPPT {


    public static void main(String[] args) throws IOException {
//        XMLSlideShow ppt = new XMLSlideShow();
//        FileInputStream is = new FileInputStream("/Users/brandl/Dropbox/private/oc2/testdata/experimental design.ppt");
        FileInputStream is = new FileInputStream("testdata/testdata 1 reordered slides.ppt");
        HSLFSlideShow ppt = new HSLFSlideShow(is);

        for (HSLFSlide slide : ppt.getSlides()) {
            String slideTitle = slide.getTitle();

            System.err.println("-----------");
            System.err.println(slideTitle);


//            System.err.println("sheetid   : "+slide.getSlideRecord().getSheetId());
//            // does just reflect the slide number
//
//            System.err.println("refsheetid: "+ slide._getSheetRefId());
//
//            System.err.println("atomhah: "+ slide.getSlideRecord().getSlideAtom().toString());
//
//            System.err.println("ppdrawing: "+ slide.getSlideRecord().toString());

            System.err.println(slide.getSlideRecord().getPPDrawing());

            slide.getSlideRecord().getPPDrawing().toString();
            slide.getSlideRecord().getSlideAtom().hashCode();


//        XSLFSlide slide getTitle= ppt.getSlides()[0];0

//         new org.apache.poi.hslf.extractor.PowerPointExtractor("xslf-demo.pptx").getSlides
        }
    }
}
