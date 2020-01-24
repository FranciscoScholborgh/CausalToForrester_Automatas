/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils.graph.factories;

import java.awt.Point;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.SelectProvider;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;
import utils.graph.events.RenameEventProvider;

/**
 *
 * @author frank
 */
public class CausalDiagramEditor extends DiagramViewer{
    
    private final WidgetAction renameAction;

    public CausalDiagramEditor() {
        super();
        this.renameAction = ActionFactory.createInplaceEditorAction (new RenameEventProvider ());
        this.getActions ().addAction (ActionFactory.createSelectAction (new CreateProvider ()));
    }
    
    @Override
    public void createLabel(String label, Point location) {
        LayerWidget mainLayer = super.getMainLayer();
        Scene scene = mainLayer.getScene ();
        Widget widget = new LabelWidget (scene, label);

        widget.setOpaque (true);
        widget.setPreferredLocation (location);

        widget.getActions ().addAction (super.getSelectAction());
        widget.getActions ().addAction (this.renameAction);
        //widget.getActions ().addAction (super.getResizeAction());
        widget.getActions ().addAction (super.getMoveAction());
        
        mainLayer.addChild (widget);
    }
    
    private class CreateProvider implements SelectProvider {

        @Override
        public boolean isAimingAllowed (Widget widget, Point localLocation, boolean invertSelection) {
            return false;
        }

        @Override
        public boolean isSelectionAllowed (Widget widget, Point localLocation, boolean invertSelection) {
            return true;
        }

        @Override
        public void select (Widget widget, Point localLocation, boolean invertSelection) {
            createLabel ("Double-click to rename me", localLocation);
        }

    }
    
}
