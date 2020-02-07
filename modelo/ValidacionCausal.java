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
public class ValidacionCausal {
    
    private int tam;
    private int[][] matriz;
    private ArrayList<Integer> nodosRecorridos = new ArrayList<>();
    private ArrayList<String> cadenasGeneradas = new ArrayList<>();
    
    public ValidacionCausal(int t){
        this.tam = t;
        matriz  = new int [tam][tam];
//        for (int i = 0; i < tam; i++) {
//            for (int j = 0; j < tam; j++) {
//                matriz[i][j] = 0;
//            }
//        }
    }

    public int getTam() {
        return tam;
    }

    public void setTam(int tam) {
        this.tam = tam;
    }

    public int[][] getMatriz() {
        return matriz;
    }

    public void setMatriz(int[][] matriz) {
        this.matriz = matriz;
    }

    public ArrayList<Integer> getNodosRecorridos() {
        return nodosRecorridos;
    }

    public void setNodosRecorridos(ArrayList<Integer> nodosRecorridos) {
        this.nodosRecorridos = nodosRecorridos;
    }

    public ArrayList<String> getCadenasGeneradas() {
        return cadenasGeneradas;
    }

    public void setCadenasGeneradas(ArrayList<String> cadenasGeneradas) {
        this.cadenasGeneradas = cadenasGeneradas;
    }
    
    public void rellenar(int[][] m){
        for (int i = 0; i < tam; i++) {
            for (int j = 0; j < tam; j++) {
                matriz[i][j] = m[i][j];
            }
        }
    }
    
    public void recorrer(){
        boolean param;
        String cadena = "";
        for (int i = 0; i < tam; i++) {
            param = true;
            for (int j = 0; j < tam; j++) {
//                if(matriz[i][j] == 1){
//                    nodosRecorridos.add(i);
//                    int y = i+1; int z = j+1;
//                    System.out.println(generadorCadenas(j, "["+y+"]+["+z+"]"));
//                }
//                else if(matriz[i][j] == -1){
//                    nodosRecorridos.add(i);
//                    int y = i+1; int z = j+1;
//                    System.out.println(generadorCadenas(j, "["+y+"]-["+z+"]"));
//                }
                if(matriz[j][i] == 1 || matriz[j][i] == -1){
                    param = false;
                    break;
                }
            }
            if(param){
                int y = i+1;
                //cadena = generadorCadenas(i, "["+y+"]");
                cadena = generadorCadenas(i, y+"");
                System.out.println(cadena);
                cadenasGeneradas.add(cadena);
            }
            nodosRecorridos.clear();
        }
    }
    
    public String generadorCadenas(int indice, String cadena){
        for (int j = 0; j < tam; j++) {
            if(matriz[indice][j] == 1 && !nodosRecorridos.contains(j)){
                nodosRecorridos.add(indice);
                int z = j+1;
                //cadena = generadorCadenas(j, cadena+"+["+z+"]");
                cadena = generadorCadenas(j, cadena+"+"+z);
            }
            else if(matriz[indice][j] == -1 && !nodosRecorridos.contains(j)){
                nodosRecorridos.add(indice);
                int z = j+1;
                //cadena = generadorCadenas(j, cadena+"-["+z+"]");
                cadena = generadorCadenas(j, cadena+"-"+z);
            }
        }
        return cadena;
    }
    
    public boolean esCausal(){
        String regex1 = "(([A-Z]|[a-z]|[0-9]|[äÄëËïÏöÖüÜáéíóúáéíóúÁÉÍÓÚÂÊÎÔÛâêîôûàèìòùÀÈÌÒÙ])[ ]?)+"; //quita los numeros
        //("^"+regex1+"([+|-]"+regex1+")*$")
        this.recorrer();
        
        if(esConexo()){
            for (String cad : cadenasGeneradas) {
                boolean matches = cad.matches("^" + regex1 + "([+|-]" + regex1 + ")+$");
                if (matches == false) {
                    return false;
                }
            }
            return true && esConexo();
        }
        else{
            return false;
        }
        
    }
    
    public boolean tieneRetroalimentacion(){
        boolean conexion = false;
        nodosRecorridos.clear();
        
        for (int i = 0; i < tam; i++) {
            for (int j = 0; j < tam; j++) {
                if(matriz[i][j] == 1 || matriz[i][j] == -1){
                    conexion = true;
                    if ( revisarPila(nodosRecorridos.toString(),j) ) {
                        nodosRecorridos.clear();
                        return true;
                    }
                }
            }
            if(conexion){
                nodosRecorridos.add(i);
                conexion = false;
            }
        }
        
        return false;
    }
    
    public boolean revisarPila(String pila, int variable){
        String j = Integer.toString(variable);
        
        for (int n = pila.length() - 1; n >= 0; n--) { 
            char c = pila.charAt (n); 
            if( j.equals(String.valueOf(c)) ){
                return true;
            }
        }
        
        return false;
    }
    
    public boolean esConexo(){
        int cont = 0;
        for (int i = 0; i < tam; i++) {
            for (int j = 0; j < tam; j++) {
                if(matriz[i][j] == 1 || matriz[i][j] == -1){
                    cont++;
                }
            }
        }
        
        return cont >= tam-1;
    }
    
    
    
}
