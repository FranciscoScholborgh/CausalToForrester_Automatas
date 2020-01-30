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
import org.netbeans.api.visual.graph.GraphScene;
import org.netbeans.api.visual.widget.BirdViewController;
import org.netbeans.api.visual.widget.LayerWidget;
import utils.graph.events.DeleteEventProvider;
import utils.graph.events.SelectEventProvider;

/**
 *
 * @author frank
 */
public abstract class DiagramViewer extends GraphScene.StringGraph{
    
    private final LayerWidget mainLayer;
    
    private final LayerWidget interractionLayer;
    
    private final LayerWidget connectionLayer;
    
    private final WidgetAction moveAction;
    
    //private WidgetAction resizeAction;
        
    private final WidgetAction selectAction;
    
    private final WidgetAction deleteAction;
       
    private final BirdViewController birdViewController;
    
    private String relationType;

    public DiagramViewer() {
        this.setBackground (Color.WHITE);
        
        this.mainLayer = new LayerWidget (this);
        this.addChild (mainLayer);

        this.interractionLayer = new LayerWidget (this);
        this.addChild (this.interractionLayer);
        
        this.connectionLayer = new LayerWidget (this);
        this.addChild (this.connectionLayer);
        
        //this.resizeAction = ActionFactory.createAlignWithResizeAction (mainLayer, interractionLayer, null);
        this.moveAction = ActionFactory.createAlignWithMoveAction (mainLayer, interractionLayer, null);
        this.selectAction = ActionFactory.createSelectAction (new SelectEventProvider (), false);
        this.deleteAction = ActionFactory.createSelectAction(new DeleteEventProvider(), false);
        this.getActions().addAction(ActionFactory.createZoomAction ());
        this.getActions().addAction(ActionFactory.createPanAction());
        
        this.birdViewController = this.createBirdView();
    }
    
    public abstract void createLabel (String label, Point location);
    
    public LayerWidget getMainLayer() {
        return mainLayer;
    }

    public LayerWidget getInterractionLayer() {
        return interractionLayer;
    }

    public LayerWidget getConnectionLayer() {
        return connectionLayer;
    }

    public WidgetAction getMoveAction() {
        return moveAction;
    }

    public WidgetAction getSelectAction() {
        return selectAction;
    }

    public WidgetAction getDeleteAction() {
        return deleteAction;
    }

    public BirdViewController getBirdViewController() {
        return birdViewController;
    }

    public String getRelationType() {
        return relationType;
    }

    public void setRelationType(String relationType) {
        this.relationType = relationType;
    }
    
    public void enable_birdview(boolean enable) {
        if(enable){
            this.birdViewController.show();
        } else {
            this.birdViewController.hide();
        }
    }
}
