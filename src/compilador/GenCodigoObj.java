/*:-----------------------------------------------------------------------------
 *:                       INSTITUTO TECNOLOGICO DE LA LAGUNA
 *:                     INGENIERIA EN SISTEMAS COMPUTACIONALES
 *:                         LENGUAJES Y AUTOMATAS II           
 *: 
 *:        SEMESTRE: ______________            HORA: ______________ HRS
 *:                                   
 *:               
 *:    # Clase con la funcionalidad del Generador de COdigo Objeto
 *                 
 *:                           
 *: Archivo       : GenCodigoInt.java
 *: Autor         : Fernando Gil  
 *: Fecha         : 03/SEP/2014
 *: Compilador    : Java JDK 7
 *: Descripción   :  
 *:                  
 *:           	     
 *: Ult.Modif.    :
 *:  Fecha  06/06/2023    Modificó            Modificacion
 *:=============================================================================
 *: 24/May/2023 F.Gil              -Generar la plantilla de programa Ensamblador
 *: 02/May/2023 Damaso, angel      -implemantacion del codigo objeto
 *:-----------------------------------------------------------------------------
 */


package compilador;

import general.Linea_TS;
import java.util.ArrayList;


public class GenCodigoObj {
 
    private Compilador cmp;

    
    //--------------------------------------------------------------------------
    // Constructor de la clase, recibe la referencia de la clase principal del 
    // compilador.
    //
    public GenCodigoObj ( Compilador c ) {
        cmp = c;
    }
    // Fin del Constructor
    //--------------------------------------------------------------------------
	
    public void generar () {
        genEncabezadoASM ();
        genDeclaraVarsASM();
        genSegmentoCodigo();
        algoritmoGCO     ();
        genPieASM        ();
    }    

    //--------------------------------------------------------------------------
    // Genera las primeras lineas del programa Ensamblador hasta antes de la 
    // declaracion de variables.
    
    private void genEncabezadoASM () {
        cmp.iuListener.mostrarCodObj ( "TITLE CodigoObjeto ( codigoObjeto.asm )"  );
        cmp.iuListener.mostrarCodObj ( "; Descripción del programa: Automatas II" );
        cmp.iuListener.mostrarCodObj ( "; Fecha de creacion: Ene-Jun/2023"        );
        cmp.iuListener.mostrarCodObj ( "; Revisiones:" );
        cmp.iuListener.mostrarCodObj ( "; Fecha de ult. modificacion:" );
        cmp.iuListener.mostrarCodObj ( "" );
       // cmp.iuListener.mostrarCodObj ( "; INCLUDE Irvine32.inc" );
        cmp.iuListener.mostrarCodObj ( ".MODEL SMALL" );
        cmp.iuListener.mostrarCodObj ( ".STACK 4096h" );
        cmp.iuListener.mostrarCodObj ( "" );
        cmp.iuListener.mostrarCodObj ( ".data" );
        cmp.iuListener.mostrarCodObj ( "  ; (aqui se insertan las variables)" );        
    }
    
    //--------------------------------------------------------------------------
    // Genera las lineas en Ensamblador de Declaracion de variables.
    // Todas las variables serán DWORD ya que por simplificacion solo se genera
    // codigo objeto de programas fuente que usaran solo variables enteras.
    
    private void genDeclaraVarsASM () {
        for ( int i = 1; i < cmp.ts.getTamaño (); i++ ) {
            
            Linea_TS elemento = cmp.ts.obt_elemento( i );
            String variable = elemento.getLexema();
            
            
            if ( elemento.getComplex().equals ( "id" ) ) 
                cmp.iuListener.mostrarCodObj ( "  " + variable + " DW 0" );
        }
        for(int i = 1; i < cmp.nt;i++){
            cmp.iuListener.mostrarCodObj ( "  " + "t"+i + " DW 0" );
        }
        cmp.iuListener.mostrarCodObj ( "" );
    }
    
    //--------------------------------------------------------------------------
    
    private void genSegmentoCodigo () {
        cmp.iuListener.mostrarCodObj ( ".code" );
        cmp.iuListener.mostrarCodObj ( "inicio:" );
        cmp.iuListener.mostrarCodObj ( "MOV ax, @Data" );
        cmp.iuListener.mostrarCodObj ( "MOV ds, ax" );
        cmp.iuListener.mostrarCodObj ( " " );
        cmp.iuListener.mostrarCodObj ( "  ; (aqui se insertan las instrucciones ejecutables)" );
    }
    
    //--------------------------------------------------------------------------
    // Genera las lineas en Ensamblador de finalizacion del programa
    
    private void genPieASM () {
        //cmp.iuListener.mostrarCodObj ( "  exit" );
        //cmp.iuListener.mostrarCodObj ( "main ENDP" );
        //cmp.iuListener.mostrarCodObj ( "" );
        //cmp.iuListener.mostrarCodObj ( "; (aqui se insertan los procedimientos adicionales)" );
        cmp.iuListener.mostrarCodObj ( " " );
        cmp.iuListener.mostrarCodObj ( "MOV ax, 4c00h" );
        cmp.iuListener.mostrarCodObj ( "INT 21h" );
        cmp.iuListener.mostrarCodObj ( " " );
        cmp.iuListener.mostrarCodObj ( "END inicio" );
    }
    
    //--------------------------------------------------------------------------
    // Algoritmo de generacion de codigo en ensamblador
    
    private void algoritmoGCO () {
        ArrayList<Cuadruplo> cuadruplos = cmp.cua.getCuadruplos();
           String [][] arrCuadruplos = new String[cmp.cua.getTamaño()][4];
            
            for (int i = 0; i < cmp.cua.getTamaño(); i++) {
                arrCuadruplos [ i ] [ 0 ] = cuadruplos.get ( i ).op;
                arrCuadruplos [ i ] [ 1 ] = cuadruplos.get ( i ).arg1;
                arrCuadruplos [ i ] [ 2 ] = cuadruplos.get ( i ).arg2;
                arrCuadruplos [ i ] [ 3 ] = cuadruplos.get ( i ).resultado;
                if(arrCuadruplos [ i ] [ 0 ].equals(":=")){
                    cmp.iuListener.mostrarCodObj("mov ax,"+arrCuadruplos [ i ] [ 1 ]);
                    cmp.iuListener.mostrarCodObj("mov "+arrCuadruplos [ i ] [ 3 ]+",ax");
                    cmp.iuListener.mostrarCodObj ( "" );
                }else if(arrCuadruplos [ i ] [ 0 ].equals("+")){
                    cmp.iuListener.mostrarCodObj("mov ax,"+arrCuadruplos [ i ] [ 1 ]);
                    cmp.iuListener.mostrarCodObj("add ax,"+arrCuadruplos [ i ] [ 2 ]);
                    cmp.iuListener.mostrarCodObj("mov "+arrCuadruplos [ i ] [ 3 ]+",ax");
                    cmp.iuListener.mostrarCodObj ( "" );
                }else if(arrCuadruplos [ i ] [ 0 ].equals("*")){
                    cmp.iuListener.mostrarCodObj("mov ax,"+arrCuadruplos [ i ] [ 1 ]);
                    cmp.iuListener.mostrarCodObj("mov bx,"+arrCuadruplos [ i ] [ 2 ]);
                    cmp.iuListener.mostrarCodObj("mul bx");
                    cmp.iuListener.mostrarCodObj("mov "+arrCuadruplos [ i ] [ 3 ]+",ax");
                    cmp.iuListener.mostrarCodObj ( "" );
                }//else
                    //cmp.me.error( Compilador.ERR_CODOBJ,"No se encontro una coincidencia de operador en el c3D");
            }
    }
    
    
    
    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    
}