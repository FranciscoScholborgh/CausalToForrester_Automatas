/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package vista;

import org.netbeans.api.visual.graph.GraphScene;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.anchor.AnchorShape;
import org.netbeans.api.visual.anchor.Anchor;
import org.netbeans.api.visual.vmd.VMDNodeAnchor;
import org.netbeans.api.visual.router.RouterFactory;
import org.netbeans.api.visual.router.Router;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.WidgetAction;

import java.awt.*;
import java.util.HashMap;

/**
 * @author David Kaspar
 */
public class LoopEdgeTest extends GraphScene.StringGraph {

    private final LayerWidget mainLayer;
    private final LayerWidget connectionLayer;
    private final Router router;
    private final WidgetAction moveAction;
    private final HashMap<String, Anchor> anchors;

    public LoopEdgeTest() {
        mainLayer = new LayerWidget (this);
        addChild (mainLayer);
        connectionLayer = new LayerWidget (this);
        addChild (connectionLayer);
        router = RouterFactory.createOrthogonalSearchRouter (mainLayer, connectionLayer);
        moveAction = ActionFactory.createMoveAction ();
        anchors = new HashMap<> ();
    }

    @Override
    protected Widget attachNodeWidget (String node) {
        LabelWidget widget = new LabelWidget (this, "| " + node + " |");
        widget.setOpaque (true);
        widget.setBackground (Color.YELLOW);
        widget.setPreferredLocation (new Point (100, 100));
        widget.getActions ().addAction (moveAction);
        mainLayer.addChild (widget);
        anchors.put (node, new VMDNodeAnchor (widget));
        return widget;
    }

    @Override
    protected void detachNodeWidget (String node, Widget widget) {
        super.detachNodeWidget (node, widget);
        anchors.remove (node);
    }

    @Override
    protected Widget attachEdgeWidget (String edge) {
        ConnectionWidget widget = new ConnectionWidget (this);
        widget.setRouter (router);
        widget.setTargetAnchorShape (AnchorShape.TRIANGLE_FILLED);
        widget.setRouter(RouterFactory.createDirectRouter());
        connectionLayer.addChild (widget);
        return widget;
    }

    @Override
    protected void attachEdgeSourceAnchor (String edge, String oldSourceNode, String sourceNode) {
        ((ConnectionWidget) findWidget (edge)).setSourceAnchor (anchors.get (sourceNode));
    }

    @Override
    protected void attachEdgeTargetAnchor (String edge, String oldTargetNode, String targetNode) {
        ((ConnectionWidget) findWidget (edge)).setTargetAnchor (anchors.get (targetNode));
    }

    public LayerWidget getMainLayer() {
        return mainLayer;
    }

    public LayerWidget getConnectionLayer() {
        return connectionLayer;
    }

    public Router getRouter() {
        return router;
    }

    public WidgetAction getMoveAction() {
        return moveAction;
    }

    public HashMap<String, Anchor> getAnchors() {
        return anchors;
    }
    
    

}
