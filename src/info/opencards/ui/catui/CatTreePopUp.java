package info.opencards.ui.catui;


import info.opencards.Utils;
import info.opencards.core.categories.CategoryUtils;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


/**
 * DOCUMENT ME!
 *
 * @author Holger Brandl
 */
class CatTreePopUp extends MouseAdapter {


    private JTree parent;

    private JPopupMenu menu;


    public CatTreePopUp(CategoryTree parent) {
        assert parent != null;
        this.parent = parent;
        menu = new JPopupMenu();

        //setup the context menu
        PasteCategoryAction pasteAction = new PasteCategoryAction(parent);

        menu.add(new JMenuItem(new CreateCategoryAction(parent)));
        menu.add(new JMenuItem(new AddCardSetAction(parent, pasteAction)));
        menu.add(new JMenuItem(new PopulateSubCatTreeFromDirectoryAction(parent)));

        menu.addSeparator();

        menu.add(new JMenuItem(new CutCategoryAction(parent, pasteAction)));
        menu.add(new JMenuItem(new CopyCategoryAction(parent, pasteAction)));
        menu.add(new JMenuItem(pasteAction));
        menu.addSeparator();

        menu.add(new JMenuItem(new DeleteCategoryAction(parent)));

        // flatten was removed after discussion with fernando, where it turned out that's not clear what it means
//        menu.add(new JMenuItem(new FlattenCategoryAction(parent)));
        menu.add(new JMenuItem(new RenameCategoryAction(parent)));

        menu.addSeparator();
        JCheckBoxMenuItem recurseChildsItem = new JCheckBoxMenuItem(new ResolveChildsAction(parent));
        recurseChildsItem.setSelected(Utils.getPrefs().getBoolean(CategoryUtils.INCLUDE_CHILDS, false));
        menu.add(recurseChildsItem);

    }


    public void mouseReleased(java.awt.event.MouseEvent e) {
        if (!e.isPopupTrigger())
            return;

//                JOptionPane.showConfirmDialog(null, "mouse event " + e);

        Point pt = e.getPoint();
        TreePath pathForLocation = parent.getPathForLocation((int) pt.getX(), (int) pt.getY());

        if (pathForLocation != null) {
//            Object object = pathForLocation.getLastPathComponent();
//            Category category = (Category) ((DefaultMutableTreeNode) object).getUserObject();

            parent.setSelectionPath(pathForLocation);
            menu.show(parent, e.getX(), e.getY());  //Have the tree display the pop up menu here
        }
    }


    public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            int row = parent.getRowForLocation(e.getX(), e.getY());
            if (row > -1) {
                parent.setSelectionRow(row);
            }
        }
    }


    /**
     * Invoked when a mouse button has been pressed on a component.
     */
    public void mousePressed(MouseEvent e) {
        mouseReleased(e);
    }
}

