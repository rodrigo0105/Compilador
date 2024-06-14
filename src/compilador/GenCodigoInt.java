/*:-----------------------------------------------------------------------------
 *:                       INSTITUTO TECNOLOGICO DE LA LAGUNA
 *:                     INGENIERIA EN SISTEMAS COMPUTACIONALES
 *:                         LENGUAJES Y AUTOMATAS II           
 *: 
 *:        SEMESTRE: ______________            HORA: ______________ HRS
 *:                                   
 *:               
 *:    # Clase con la funcionalidad del Generador de COdigo Intermedio
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
 *:  Fecha      Modificó            Modificacion
 *:=============================================================================
 *:-----------------------------------------------------------------------------
 */


package compilador;

import static compilador.SintacticoSemantico.ERROR_TIPO;
import static compilador.SintacticoSemantico.VACIO;
import static compilador.SintacticoSemantico.tiposFunciones;
import general.Linea_BE;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class GenCodigoInt {
 
    private Compilador cmp;
    private int        consecutivoEtiq;
    private String      tempFinal;
    private int temp;

    
    //--------------------------------------------------------------------------
    // Constructor de la clase, recibe la referencia de la clase principal del 
    // compilador.
    //
	public GenCodigoInt ( Compilador c ) {
        cmp = c;
    }
    // Fin del Constructor
    //--------------------------------------------------------------------------
	
    public void generar () {
        consecutivoEtiq = 1;
        temp = 1;
        cmp.cua.vaciar();
        programa ();
        cmp.nt = temp;
    }
    
    private void emite ( String c3d ) {
        cmp.iuListener.mostrarCodInt ( c3d );
        
    }
    
        //************EMPAREJAR**************//
    private void emparejar ( String t ) {
	if (cmp.be.preAnalisis.complex.equals ( t ) )
		cmp.be.siguiente ();
	else
		errorEmparejar ( "Se esperaba " + t + " se encontró " +
                                 cmp.be.preAnalisis.lexema );
    }	
    
        //--------------------------------------------------------------------------
    // Metodo para devolver un error al emparejar
    //--------------------------------------------------------------------------

    private void errorEmparejar ( String _token ) {
        String msjError = "ERROR SINTACTICO: ";
              
        if ( _token.equals ( "id" ) )
            msjError += "Se esperaba un identificador" ;
        else if ( _token.equals ( "num" ) )
            msjError += "Se esperaba una constante entera" ;
        else if ( _token.equals ( "num.num" ) )
            msjError += "Se esperaba una constante real";
        else if ( _token.equals ( "literal" ) )
            msjError += "Se esperaba una literal";
        else if ( _token.equals ( "oparit" ) )
            msjError += "Se esperaba un Operador Aritmetico";
        else if ( _token.equals ( "oprel" ) )
            msjError += "Se esperaba un Operador Relacional";
        else 
            msjError += "Se esperaba " + _token;
                
        cmp.me.error ( Compilador.ERR_SINTACTICO, msjError );    
    }            

    // Fin de ErrorEmparejar
    //--------------------------------------------------------------------------
	
    //--------------------------------------------------------------------------
    // Metodo para mostrar un error sintactico
 
    private void error ( String _token ) {
        cmp.me.error ( cmp.ERR_SINTACTICO,
         "ERROR SINTACTICO: en la produccion del simbolo  " + _token );
    }
 
    // Fin de error
    //--------------------------------------------------------------------------
    
    private String etiqNueva(){
        return "Etiq" + consecutivoEtiq++;
    }
    
     static int precedencia(char c){
        switch (c){
            case '+':
            case '-':
                return 1;
            case '*':
            case '/':
                return 2;
            case '^':
                return 3;
        }
        return -1;
    }
    
   public String infijaAPrefija(String infija) {
    Stack<Character> pila = new Stack<>();
    StringBuilder resul = new StringBuilder();
    char[] charExp = new StringBuilder(infija).reverse().toString().toCharArray();

    for (int i = 0; i < charExp.length; i++) {
        char c = charExp[i];

        if (Character.isLetterOrDigit(c)) {
            StringBuilder variable = new StringBuilder();
            while (i < charExp.length && (Character.isLetterOrDigit(charExp[i]) | (charExp[i] == '.'))) {
                variable.append(charExp[i]);
                i++;
            }
            resul.append(variable).append(" ");
            i--; // Ajustar el índice para que no se salte caracteres
        } else if (c == ')') {
            pila.push(c);
        } else if (c == '(') {
            while (!pila.isEmpty() && pila.peek() != ')') {
                resul.append(pila.pop()).append(" ");
            }
            pila.pop(); 
        } else {
            while (!pila.isEmpty() && precedencia(pila.peek()) >= precedencia(c)) {
                resul.append(pila.pop()).append(" ");
            }
            pila.push(c);
        }
    }

    while (!pila.isEmpty()) {
        resul.append(pila.pop()).append(" ");
    }

    return resul.reverse().toString().trim();
}

    
   public void conversion(String polaca) {
    polaca = infijaAPrefija(polaca);
    Pattern patron = Pattern.compile("[*+]\\s([0-9]*\\.[0-9]+|[a-z0-9]+)\\s([0-9]*\\.?[0-9]+|[a-z0-9]+)");//"[*+/-](\\st[0-9]|\\s[a-z0-9]){2}");
    Matcher matcher = patron.matcher(polaca);
    
    
    while (matcher.find()) {
        String coincidencia = matcher.group();
        String reemplazo = "t" + temp++;
        
        String[] partes = coincidencia.split("\\s");


        String emite = reemplazo + " := " + partes[1] + " " + partes[0] + " " + partes[2];
        
        cmp.cua.agregar ( new Cuadruplo ( partes[0], partes[1], partes[2], reemplazo ) );        
        emite(emite);
        
        //System.out.println("Reemplazo: " + reemplazo);
        
        polaca = matcher.replaceFirst(reemplazo);
        matcher.reset(polaca);
    }
    
    tempFinal =  polaca;
}
    
    private void programa (  ) {
        
        if ( cmp.be.preAnalisis.complex.equals ( "dim"      ) ||
             cmp.be.preAnalisis.complex.equals ( "function" ) ||
             cmp.be.preAnalisis.complex.equals ( "sub"      ) ||
             cmp.be.preAnalisis.complex.equals ( "id"       ) ||
             cmp.be.preAnalisis.complex.equals ( "if"       ) ||
             cmp.be.preAnalisis.complex.equals ( "call"     ) ||  
             cmp.be.preAnalisis.complex.equals ( "do"       ) ||  
             cmp.be.preAnalisis.complex.equals ( "end"      ) ) {
            declaraciones              (  );
            declaraciones_subprogramas (  );
            proposiciones_optativas    (  );
            emparejar                  ( "end"                    );   
        } else {
                    error ( "[programa]: Inicio incorrecto de programa."
                    + "Se esperaba una de estas opciones [dim,function,sub,id,if,call,do,end]. "
                    + "Se encontro "+cmp.be.preAnalisis.lexema + "en la linea: "+
                    "No.Linea: " + cmp.be.preAnalisis.numLinea );
        }
    }
    
    //------------------------------------------------------------------------------------
    

    private void declaraciones (  ) {
        if (cmp.be.preAnalisis.complex.equals ( "dim" ) ) {
            emparejar           ( "dim"                   );
            lista_declaraciones ( );
            declaraciones       ( );
        } else {
            // declaraciones -> empty 
        }
    }
    
    //------------------------------------------------------------------------------------
    

    private void lista_declaraciones (  ) {
        
        if ( cmp.be.preAnalisis.complex.equals ( "id" ) ) {
            emparejar ( "id" );
            emparejar ( "as" );
            tipo (  );
            lista_declaraciones_prima ( );
        } else {
            error ( "[lista_declaraciones]: Se esperaba una declaración." +
                    "No.Linea: " + cmp.be.preAnalisis.numLinea );
        }
    }
    
    //------------------------------------------------------------------------------------
    

    private void lista_declaraciones_prima ( ) {
        
        if ( cmp.be.preAnalisis.complex.equals ( "," ) ) {    
            emparejar ( "," );
            lista_declaraciones ( );
        } else {
            // lista_declaraciones_prima -> empty 
        }
    }
    
    //------------------------------------------------------------------------------------
    

    private void tipo (  ) {
        if ( cmp.be.preAnalisis.complex.equals ( "integer" ) ) {
            emparejar ( "integer" );
        } else if ( cmp.be.preAnalisis.complex.equals ( "single" ) ) {
            emparejar ( "single" );
        } else if ( cmp.be.preAnalisis.complex.equals ( "string" ) ) {
            emparejar ( "string" );
        } else {
            error ( "[tipo]: Tipo de dato no valido. " +
                    "No.Linea: " + cmp.be.preAnalisis.numLinea );
        }
    }
    
    //------------------------------------------------------------------------------------
    

    private void declaraciones_subprogramas (  ) {
        
        if ( cmp.be.preAnalisis.complex.equals ( "function" ) ||
             cmp.be.preAnalisis.complex.equals ( "sub" ) ) {
            declaracion_subprograma    ( );
            declaraciones_subprogramas (  );
        } else {
            // declaraciones_subprogramas -> empty 
        }
    }
    
    //------------------------------------------------------------------------------------
    

    private void declaracion_subprograma (  ) {
        
        if ( cmp.be.preAnalisis.complex.equals ( "function" ) ) {
            declaracion_funcion (  );
        } else if ( cmp.be.preAnalisis.complex.equals( "sub" ) ) {
            declaracion_subrutina (  );
        } else {
            error ( "[declaracion_subprograma]: Error de función o sub." +
                    "No.Linea: " + cmp.be.preAnalisis.numLinea );
        }
    }
    
    //------------------------------------------------------------------------------------
    

    private void declaracion_funcion (  ) {
        
        if ( cmp.be.preAnalisis.complex.equals ( "function" ) ) {
            emparejar ( "function" );
            emparejar  ( "id"     );
            argumentos (  );
            emparejar  ( "as"     );
            tipo       (  );
            proposiciones_optativas (  );
            emparejar ( "end"      );
            emparejar ( "function" );
        } else {
            error ( "[declaracion_funcion]: Error de función o declaración." +
                    "No.Linea: " + cmp.be.preAnalisis.numLinea );
        }
    }
    
    //------------------------------------------------------------------------------------
    

    private void declaracion_subrutina (  ) {

        if ( cmp.be.preAnalisis.complex.equals ( "sub" ) ) {
            emparejar ( "sub" );
            emparejar ( "id" );
            argumentos ( );
            proposiciones_optativas (  );
            emparejar ( "end" );
            emparejar ( "sub" );
        } else {
            error ( "[declaracion_subrutina]: Se esperaba la palabra sub." +
                    "No.Linea: " + cmp.be.preAnalisis.numLinea );
        }
    }
    
    //------------------------------------------------------------------------------------
    

    private void argumentos (  ) {
        if ( cmp.be.preAnalisis.complex.equals ( "(" ) ) {
            emparejar ( "(" );
            lista_declaraciones ( );
            emparejar ( ")" );
        } else {
            // argumentos -> empty 

        }
    }
    
    //------------------------------------------------------------------------------------
    

    private void proposiciones_optativas (  ) {
        
        if ( cmp.be.preAnalisis.complex.equals ( "id"   ) || 
             cmp.be.preAnalisis.complex.equals ( "call" ) ||
             cmp.be.preAnalisis.complex.equals ( "if"   ) ||
             cmp.be.preAnalisis.complex.equals ( "do"   ) ) {

            proposicion ( new Atributos() );
            proposiciones_optativas (  );            
            
        } else {
            // proposiciones_optativas -> empty 

        }
    }
    
    //------------------------------------------------------------------------------------
    

    private void proposicion ( Atributos proposicion ) {
        
        Atributos condicion = new Atributos();
        Atributos proposiciones_optativas1 = new Atributos();
        Atributos proposiciones_optativas2 = new Atributos();
        Atributos proposiciones_optativas3 = new Atributos();
        Atributos expresion = new Atributos();
        Linea_BE id = new Linea_BE();
        if ( cmp.be.preAnalisis.complex.equals ( "id" ) ) {
            // proposicion -> id opasig expresion {1}
            id = cmp.be.preAnalisis;
            emparejar ( "id"     );
            emparejar ( "opasig" );
            
            expresion ( expresion );
            //Accion semantica 1
            conversion( expresion.tipo );
            emite( id.lexema + ":=" + tempFinal );
            cmp.cua.agregar(new Cuadruplo(":=",tempFinal,"",id.lexema));
            //--------------------------------
 
        } else if (cmp.be.preAnalisis.complex.equals ( "call" ) ) {
            emparejar ( "call" );
            emparejar ( "id" );
            proposicion_prima(  );
            
        } else if ( cmp.be.preAnalisis.complex.equals ( "if" ) ) {
            // proposicion -> if {2} condicion then proposiciones_optativas else {3} proposiciones_optativas end if
            emparejar               ( "if"                                     );
            //Accion semantica 2
            condicion.verdadero = etiqNueva();
            condicion.falso = etiqNueva();
            proposicion.siguiente = etiqNueva();
            proposiciones_optativas2.siguiente = proposicion.siguiente;
            proposiciones_optativas3.siguiente = proposicion.siguiente;
            //---------------------
            
            condicion               ( condicion );
            emparejar               ( "then"                                    );
            proposiciones_optativas (  );
            emparejar               ( "else"                                    );
            
            //Accion semantica 3
            //proposicion.codigo =
            emite("goto" + proposicion.siguiente);
            cmp.cua.agregar(new Cuadruplo("goto", "", "", proposicion.siguiente));
            emite(condicion.falso+":");
            cmp.cua.agregar(new Cuadruplo("", "", "", condicion.falso));
            //------------------------
            proposiciones_optativas (  ); 
            emparejar ( "end" );
            emparejar ( "if"  );
        } else if ( cmp.be.preAnalisis.complex.equals ( "do" ) ) {
            // proposicion -> do while {4} condicion proposiciones_optativas {5} loop
            emparejar ( "do"    );
            emparejar ( "while" );
            
            //Accion semantica 4
            proposicion.comienzo = etiqNueva();
            //proposicion.siguiente = etiqNueva();
            condicion.verdadero = etiqNueva();
            condicion.falso = etiqNueva();
            proposiciones_optativas1.siguiente = proposicion.comienzo;
            emite(proposicion.comienzo+":");
            cmp.cua.agregar(new Cuadruplo("", "", "", proposicion.comienzo));
            //--------------------
            condicion ( condicion );
            proposiciones_optativas (  );
            //Accion semantica 5
            emite("goto" + proposicion.comienzo);
            cmp.cua.agregar(new Cuadruplo("goto", "", "", condicion.falso));
            emite(condicion.falso+":");
            cmp.cua.agregar(new Cuadruplo("", "", "", condicion.falso));
            //-------------------------           
            emparejar ( "loop" );
        } else {
            error ( "[proposicion]: Se esperaba do." +
                    "No.Linea: " + cmp.be.preAnalisis.numLinea );
        }
    }
    
    //------------------------------------------------------------------------------------
    

    private void proposicion_prima (  ) {
        
        if ( cmp.be.preAnalisis.complex.equals( "(" ) ) {
            emparejar ( "(" );
            lista_expresiones (  );
            emparejar ( ")" );
            
        } else {
            // proposicion_prima -> empty 

        }
    }
    
    //------------------------------------------------------------------------------------
    

    private void lista_expresiones (  ) {
        
        if ( cmp.be.preAnalisis.complex.equals ( "id"      ) || 
             cmp.be.preAnalisis.complex.equals ( "num"     ) ||  
             cmp.be.preAnalisis.complex.equals ( "num.num" ) || 
             cmp.be.preAnalisis.complex.equals ( "("       ) ||
             cmp.be.preAnalisis.complex.equals ( "literal"       ) )  {
            expresion ( new Atributos() );
            lista_expresiones_prima (  );
            
        } else {
            // lista_expresiones -> empty 
            
        }
    }
    
    //------------------------------------------------------------------------------------
    

    private void lista_expresiones_prima (  ) {
        
        if ( cmp.be.preAnalisis.complex.equals ( "," ) ) {
            emparejar ( "," );
            expresion ( new Atributos() );
            lista_expresiones_prima (  );
            
        } else {
            // lista_expresiones_prima -> empty 
            
            
        }
    }
    
    //------------------------------------------------------------------------------------
    

    private void condicion ( Atributos condicion ) {
        
        Atributos expresion = new Atributos();
        Atributos expresion2 = new Atributos();
        Linea_BE  oprel = new Linea_BE();
        
        if ( cmp.be.preAnalisis.complex.equals ( "id"      )    ||
             cmp.be.preAnalisis.complex.equals ( "num"     )    ||
             cmp.be.preAnalisis.complex.equals ( "num.num" )    ||
             cmp.be.preAnalisis.complex.equals ( "("       )    ||
             cmp.be.preAnalisis.complex.equals("literal")   ) {
            // condicion -> expresion oprel expresion {6}
           
            expresion ( expresion );
            oprel = cmp.be.preAnalisis;
            emparejar ( "oprel" );

            expresion ( expresion2 );
            //Accion semantica 6
            conversion(expresion.tipo);
            String t1 = tempFinal;
            conversion(expresion2.tipo);
            String t2 = tempFinal;
            emite("if " + t1 + oprel.lexema + t2 + " goto " + condicion.verdadero);
            cmp.cua.agregar(new Cuadruplo(oprel.lexema, t1, t2, condicion.verdadero));
            emite("goto "+condicion.falso);
            cmp.cua.agregar(new Cuadruplo("goto", "", "", condicion.falso));
            emite(condicion.verdadero+":");
            cmp.cua.agregar(new Cuadruplo("", "", "", condicion.verdadero));
            //-------------------
             
            
        } else {
            error ( "[condicion]: Error de condición. Se esperaba [id, num , num.num ,(]" +
                    "No.Linea: " + cmp.be.preAnalisis.numLinea );
        }
    }
    
    //------------------------------------------------------------------------------------
    

    private void expresion ( Atributos expresion ) {
        
        Atributos termino = new Atributos ();
        Atributos expresion_prima = new Atributos();
        Linea_BE literal = new Linea_BE();
        
        if ( cmp.be.preAnalisis.complex.equals ( "id" )        ||
             cmp.be.preAnalisis.complex.equals ( "num" )       ||
             cmp.be.preAnalisis.complex.equals ( "num.num" )   ||
             cmp.be.preAnalisis.complex.equals ( "(" ) ) {
            // expresion -> termino  expresion_prima {7}
            termino ( termino );
            expresion_prima ( expresion_prima );
            //Accion semantica 7
            expresion.tipo = termino.tipo + expresion_prima.tipo; 
            //---------------
            
        } else if ( cmp.be.preAnalisis.complex.equals ( "literal" ) ) {
            // expresion -> literal {8}
            literal = cmp.be.preAnalisis;
            emparejar ( "literal" );
            //Accion semantica 8
            expresion.tipo = literal.lexema;
            //-----------
            
            
        } else {  
            error ( "[expresion]: Expresión no valida:." +cmp.be.preAnalisis.lexema +
                    ". No.Linea: " + cmp.be.preAnalisis.numLinea );
        }
    }
    
    //------------------------------------------------------------------------------------
    

    private void expresion_prima ( Atributos expresion_prima ) {
        
        Atributos termino = new Atributos();
        Atributos expresion2 = new Atributos();
        Linea_BE oparit = new Linea_BE();
        
        if ( cmp.be.preAnalisis.complex.equals ( "opsuma" ) ) {
            // expresion_prima -> opsuma termino expresion_prima {9}
            oparit = cmp.be.preAnalisis;
            emparejar ( "opsuma" );
            termino   ( termino );
            expresion_prima ( expresion2 ); 
           //Accion semantica 9
           expresion_prima.tipo = oparit.lexema + termino.tipo + expresion2.tipo ;
 
           
           //--------------
            
        } else {
            // expresion_prima -> empty {10}
            //Accion semantica 10
            expresion_prima.tipo = "";
            //-------------------
          
        }
    }
    
    //------------------------------------------------------------------------------------
    
     private void termino ( Atributos termino ) {
        
         Atributos factor = new Atributos();
         Atributos termino_prima = new Atributos();
         
        if ( cmp.be.preAnalisis.complex.equals ( "id" )        ||
             cmp.be.preAnalisis.complex.equals ( "num" )       ||
             cmp.be.preAnalisis.complex.equals ( "num.num" )   ||
             cmp.be.preAnalisis.complex.equals ( "(" ) ) {
            // termino -> factor termino_prima {11}
            factor ( factor );
            
            termino_prima ( termino_prima );
            //Accion semantica 11
            termino.tipo = factor.tipo + termino_prima.tipo;
            //---------------
        } else {
            error ( "[termino]: Error de término.Se esperaba id, num , num.num o (" +
                    "No.Linea: " + cmp.be.preAnalisis.numLinea );
        }
    }
    
    //------------------------------------------------------------------------------------
    

    private void termino_prima ( Atributos termino_prima ) {
        
        Linea_BE oparit = new Linea_BE();
        Atributos factor = new Atributos();
     
        if ( cmp.be.preAnalisis.complex.equals ( "opmult" ) ) {
            // termino_prima -> opmult factor termino_prima {12}
            oparit = cmp.be.preAnalisis;
            emparejar ( "opmult" );
           
            factor    ( factor );
            
            termino_prima ( termino_prima );
            
            //Accion semantica 12
            termino_prima.tipo = oparit.lexema + factor.tipo + termino_prima.tipo;
            
            //----------------
            
        } else {
            // termino -> empty {13}
            //Accion semantica 13
            termino_prima.tipo = "";
            //--------
        }
    }
    
    //------------------------------------------------------------------------------------
    

    private void factor ( Atributos factor ) {
        
        Linea_BE id = new Linea_BE();
        Linea_BE num = new Linea_BE();
        Linea_BE numnum = new Linea_BE();
        Atributos expresion = new Atributos();
        
        if ( cmp.be.preAnalisis.complex.equals ( "id" ) ) {
            
            id = cmp.be.preAnalisis;
            emparejar ( "id" );
            factor_prima (  );
            
            //Accion semantica 14
            factor.tipo = id.lexema;
            //
            
        } else if ( cmp.be.preAnalisis.complex.equals ( "num" ) ) {
            // factor -> num {15}
            num = cmp.be.preAnalisis;
            emparejar ( "num" );
            //Accion semantica 15
            factor.tipo= num.lexema;
            //------------
       
        } else if ( cmp.be.preAnalisis.complex.equals ( "num.num") ) {
            // factor -> num.num {16}
            numnum = cmp.be.preAnalisis;
            emparejar ( "num.num" );
            //Accion semantica 16
            factor.tipo = numnum.lexema;
            //--------
        } else if ( cmp.be.preAnalisis.complex.equals ( "(" ) ) {
            emparejar ( "(" );
            expresion ( expresion );
            emparejar ( ")" );
            
            //Accion semantica 17
            factor.tipo = "(" + expresion.tipo + ")";
            //-----------------------

        } else {
            error ( "[factor]: Se esperaba un (." +
                    "No.Linea: " + cmp.be.preAnalisis.numLinea );
        }
    }
    
    //------------------------------------------------------------------------------------
    

    private void factor_prima (  ) {
                
        if ( cmp.be.preAnalisis.complex.equals( "(" ) ) {
            emparejar ( "(" );
            lista_expresiones (  );
            emparejar ( ")" ); 
        } else {
            // factor_prima -> empty 
            
       
        }
    }
}
