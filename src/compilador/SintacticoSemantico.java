/*:-----------------------------------------------------------------------------
 *:                       INSTITUTO TECNOLOGICO DE LA LAGUNA
 *:                     INGENIERIA EN SISTEMAS COMPUTACIONALES
 *:                         LENGUAJES Y AUTOMATAS II           
 *: 
 *:                  SEMESTRE: ___________    HORA: ___________ HRS
 *:                                   
 *:               
 *:         Clase con la funcionalidad del Analizador Sintactico
 *                 
 *:                           
 *: Archivo       : SintacticoSemantico.java
 *: Autor         : Fernando Gil  ( Estructura general de la clase  )
 *:                 Grupo de Lenguajes y Automatas II ( Procedures  )
 *: Fecha         : 03/SEP/2014
 *: Compilador    : Java JDK 7
 *: Descripción   : Esta clase implementa un parser descendente del tipo 
 *:                 Predictivo Recursivo. Se forma por un metodo por cada simbolo
 *:                 No-Terminal de la gramatica mas el metodo emparejar ().
 *:                 El analisis empieza invocando al metodo del simbolo inicial.
 *: Ult.Modif.    :
 *:  Fecha      Modificó            Modificacion
 *:=============================================================================
 *: 22/Feb/2015 FGil                -Se mejoro errorEmparejar () para mostrar el
 *:                                 numero de linea en el codigo fuente donde 
 *:                                 ocurrio el error.
 *: 08/Sep/2015 FGil                -Se dejo lista para iniciar un nuevo analizador
 *:                                 sintactico.
 *:-----------------------------------------------------------------------------
 */
package compilador;

import general.Linea_BE;
import javax.swing.JOptionPane;

public class SintacticoSemantico {

    
      private Compilador cmp;
    private boolean    analizarSemantica = false;
    
    public static final String VACIO = "vacio";
    public static final String ERROR_TIPO = "error_tipo";
    
    //--------------------------------------------------------------------------
    // Constructor de la clase, recibe la referencia de la clase principal del 
    // compilador.
    //

    public SintacticoSemantico(Compilador c) {
        cmp = c;
    }
   // Funciones del Dominio y Rango de los "tipo".
    
    public String getDmno (String tipo) {
        String [] partes = tipo.split("->");
        return partes[0];
    }
    
    public String getRng(String tipo) {
        String [] partes = tipo.split("->");
        return partes[1];
    }
    public static boolean tiposFunciones(String declaracion, String llamada){
        String remplazad = declaracion.replaceAll("->", "x");
        String [] partesDeclaracion = remplazad.split("x");
        
        String[]aux = new String[partesDeclaracion.length-1];
        for(int i = 0; i < aux.length; i++){
            aux[i]=partesDeclaracion[i];
            }
        
        partesDeclaracion=aux;

        String remplazall = llamada.replaceAll("->", "x");
        String [] partesLLamada = remplazall.split("x");

        boolean correcto = true;
        
         if(partesDeclaracion.length==partesLLamada.length){
        for(int i=0;i<partesDeclaracion.length;i++){
            if(partesDeclaracion[i].equals(partesLLamada[i]))
                correcto = true;
            else if (partesDeclaracion[i].equals("SINGLE") && partesLLamada[i].equals("INTEGER"))
                correcto = true;
            else
                return false;
        }
         }
         else 
             return false;
        
        
        return correcto;
    }


    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    // Metodo que inicia la ejecucion del analisis sintactico predictivo.
    // analizarSemantica : true = realiza el analisis semantico a la par del sintactico
    //                     false= realiza solo el analisis sintactico sin comprobacion semantica

    public void analizar(boolean analizarSemantica) {
        this.analizarSemantica = analizarSemantica;

        // * * *   INVOCAR AQUI EL PROCEDURE DEL SIMBOLO INICIAL   * * *
        programa(new Atributos());
        
    }
//prueba2
    //--------------------------------------------------------------------------

    private void emparejar(String t) {
        if (cmp.be.preAnalisis.complex.equals(t)) {
            cmp.be.siguiente();
       
        } else {
            errorEmparejar( t, cmp.be.preAnalisis.lexema, cmp.be.preAnalisis.numLinea );
        }
    }
    
    //--------------------------------------------------------------------------
    // Metodo para devolver un error al emparejar
    //--------------------------------------------------------------------------
 
    private void errorEmparejar(String _token, String _lexema, int numLinea ) {
        String msjError = "";

        if (_token.equals("id")) {
            msjError += "Se esperaba un identificador";
        } else if (_token.equals("num")) {
            msjError += "Se esperaba una constante entera";
        } else if (_token.equals("num.num")) {
            msjError += "Se esperaba una constante real";
        } else if (_token.equals("literal")) {
            msjError += "Se esperaba una literal";
        } else if (_token.equals("oparit")) {
            msjError += "Se esperaba un operador aritmetico";
        } else if (_token.equals("oprel")) {
            msjError += "Se esperaba un operador relacional";
        } else if (_token.equals("opasig")) {
            msjError += "Se esperaba operador de asignacion";
        } else {
            msjError += "Se esperaba " + _token;
        }
        msjError += " se encontró " + ( _lexema.equals ( "$" )? "fin de archivo" : _lexema ) + 
                    ". Linea " + numLinea;        // FGil: Se agregó el numero de linea

        cmp.me.error(Compilador.ERR_SINTACTICO, msjError);
    }

    // Fin de ErrorEmparejar
    //--------------------------------------------------------------------------
    // Metodo para mostrar un error sintactico

    private void error(String _descripError) {
        cmp.me.error(cmp.ERR_SINTACTICO, _descripError);
    }

    // Fin de error
    //--------------------------------------------------------------------------
    //  *  *   *   *    PEGAR AQUI EL CODIGO DE LOS PROCEDURES  *  *  *  *
    //--------------------------------------------------------------------------

    private void programa ( Atributos programa ) {
        
        Atributos declaraciones              = new Atributos();
        Atributos declaraciones_subprogramas = new Atributos();
        Atributos proposiciones_optativas    = new Atributos();
        
        if ( cmp.be.preAnalisis.complex.equals ( "dim"      ) ||
             cmp.be.preAnalisis.complex.equals ( "function" ) ||
             cmp.be.preAnalisis.complex.equals ( "sub"      ) ||
             cmp.be.preAnalisis.complex.equals ( "id"       ) ||
             cmp.be.preAnalisis.complex.equals ( "if"       ) ||
             cmp.be.preAnalisis.complex.equals ( "call"     ) ||  
             cmp.be.preAnalisis.complex.equals ( "do"       ) ||  
             cmp.be.preAnalisis.complex.equals ( "end"      ) ) {
            
            // programa -> declaraciones declaraciones_subprogramas proposiciones_optativas end {1}
            declaraciones              ( declaraciones              );
            declaraciones_subprogramas ( declaraciones_subprogramas );
            proposiciones_optativas    ( proposiciones_optativas    );
            emparejar                  ( "end"                    );
            
            // ACCION SEMANTICA 1
            if ( analizarSemantica ) {
                if (  declaraciones.tipo.equals             ( VACIO      ) &&
                     !declaraciones_subprogramas.tipo.equals( ERROR_TIPO ) &&
                     !proposiciones_optativas.tipo.equals   ( ERROR_TIPO ) )
                    programa.tipo = VACIO;
                else
                {
                    programa.tipo = ERROR_TIPO;
                    
                    cmp.me.error( cmp.ERR_SEMANTICO
                    ,    "Error en declaraciones    |"
                       + " declaraciones_subprograma |"
                       + " proposiciones_optativas {1}"   );
                }
            }
            // FIN ACCION SEMANTICA
            
        } else {
                    error ( "[programa]: Inicio incorrecto de programa."
                    + "Se esperaba una de estas opciones [dim,function,sub,id,if,call,do,end]. "
                    + "Se encontro "+cmp.be.preAnalisis.lexema + "en la linea: "+
                    "No.Linea: " + cmp.be.preAnalisis.numLinea );
        }
    }
    
    

    private void declaraciones ( Atributos declaraciones ) {
        
        Atributos lista_declaraciones = new Atributos();
        Atributos declaraciones2      = new Atributos();
        
        if (cmp.be.preAnalisis.complex.equals ( "dim" ) ) {
            // declaraciones -> dim lista_declaraciones declaraciones {2}
            emparejar           ( "dim"                   );
            lista_declaraciones ( lista_declaraciones       );
            declaraciones       ( declaraciones2 );
            
            // ACCION SEMANTICA 2
            if ( analizarSemantica ) {
                if ( !lista_declaraciones.tipo.equals ( ERROR_TIPO ) &&
                     !declaraciones2.tipo.equals      ( ERROR_TIPO ) )
                      declaraciones.tipo = VACIO;
                else
                {
                    declaraciones.tipo = ERROR_TIPO;
                    cmp.me.error( cmp.ERR_SEMANTICO,"ERROR_TIPO en lista_declaraciones  o "
                                                                 + "ERROR_TIPO en declaraciones2.tipo, se esperaba un id {2}" );
                }
            }
            // FIN ACCION SEMANTICA
            
        } else {
            // declaraciones -> empty {3}
            // ACCION SEMANTICA 3
            if ( analizarSemantica ) {
                declaraciones.tipo = VACIO;
            }
            // 
        }
    }
    
    

    private void lista_declaraciones ( Atributos lista_declaraciones ) {
        
        Linea_BE id                         = new Linea_BE   ();
        Atributos tipo                      = new Atributos ();
        Atributos lista_declaraciones_prima = new Atributos ();
        
        if ( cmp.be.preAnalisis.complex.equals ( "id" ) ) {
            
            // Se salvan los atributos de id
            id = cmp.be.preAnalisis;
            
            // lista_declaraciones -> id as tipo lista_declaraciones_prima {4}
            emparejar ( "id" );
            emparejar ( "as" );
            tipo ( tipo );
            lista_declaraciones_prima ( lista_declaraciones_prima );
            
            // ACCION SEMANTICA 4
            if ( analizarSemantica ) {
                if ( cmp.ts.buscaTipo ( id.entrada ).equals ( "" ) ) {
                    // Si no esta declarado el ID, lo agregamos
                    cmp.ts.anadeTipo( id.entrada, tipo.tipo );
                    
                    // Si solo es una declaración, la agregamos
                    if ( lista_declaraciones_prima.tipo.equals( VACIO ) ) {
                        lista_declaraciones.tipo = tipo.tipo;
                    // Si son más de de dos, las concatenamos.
                    } else if ( !lista_declaraciones.tipo.equals( ERROR_TIPO ) ) {
                        lista_declaraciones.tipo = tipo.tipo + "x" + lista_declaraciones_prima.tipo;
                    } else {
                        lista_declaraciones.tipo = ERROR_TIPO;
                        cmp.me.error( cmp.ERR_SEMANTICO, "ERROR_TIPO en lista_declaraciones {4}" );
                    }
                } else {
                    lista_declaraciones.tipo = ERROR_TIPO;
                    cmp.me.error( cmp.ERR_SEMANTICO, "ERROR: el identificador ya se ha declarado {4}" );
                }
            }
            // FIN ACCION SEMANTICA
            
        } else {
            error ( "[lista_declaraciones]: Se esperaba una declaración." +
                    "No.Linea: " + cmp.be.preAnalisis.numLinea );
        }
    }
    

    private void lista_declaraciones_prima ( Atributos lista_declaraciones_prima ) {
        
        Atributos lista_declaraciones = new Atributos ();
        
        if ( cmp.be.preAnalisis.complex.equals ( "," ) ) {
            // lista_declaraciones_prima -> , lista_declaraciones {5}
            
            emparejar ( "," );
            lista_declaraciones ( lista_declaraciones );
            
            // -----------------------ACCION SEMANTICA 5-----------------------
            if ( analizarSemantica ) {
                lista_declaraciones_prima.tipo = lista_declaraciones.tipo;
            }
            // ----------------------------------------------------------------
            
        } else {
            // lista_declaraciones_prima -> empty {6}
            // -----------------------ACCION SEMANTICA 6-----------------------
            if ( analizarSemantica ) {
                lista_declaraciones_prima.tipo = VACIO;
            }
            // ----------------------------------------------------------------
        }
    }
    
    //------------------------------------------------------------------------------------
    

    private void tipo ( Atributos tipo ) {
        if ( cmp.be.preAnalisis.complex.equals ( "integer" ) ) {
            // tipo -> integer {7}
            emparejar ( "integer" );
            
            // -----------------------ACCION SEMANTICA 7-----------------------
            if ( analizarSemantica ) {
                tipo.tipo = "INTEGER";
            }
            // ----------------------------------------------------------------
        } else if ( cmp.be.preAnalisis.complex.equals ( "single" ) ) {
            // tipo -> single {8}
            emparejar ( "single" );
            
            // -----------------------ACCION SEMANTICA 8-----------------------
            if ( analizarSemantica ) {
                tipo.tipo = "SINGLE";
            }
            // ----------------------------------------------------------------
        } else if ( cmp.be.preAnalisis.complex.equals ( "string" ) ) {
            // tipo -> string {9}
            emparejar ( "string" );
            
            // -----------------------ACCION SEMANTICA 9-----------------------
            if ( analizarSemantica ) {
                tipo.tipo = "STRING";
            }
            // ----------------------------------------------------------------
        } else {
            error ( "[tipo]: Tipo de dato no valido. " +
                    "No.Linea: " + cmp.be.preAnalisis.numLinea );
        }
    }
    
    //------------------------------------------------------------------------------------
    

    private void declaraciones_subprogramas ( Atributos declaraciones_subprogramas ) {
        
        Atributos declaracion_subprograma     = new Atributos ();
        Atributos declaraciones_subprogramas1 = new Atributos ();
        
        if ( cmp.be.preAnalisis.complex.equals ( "function" ) ||
             cmp.be.preAnalisis.complex.equals ( "sub" ) ) {
            // declaraciones_subprogramas -> declaracion_subprograma declaraciones_subprogramas {10}
            declaracion_subprograma    ( declaracion_subprograma                          );
            declaraciones_subprogramas ( declaraciones_subprogramas1 );
            
            // -----------------------ACCION SEMANTICA 10-----------------------
            if ( analizarSemantica ) {
                if ( declaracion_subprograma.tipo.equals     ( VACIO ) &&
                     declaraciones_subprogramas1.tipo.equals ( VACIO ) ) {
                     declaraciones_subprogramas.tipo = VACIO;
                } else {
                    declaraciones_subprogramas.tipo = ERROR_TIPO;
                    cmp.me.error( cmp.ERR_SEMANTICO, "ERROR_TIPO declaracion_subprogrma o "
                                                                  + "ERROR_TIPO declaraciones_subprogramas1 {10}" );
                }
            }
        } else {
            // declaraciones_subprogramas -> empty {11}
            // -----------------------ACCION SEMANTICA 11-----------------------
            if ( analizarSemantica ) {
                declaraciones_subprogramas.tipo = VACIO;
            }
            // -----------------------------------------------------------------
        }
    }
    
    //------------------------------------------------------------------------------------
    

    private void declaracion_subprograma ( Atributos declaracion_subprograma ) {
        
        Atributos declaracion_funcion   = new Atributos ();
        Atributos declaracion_subrutina = new Atributos ();
        
        if ( cmp.be.preAnalisis.complex.equals ( "function" ) ) {
            // declaracion_subprograma -> declaracion_funcion {12}
            declaracion_funcion ( declaracion_funcion );
            
            // -----------------------ACCION SEMANTICA 12-----------------------
            if ( analizarSemantica ) {

                declaracion_subprograma.tipo = declaracion_funcion.tipo;
            }
            // -----------------------------------------------------------------
        } else if ( cmp.be.preAnalisis.complex.equals( "sub" ) ) {
            // declaracion_subprograma -> declaracion_subrutina {13}
            declaracion_subrutina ( declaracion_subrutina );
            
            // -----------------------ACCION SEMANTICA 13-----------------------
            if ( analizarSemantica ) {
                declaracion_subprograma.tipo = declaracion_subrutina.tipo;
            }
            // -----------------------------------------------------------------
        } else {
            error ( "[declaracion_subprograma]: Error de función o sub." +
                    "No.Linea: " + cmp.be.preAnalisis.numLinea );
        }
    }
    
    //------------------------------------------------------------------------------------
    

    private void declaracion_funcion ( Atributos declaracion_funcion ) {
        
        Linea_BE id                       = new Linea_BE ();
        Atributos argumentos              = new Atributos ();
        Atributos tipo                    = new Atributos ();
        Atributos proposiciones_optativas = new Atributos ();
        
        if ( cmp.be.preAnalisis.complex.equals ( "function" ) ) {
            // declaracion_funcion -> function id argumentos as tipo proposiciones_optativas {14} end function
            emparejar ( "function" );
            
            // Se salvan los atributos de id
            id = cmp.be.preAnalisis;
            
            emparejar  ( "id"     );
            argumentos ( argumentos );
            emparejar  ( "as"     );
            tipo       ( tipo       );
            proposiciones_optativas ( proposiciones_optativas );
            
            // ACCION SEMANTICA 14
            if ( analizarSemantica ) {
                if ( cmp.ts.buscaTipo ( id.entrada ). equals("") ) {
                    // Es la primera vez que se declara esta función
                    if ( !argumentos.tipo.equals ( ERROR_TIPO ) ) {
                        cmp.ts.anadeTipo( id.entrada , argumentos.tipo + "->" + tipo.tipo );
                        if ( proposiciones_optativas.tipo.equals( VACIO ) ) 
                            declaracion_funcion.tipo = VACIO;
                        else {
                            declaracion_funcion.tipo = ERROR_TIPO;
                            cmp.me.error( cmp.ERR_SEMANTICO, "ERROR en proposiciones_optativas" );
                        }
                    } else {
                        declaracion_funcion.tipo = ERROR_TIPO;
                        cmp.me.error( cmp.ERR_SEMANTICO, "ERROR en argumentos, no contiene un tipo de dato" );
                    }
                } else {
                    declaracion_funcion.tipo = ERROR_TIPO;
                    cmp.me.error( cmp.ERR_SEMANTICO, "Esta funcion ya se habia declarado" );
                }
            }
            // -----------------------------------------------------------------
            
            emparejar ( "end"      );
            emparejar ( "function" );
        } else {
            error ( "[declaracion_funcion]: Error de función o declaración." +
                    "No.Linea: " + cmp.be.preAnalisis.numLinea );
        }
    }
    
    //------------------------------------------------------------------------------------
    

    private void declaracion_subrutina ( Atributos declaracion_subrutina ) {
        
        Linea_BE id                       = new Linea_BE ();
        Atributos argumentos              = new Atributos ();
        Atributos proposiciones_optativas = new Atributos ();
        
        if ( cmp.be.preAnalisis.complex.equals ( "sub" ) ) {
            // declaracion_subrutina -> sub id argumentos proposiciones_optativas {15} end sub
            emparejar ( "sub" );
            
            // Se salvan los atributos de id
            id = cmp.be.preAnalisis;
            
            emparejar ( "id" );
            argumentos ( argumentos );
            proposiciones_optativas ( proposiciones_optativas );
            
            // -----------------------ACCION SEMANTICA 15-----------------------
            if ( analizarSemantica ) {
                if ( cmp.ts.buscaTipo ( id.entrada ). equals("") ) {
                    // Es la primera vez que se declara esta función
                    if ( !argumentos.tipo.equals ( ERROR_TIPO ) ) {
                        cmp.ts.anadeTipo( id.entrada , argumentos.tipo + "->" + "VOID" );
                        if ( proposiciones_optativas.tipo.equals( VACIO ) ) 
                            declaracion_subrutina.tipo = VACIO;
                        else {
                            declaracion_subrutina.tipo = ERROR_TIPO;
                            cmp.me.error( cmp.ERR_SEMANTICO, "ERROR en proposiciones_optativas" );
                        }
                    } else {
                        declaracion_subrutina.tipo = ERROR_TIPO;
                        cmp.me.error( cmp.ERR_SEMANTICO, "ERROR en argumentos, no contiene un tipo de datos" );
                    }
                } else {
                    declaracion_subrutina.tipo = ERROR_TIPO;
                    cmp.me.error( cmp.ERR_SEMANTICO, "Esta Función ya ha sido declarada" );
                }
            }
            // -----------------------------------------------------------------
            
            emparejar ( "end" );
            emparejar ( "sub" );
        } else {
            error ( "[declaracion_subrutina]: Se esperaba la palabra sub." +
                    "No.Linea: " + cmp.be.preAnalisis.numLinea );
        }
    }
    
    //------------------------------------------------------------------------------------
    

    private void argumentos ( Atributos argumentos ) {
        
        Atributos lista_declaraciones = new Atributos ();
        
        if ( cmp.be.preAnalisis.complex.equals ( "(" ) ) {
            // argumentos -> ( lista_declaraciones ) {16}
            emparejar ( "(" );
            lista_declaraciones ( lista_declaraciones );
            emparejar ( ")" );
            
            // -----------------------ACCION SEMANTICA 16-----------------------
            if ( analizarSemantica ) {
                argumentos.tipo = lista_declaraciones.tipo;
            }
            // -----------------------------------------------------------------
            
        } else {
            // argumentos -> empty {17}
            // -----------------------ACCION SEMANTICA 17-----------------------
            if ( analizarSemantica ) {
                argumentos.tipo = "VOID";
            }
            // -----------------------------------------------------------------
        }
    }
    
    //------------------------------------------------------------------------------------
    

    private void proposiciones_optativas ( Atributos proposiciones_optativas ) {
        
        Atributos proposicion = new Atributos ();
        Atributos proposiciones_optativas1 = new Atributos ();
        
        if ( cmp.be.preAnalisis.complex.equals ( "id"   ) || 
             cmp.be.preAnalisis.complex.equals ( "call" ) ||
             cmp.be.preAnalisis.complex.equals ( "if"   ) ||
             cmp.be.preAnalisis.complex.equals ( "do"   ) ) {
            
            // proposiciones_optativas -> proposicion proposiciones_optativas {18}
            proposicion ( proposicion );
            proposiciones_optativas ( proposiciones_optativas1 );
            
            // -----------------------ACCION SEMANTICA 18-----------------------
            if ( analizarSemantica ) {
                if ( proposicion.tipo.equals( VACIO ) && 
                     proposiciones_optativas1.tipo.equals( VACIO ) ) {
                    proposiciones_optativas.tipo = VACIO;
                } else {
                    proposiciones_optativas.tipo = ERROR_TIPO;
                    cmp.me.error( cmp.ERR_SEMANTICO, "Error en proposiciones, proposiciones_optativas" );
                }
            }
            // -----------------------------------------------------------------
            
        } else {
            // proposiciones_optativas -> empty {19}
            // -----------------------ACCION SEMANTICA 19-----------------------
            if ( analizarSemantica ) {
                proposiciones_optativas.tipo = VACIO;
            }
            // -----------------------------------------------------------------
        }
    }
    
    //------------------------------------------------------------------------------------
    

    private void proposicion ( Atributos proposicion ) {
        
        Atributos expresion                = new Atributos ();
        Atributos proposicion_prima        = new Atributos ();
        Atributos condicion                = new Atributos ();
        Atributos proposiciones_optativas1 = new Atributos ();
        Atributos proposiciones_optativas2 = new Atributos ();
        Atributos condicion1               = new Atributos ();
        Atributos proposiciones_optativas3 = new Atributos ();
        Linea_BE id                        = new Linea_BE ();
      //  String id2="";
        
      String varNoDeclarada="";//variable para guardar un lexema no declarado
      String varNoDeclarada2="";//variable para guardar un lexema no declarado
      String tipo="";

        if ( cmp.be.preAnalisis.complex.equals ( "id" ) ) {
            // proposicion -> id opasig expresion {20}
            
            id = cmp.be.preAnalisis;

             //-------------------------------------------------------------------
            tipo=cmp.ts.buscaTipo(cmp.be.preAnalisis.entrada);
            if(tipo == "" ){
                 if(cmp.be.preAnalisis.complex != "literal" && cmp.be.preAnalisis.complex != "num" && cmp.be.preAnalisis.complex != "num.num")
                     varNoDeclarada2=cmp.be.preAnalisis.lexema;
            }
            //            id2 = cmp.be.preAnalisis.lexema;
            //------------------------------------------------------------------
 
            
            emparejar ( "id"     );
            emparejar ( "opasig" );
            
           //-------------------------------------------------------------------
            tipo=cmp.ts.buscaTipo(cmp.be.preAnalisis.entrada);
            if(tipo == "" ){
                 if(cmp.be.preAnalisis.complex != "literal" && cmp.be.preAnalisis.complex != "num" && cmp.be.preAnalisis.complex != "num.num")
                     varNoDeclarada=cmp.be.preAnalisis.lexema;
            }
            //            id2 = cmp.be.preAnalisis.lexema;
            //------------------------------------------------------------------
            expresion ( expresion  );

            
            // -----------------------ACCION SEMANTICA 20-----------------------
            if ( analizarSemantica ) {

                   if (!cmp.ts.buscaTipo( id.entrada ).equals ( "" ) && cmp.ts.buscaTipo( id.entrada ).equals ( expresion.tipo ) ||
                        cmp.ts.buscaTipo( id.entrada ).equals ( "SINGLE"       )
                        && expresion.tipo.equals("INTEGER")   )
                    
                         proposicion.tipo = VACIO;
                
                else {
                    proposicion.tipo = ERROR_TIPO;
                    
                    
                    if(!varNoDeclarada.equals("") && !varNoDeclarada2.equals(""))
                        cmp.me.error( cmp.ERR_SEMANTICO, "Variables no declaradas "+varNoDeclarada +" y "+varNoDeclarada2+ " {20} "+
                    "No.Linea: " + cmp.be.preAnalisis.numLinea );
                    else    if(!varNoDeclarada.equals("")  )
                    cmp.me.error( cmp.ERR_SEMANTICO, "Variable no declarada "+varNoDeclarada+ " {20} "+
                    "No.Linea: " + cmp.be.preAnalisis.numLinea );
                    else    if(!varNoDeclarada2.equals("")  )
                    cmp.me.error( cmp.ERR_SEMANTICO, "Variable no declarada "+varNoDeclarada2+ " {20} "+
                    "No.Linea: " + cmp.be.preAnalisis.numLinea );
                    else 
                    cmp.me.error( cmp.ERR_SEMANTICO, "Tipos y expresiones no concuerdan {20} "+
                    "No.Linea: " + cmp.be.preAnalisis.numLinea );
                    
                   // cmp.me.error( cmp.ERR_SEMANTICO, "Tipo y expresion no concuerdan. Variable no declarada  {20}" );
                }
                    
            }
            // -----------------------------------------------------------------
            
        } else if (cmp.be.preAnalisis.complex.equals ( "call" ) ) {
            // proposicion -> call id proposicion_prima {21}
            
            emparejar ( "call" );
            id = cmp.be.preAnalisis;
            emparejar ( "id" );
            proposicion_prima( proposicion_prima );
            
            // -----------------------ACCION SEMANTICA 21-----------------------
            if ( analizarSemantica ) {
                if ( proposicion_prima.tipo.equals( getDmno(cmp.ts.buscaTipo( id.entrada ) ) ) ) 
                    proposicion.tipo = VACIO;
                else {
                    proposicion.tipo = ERROR_TIPO;
                    cmp.me.error( cmp.ERR_SEMANTICO, "Argumentos no concuerdan con la tabla de simbolos {21}" );
                }
                    
            }
            // -----------------------------------------------------------------
            
        } else if ( cmp.be.preAnalisis.complex.equals ( "if" ) ) {
            // proposicion -> if condicion then proposiciones_optativas else proposiciones_optativas {22} end if
            emparejar               ( "if"                                     );
            condicion               ( condicion                                   );
            emparejar               ( "then"                                    );
            proposiciones_optativas ( proposiciones_optativas1  );
            emparejar               ( "else"                                    );
            proposiciones_optativas ( proposiciones_optativas2 );
            
            // -----------------------ACCION SEMANTICA 22-----------------------
            if ( analizarSemantica ) {
                if ( condicion.tipo.equals("BOOLEAN")) {
                    if ( proposiciones_optativas1.tipo.equals( VACIO ) &&
                         proposiciones_optativas2.tipo.equals( VACIO ) ) 
                         proposicion.tipo = VACIO;
                    else {
                        proposicion.tipo = ERROR_TIPO;
                        cmp.me.error( cmp.ERR_SEMANTICO, "Error en las proposiciones {22}" );
                    }
                        
                } else {
                    proposicion.tipo = ERROR_TIPO;
                    cmp.me.error( cmp.ERR_SEMANTICO, "Se esperaba que expresion fuera BOOLEAN{22}" );
                }
            }
            // -----------------------------------------------------------------
            
            emparejar ( "end" );
            emparejar ( "if"  );
        } else if ( cmp.be.preAnalisis.complex.equals ( "do" ) ) {
            // proposicion -> do while condicion proposiciones_optativas {23} loop
            emparejar ( "do"    );
            emparejar ( "while" );
            condicion ( condicion1 );
            proposiciones_optativas ( proposiciones_optativas3 );
            
            // -----------------------ACCION SEMANTICA 23-----------------------
            if ( analizarSemantica ) {
                if ( condicion1.tipo.equals( "BOOLEAN" ) ) {
                    if ( proposiciones_optativas3.tipo.equals( VACIO ) )
                         proposicion.tipo = VACIO;
                    else {
                        proposicion.tipo = ERROR_TIPO;
                        cmp.me.error( cmp.ERR_SEMANTICO, "Error en proposiciones_optativas3 {23}" );
                    }
                } else {
                    proposicion.tipo = ERROR_TIPO;
                    cmp.me.error( cmp.ERR_SEMANTICO, "Se esperaba que condicion1 fuera BOOLEAN{23}" );
                }
            }
            // -----------------------------------------------------------------
            
            emparejar ( "loop" );
        } else {
            error ( "[proposicion]: Se esperaba do." +
                    "No.Linea: " + cmp.be.preAnalisis.numLinea );
        }
    }
    
    //------------------------------------------------------------------------------------
    

    private void proposicion_prima ( Atributos proposicion_prima ) {
        
        Atributos lista_expresiones = new Atributos ();
        
        if ( cmp.be.preAnalisis.complex.equals( "(" ) ) {
            // proposicion_prima -> ( lista_expresiones ) {24}
            emparejar ( "(" );
            lista_expresiones ( lista_expresiones );
            emparejar ( ")" );
            
            // -----------------------ACCION SEMANTICA 24-----------------------
            if ( analizarSemantica ) {
                proposicion_prima.tipo = lista_expresiones.tipo;
            }
            // -----------------------------------------------------------------
            
        } else {
            // proposicion_prima -> empty {25}
            
            // -----------------------ACCION SEMANTICA 25-----------------------
            if ( analizarSemantica ) {
                proposicion_prima.tipo = "VOID";
            }
            // -----------------------------------------------------------------
        }
    }
    
    //------------------------------------------------------------------------------------
    

    private void lista_expresiones ( Atributos lista_expresiones ) {
        
        Atributos expresion               = new Atributos ();
        Atributos lista_expresiones_prima = new Atributos ();
        
        if ( cmp.be.preAnalisis.complex.equals ( "id"      ) || 
             cmp.be.preAnalisis.complex.equals ( "num"     ) ||  
             cmp.be.preAnalisis.complex.equals ( "num.num" ) || 
             cmp.be.preAnalisis.complex.equals ( "("       ) ||
             cmp.be.preAnalisis.complex.equals ( "literal"       ) )  {
            // lista_expresiones -> expresion lista_expresiones_prima {26}
            expresion ( expresion );
            lista_expresiones_prima ( lista_expresiones_prima );
            
            // -----------------------ACCION SEMANTICA 26-----------------------
            if ( analizarSemantica ) {
                if ( !expresion.tipo.equals( ERROR_TIPO ) && !lista_expresiones_prima.tipo.equals( ERROR_TIPO ) )
                    if ( lista_expresiones_prima.tipo.equals( VACIO ) )
                        lista_expresiones.tipo = expresion.tipo;
                    else
                        lista_expresiones.tipo = expresion.tipo + "x" + lista_expresiones_prima.tipo;
                else {
                    lista_expresiones.tipo = ERROR_TIPO;
                    cmp.me.error( cmp.ERR_SEMANTICO, "ERROR en la Expresión o en lista_expresiones_prima {26}" );
                }
            }
            // -----------------------------------------------------------------
            
        } else {
            // lista_expresiones -> empty {27}
            
            // -----------------------ACCION SEMANTICA 27-----------------------
            if ( analizarSemantica ) {
                lista_expresiones.tipo = VACIO;
            }
            // -----------------------------------------------------------------
        }
    }
    
    //------------------------------------------------------------------------------------
    

    private void lista_expresiones_prima ( Atributos lista_expresiones_prima ) {
        
        Atributos expresion                = new Atributos ();
        Atributos lista_expresiones_prima1 = new Atributos ();
        
        if ( cmp.be.preAnalisis.complex.equals ( "," ) ) {
            // lista_expresiones_prima -> , expresion lista_expresiones_prima {28}
            emparejar ( "," );
            expresion ( expresion );
            lista_expresiones_prima ( lista_expresiones_prima1 );
            
            // -----------------------ACCION SEMANTICA 28-----------------------
            if ( analizarSemantica ) {
                if ( !expresion.tipo.equals( ERROR_TIPO ) && !lista_expresiones_prima1.tipo.equals( ERROR_TIPO ) )
                    if ( lista_expresiones_prima1.tipo.equals( VACIO ) ) 
                        lista_expresiones_prima.tipo = expresion.tipo;
                    else
                        lista_expresiones_prima.tipo = expresion.tipo + "x" + lista_expresiones_prima1.tipo;
                else {
                    lista_expresiones_prima.tipo = ERROR_TIPO;
                    cmp.me.error( cmp.ERR_SEMANTICO, "ERROR en la Expresión o en lista_expresiones_prima {28}" );
                }
            }
            // -----------------------------------------------------------------
        } else {
            // lista_expresiones_prima -> empty {29}
            
            // -----------------------ACCION SEMANTICA 29-----------------------
            if ( analizarSemantica ) {
                lista_expresiones_prima.tipo = VACIO;
            }
            // -----------------------------------------------------------------
            
        }
    }
    
    //------------------------------------------------------------------------------------
    

    private void condicion ( Atributos condicion ) {
        
        Atributos expresion1 = new Atributos ();
        Atributos expresion2 = new Atributos ();
        String varNoDeclarada="";//variable para guardar un lexema no declarado
        
        String tipo="";
        
        if ( cmp.be.preAnalisis.complex.equals ( "id"      )    ||
             cmp.be.preAnalisis.complex.equals ( "num"     )    ||
             cmp.be.preAnalisis.complex.equals ( "num.num" )    ||
             cmp.be.preAnalisis.complex.equals ( "("       )    ||
             cmp.be.preAnalisis.complex.equals("literal")   ) {
            // condicion -> expresion oprel expresion {30}
           
            //---------------------------------------------------------
            //si la variable no esta declarada guardamos el lexema y mostramos en el error ese lexema
            
          
            //----------------------------------------------------------
             tipo=cmp.ts.buscaTipo(cmp.be.preAnalisis.entrada);
            if(tipo == "" )
            if(cmp.be.preAnalisis.complex != "literal" && cmp.be.preAnalisis.complex != "num" && cmp.be.preAnalisis.complex != "num.num")
             varNoDeclarada=cmp.be.preAnalisis.lexema;
            expresion ( expresion1 );
            emparejar ( "oprel" );
              //------------------------------------------------------------------
           
             //-----------------------------------------------------------------------------------------------------  
               expresion ( expresion2 );
             
            // -----------------------ACCION SEMANTICA 30-----------------------
            if ( analizarSemantica ) {
                if( expresion1.tipo.equals( expresion2.tipo  )  ||
                    expresion1.tipo.equals( "SINGLE" ) &&  expresion2.tipo.equals( "INTEGER" ) ||
                    expresion1.tipo.equals( "INTEGER" )  && expresion2.tipo.equals( "SINGLE" ) ) {
                    condicion.tipo = "BOOLEAN";
                } else {
                    
                    //se le agrego el if
                    condicion.tipo = "ERROR_TIPO";
                    if(!varNoDeclarada.equals(""))
                    cmp.me.error( cmp.ERR_SEMANTICO, "Variable no declarada "+varNoDeclarada+ " {30} "+
                    "No.Linea: " + cmp.be.preAnalisis.numLinea );
                    else 
                    cmp.me.error( cmp.ERR_SEMANTICO, "Las expresiones no concuerdan {30} "+
                    "No.Linea: " + cmp.be.preAnalisis.numLinea );
                }
            }
            // -----------------------------------------------------------------
            
        } else {
            error ( "[condicion]: Error de condición. Se esperaba [id, num , num.num ,(]" +
                    "No.Linea: " + cmp.be.preAnalisis.numLinea );
        }
    }
    
    //------------------------------------------------------------------------------------
    

    private void expresion ( Atributos expresion ) {
        
        Atributos termino         = new Atributos ();
        Atributos expresion_prima = new Atributos ();
        
        if ( cmp.be.preAnalisis.complex.equals ( "id" )        ||
             cmp.be.preAnalisis.complex.equals ( "num" )       ||
             cmp.be.preAnalisis.complex.equals ( "num.num" )   ||
             cmp.be.preAnalisis.complex.equals ( "(" ) ) {
            // expresion -> termino {31} expresion_prima {32}
            termino ( termino );
            
            // -----------------------ACCION SEMANTICA 31-----------------------
            if ( analizarSemantica ) {
                expresion_prima.h = termino.tipo;
            }
            // -----------------------------------------------------------------
            
            expresion_prima ( expresion_prima );
            
            // -----------------------ACCION SEMANTICA 32-----------------------
            if ( analizarSemantica ) {
                if ( !expresion_prima.h.   equals   ( ERROR_TIPO ) && 
                     !expresion_prima.tipo.equals   ( ERROR_TIPO ) )
                    expresion.tipo = expresion_prima.tipo;
                else {
                    expresion.tipo = ERROR_TIPO;
                    cmp.me.error( cmp.ERR_SEMANTICO, "ERROR en expresion_prima.h o expresion_prima.tipo"
                            + ", alguna o las dos contiene ERROR_TIPO  {32}" );
                }
            }
            // -----------------------------------------------------------------
            
        } else if ( cmp.be.preAnalisis.complex.equals ( "literal" ) ) {
            // expresion -> literal {33}
            emparejar ( "literal" );
            
            // -----------------------ACCION SEMANTICA 33-----------------------
            if ( analizarSemantica ) {
                expresion.tipo = "STRING";
            }
            // -----------------------------------------------------------------
            
        } else {  
            error ( "[expresion]: Expresión no valida:." +cmp.be.preAnalisis.lexema +
                    ". No.Linea: " + cmp.be.preAnalisis.numLinea );
        }
    }
    
    //------------------------------------------------------------------------------------
    

    private void expresion_prima ( Atributos expresion_prima ) {
        
        Atributos termino          = new Atributos ();
        Atributos expresion_prima1 = new Atributos ();
        //String id="";
         String varNoDeclarada="";//variable para guardar un lexema no declarado
         String tipo="";

        
        if ( cmp.be.preAnalisis.complex.equals ( "opsuma" ) ) {
            // expresion_prima -> opsuma termino {34} expresion_prima {35}
            emparejar ( "opsuma" );
          
           //-------------------------------------------------            
             // id=cmp.be.preAnalisis.lexema;
            tipo=cmp.ts.buscaTipo(cmp.be.preAnalisis.entrada);
            if(tipo == "" )
               if(cmp.be.preAnalisis.complex != "literal" && cmp.be.preAnalisis.complex != "num" && cmp.be.preAnalisis.complex != "num.num")
                  varNoDeclarada=cmp.be.preAnalisis.lexema;
            //-----------------------------------------
            termino   ( termino );
            
            // -----------------------ACCION SEMANTICA 34-----------------------
            if ( analizarSemantica ) {
                if ( expresion_prima.h.equals( termino.tipo ) )
                    expresion_prima1.h = termino.tipo;
                else if ( expresion_prima.h.equals( "SINGLE" ) && termino.tipo.equals( "INTEGER" ) ||
                          expresion_prima.h.equals( "INTEGER" ) && termino.tipo.equals( "SINGLE" ) ) 
                    expresion_prima1.h = "SINGLE";
                else {
                    expresion_prima1.h = ERROR_TIPO;
                   
                    if(!varNoDeclarada.equals(""))
                    cmp.me.error( cmp.ERR_SEMANTICO, "Variable no declarada "+varNoDeclarada+ " {34} "+
                    "No.Linea: " + cmp.be.preAnalisis.numLinea );
                    else 
                    cmp.me.error( cmp.ERR_SEMANTICO, "Tipos de datos incorrectos expresion_prima.h y termino.tipo tiene tipos de datos incompatibles {34} "+
                    "No.Linea: " + cmp.be.preAnalisis.numLinea );



// cmp.me.error( cmp.ERR_SEMANTICO, "ERROR. Tipos de datos incorrectos,"
                     //       + "expresion_prima.h y termino.tipo tiene tipos de datos incompatibles, variable no declarada " + id + " {34}" );
                }
            }
            // -----------------------------------------------------------------
            
            expresion_prima ( expresion_prima1 ); 
            
            // -----------------------ACCION SEMANTICA 35-----------------------
            if ( analizarSemantica ) {
                if ( !expresion_prima1.h.equals( ERROR_TIPO ) && !expresion_prima1.tipo.equals( ERROR_TIPO ) )
                    expresion_prima.tipo = expresion_prima1.tipo;
                else {
                    expresion_prima.tipo = ERROR_TIPO;
                    cmp.me.error( cmp.ERR_SEMANTICO, "ERROR. los tipos de expresion_prima son ERROR_TIPO {35}" );
                }
            }
            // -----------------------------------------------------------------
            
        } else {
            // expresion_prima -> empty {36}
            
            // -----------------------ACCION SEMANTICA 36-----------------------
            if ( analizarSemantica ) {
                expresion_prima.tipo = expresion_prima.h;
            }
            // -----------------------------------------------------------------
        }
    }
    
    //------------------------------------------------------------------------------------
    
     private void termino ( Atributos termino ) {
        
        Atributos factor        = new Atributos ();
        Atributos termino_prima = new Atributos ();
        
        if ( cmp.be.preAnalisis.complex.equals ( "id" )        ||
             cmp.be.preAnalisis.complex.equals ( "num" )       ||
             cmp.be.preAnalisis.complex.equals ( "num.num" )   ||
             cmp.be.preAnalisis.complex.equals ( "(" ) ) {
            // termino -> factor {37} termino_prima {38}
            factor ( factor );
            
            // -----------------------ACCION SEMANTICA 37-----------------------
            if ( analizarSemantica ) {
                termino_prima.h = factor.tipo;
            }
            // -----------------------------------------------------------------
            
            termino_prima ( termino_prima );
            
            // -----------------------ACCION SEMANTICA 38-----------------------
            if ( analizarSemantica ) {
                if ( !termino_prima.h.equals   ( ERROR_TIPO ) &&
                     !termino_prima.tipo.equals( ERROR_TIPO ) ) 
                    termino.tipo = termino_prima.tipo;
                else {
                    termino.tipo = ERROR_TIPO;
                    cmp.me.error( cmp.ERR_SEMANTICO, "ERROR. Los atributos h y tipo de termino_prima son ERROR_TIPO {38}" );
                }
            }
            // -----------------------------------------------------------------
            
        } else {
            error ( "[termino]: Error de término.Se esperaba id, num , num.num o (" +
                    "No.Linea: " + cmp.be.preAnalisis.numLinea );
        }
    }
    
    //------------------------------------------------------------------------------------
    

    private void termino_prima ( Atributos termino_prima ) {
        
        Atributos factor         = new Atributos ();
        Atributos termino_prima1 = new Atributos ();
       
       String varNoDeclarada="";//variable para guardar un lexema no declarado
       String tipo="";

        
        if ( cmp.be.preAnalisis.complex.equals ( "opmult" ) ) {
            // termino_prima -> opmult factor {39} termino_prima {40}
            emparejar ( "opmult" );
           // id=cmp.be.preAnalisis.lexema;
           
            tipo=cmp.ts.buscaTipo(cmp.be.preAnalisis.entrada);
            if(tipo == "" )
            if(cmp.be.preAnalisis.complex != "literal" && cmp.be.preAnalisis.complex != "num" && cmp.be.preAnalisis.complex != "num.num" )
             varNoDeclarada=cmp.be.preAnalisis.lexema;

            factor    ( factor );
            
            // -----------------------ACCION SEMANTICA 39-----------------------
            if ( analizarSemantica ) {
                if ( termino_prima.h.equals( factor.tipo ) ) 
                    termino_prima1.h = factor.tipo;
                else if ( termino_prima.h.equals( "SINGLE" ) && factor.tipo.equals( "INTEGER" ) ||
                          termino_prima.h.equals( "INTEGER" ) && factor.tipo.equals( "SINGLE" ) )
                {
                    termino_prima1.h = "SINGLE";
                
                }
                //else //if (termino_prima.h.equals(getRng(factor.tipo)))// de aqui se va a la excepcion con el programa d = p * 5
                //{
                   // termino_prima1.h = getRng(factor.tipo);
                //}
                        
                else {
                    termino_prima1.h = ERROR_TIPO;
                     if(!varNoDeclarada.equals(""))
                    cmp.me.error( cmp.ERR_SEMANTICO, "Variable no declarada "+varNoDeclarada+ " {39} "+
                    "No.Linea: " + cmp.be.preAnalisis.numLinea );
                    else 
                    cmp.me.error( cmp.ERR_SEMANTICO, "ERROR. Tipos de datos incorrectos termino_prima.h y factor.tipo tiene tipos de datos incompatibles {39} "+
                    "No.Linea: " + cmp.be.preAnalisis.numLinea );

                      // cmp.me.error( cmp.ERR_SEMANTICO, "ERROR. Tipos de datos incorrectos,"
                     //       + "termino_prima.h y factor.tipo tiene tipos de datos incompatibles, variable no declarada " +id+" {39}" );
                }
            }
            // -----------------------------------------------------------------
            
            termino_prima ( termino_prima1 );
            
            // -----------------------ACCION SEMANTICA 40-----------------------
            if ( analizarSemantica ) {
                if ( !termino_prima1.h.equals( ERROR_TIPO ) && !termino_prima1.tipo.equals( ERROR_TIPO ) )
                      termino_prima.tipo = termino_prima1.tipo;
                else {
                    termino_prima.tipo = ERROR_TIPO;
                    cmp.me.error( cmp.ERR_SEMANTICO, "ERROR. Tipos de datos incorrectos,"
                            + "termino_prima.h y termino_prima.tipo tiene ERROR_TIPO {40}" );
                }
            }
            // -----------------------------------------------------------------
            
        } else {
            // termino -> empty {41}
            // -----------------------ACCION SEMANTICA 41-----------------------
            if ( analizarSemantica ) {
                termino_prima.tipo = termino_prima.h;
            }
            // -----------------------------------------------------------------
        }
    }
    
    //------------------------------------------------------------------------------------
    

    private void factor ( Atributos factor ) {
        
        Linea_BE id            = new Linea_BE ();
        Atributos factor_prima = new Atributos ();
        Atributos expresion    = new Atributos ();
        String id2 = "";
        
        if ( cmp.be.preAnalisis.complex.equals ( "id" ) ) {
            // factor -> id factor_prima {42}
            
            id = cmp.be.preAnalisis;
            
            emparejar ( "id" );
 
            factor_prima ( factor_prima );
         //   id2=cmp.be.preAnalisis.lexema;
            // -----------------------ACCION SEMANTICA 42-----------------------
            if ( analizarSemantica ) {
                //se le agrego este if 
               if(factor_prima.tipo.equals(VACIO) && getDmno(cmp.ts.buscaTipo(id.entrada)).equals("VOID")){
                   factor.tipo=getRng(cmp.ts.buscaTipo(id.entrada));
               }       

               else  if ( factor_prima.tipo.equals( VACIO ) && (!cmp.ts.buscaTipo(id.entrada).equals(""))) {
                    factor.tipo = cmp.ts.buscaTipo( id.entrada );
                } else if (tiposFunciones(cmp.ts.buscaTipo(id.entrada), factor_prima.tipo))//(getDmno(cmp.ts.buscaTipo( id.entrada ) ).equals( factor_prima.tipo ) )
                    factor.tipo = getRng(cmp.ts.buscaTipo( id.entrada ) );
                else {
                    factor.tipo = ERROR_TIPO;
                    if(cmp.ts.buscaTipo(id.entrada).equals(""))
                        cmp.me.error(cmp.ERR_SEMANTICO, "Variable no declarada "+id.lexema);
                    else
                    cmp.me.error( cmp.ERR_SEMANTICO, "ERROR. El tipo de dato de factor"
                            + " no coindice con el tipo de dato del id  {42}" );
                }
            }
            // -----------------------------------------------------------------
            
        } else if ( cmp.be.preAnalisis.complex.equals ( "num" ) ) {
            // factor -> num {43}
            emparejar ( "num" );
            
            // -----------------------ACCION SEMANTICA 43-----------------------
            if ( analizarSemantica ) {
                factor.tipo = "INTEGER";
            }
            // -----------------------------------------------------------------
            
        } else if ( cmp.be.preAnalisis.complex.equals ( "num.num") ) {
            // factor -> num.num {44}
            emparejar ( "num.num" );
            
            // -----------------------ACCION SEMANTICA 44-----------------------
            if ( analizarSemantica ) {
                factor.tipo = "SINGLE";
            }
            // -----------------------------------------------------------------
            
        } else if ( cmp.be.preAnalisis.complex.equals ( "(" ) ) {
            // factor -> ( expresion ) {45}
            emparejar ( "(" );
            expresion ( expresion );
            emparejar ( ")" );
            
            // -----------------------ACCION SEMANTICA 45-----------------------
            if ( analizarSemantica ) {
                factor.tipo = expresion.tipo;
            }
            // -----------------------------------------------------------------
             
        } else {
            error ( "[factor]: Se esperaba un (." +
                    "No.Linea: " + cmp.be.preAnalisis.numLinea );
        }
    }
    
    //------------------------------------------------------------------------------------
    

    private void factor_prima ( Atributos factor_prima ) {
        
        Atributos lista_expresiones = new Atributos ();
        
        if ( cmp.be.preAnalisis.complex.equals( "(" ) ) {
            // factor_prima -> ( lista_expresiones ) {46}
            emparejar ( "(" );
            
            lista_expresiones ( lista_expresiones );
            
            emparejar ( ")" );
            
            // -----------------------ACCION SEMANTICA 46-----------------------
            if ( analizarSemantica ) {
                factor_prima.tipo = lista_expresiones.tipo;
            }
            // -----------------------------------------------------------------
            
        } else {
            // factor_prima -> empty {47}
            
            // -----------------------ACCION SEMANTICA 47-----------------------
            if ( analizarSemantica ) {
                factor_prima.tipo = VACIO;
            }
            // -----------------------------------------------------------------
            
        }
    }
}
//------------------------------------------------------------------------------
//::
