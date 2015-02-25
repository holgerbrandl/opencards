package info.opencards.pptintegration;

import info.opencards.Utils;
import info.opencards.core.CardFile;
import org.apache.poi.hslf.model.AutoShape;
import org.apache.poi.hslf.model.MasterSheet;
import org.apache.poi.hslf.model.Shape;
import org.apache.poi.hslf.model.Slide;
import org.apache.poi.hslf.record.TextHeaderAtom;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;


/**
 * Allows to render incomplete powerpoint slides (given as poi-objects)
 *
 * @author Holger Brandl
 */
public class PPTSlideRenderPanel extends JPanel {


    private Slide slide;
    private boolean showTitleShape;
    private boolean showContent;

    private Dimension baseSize;
    private CardFile curCardFile;


    public PPTSlideRenderPanel() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                if (mouseEvent.getClickCount() == 2) {
                    // show the slide in the system's ppt-editor
                    OpenCurrentSlide.showCurrentSlideInPPTEditor(curCardFile, slide);
                }
            }
        });
    }


    private void drawSlidesPartially(Graphics2D graphics, Slide slide) {
        MasterSheet master = slide.getMasterSheet();
        if (slide.getFollowMasterBackground()) master.getBackground().draw(graphics);
        if (slide.getFollowMasterObjects()) {
            org.apache.poi.hslf.model.Shape[] sh = master.getShapes();
            for (org.apache.poi.hslf.model.Shape aSh : sh) {
                if (MasterSheet.isPlaceholder(aSh)) continue;

                aSh.draw(graphics);
            }
        }


        Shape titleShape = getTitleShape(slide);

        for (Shape shape : slide.getShapes()) {
            boolean isTitleShape = shape.getShapeId() == titleShape.getShapeId();

            if (isTitleShape && showTitleShape) {
                shape.draw(graphics);
            }

            if (!isTitleShape && showContent) {
                shape.draw(graphics);
            }
        }
    }


    Shape getTitleShape(Slide slide) {
        String slideTitle = slide.getTitle();

        for (org.apache.poi.hslf.model.Shape shape : slide.getShapes()) {
            if (shape instanceof AutoShape) {
                AutoShape autoShape = (AutoShape) shape;
                if (autoShape.getText() != null && autoShape.getText().equals(slideTitle)) {
                    int type = autoShape.getTextRun().getRunType();
                    if (type == TextHeaderAtom.CENTER_TITLE_TYPE || type == TextHeaderAtom.TITLE_TYPE) {
                        return shape;
                    }
                }
            }
        }

        return null;

        // can not work as we don't have a slide title for slides without a title element
//        // if we don't find a title shape than use the most topwards element as question
//        if(slide.getShapes().length ==0)
//            return null;
//
//        return Collections.max(Arrays.asList(slide.getShapes()), new Comparator<Shape>() {
//            @Override
//            public int compare(Shape o1, Shape o2) {
//                return o1.getAnchor().getCenterY() - o2.getAnchor().getCenterY() < 0 ? -1 : 1;
//            }
//        });
    }


    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g2d = (Graphics2D) graphics;

        if (!Utils.isMacOSX()) {
            RenderingHints renderHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            renderHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHints(renderHints);
        }

        super.paintComponent(graphics);

        if (slide == null) {
            return;
        }

//        System.err.println("dimension: " + getSize());
//        System.err.println("baseSize: " + baseSize);
//        System.err.println("scaling: " + slideScaleTransform);

        AffineTransform slideScaleTransform = new AffineTransform();
        double xScale = getWidth() / baseSize.getWidth();
        double yScale = getHeight() / baseSize.getHeight();
        slideScaleTransform.scale(xScale, yScale);

        g2d.setTransform(slideScaleTransform);
        drawSlidesPartially(g2d, slide);
    }


    public void configure(Slide slide, boolean showTitle, boolean showContent) {
        this.slide = slide;

        this.showTitleShape = showTitle;
        this.showContent = showContent;

        repaint();
    }


    public void setBaseSize(Dimension baseSize) {
        this.baseSize = baseSize;
    }


    public void setCardFile(CardFile cardFile) {
        curCardFile = cardFile;
    }
}
