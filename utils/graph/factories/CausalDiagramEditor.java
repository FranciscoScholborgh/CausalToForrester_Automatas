/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils.graph.factories;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Optional;
import javafx.application.Platform;
import javafx.scene.control.TextInputDialog;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.ConnectProvider;
import org.netbeans.api.visual.action.ConnectorState;
import org.netbeans.api.visual.action.ReconnectProvider;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.anchor.AnchorFactory;
import org.netbeans.api.visual.anchor.AnchorShape;
import org.netbeans.api.visual.anchor.PointShape;
import org.netbeans.api.visual.layout.LayoutFactory;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;
import utils.graph.events.RenameEventProvider;

/**
 *
 * @author frank
 */
public class CausalDiagramEditor extends DiagramViewer{
    
    private ArrayList<LabelWidget> variables;
    
    private final WidgetAction createAction;
    private final WidgetAction connectAction;
    private final WidgetAction reconnectAction;
    private final WidgetAction renameAction;
    
    private long edgeCounter = 0;

    public CausalDiagramEditor () {
        this.variables = new ArrayList<>();
        
        this.createAction = new SceneCreateAction ();
        this.connectAction = ActionFactory.createConnectAction (super.getInterractionLayer(), new SceneConnectProvider (this));
        this.reconnectAction = ActionFactory.createReconnectAction (new SceneReconnectProvider ());
        this.renameAction = ActionFactory.createInplaceEditorAction (new RenameEventProvider ());
           
        this.addChild (super.getMainLayer());
        this.addChild (super.getConnectionLayer());
        this.addChild (super.getInterractionLayer());
    }

    public ArrayList<LabelWidget> getVariables() {
        return variables;
    }

    public void setVariables(ArrayList<LabelWidget> variables) {
        this.variables = variables;
    }

    @Override
    public void createLabel(String node, Point location) {
        System.out.println("Not done");
    }

    private void enable_insertVariable (boolean enable) {
        if(enable) {
            this.getActions ().addAction (this.createAction);
        } else {
            this.getActions ().removeAction(this.createAction);
        }        
    }
    
    private void enable_relationVariables (boolean enable) {
        if(enable) {
            variables.forEach((variable) -> {
                if(!variable.getActions().getActions().contains(this.connectAction)) {
                    variable.getActions().addAction(this.connectAction);
                    variable.getActions().removeAction(this.getMoveAction());
                }           
            });
        } else {
            this.variables.forEach((variable) -> {
                variable.getActions().removeAction(this.connectAction);
                variable.getActions().addAction(super.getMoveAction());
            });
        }
    }
    
    public void activate_insertVariable() {
        this.enable_insertVariable(true);
        this.enable_relationVariables(false);
    }
    
    public void activate_relationVariables(String type) {
        this.enable_insertVariable(false);
        super.setRelationType(type);
        this.enable_relationVariables(true);
    }
    
    public void deactivate_all () {
        this.enable_insertVariable(false);
        this.enable_relationVariables(false);
    }

    @Override
    protected Widget attachNodeWidget (String node) {
        LabelWidget label = new LabelWidget (this, node);
        //label.setBorder (BorderFactory.createLineBorder (4));
        label.getActions ().addAction (createObjectHoverAction ());
        label.getActions ().addAction (this.renameAction);
        label.getActions ().addAction (super.getMoveAction());
        //label.getActions ().addAction (this.connectAction);
        super.getMainLayer().addChild (label);
        this.variables.add(label);
        return label;
    }

    @Override
    protected Widget attachEdgeWidget (String edge) {
        ConnectionWidget connection = new ConnectionWidget (this);
        connection.setTargetAnchorShape (AnchorShape.TRIANGLE_FILLED);
        connection.setEndPointShape (PointShape.SQUARE_FILLED_BIG);
        connection.getActions ().addAction (createObjectHoverAction ());
        connection.getActions ().addAction (createSelectAction ());
        connection.getActions ().addAction (reconnectAction);
        super.getConnectionLayer().addChild (connection);
        return connection;
    }

    @Override
    protected void attachEdgeSourceAnchor (String edge, String oldSourceNode, String sourceNode) {
        Widget w = sourceNode != null ? findWidget (sourceNode) : null;
        ((ConnectionWidget) findWidget (edge)).setSourceAnchor (AnchorFactory.createRectangularAnchor (w));
    }

    @Override
    protected void attachEdgeTargetAnchor (String edge, String oldTargetNode, String targetNode) {
        Widget w = targetNode != null ? findWidget (targetNode) : null;
        ((ConnectionWidget) findWidget (edge)).setTargetAnchor (AnchorFactory.createRectangularAnchor (w));
    }

    private class SceneCreateAction extends WidgetAction.Adapter {

        @Override
        public WidgetAction.State mousePressed (Widget widget, WidgetAction.WidgetMouseEvent event) {
            if (event.getClickCount () == 1)
                if (event.getButton () == MouseEvent.BUTTON1 || event.getButton () == MouseEvent.BUTTON2) {
                    Platform.runLater(() -> {
                        TextInputDialog dialog = new TextInputDialog("");
                        dialog.setHeaderText(null);
                        dialog.setTitle("Nombre variable");

                        Optional<String> result = dialog.showAndWait();
                        String entered = "none.";

                        if (result.isPresent()) {
                            addNode (result.get()).setPreferredLocation (widget.convertLocalToScene (event.getPoint ()));
                        }
                    });
                    return WidgetAction.State.CONSUMED;
                }
            return WidgetAction.State.REJECTED;
        }
    }

    private class SceneConnectProvider implements ConnectProvider {

        private String source = null;
        private String target = null;
        private DiagramViewer scene;

        public SceneConnectProvider(DiagramViewer scene) {
            this.scene = scene;
        }
        
        @Override
        public boolean isSourceWidget (Widget sourceWidget) {
            Object object = findObject (sourceWidget);
            source = isNode (object) ? (String) object : null;
            return source != null;
        }

        @Override
        public ConnectorState isTargetWidget (Widget sourceWidget, Widget targetWidget) {
            Object object = findObject (targetWidget);
            target = isNode (object) ? (String) object : null;
            if (target != null)
                return ! source.equals (target) ? ConnectorState.ACCEPT : ConnectorState.REJECT_AND_STOP;
            return object != null ? ConnectorState.REJECT_AND_STOP : ConnectorState.REJECT;
        }

        @Override
        public boolean hasCustomTargetWidgetResolver (Scene scene) {
            return false;
        }

        @Override
        public Widget resolveTargetWidget (Scene scene, Point sceneLocation) {
            return null;
        }

        @Override
        public void createConnection (Widget sourceWidget, Widget targetWidget) {
            //String edge = "edge" + edgeCounter ++;
            //addEdge (edge);
            //setEdgeSource (edge, source);
            //setEdgeTarget (edge, target);
            ConnectionWidget connection = new ConnectionWidget (this.scene.getScene());
            connection.setSourceAnchor (AnchorFactory.createDirectionalAnchor (sourceWidget, AnchorFactory.DirectionalAnchorKind.HORIZONTAL));
            connection.setTargetAnchor (AnchorFactory.createDirectionalAnchor (targetWidget, AnchorFactory.DirectionalAnchorKind.HORIZONTAL));
            connection.setTargetAnchorShape (AnchorShape.TRIANGLE_FILLED);
            connection.setPaintControlPoints (true);
            connection.setControlPointShape (PointShape.SQUARE_FILLED_BIG);
            //connection.setRouter (RouterFactory.createOrthogonalSearchRouter (getMainLayer()));
            connection.getActions ().addAction (ActionFactory.createAddRemoveControlPointAction ());
            connection.getActions ().addAction (ActionFactory.createFreeMoveControlPointAction ());
            LabelWidget signo = new LabelWidget (scene.getScene(), scene.getRelationType());
            signo.setOpaque (true);
            connection.addChild (signo);
            connection.setConstraint (signo, LayoutFactory.ConnectionWidgetLayoutAlignment.BOTTOM_LEFT, -25);
            getConnectionLayer().addChild (connection);
        }
    }

    private class SceneReconnectProvider implements ReconnectProvider {

        String edge;
        String originalNode;
        String replacementNode;

        @Override
        public void reconnectingStarted (ConnectionWidget connectionWidget, boolean reconnectingSource) {
        }

        @Override
        public void reconnectingFinished (ConnectionWidget connectionWidget, boolean reconnectingSource) {
        }

        @Override
        public boolean isSourceReconnectable (ConnectionWidget connectionWidget) {
            Object object = findObject (connectionWidget);
            edge = isEdge (object) ? (String) object : null;
            originalNode = edge != null ? getEdgeSource (edge) : null;
            return originalNode != null;
        }

        @Override
        public boolean isTargetReconnectable (ConnectionWidget connectionWidget) {
            Object object = findObject (connectionWidget);
            edge = isEdge (object) ? (String) object : null;
            originalNode = edge != null ? getEdgeTarget (edge) : null;
            return originalNode != null;
        }

        @Override
        public ConnectorState isReplacementWidget (ConnectionWidget connectionWidget, Widget replacementWidget, boolean reconnectingSource) {
            Object object = findObject (replacementWidget);
            replacementNode = isNode (object) ? (String) object : null;
            if (replacementNode != null)
                return ConnectorState.ACCEPT;
            return object != null ? ConnectorState.REJECT_AND_STOP : ConnectorState.REJECT;
        }

        @Override
        public boolean hasCustomReplacementWidgetResolver (Scene scene) {
            return false;
        }

        @Override
        public Widget resolveReplacementWidget (Scene scene, Point sceneLocation) {
            return null;
        }
        
        @Override
        public void reconnect (ConnectionWidget connectionWidget, Widget replacementWidget, boolean reconnectingSource) {
            if (replacementWidget == null)
                removeEdge (edge);
            else if (reconnectingSource)
                setEdgeSource (edge, replacementNode);
            else
                setEdgeTarget (edge, replacementNode);
        }
    }
}

