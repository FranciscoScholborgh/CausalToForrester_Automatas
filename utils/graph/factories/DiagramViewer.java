/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils.graph.factories;

import java.awt.Color;
import java.awt.Point;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Scene;
import utils.graph.events.SelectEventProvider;

/**
 *
 * @author frank
 */
public abstract class DiagramViewer extends Scene{
    
    private LayerWidget mainLayer;
    
    private WidgetAction moveAction;
    
    //private WidgetAction resizeAction;
        
    private WidgetAction selectAction;

    public DiagramViewer() {
        this.setBackground (Color.WHITE);
        
        this.mainLayer = new LayerWidget (this);
        this.addChild (mainLayer);

        LayerWidget interractionLayer = new LayerWidget (this);
        this.addChild (interractionLayer);
        
        //this.resizeAction = ActionFactory.createAlignWithResizeAction (mainLayer, interractionLayer, null);
        this.moveAction = ActionFactory.createAlignWithMoveAction (mainLayer, interractionLayer, null);
        this.selectAction = ActionFactory.createSelectAction (new SelectEventProvider ());
    }
    
    public abstract void createLabel (String label, Point location);

    public LayerWidget getMainLayer() {
        return mainLayer;
    }

    public void setMainLayer(LayerWidget mainLayer) {
        this.mainLayer = mainLayer;
    }

    public WidgetAction getMoveAction() {
        return moveAction;
    }

    public void setMoveAction(WidgetAction moveAction) {
        this.moveAction = moveAction;
    }
    
    /*
    public WidgetAction getResizeAction() {
        return resizeAction;
    }

    public void setResizeAction(WidgetAction resizeAction) {
        this.resizeAction = resizeAction;
    }*/

    public WidgetAction getSelectAction() {
        return selectAction;
    }

    public void setSelectAction(WidgetAction selectAction) {
        this.selectAction = selectAction;
    }
    
}
