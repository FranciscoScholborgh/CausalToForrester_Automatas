/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import java.util.ArrayList;

/**
 *
 *
 */
public class Conversion {
    
    private int tam;
    private int[][] matriz;
    private String[][] matrizTipos;
    private String[] ecuaciones;
    
    public Conversion(int t, int[][] m){
        this.tam = t;
        matriz  = new int [tam][tam];
        matrizTipos = new String[tam][tam];
        ecuaciones = new String[tam];
        
        for (int i = 0; i < tam; i++) {
            for (int j = 0; j < tam; j++) {
                matrizTipos[i][j] = " ";
            }
        }
        
        for (int i = 0; i < tam; i++) {
            for (int j = 0; j < tam; j++) {
                matriz[i][j] = m[i][j];
            }
        }
        
        for (int i = 0; i < tam; i++) {
            ecuaciones[i] = "";
        }
    }

    public int getTam() {
        return tam;
    }

    public int[][] getMatriz() {
        return matriz;
    }

    public String[][] getMatrizTipos() {
        return matrizTipos;
    }

    public String[] getEcuaciones() {
        return ecuaciones;
    }

    public void identificarParams(){
        boolean param;
        for (int i = 0; i < tam; i++) {
            param = true;
            for (int j = 0; j < tam; j++) {
                if(matriz[j][i] == 1 || matriz[j][i] == -1){
                    param = false;
                    break;
                }
            }
            if(param){
                matrizTipos[i][i] = "P";
                for (int j = 0; j < tam; j++) {
                    if(matriz[i][j] == 1){
                        matrizTipos[i][j] = "I";
                    }
                    else if (matriz[i][j] == -1){
                        matrizTipos[i][j] = "-I";
                    }
                }
            }
        }
        
    }
    
    public void identificarConexion(String c){
        for (int i = 0; i < tam; i++) {
            for (int j = 0; j < tam; j++) {
                
                if (matrizTipos[i][j].equals(c) || matrizTipos[i][j].equals("-"+c)){
                    for (int k = 0; k < tam; k++) {
                        if(matriz[i][k] == 1){
                            matrizTipos[i][k] = c;
                        }
                        else if(matriz[i][k] == -1){
                            matrizTipos[i][k] = "-"+c;
                        }
                    }
                    
                    for (int k = 0; k < tam; k++) {
                        if(matriz[k][j] == 1){
                            matrizTipos[k][j] = c;
                        }
                        else if(matriz[k][j] == -1){
                            matrizTipos[k][j] = "-"+c;
                        }
                    }
                    
                }
                
            }
            
        }
    }
    
    public void identificarCiclosMinimos(){
        int k;
        
        for (int i = 0; i < tam; i++) {
            for (int j = 0; j < tam; j++) {
                
                if( (matriz[i][j] == 1 || matriz[i][j] == -1) && (matriz[j][i] == 1 || matriz[j][i] == -1) ){
                    //en teoria faltarian los q ϵ ...
                    for(int h = 0; h < tam; h++){
                        k = h;
                        //C12
                        if( matrizTipos[h][i].equals("I") || matrizTipos[h][i].equals("-I") ){
                            //C32
                            if(matriz[j][i] == 1){
                                matrizTipos[j][i] = "I";
                            }
                            else if(matriz[j][i] == -1){
                                matrizTipos[j][i] = "-I";
                            }
                            
                            //C23
                            if(matriz[i][j] == 1){
                                matrizTipos[i][j] = "F";
                            }
                            else if(matriz[i][j] == -1){
                                matrizTipos[i][j] = "-F";
                            }
                            
                            //C43
                            if(matriz[k][j] == 1){
                                matrizTipos[k][j] = "F";
                            }
                            else if(matriz[k][j] == -1){
                                matrizTipos[k][j] = "-F";
                            }
                            
                            //C34
                            if(matriz[j][k] == 1){
                                matrizTipos[j][k] = "I";
                            }
                            else if(matriz[j][k] == -1){
                                matrizTipos[j][k] = "-I";
                            }
                        }
                        //C12
                        else if(matrizTipos[h][i].equals("F") || matrizTipos[h][i].equals("-F")){
                            //C32
                            if(matriz[j][i] == 1){
                                matrizTipos[j][i] = "F";
                            }
                            else if(matriz[j][i] == -1){
                                matrizTipos[j][i] = "-F";
                            }
                            
                            //C23
                            if(matriz[i][j] == 1){
                                matrizTipos[i][j] = "I";
                            }
                            else if(matriz[i][j] == -1){
                                matrizTipos[i][j] = "-I";
                            }
                            
                            //C43
                            if(matriz[k][j] == 1){
                                matrizTipos[k][j] = "I";
                            }
                            else if(matriz[k][j] == -1){
                                matrizTipos[k][j] = "-I";
                            }
                            
                            //C34
                            if(matriz[j][k] == 1){
                                matrizTipos[j][k] = "F";
                            }
                            else if(matriz[j][k] == -1){
                                matrizTipos[j][k] = "-F";
                            }
                        }
                        //C21
                        else if(matrizTipos[i][h].equals("I") || matrizTipos[i][h].equals("-I")){
                            //C23
                            if(matriz[i][j] == 1){
                                matrizTipos[i][j] = "I";
                            }
                            else if(matriz[i][j] == -1){
                                matrizTipos[i][j] = "-I";
                            }
                            
                            //C32
                            if(matriz[j][i] == 1){
                                matrizTipos[j][i] = "F";
                            }
                            else if(matriz[j][i] == -1){
                                matrizTipos[j][i] = "-F";
                            }
                            
                            //C34
                            if(matriz[j][k] == 1){
                                matrizTipos[j][k] = "F";
                            }
                            else if(matriz[j][k] == -1){
                                matrizTipos[j][k] = "-F";
                            }
                            
                            //C43
                            if(matriz[k][j] == 1){
                                matrizTipos[k][j] = "I";
                            }
                            else if(matriz[k][j] == -1){
                                matrizTipos[k][j] = "-I";
                            }
                        }
                        //C21
                        else if(matrizTipos[i][h].equals("F") || matrizTipos[i][h].equals("-F")){
                            //C23
                            if(matriz[i][j] == 1){
                                matrizTipos[i][j] = "F";
                            }
                            else if(matriz[i][j] == -1){
                                matrizTipos[i][j] = "-F";
                            }
                            
                            //C32
                            if(matriz[j][i] == 1){
                                matrizTipos[j][i] = "I";
                            }
                            else if(matriz[j][i] == -1){
                                matrizTipos[j][i] = "-I";
                            }
                            
                            //C34
                            if(matriz[j][k] == 1){
                                matrizTipos[j][k] = "I";
                            }
                            else if(matriz[j][k] == -1){
                                matrizTipos[j][k] = "-I";
                            }
                            
                            //C43
                            if(matriz[k][j] == 1){
                                matrizTipos[k][j] = "F";
                            }
                            else if(matriz[k][j] == -1){
                                matrizTipos[k][j] = "-F";
                            }
                        }
                        
                    }
                    
                }
                
            }
        }
    }
    
    
    public void rellenarIFaltantes(){
        for (int i = 0; i < tam; i++) {
            for (int j = 0; j < tam; j++) {
                if(matrizTipos[i][j].equals("F") || matrizTipos[i][j].equals("-F")){
                    for (int k = 0; k < tam; k++) {
                        //I -> F
                        if(matriz[k][i] == 1 && matrizTipos[k][i].equals(" ")){
                            matrizTipos[k][i] = "I";
                        }
                        else if(matriz[k][i] == -1 && matrizTipos[k][i].equals(" ")){
                            matrizTipos[k][i] = "-I";
                        }
                        
                        //F -> I
                        
                    }
                }
            }
        }
    }
    
    public void establecerRX(){
        for (int i = 0; i < tam; i++) {
            for (int j = 0; j < tam; j++) {
                
                if(matrizTipos[i][j].equals("I") || matrizTipos[i][j].equals("-I")){
                    
                    for (int k = 0; k < tam; k++) {
                        if(matrizTipos[k][i].equals("F") || matrizTipos[k][i].equals("-F")){
                            matrizTipos[i][i] = "X";
                            break;
                        }
                        else if(matrizTipos[k][i].equals("I") || matrizTipos[k][i].equals("-I")){
                            matrizTipos[i][i] = "V";
                            break;
                        }
                    }
                    break;
                    
                }
                else if(matrizTipos[i][j].equals("F") || matrizTipos[i][j].equals("-F")){
                    
                    for (int k = 0; k < tam; k++) {
                        if(matrizTipos[k][i].equals("I") || matrizTipos[k][i].equals("-I")){
                            matrizTipos[i][i] = "R";
                            break;
                        }
                    }
                    
                }
                
            }
        }
    }
    
    //borrar en modo produccion
    public void mostrarVariablesForrester(){
        System.out.println("");
        for (int i = 0; i < tam; i++) {
            if(matrizTipos[i][i].equals("P")){
                System.out.println("["+i+"] es un parametro"); // parametro = constante
            }
            else if(matrizTipos[i][i].equals("V")){
                System.out.println("["+i+"] es un auxiliar"); // auxiliar = constante * auxiliar (?) ó constante +/- auxiliar
            }
            else if(matrizTipos[i][i].equals("R")){
                System.out.println("["+i+"] es un flujo"); // flujo = constante * auxiliar * nivel
            }
            else if(matrizTipos[i][i].equals("X")){
                System.out.println("["+i+"] es un nivel");// xi(t + Δt) = xi(t) + Δt{rj - rk}       rj = entrada y rk = salida, "el - es por la salida" (creo) 
            }
        }
    }
    
    public void calcularEcuaciones(){
        String ecuacion = "";
        
        for (int i = 0; i < tam; i++) {
            ecuacion = generarEcuacion(i, matrizTipos[i][i]);
            ecuaciones[i] = ecuacion;
        }
    }
    
    public String generarEcuacion(int indice, String tipo){
        String ecuacion = "";
        String numerador = "";
        String denominador = "";
        
        if(tipo.equals("P")){
            ecuacion = "Const";
        }
        else if(tipo.equals("X")){
            for (int i = 0; i < tam; i++) {
                if(matriz[i][indice] == 1){
                    ecuacion += " + ("+i+")";
                }
                else if(matriz[i][indice] == -1){
                    ecuacion += " - ("+i+")";
                }
            }
            ecuacion = "("+indice+")"+"{t-1}"+ecuacion;
        }
        else if(tipo.equals("R")){
            boolean resta = false;
            for (int i = 0; i < tam; i++) {
                for (int j = 0; j < tam; j++) {
                    if(matrizTipos[j][j].equals("X") && matriz[j][indice] == -1){
                        resta = true;
                    }
                }
                
                if(resta == false){
                    if(matriz[i][indice] == 1) {
                        if (numerador.equals("")) {
                            numerador += "(" + i + ")";
                        } else {
                            numerador += "*(" + i + ")";
                        }
                    }
                }
                else{ //resta == true
                    if(matriz[i][indice] == 1) {
                        if (numerador.equals("")) {
                            numerador += "(" + i + ")";
                        } else {
                            numerador += "+(" + i + ")";
                        }
                    }
                    else if(matriz[i][indice] == -1 && matrizTipos[i][i].equals("X")) {
                        if (numerador.equals("")) {
                            numerador += "(" + i + ")";
                        } else {
                            numerador += "-(" + i + ")";
                        }
                    }
                }
                
                if (matriz[i][indice] == -1 && matrizTipos[i][i].equals("P")) {
                    if (denominador.equals("")) {
                        denominador += "(" + i + ")";
                    } else {
                        denominador += "*(" + i + ")";
                    }
                }
                
            }
            
            if(denominador.equals("")){
                ecuacion = "("+numerador +")";
            }
            else if(numerador.equals("")){
                ecuacion = "1 / ("+denominador+")";
            }
            else{
                ecuacion = "("+numerador +") / ("+ denominador+")";
            }
            
        }
        else if(tipo.equals("V")){
            boolean resta = false;
            for (int i = 0; i < tam; i++) {
                for (int j = 0; j < tam; j++) {
                    if(matrizTipos[j][j].equals("V") && matriz[j][indice] == -1){
                        resta = true;
                    }
                }
                
                if(resta == false){
                    if (matriz[i][indice] == 1) {
                        if (numerador.equals("")) {
                            numerador += "(" + i + ")";
                        } else {
                            numerador += "*(" + i + ")";
                        }
                    }
                }
                else{
                    if(matriz[i][indice] == 1) {
                        if (numerador.equals("")) {
                            numerador += "(" + i + ")";
                        } else {
                            numerador += "+(" + i + ")";
                        }
                    }
                    else if(matriz[i][indice] == -1 && matrizTipos[i][i].equals("V")) {
                        if (numerador.equals("")) {
                            numerador += "(" + i + ")";
                        } else {
                            numerador += "-(" + i + ")";
                        }
                    }
                }
                
                if (matriz[i][indice] == -1 && matrizTipos[i][i].equals("P")) {
                    if (denominador.equals("")) {
                        denominador += "(" + i + ")";
                    } else {
                        denominador += "*(" + i + ")";
                    }
                }
                
            }
            
            if(denominador.equals("")){
                ecuacion = "("+numerador +")";
            }
            else if(numerador.equals("")){
                ecuacion = "1 / ("+denominador+")";
            }
            else{
                ecuacion = "("+numerador +") / ("+ denominador+")";
            }
            
        }
        
        return ecuacion;
    }
    
    public boolean estaCompleta(){
        for (int i = 0; i < tam; i++) {
            if(matrizTipos[i][i].equals(" "))
                return false;
        }
        
        return true;
    }
    
    public void procesarInformacion(ArrayList<Integer> variablesIndex){
        for (Integer i : variablesIndex) {
            matrizTipos[i][i] = "X";
            for (int k = 0; k < tam; k++) {
                if (matriz[i][k] == 1) {
                    matrizTipos[i][k] = "I";
                } else if (matriz[i][k] == -1) {
                    matrizTipos[i][k] = "-I";
                }
            }

            for (int k = 0; k < tam; k++) {
                if (matriz[k][i] == 1) {
                    matrizTipos[k][i] = "F";
                } else if (matriz[k][i] == -1) {
                    matrizTipos[k][i] = "-F";
                }
            }
        }
    }
    
    
}
