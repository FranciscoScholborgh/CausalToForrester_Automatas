/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controlador;

import java.util.ArrayList;
import vista.VariablesView;

/**
 *
 * @author frank
 */
public class VariablesViewController {
    
    private MainMenuController parent;
    
    private final VariablesView view;

    public VariablesViewController(VariablesView view) {
        this.view = view;
    }

    public MainMenuController getParent() {
        return parent;
    }

    public void setParent(MainMenuController parent) {
        this.parent = parent;
    }
    
    public void sendInfo_toParents() {
        ArrayList<String> preset = this.view.getCheckedItems();
        this.parent.convertion_userPreset(preset);
    }
    
    public void loadViariables (ArrayList<String> variables) {
        try{
            if(!variables.isEmpty()) {
                this.view.setItems_VariablesHolder(variables);
            }
        } catch (NullPointerException npe) {
            System.out.println("Null");
        }
        
    }
    
    public void show() {
        this.view.setVisible(true);     
    }
    
    public void hide() {
        this.view.setVisible(false);
        this.view.clear();
    }
       
}
