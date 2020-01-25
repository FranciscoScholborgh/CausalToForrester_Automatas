/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils.graph.factories;

import java.awt.Point;
import java.util.Optional;
import javafx.application.Platform;
import javafx.scene.control.TextInputDialog;
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
    
    private final WidgetAction createAction;

    public CausalDiagramEditor() {
        super();
        this.renameAction = ActionFactory.createInplaceEditorAction (new RenameEventProvider ());
        this.createAction = ActionFactory.createSelectAction (new CreateProvider ());
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

    public void enable_insertVariable (boolean enable) {
        if(enable) {
            this.getActions ().addAction (this.createAction);
        } else {
            this.getActions ().removeAction(this.createAction);
        }
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
            Platform.runLater(() -> {
                TextInputDialog dialog = new TextInputDialog("");
                dialog.setHeaderText(null);
                dialog.setTitle("Nombre variable");
                
                Optional<String> result = dialog.showAndWait();
                String entered = "none.";
                
                if (result.isPresent()) {
                    createLabel (result.get(), localLocation);
                }
            });
        }

    }  
}
