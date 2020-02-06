/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils.graph.factories;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.ConnectProvider;
import org.netbeans.api.visual.action.ConnectorState;
import org.netbeans.api.visual.action.ReconnectProvider;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.anchor.AnchorFactory;
import org.netbeans.api.visual.anchor.AnchorShape;
import org.netbeans.api.visual.anchor.PointShape;
import org.netbeans.api.visual.layout.LayoutFactory;
import org.netbeans.api.visual.router.RouterFactory;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;
import utils.graph.dataRepresentation.DiagramData;
import utils.graph.events.RenameEventProvider;

/**
 *
 * @author frank
 */
public class CausalDiagramEditor extends DiagramViewer{
    
    private ArrayList<LabelWidget> variables;
    private ArrayList<ConnectionWidget> connections;
    
    private final WidgetAction createAction;
    private final WidgetAction connectAction;
    private final WidgetAction reconnectAction;
    private final WidgetAction renameAction;
    
    private final WidgetAction addRemoveControlPoint; 
    private final WidgetAction freeMoveControlPoint; 
    
    private long edgeCounter = 0;

    public CausalDiagramEditor () {
        this.variables = new ArrayList<>();
        this.connections = new ArrayList<>();
        
        this.createAction = new SceneCreateAction ();
        this.connectAction = ActionFactory.createConnectAction (super.getInterractionLayer(), new SceneConnectProvider (this));
        this.reconnectAction = ActionFactory.createReconnectAction (new SceneReconnectProvider ());
        this.renameAction = ActionFactory.createInplaceEditorAction (new RenameEventProvider ());
        
        this.addRemoveControlPoint = ActionFactory.createAddRemoveControlPointAction ();
        this.freeMoveControlPoint = ActionFactory.createFreeMoveControlPointAction ();
           
        this.addChild(super.getMainLayer());
        this.addChild(super.getConnectionLayer());
        this.addChild(super.getInterractionLayer());
        
        this.getDeleteProvider().attachDiagramViewer(this);
    }

    public ArrayList<LabelWidget> getVariables() {
        return variables;
    }

    public void setVariables(ArrayList<LabelWidget> variables) {
        this.variables = variables;
    }

    public ArrayList<ConnectionWidget> getConnections() {
        return connections;
    }

    public void setConnections(ArrayList<ConnectionWidget> connections) {
        this.connections = connections;
    }

    protected WidgetAction getAddRemoveControlPoint() {
        return addRemoveControlPoint;
    }
    
    protected WidgetAction getFreeMoveControlPoint() {
        return freeMoveControlPoint;
    }

    protected WidgetAction getConnectAction() {
        return connectAction;
    }

    @Override
    public void createLabel(String node, Point location) {
        System.out.println("Not done");
    }
    
    @Override
    public void removeWidget(Widget widget) {
        if(widget instanceof LabelWidget) {
            this.variables.remove(widget);
            LabelWidget nodeWidget = (LabelWidget) widget;
            for (ConnectionWidget connection : this.connections) {
                LabelWidget source = (LabelWidget) connection.getSourceAnchor().getRelatedWidget();
                LabelWidget target = (LabelWidget) connection.getTargetAnchor().getRelatedWidget();
                if(nodeWidget.equals(source) || nodeWidget.equals(target)) {
                    Thread t = new Thread( () -> {
                        this.connections.remove(connection);
                    });
                    connection.removeFromParent();
                }
            }
        } else if (widget instanceof ConnectionWidget) {
            ConnectionWidget connection = (ConnectionWidget) widget;
            this.connections.remove(widget);
        } else {
            System.out.println("Widget no soportado por este diagrama");
        }
        widget.removeFromParent();
    }
    
    @Override
    public DiagramData getDiagramData() {
        DiagramData data = new DiagramData(this.variables.size());
        int counter = 0;
        for (LabelWidget label : variables) {
            String variable = label.getLabel();
            data.insert_node(counter++, variable);
        }
        for (ConnectionWidget connection : this.connections) {
            LabelWidget sourceLabel = (LabelWidget) connection.getSourceAnchor().getRelatedWidget();
            LabelWidget targetLabel = (LabelWidget) connection.getTargetAnchor().getRelatedWidget();
            
            String sourceVar = sourceLabel.getLabel();
            String targetVar = targetLabel.getLabel();
            
            int row = -1; 
            int column = -1;
            
            for (int index = 0; index < counter; index++) {
                String value = data.getNodes().get(index);
                if(value.equals(sourceVar)) {
                    row = index;
                    if(row != -1 && column != -1)   
                        break;
                } else if (value.equals(targetVar)) {
                    column = index;
                    if(row != -1 && column != -1)   
                        break;
                }
            }
            
            if(row != -1 && column != -1) {
                LabelWidget signoWodget = (LabelWidget) connection.getChildren().get(0);
                String signo = signoWodget.getLabel();
                int value;
                if(signo.contains("+")){
                    value = 1;
                } else {
                    value = -1;
                }
                data.insert_matrixRelation(row, column, value);
            }
        }
        return data;
    }
    
    protected void addRelation(ConnectionWidget connection) {
        this.connections.add(connection);
    }

    private void enable_insertVariable (boolean enable) {
        if(enable) {
            if(!this.getActions().getActions().contains(this.createAction)) {
                this.getActions ().addAction (this.createAction);
            }           
        } else {
            this.getActions ().removeAction(this.createAction);
        }        
    }
    
    private void enable_relationVariables (boolean enable) {
        if(enable) {
            this.variables.forEach((variable) -> {
                if(!variable.getActions().getActions().contains(this.connectAction)) {
                    variable.getActions().addAction(this.connectAction);
                    variable.getActions().removeAction(this.getMoveAction());
                }
            });
        } else {
            this.variables.forEach((variable) -> {
                variable.getActions().removeAction(this.connectAction);
                variable.getActions().addAction(this.getMoveAction());
            });         
        }
    }
    
    public void activate_insertVariable() {
        this.enable_insertVariable(true);
        this.enable_relationVariables(false);
        this.enable_deleteMode(false);
    }
    
    public void activate_relationVariables(String type) {
        this.enable_insertVariable(false);
        super.setRelationType(type);
        this.enable_relationVariables(true);
        this.enable_deleteMode(false);
    }
    
    public void deactivate_all () {
        this.enable_insertVariable(false);
        this.enable_relationVariables(false);
        this.enable_deleteMode(false);
    }
    
    public void enable_deleteMode(boolean enable) {
        if (enable) {
            this.enable_insertVariable(false);
            this.enable_relationVariables(false);
            this.variables.forEach((variable) -> {
                if(variable.getParentWidget() != null) {
                    if(!variable.getActions().getActions().contains(this.getDeleteAction())) {
                        variable.getActions().addAction(this.getDeleteAction());
                    }
                }
            });
            this.connections.forEach((connection) -> {
                if(connection.getParentWidget() != null) {
                    if(!connection.getActions().getActions().contains(this.getDeleteAction())) {
                        connection.getActions().addAction(this.getDeleteAction());
                        connection.getActions ().removeAction(this.addRemoveControlPoint);
                        connection.getActions ().removeAction(this.freeMoveControlPoint);
                    }
                }
            });

        } else {
            this.variables.forEach((variable) -> {
                if(variable.getParentWidget() != null){
                    variable.getActions().removeAction(this.getDeleteAction());
                }
            });
            this.connections.forEach((connection) -> {
                if(connection.getParentWidget() != null) {
                    connection.getActions().removeAction(this.getDeleteAction());
                    connection.getActions ().addAction(this.addRemoveControlPoint);
                    connection.getActions ().addAction(this.freeMoveControlPoint);
                }
            });
        }
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
        connection.setRouter(RouterFactory.createOrthogonalSearchRouter (super.getMainLayer(), super.getConnectionLayer()));
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
                    String variable = JOptionPane.showInputDialog(null, "Inserte titulo de la variable", 
                            "Variable", JOptionPane.INFORMATION_MESSAGE);
                        try {
                            boolean empty = variable.isEmpty();
                            if(!empty){
                                addNode (variable).setPreferredLocation (widget.convertLocalToScene (event.getPoint ()));
                            } 
                        } catch (NullPointerException npe) {
                            return WidgetAction.State.REJECTED;
                        }
                        return WidgetAction.State.CONSUMED;
                }
            return WidgetAction.State.REJECTED;
        }
    }

    private class SceneConnectProvider implements ConnectProvider {

        private String source = null;
        private String target = null;
        private final CausalDiagramEditor scene;

        public SceneConnectProvider(CausalDiagramEditor scene) {
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
            connection.setSourceAnchor (AnchorFactory.createRectangularAnchor (sourceWidget));
            connection.setTargetAnchor (AnchorFactory.createRectangularAnchor (targetWidget));
            connection.setTargetAnchorShape (AnchorShape.TRIANGLE_FILLED);
            connection.setPaintControlPoints (true);
            connection.setControlPointShape (PointShape.SQUARE_FILLED_BIG);
            //connection.setRouter (RouterFactory.createOrthogonalSearchRouter (getMainLayer()));
            connection.getActions ().addAction (this.scene.getAddRemoveControlPoint());
            connection.getActions ().addAction (this.scene.getFreeMoveControlPoint());
            //connection.getActions().addAction(this.scene.getDeleteAction());
            LabelWidget signo = new LabelWidget (this.scene.getScene(), this.scene.getRelationType());
            signo.setOpaque (false);
            connection.addChild (signo);
            connection.setConstraint (signo, LayoutFactory.ConnectionWidgetLayoutAlignment.BOTTOM_LEFT, -25);
            connection.setRouter(RouterFactory.createOrthogonalSearchRouter (scene.getMainLayer(), scene.getConnectionLayer()));
            getConnectionLayer().addChild (connection);
            this.scene.addRelation(connection);
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

