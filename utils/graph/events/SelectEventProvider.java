/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils.graph.events;

import java.awt.Point;
import org.netbeans.api.visual.action.SelectProvider;
import org.netbeans.api.visual.widget.Widget;

/**
 *
 * @author frank
 */
public class SelectEventProvider implements SelectProvider {

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
    }
       
}
