/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils.graph.factories;

import java.awt.Point;
import java.awt.event.MouseEvent;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.ConnectProvider;
import org.netbeans.api.visual.action.ConnectorState;
import org.netbeans.api.visual.action.ReconnectProvider;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.anchor.AnchorFactory;
import org.netbeans.api.visual.anchor.AnchorShape;
import org.netbeans.api.visual.anchor.PointShape;
import org.netbeans.api.visual.border.BorderFactory;
import org.netbeans.api.visual.widget.ConnectionWidget;
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
    
    private final LayerWidget mainLayer;
    private final LayerWidget connectionLayer;
    private final LayerWidget interractionLayer;

    private final WidgetAction createAction;
    private final WidgetAction connectAction;
    private final WidgetAction reconnectAction;
    private final WidgetAction renameAction;

    private long nodeCounter = 0;
    private long edgeCounter = 0;

    public CausalDiagramEditor () {
        this.mainLayer = super.getMainLayer();
        this.interractionLayer = super.getInterractionLayer();
        this.connectionLayer = super.getConnectionLayer();
        
        this.createAction = new SceneCreateAction ();
        this.connectAction = ActionFactory.createConnectAction (this.interractionLayer, new SceneConnectProvider ());
        this.reconnectAction = ActionFactory.createReconnectAction (new SceneReconnectProvider ());
        this.renameAction = ActionFactory.createInplaceEditorAction (new RenameEventProvider ());
        
        this.addChild (this.mainLayer);
        this.addChild (this.connectionLayer);
        this.addChild (this.interractionLayer);

        //this.getActions ().addAction (createAction);

    }
    
    @Override
    public void createLabel(String label, Point location) {
        LayerWidget mainLayer = super.getMainLayer();
        Scene scene = mainLayer.getScene ();
        Widget widget = new LabelWidget (scene, label);

        widget.setOpaque (true);
        widget.setPreferredLocation (location);
        widget.getActions ().addAction (createObjectHoverAction ());
        widget.setBorder (BorderFactory.createLineBorder (4));

        //widget.getActions ().addAction (super.getSelectAction());
        widget.getActions ().addAction (this.renameAction);
        //widget.getActions ().addAction (super.getResizeAction());
        //widget.getActions ().addAction (super.getMoveAction());
        widget.getActions ().addAction (this.connectAction);
        
        
        mainLayer.addChild (widget);
    }

    public void enable_insertVariable (boolean enable) {
        if(enable) {
            this.getActions ().addAction (this.createAction);
        } else {
            this.getActions ().removeAction(this.createAction);
        }
    }
    

    protected Widget attachNodeWidget (String node) {
        LabelWidget label = new LabelWidget (this, node);
        //label.setBorder (BorderFactory.createLineBorder (4));
        label.getActions ().addAction (createObjectHoverAction ());
        label.getActions ().addAction (this.renameAction);
        //label.getActions ().addAction (super.getMoveAction());
        label.getActions ().addAction (this.connectAction);
        this.mainLayer.addChild (label);
        return label;
    }

    protected Widget attachEdgeWidget (String edge) {
        ConnectionWidget connection = new ConnectionWidget (this);
        connection.setTargetAnchorShape (AnchorShape.TRIANGLE_FILLED);
        connection.setEndPointShape (PointShape.SQUARE_FILLED_BIG);
        connection.getActions ().addAction (createObjectHoverAction ());
        connection.getActions ().addAction (createSelectAction ());
        connection.getActions ().addAction (reconnectAction);
        this.connectionLayer.addChild (connection);
        return connection;
    }

    protected void attachEdgeSourceAnchor (String edge, String oldSourceNode, String sourceNode) {
        Widget w = sourceNode != null ? findWidget (sourceNode) : null;
        ((ConnectionWidget) findWidget (edge)).setSourceAnchor (AnchorFactory.createRectangularAnchor (w));
    }

    protected void attachEdgeTargetAnchor (String edge, String oldTargetNode, String targetNode) {
        Widget w = targetNode != null ? findWidget (targetNode) : null;
        ((ConnectionWidget) findWidget (edge)).setTargetAnchor (AnchorFactory.createRectangularAnchor (w));
    }

    private class SceneCreateAction extends WidgetAction.Adapter {

        public WidgetAction.State mousePressed (Widget widget, WidgetAction.WidgetMouseEvent event) {
            if (event.getClickCount () == 1)
                if (event.getButton () == MouseEvent.BUTTON1 || event.getButton () == MouseEvent.BUTTON2) {

                    addNode ("node" + nodeCounter ++).setPreferredLocation (widget.convertLocalToScene (event.getPoint ()));

                    return WidgetAction.State.CONSUMED;
                }
            return WidgetAction.State.REJECTED;
        }

    }

    private class SceneConnectProvider implements ConnectProvider {

        private String source = null;
        private String target = null;

        public boolean isSourceWidget (Widget sourceWidget) {
            Object object = findObject (sourceWidget);
            source = isNode (object) ? (String) object : null;
            return source != null;
        }

        public ConnectorState isTargetWidget (Widget sourceWidget, Widget targetWidget) {
            Object object = findObject (targetWidget);
            target = isNode (object) ? (String) object : null;
            if (target != null)
                return ! source.equals (target) ? ConnectorState.ACCEPT : ConnectorState.REJECT_AND_STOP;
            return object != null ? ConnectorState.REJECT_AND_STOP : ConnectorState.REJECT;
        }

        public boolean hasCustomTargetWidgetResolver (Scene scene) {
            return false;
        }

        public Widget resolveTargetWidget (Scene scene, Point sceneLocation) {
            return null;
        }

        public void createConnection (Widget sourceWidget, Widget targetWidget) {
            String edge = "edge" + edgeCounter ++;
            addEdge (edge);
            setEdgeSource (edge, source);
            setEdgeTarget (edge, target);
        }

    }

    private class SceneReconnectProvider implements ReconnectProvider {

        String edge;
        String originalNode;
        String replacementNode;

        public void reconnectingStarted (ConnectionWidget connectionWidget, boolean reconnectingSource) {
        }

        public void reconnectingFinished (ConnectionWidget connectionWidget, boolean reconnectingSource) {
        }

        public boolean isSourceReconnectable (ConnectionWidget connectionWidget) {
            Object object = findObject (connectionWidget);
            edge = isEdge (object) ? (String) object : null;
            originalNode = edge != null ? getEdgeSource (edge) : null;
            return originalNode != null;
        }

        public boolean isTargetReconnectable (ConnectionWidget connectionWidget) {
            Object object = findObject (connectionWidget);
            edge = isEdge (object) ? (String) object : null;
            originalNode = edge != null ? getEdgeTarget (edge) : null;
            return originalNode != null;
        }

        public ConnectorState isReplacementWidget (ConnectionWidget connectionWidget, Widget replacementWidget, boolean reconnectingSource) {
            Object object = findObject (replacementWidget);
            replacementNode = isNode (object) ? (String) object : null;
            if (replacementNode != null)
                return ConnectorState.ACCEPT;
            return object != null ? ConnectorState.REJECT_AND_STOP : ConnectorState.REJECT;
        }

        public boolean hasCustomReplacementWidgetResolver (Scene scene) {
            return false;
        }

        public Widget resolveReplacementWidget (Scene scene, Point sceneLocation) {
            return null;
        }
        
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

