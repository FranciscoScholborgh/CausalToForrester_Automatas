/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils.graph.events;

import java.awt.Point;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import org.netbeans.api.visual.action.SelectProvider;
import org.netbeans.api.visual.widget.Widget;

/**
 *
 * @author frank
 */
public class DeleteEventProvider implements SelectProvider{

    @Override
    public boolean isAimingAllowed(Widget widget, Point point, boolean bln) {
        return false;
    }

    @Override
    public boolean isSelectionAllowed(Widget widget, Point point, boolean bln) {
        return true;
    }

    @Override
    public void select(Widget widget, Point point, boolean bln) {
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.CONFIRMATION, "¿Desea eliminar el elemento seleccionado?", ButtonType.YES, ButtonType.NO);
            alert.setHeaderText(null);
            alert.showAndWait();
            if (alert.getResult() == ButtonType.YES) {
                widget.removeFromParent();
            }
        });
    }
    
}
