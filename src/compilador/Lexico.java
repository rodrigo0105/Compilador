/*:-----------------------------------------------------------------------------
 *:                       INSTITUTO TECNOLOGICO DE LA LAGUNA
 *:                     INGENIERIA EN SISTEMAS COMPUTACIONALES
 *:                         LENGUAJES Y AUTOMATAS II           
 *: 
 *:                  SEMESTRE: ENERO-JUNIO 2023     HORA: 6:00 - 7:00 HRS
 *:                                   
 *:               
 *:    # Clase que implementa la etapa de Analisis de Lexico
 *:                           
 *: Archivo       : Lexico.java
 *: Autor         : Fernando Gil  
 *: Fecha         : 03/SEP/2014
 *: Compilador    : Java JDK 7
 *: Descripción   : Analizador Lexico del lenguaje BASICTec
 *:                  
 *:           	     
 *: Ult.Modif.    :
 *:  Fecha      Modificó            Modificacion
 *:=============================================================================
 *: 22/Feb/2015 FGil               -Se creo el arreglo _noLineas[] y la variable
 *:                                _noLinea para rastrear el numero de linea del
 *:                                codigo fuente.
 *: 08/Sep/2015 FGil               -Se agrego el automata 16 para reconocer constantes
 *:                                caracter, ejemplo '&'.
 *: 15/FEB/2016                    -Se corrigio automatas de opasig, caresp, operador
 *:                                 relacional de igualdad y de
 *:                                 literal para que reconozcan correctamente las
 *:                                 cadenas :=, :, = y
 *:                                 literales entre comillas sencillas.
 *:-----------------------------------------------------------------------------
 */

package compilador;

import general.Linea_BE;
import general.Linea_TS;

public class Lexico {
    final int TOKREC = 17;
   final int MAXTOKENS = 500;
   private String[] _lexemas;
   private String[] _tokens;
   private int   [] _noLineas;   // FGil: Numero de linea donde se encontró cada token
   private String _lexema;
   private int _noTokens;
   private int _i;
   private int _iniToken;
   private Automata oAFD;
   private Compilador cmp;
   private int _noLinea;     // FGil: Numero de linea fuente

   //--------------------------------------------------------------------------   
   
   public Lexico ( Compilador cmp )  // constructor por defecto
   {
     this.cmp = cmp;   
     _lexemas = new String[MAXTOKENS];
     _tokens = new String[MAXTOKENS];
     _noLineas = new int [MAXTOKENS];   // FGil: Instanciar el arreglo _noLineas
     oAFD = new Automata();
     _i = 0;
     _iniToken = 0;
     _noTokens = 0;
   }

   //--------------------------------------------------------------------------   
   
   public void Inicia() 
   {
     _i = 0;
     _iniToken = 0;
     _noTokens = 0;
     _noLinea  = 1;                     // FGil: Inicia en la linea 1 del codigo fuente     
     _lexemas = new String[MAXTOKENS];
     _tokens = new String[MAXTOKENS];
     _noLineas = new int [MAXTOKENS];   // FGil: Instanciar el arreglo _noLineas
   }

   //--------------------------------------------------------------------------   
   
   public void Analiza(String texto) 
   {
     Boolean recAuto;
     int noAuto;
     while (_i < texto.length()) 
     {
       recAuto=false;
       noAuto=0;
       for(;noAuto<TOKREC&&!recAuto;)
         if(oAFD.Reconoce(texto,this,noAuto))
           recAuto=true;
         else
           noAuto++;
       if (recAuto)
       {
         _lexema = texto.substring(_iniToken, _i);
         switch (noAuto)
         {     
           //--------------  Automata  Delimita--------------
          case 0 : //_tokens[_noTokens] = "Delimita";
                        break;
           //--------------  Automata  Opmult--------------
          case 1 : _tokens[_noTokens] = "opmult";
                        break;
           //--------------  Automata  Opsuma--------------
          case 2 : _tokens[_noTokens] = "opsuma";
                        break;
           //--------------  Automata  Identi--------------
          case 3 : _tokens[_noTokens] = "id";
              break;
           //--------------  Automata  Literal --------------
          case 4 : _tokens[_noTokens] = "literal";
                        break;
           //--------------  Automata  Signo--------------
          case 5 : _tokens[_noTokens] = "signo";
                        break;
           //--------------  Automata  Opasigna--------------
          case 6 : _tokens[_noTokens] = "opasig";
                        break;
           //--------------  Automata  Reales1--------------
          case 7 : _tokens[_noTokens] = "num.num";//"Reales1";
                        break;
           //--------------  Automata  Reales2--------------
          case 8 : _tokens[_noTokens] = "num.num";//"Reales2";
                        break;
           //--------------  Automata  Reales3--------------
          case 9 : _tokens[_noTokens] = "num.num";//"Reales3";
                        break;
           //--------------  Automata  Enteros--------------
          case 10 : _tokens[_noTokens] = "num";//"Enteros";
                        break;
           //--------------  Automata  Oprel--------------
          case 11 : _tokens[_noTokens] = "oprel";
                        break;
           //--------------  Automata  Oprel2--------------
          case 12 : _tokens[_noTokens] = "oprel";
                        break;
           //--------------  Automata  ASignacion--------------
          case 13 : _tokens[_noTokens] = "opasig";
                        break;
           //--------------  Automata  Punto--------------
          case 14 : _tokens[_noTokens] = "punto";
                        break;
           //--------------  Automata  CarEsp--------------
          case 15 : _tokens[_noTokens] = "caresp";
                        break;
           //--------------  Automata  Caracter--------------
          case 16 : _tokens[_noTokens] = "caracter";
                        break;
         }
         if(noAuto != 0) {
             _lexemas  [_noTokens  ] = _lexema;
             _noLineas [_noTokens++] = _noLinea;   // FGil: Guardamos la info del num.de linea fuente
         }
       }
       else
         _i++;
       _iniToken = _i;
     }
     //Activar metodo pasarBufferEntrada
     pasarBufferTabla ( );
     
   } // fin del método Analiza()

   //--------------------------------------------------------------------------   
   
   public int getI()
   {
       return _i;
   }

   //--------------------------------------------------------------------------   
   
   public void setI(int valor)
   {
       _i=valor;
   }
   //--------------------------------------------------------------------------   
   // FGil: Establece el numero de linea del codigo fuente
   public void setNoLinea ( int valor ) { 
       _noLinea = valor;
   }
   //--------------------------------------------------------------------------   
   // FGil: Devuelve el numero de linea del codigo fuente   
   public int getNoLinea () {
       return _noLinea;
   }
   //--------------------------------------------------------------------------   
   public int getIniToken()
   {
       return _iniToken;
   }
   //--------------------------------------------------------------------------   
   
   public String[] Tokens()
   {
       return _tokens;
   }

   //--------------------------------------------------------------------------   
   
   public String[] Lexemas()
   {
       return _lexemas;
   }

   //--------------------------------------------------------------------------   
   
   public int NOTOKENS()
   {
       return _noTokens;
   }

   //--------------------------------------------------------------------------   
   
   private Boolean EsPalabraReservada(String lex)
   {      
       String palres[] = { "dim", "as", "integer", "single", "string", "function",
                           "end", "sub", "call", "if", "then", "else", "do", "while",
                           "loop"
                         };
       for (int i = 0; i < palres.length; i++) {
           if (lex.equals ( palres[i] ) ) {       
               return true; 
           }
       }
       return false;
   }    
  
    //--------------------------------------------------------------------------
	// Toma los tokens y los pasa a la tabla de simbolos y buffer de entrada
	// Revision en 22/Nov/2012
    private void pasarBufferTabla ( )
    {
        // Comenzamos con establecer la entrada, la l?nea y una bandera para
        // palabras reservadas
        int entrada = 0;
        Linea_BE lineaBE = null;
        Linea_TS lineaTS = null;

        Boolean noPalres = true;

        //tabla de simbolos, linea reservada
        lineaTS = new Linea_TS ( "", "", "", "");
        entrada = cmp.ts.insertar ( lineaTS );
        lineaTS = null;

        //Vamos a comparar todos los tokens obtenidos e insertar en la tabla
        //de s?mbolos
        for ( int i = 0; i < _noTokens; i++ )
        {
            //Comparando el identificador que no sea palabra reservada
            if ( _tokens[ i ].equals  ( "id" ) )
            {
                if(EsPalabraReservada(_lexemas[i])){
                    lineaBE = new Linea_BE (
               _lexemas [ i ], _lexemas [ i ], 0, _noLineas [ i ] );
                }
                else {
               lineaTS = new Linea_TS (
               _tokens [ i ], _lexemas [ i ], "", "" );
               entrada = cmp.ts.insertar ( lineaTS );
               lineaBE = new Linea_BE (
               _tokens [ i ], _lexemas [ i ], entrada, _noLineas [ i ] );
                }
               
            }                 
            //Variables que deja pasar a tabla de simbolos
            else if ( _tokens [ i ].equals ( "num"     ) ||
                      _tokens [ i ].equals ( "num.num" ) ||
                      _tokens [ i ].equals ( "literal" ) ||
                      _tokens [ i ].equals ( "caracter")
                    )
            {
                lineaTS = new Linea_TS (
                           _tokens [ i ], _lexemas [ i ], "", "" );
                entrada = cmp.ts.insertar ( lineaTS );
                lineaBE = new Linea_BE (
                        _tokens [ i ], _lexemas [ i ], entrada, _noLineas [ i ] );
            }
            //Los siguientes no se insertan en tabla simbolos
            else if ( _tokens [ i ].equals  ( "opmult" ) 
                    || _tokens [ i ].equals ( "opsuma" )                     
                    || _tokens [ i ].equals ( "signo"  )
                    || _tokens [ i ].equals ( "opdif"  )
                    || _tokens [ i ].equals ( "opasig" ) 
                    || _tokens [ i ].equals ( "oprel"  ) 
                    )
                lineaBE = new Linea_BE (
                        _tokens [ i ], _lexemas [ i ], 0, _noLineas [ i ] );
            else if ( _tokens [ i ].equals ( "caresp" ) ||
                      _tokens [ i ].equals ( "punto"  )  )
                lineaBE = new Linea_BE (
                        _lexemas [ i ], _lexemas [ i ], 0, _noLineas [ i ] );		

            //Verificar que la línea no está vacía para agregar a la tabla
            if ( lineaBE != null )
                cmp.be.insertar ( lineaBE );
            

            //Limpiar lineas
            lineaBE = null; lineaTS = null;
        }

    }   //Fin del metodo para pasar al buffer entrada y tabla simbolos
	//--------------------------------------------------------------------------   
}
