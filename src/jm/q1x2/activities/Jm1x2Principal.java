package jm.q1x2.activities;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import jm.q1x2.R;
import jm.q1x2.bbdd.Basedatos;
import jm.q1x2.bbdd.dao.EquipoDao;
import jm.q1x2.bbdd.dao.UsuarioDao;
import jm.q1x2.logneg.AplicacionOp;
import jm.q1x2.logneg.JornadaOp;
import jm.q1x2.logneg.NoticiaOp;
import jm.q1x2.logneg.QuinielaOp;
import jm.q1x2.logneg.UsuarioOp;
import jm.q1x2.transobj.Equipo;
import jm.q1x2.transobj.Noticia;
import jm.q1x2.transobj.Noticias;
import jm.q1x2.transobj.Usuario;
import jm.q1x2.utils.CargaFichero;
import jm.q1x2.utils.Constantes;
import jm.q1x2.utils.Mensajes;
import jm.q1x2.utils.Notificaciones;
import jm.q1x2.utils.Preferencias;
import jm.q1x2.utils.Utils;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TabHost;
import android.widget.TextView;

public class Jm1x2Principal extends TabActivity 
{
    private ProgressDialog dialog= null;
    
	private boolean bSeSoportaTituloPersonalizado;
	private boolean bHayConexionInternet;
	
	@Override
	protected void onDestroy() 
	{
      	if(dialog!=null)
    		dialog.dismiss();  
		super.onDestroy();
	}
	
	@Override
	protected void onStop() 
	{
      	if(dialog!=null)
    		dialog.dismiss();  
		super.onStop();
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) 
    {
    	super.onCreate(savedInstanceState);   	
    	bSeSoportaTituloPersonalizado= requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        bHayConexionInternet= Utils.hayConexionInternet(getApplicationContext());        
    	
        if (!bHayConexionInternet)
        {
        	Mensajes.alerta(getApplicationContext(), "No hay conexión a Internet...");
        	finish();
        }
        else
        {
        	boolean bIniciar= true;

        	String error= inicializarBBDD();  // NO QUITAR ESTA LINEA BAJO NINGÚN CONCEPTO
        	if (error != null)
        	{
        		bIniciar= false;        	
    			Mensajes.alerta(getApplicationContext(), error);
         	    finish();
        	}
        	else
        	{
            	String sVersionCodeMin= Notificaciones.getNotificacion(Constantes.NOTIFICACION_VERSIONCODE_MINIMO);
            	if (sVersionCodeMin != null)
            	{
            		int iVersionCodeMin= Integer.parseInt(sVersionCodeMin);
            		int vc= Utils.getVersionCodeAplicacion(getApplicationContext());
            		if (vc < iVersionCodeMin)
            		{
            			bIniciar= false;
                    	Mensajes.alerta(getApplicationContext(), "Necesitas actualizar Belfy1x2 a la última versión.");
                    	finish();
            		}
            	}
        	}
        	        	
        	if (bIniciar)
        		inicio();
        }
    }
	
	/*
	 * @return null si todo OK. Mensaje de error en caso de error
	 */
	private String inicializarBBDD()
	{		
    	/*
    	 *  Hago un primer acceso en modo ESCRITURA por si requiere actualizar la BB.DD.
    	 *  NO QUITAR LAS SIGUIENTES 2 LÍNEAS BAJO NINGÚN CONCEPTO
    	 */
		String ret= null;
        SQLiteDatabase con= Basedatos.getConexion(getApplicationContext(), Basedatos.ESCRITURA);
        if (con == null)
        	ret= "Se ha producido un error en la comunicación con el servidor.";
        else 
        	con.close();
        return ret;
	}
	
	private boolean esPrimeraInvocacion()
	{
		return esPrimeraInvocacion(true);
	}
	private boolean esPrimeraInvocacion(boolean actualizarDespuesDeLeer)
	{
		boolean ret;
		int v= Preferencias.getPreferenciaInt(getApplicationContext(), Constantes.PREFERENCIAS_PRIMERA_PRESENTACION, -1);
		ret= (v != 0);
		if (ret && actualizarDespuesDeLeer)
			Preferencias.grabarPreferenciaInt(getApplicationContext(), Constantes.PREFERENCIAS_PRIMERA_PRESENTACION, 0);
		return ret;
	}
	
    private void inicio()
    {
        SQLiteDatabase con= Basedatos.getConexion(getApplicationContext(), Basedatos.ESCRITURA);        
        crearUsuarioDemoSiNoExisteNingunUsuario(con);
        con.close();

        setContentView(R.layout.lay_main);              
        
        Resources res = getResources(); // Resource object to get Drawables
        TabHost tabHost = getTabHost();  // The activity TabHost
        TabHost.TabSpec spec;  // Reusable TabSpec for each tab
        Intent intent;  // Reusable Intent for each tab

        // Create an Intent to launch an Activity for the tab (to be reused)
        intent = new Intent().setClass(getApplicationContext(), Usuarios.class);
        spec = tabHost.newTabSpec(Constantes.PESTANA_USUARIOS_ID).setIndicator(Constantes.PESTANA_USUARIOS_TIT, res.getDrawable(R.drawable.ic_tab_usuarios)).setContent(intent);
        tabHost.addTab(spec);
        
        // Do the same for the other tabs
        intent = new Intent().setClass(getApplicationContext(), Quinielas.class);
        spec = tabHost.newTabSpec(Constantes.PESTANA_QUINIELAS_ID).setIndicator(Constantes.PESTANA_QUINIELAS_TIT, res.getDrawable(R.drawable.ic_tab_quinielas)).setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(getApplicationContext(), Equipos.class);
        spec = tabHost.newTabSpec(Constantes.PESTANA_EQUIPOS_ID).setIndicator(Constantes.PESTANA_EQUIPOS_TIT,res.getDrawable(R.drawable.ic_tab_equipos)).setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(getApplicationContext(), Config.class);
        spec = tabHost.newTabSpec(Constantes.PESTANA_CONFIG_ID).setIndicator(Constantes.PESTANA_CONFIG_TIT,res.getDrawable(R.drawable.ic_tab_config)).setContent(intent);        
        tabHost.addTab(spec);
        
        tabHost.setCurrentTab(0);
        ponerTituloInicial();
        
        presentarNoticias();
    }
        
    private void presentarNoticias()
    {
    	String ultimaNoticiaCargada= Preferencias.getPreferenciaString(getApplicationContext(), Constantes.PREFERENCIAS_NOTICIAS_ULTIMA, null);
    	Noticias nots= NoticiaOp.getNoticias(ultimaNoticiaCargada, getApplicationContext());
    	if (nots != null)
    	{
	    	AlertDialog.Builder ad= new AlertDialog.Builder(this);
			ad.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {					
		           }
		        });
	
			for(Noticia not : nots.getNoticias())
	    		Mensajes.dialogo(ad, "Belfy1X2 - noticia", not.getTexto());
	
	    	Preferencias.grabarPreferenciaString(getApplicationContext(), Constantes.PREFERENCIAS_NOTICIAS_ULTIMA, nots.getUltima());
    	}
    }
    
    private void ponerTituloInicial()
    {
		String letreroTemporada= Notificaciones.getNotificacion(Constantes.NOTIFICACION_TEMPORADA_LETRERO);            
				
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.lay_titulo_app); 
		TextView appLabel = (TextView) findViewById(R.id.app_nombreyVersion);        
		appLabel.setText("Belfy1X2 v" + Utils.getVersionAplicacion(getApplicationContext()) + (letreroTemporada==null?"":" - temp. "+letreroTemporada));            	
		
    	ponerTituloApp(null);
    }
    
    @Override
	protected void onStart() 
    {    	
		super.onStart();
		
    	if (bHayConexionInternet)
            cargaDatosIniciales();
		
    	UsuarioOp.crearRegistrosPrefUsuarioSiNoExisten(getApplicationContext());    	
    	actualizarDatos();
		
    	if (!estanDisponiblesDatosImprescindibles())
    	{
    		/* 
    		 * Si el programa no dispone del dato de 'temporada actual' (que se carga por notificaciones), no podría funcionar.
    		 * Por lo tanto, no le voy a dejar al usuario continuar.
    		 */
    		AlertDialog.Builder builder = new AlertDialog.Builder(this);
    		builder.setNegativeButton("Salir", new DialogInterface.OnClickListener() {
    		           public void onClick(DialogInterface dialog, int id) {
    		                finish(); 
    		           }
    		       });
    		Mensajes.dialogo(builder, Constantes.TITULO_APLICACION, "La conexión a Internet está desactivada y no han podido cargarse datos iniciales imprescindibles para el funcionamiento. Por favor, actívela y vuelva a iniciar el programa.");
    	}
		
    }

    private void actualizarDatos()
    {
    	actualizarDatos(true);
    }

    private void actualizarDatos(boolean comprobarAntesSiHayDatosQueActualizar)
    {   
        SQLiteDatabase con= Basedatos.getConexion(getApplicationContext(), Basedatos.ESCRITURA);  // esto tiene que ser en modo ESCRITURA por si requiere actualizar la BB.DD.        
        boolean bHayDatosQueActualizar= false;
        if (!comprobarAntesSiHayDatosQueActualizar)
        	bHayDatosQueActualizar= true;
        else if (bHayConexionInternet)
        	bHayDatosQueActualizar= hayDatosQueActualizar(con);
        con.close();
        
    	if (bHayConexionInternet)
    	{    		
    		if (bHayDatosQueActualizar)
    		{
			    dialog = new ProgressDialog(this);

			    if (esPrimeraInvocacion())
			    {
				    dialog.setTitle("Bienvenido a Belfy1X2");
				    dialog.setMessage("Con esta aplicación podrá realizar quinielas según ciertos criterios. " +
					    				"Podrá gestionar varios usuarios, y configurar los criterios para cada uno de ellos. " +
					    				"Mucha suerte !!! \n\n" +
					    				"Espere mientras se cargan los datos iniciales ...");			    	
			    }
			    else
			    {
				    dialog.setTitle("Por favor, espere");
				    dialog.setMessage("Actualizando datos ...");
			    }
			    dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			    dialog.setCancelable(false);
			    new TareaSegundoPlano().execute("");
    		}
    	}
    	else if (bHayDatosQueActualizar)
    		Mensajes.alerta(getApplicationContext(), "No se han podido actualizar los datos porque no hay conexión a Internet");

    }
    
    private boolean estanDisponiblesDatosImprescindibles()
    {
    	// Por ahora, el único dato imprescindible para la aplicación es la temporada actual
    	int temporada= Preferencias.getPreferenciaInt(getApplicationContext(), Constantes.PREFERENCIAS_TEMPORADA_ACTUAL, -1);    	
    	return (temporada != -1);
    }
    
    public class TareaSegundoPlano extends AsyncTask<String, Float, Integer>
    {
	   	 protected void onPreExecute() 
	   	 {
	   		 dialog.setProgress(0);
	   		 dialog.setMax(100);
             dialog.show(); //Mostramos el diálogo antes de comenzar
         }

	   	 protected Integer doInBackground(String... urls)
	   	 {
	   		 TareasSegundoPlano2.realizarTareas(this, getApplicationContext());
	   		 return 1;
	   	 }
         
         protected void onProgressUpdate (Float... valores) 
         {
	       	  int p = Math.round(100*valores[0]);
	       	  dialog.setProgress(p);	       	 
         }

         protected void onPostExecute(Integer bytes) 
         {
           	 if(dialog!=null && dialog.isShowing())
         		dialog.dismiss();  // si se siguen produciendo excepciones en esta linea, usar la solución de abajo (http://stackoverflow.com/questions/2745061/java-lang-illegalargumentexception-view-not-attached-to-window-manager)
        	 
        	 
//        	  try 
//        	  {
//               	 if(dialog!=null && dialog.isShowing())
//                		dialog.dismiss();
//        	  } 
//        	  catch (final IllegalArgumentException e) {}  // Handle or log or ignore        	        
//        	  catch (final Exception e) {}  // Handle or log or ignore
//        	  finally 
//        	  {
//        	        dialog = null;
//        	  }           
         }
         
         /*
          * 'valor' será tendrá un valor entre 0 y 100
          * Con este método se extrapolará al rango [min,max] de tal forma que:
          * 	- si 'valor'= 0 , entonces se mandará valor min
          * 	- si 'valor'= 100 , entonces se mandará valor max
          * 	- si 'valor'= 50 , entonces se mandará el valor intermedio entre min y max
          * 	- etc...
          * Ejemplo: min=20, max= 50, valor= 50 ==> 35
          */
         public void publicarProgresoEnRango(float min, float max, float valor)
         {        	
        	 float v= min + valor*(max-min)/100f;
        	 publicarProgreso(v);
         }
         
         /*
          * valores debe ser un valor entre 0 y 100
          */
         public void publicarProgreso(float v)
         {
        	 _publicarProgreso(v/100f);
         }
         private void _publicarProgreso(Float... valores )
         {
        	 publishProgress(valores);
         }

   }
    
    public static class TareasSegundoPlano2 
    {
        public static void realizarTareas(TareaSegundoPlano task, Context ctx)
        {
            SQLiteDatabase con= Basedatos.getConexion(ctx, Basedatos.ESCRITURA);  // esto tiene que ser en modo ESCRITURA por si requiere actualizar la BB.DD.                     

            float umbral= 15f;
            task.publicarProgreso(0f);
            gestionarCambioDeTemporadaSiProcede(con, ctx, task, 0f, umbral);
            task.publicarProgreso(umbral);
        	actualizarDatosEnBackground(con, ctx, task, umbral, 100f);
            task.publicarProgreso(100f);
            
        	con.close();
        }
    }
        
    @Override
    protected void onChildTitleChanged(Activity childActivity, CharSequence usuario) 
    {
    	super.onChildTitleChanged(childActivity, usuario);
    	ponerTituloApp(usuario.toString());
    }
    
    /*
     * Si hay algo en preferencias, se coge y se pone.
     * Si no hay nada en preferencias:
     * 1.- Si hay algún usuario "demo", se le pone
     * 2.- Si no, se crea usuario "demo" y se le pone por defecto
     */
	public void ponerTituloApp(String usuario) 
	{
        if (bSeSoportaTituloPersonalizado) 
        {
			usuario= Preferencias.getPreferenciaString(getApplicationContext(), Constantes.PREFERENCIAS_USUARIO_NOMBRE, null);
    		if (usuario == null)
    		{
    			UsuarioOp op= new UsuarioOp();
    			Usuario usu= op.getUsuarioDemo(getApplicationContext());
    			if (usu == null)
    				usu= op.crearUsuarioDemo(getApplicationContext());
    			usuario= usu.getNombre();
 	    	    
    			Preferencias.grabarPreferenciaInt(getApplicationContext(), Constantes.PREFERENCIAS_USUARIO_ID, usu.getId());        			
    			Preferencias.grabarPreferenciaString(getApplicationContext(), Constantes.PREFERENCIAS_USUARIO_NOMBRE, usuario);
    		}

    		TextView usu = (TextView) findViewById(R.id.app_usuario);
            usu.setText(/*"usuario: " +*/ usuario);
        }
    }
    
	/*
	 * Si no hay ningún usuario en BB.DD: lo crea y lo pone como usuario actual
	 */
	private void crearUsuarioDemoSiNoExisteNingunUsuario(SQLiteDatabase con)
	{
		UsuarioDao usuDao= new UsuarioDao(con);		
		if (usuDao.getNumUsuarios() == 0)
		{
			UsuarioOp op= new UsuarioOp();
			Usuario usu= op.crearUsuarioDemo(getApplicationContext());
	    	    
			Preferencias.grabarPreferenciaInt(getApplicationContext(), Constantes.PREFERENCIAS_USUARIO_ID, usu.getId());        			
			Preferencias.grabarPreferenciaString(getApplicationContext(), Constantes.PREFERENCIAS_USUARIO_NOMBRE, usu.getNombre());
		}
	}
    
	private boolean hayDatosQueActualizar(SQLiteDatabase con)
	{
		int temporada= Preferencias.getTemporadaActual(getApplicationContext());
		return 		JornadaOp.hayDatosQueActualizar(getApplicationContext(), Equipo.DIVISION_1)  
				||  JornadaOp.hayDatosQueActualizar(getApplicationContext(), Equipo.DIVISION_2)
				||  QuinielaOp.hayDatosQueActualizar(temporada, con, getApplicationContext())
				||  AplicacionOp.hayCambiosEnDatosGeneralesSinActualizar(con, getApplicationContext())
				||  hayCambioDeTemporada(con, getApplicationContext());
	}
	
    /*
     * Actualizará los datos hasta lo más reciente disponible
     */
    private static void actualizarDatosEnBackground(SQLiteDatabase con, Context ctx, TareaSegundoPlano task, float prcMin, float prcMax)
    {
    	// estimo que el 75% del tiempo será para actualizar jornadas y 10% a datos generales:
    	float umbral_jor_quin= prcMin+(prcMax-prcMin)*75f/100f;  
    	float umbral_quin_datGen= prcMin+(prcMax-prcMin)*90f/100f;  
    	
    	JornadaOp.actualizarDatos(ctx, con, task, prcMin, umbral_jor_quin);
    	QuinielaOp.actualizarDatos(ctx, con, task, umbral_jor_quin, umbral_quin_datGen);
    	AplicacionOp.actualizarDatosGenerales(ctx, con, task, umbral_quin_datGen, prcMax);
    }
 
    private void cargaDatosIniciales()
    {
    	String notifTemp= Notificaciones.getNotificacion(Constantes.NOTIFICACION_TEMPORADA_ACTUAL);
    	if (notifTemp != null)
    		Preferencias.grabarPreferenciaInt(getApplicationContext(), Constantes.PREFERENCIAS_TEMPORADA_ACTUAL, new Integer(notifTemp).intValue());    	
    }
        

    private static boolean hayCambioDeTemporada(SQLiteDatabase con, Context ctx)
    {
    	int iUltimaTemporadaEnBBDD= JornadaOp.getUltimaTemporadaCargada(ctx);
    	int iTemporadaActual= Preferencias.getTemporadaActual(ctx);
    	return (iUltimaTemporadaEnBBDD < iTemporadaActual);
    }
    
    private static void gestionarCambioDeTemporada(SQLiteDatabase con, Context ctx, TareaSegundoPlano task, float prcMin, float prcMax)
    {
    	task.publicarProgreso(prcMin);    	
    	
    	int iUltimaTemporadaEnBBDD= JornadaOp.getUltimaTemporadaCargada(ctx);
    	int iTemporadaActual= Preferencias.getTemporadaActual(ctx);
    	
    	task.publicarProgresoEnRango(prcMin, prcMax, 10f);  // 10%
		/*
		 * Tablas afectadas:
				(a) CREATE TABLE temporadas (id integer PRIMARY KEY  NOT NULL  UNIQUE , nombre VARCHAR NOT NULL );
				(b) CREATE TABLE equipos (id varchar PRIMARY KEY  NOT NULL  UNIQUE , nombre varchar NOT NULL );
				(c) CREATE TABLE equipos_temporada (id_temporada integer NOT NULL , id_equipo varchar NOT NULL , division integer NOT NULL ,  PRIMARY KEY (id_temporada, id_equipo));
				(d) CREATE TABLE valoresdefecto_equipos_calidadintrinseca (id_temporada integer NOT NULL , id_equipo varchar NOT NULL , calidad_intrinseca integer NOT NULL,  PRIMARY KEY (id_temporada, id_equipo));
				(e) CREATE TABLE equipos_usuarios (id_temporada integer NOT NULL , id_equipo varchar NOT NULL , id_usuario integer NOT NULL, calidad_intrinseca integer NOT NULL,  PRIMARY KEY (id_temporada, id_equipo));
		 */
		
		/*
		 * (a), (b), (c) y (d) se harán al procesar el fichero .sql de migración (recurso web)
		 */
		StringBuffer _url= new StringBuffer(jm.q1x2.config.Config.getURLCarpetaMigracionesTemporadas()).append("/mig_").append(iTemporadaActual).append(".sql");
		String contenido= null;
		try
		{
			contenido= CargaFichero.getContenidoURL(_url.toString());
	    	task.publicarProgresoEnRango(prcMin, prcMax, 50f);  // 50%
		}
		catch (MalformedURLException e) 
		{
			if (Log.isLoggable(Constantes.LOG_TAG, Log.WARN))
				Log.w(Constantes.LOG_TAG, "Se ha producido un error al cargar fichero.", e);
		}
		catch (IOException e) 
		{ 
			if (Log.isLoggable(Constantes.LOG_TAG, Log.WARN))
				Log.w(Constantes.LOG_TAG, "Se ha producido un error al cargar fichero.", e);
		}
		
		if (contenido == null)
		{
			if (Log.isLoggable(Constantes.LOG_TAG, Log.WARN))
				Log.w(Constantes.LOG_TAG, "Se ha producido un error al cargar el fichero de migración de temporada ("+iTemporadaActual+")");
			Utils.registrarIncidencia("Se ha producido un error al cargar el fichero de migración de temporada ("+iTemporadaActual+")", con);
		}
		else
		{
			String[] sqlsMig= contenido.split(";");
    		for (String sql : sqlsMig)
    			Basedatos.db_execSQL_log(con, sql);
		}
    	task.publicarProgresoEnRango(prcMin, prcMax, 55f);

    		
		/*
		 * (e) para cada usuario, se le pondrá la calidad intrínseca de cada equipo la que por defecto 
		 */
        UsuarioDao usuDao= new UsuarioDao(con);       
        EquipoDao eqDao= new EquipoDao(con);       
		ArrayList<Usuario> usus= usuDao.getUsuarios();
		for (Usuario usu : usus)
		{
			int idUsuario= usu.getId();

	        ArrayList<Equipo> eqs= eqDao.getEquipos(iTemporadaActual);  
	        for (Equipo eq : eqs)
        		eqDao.anadirEquipoUsuario(eq.getId(), idUsuario, iTemporadaActual, eqDao.getValorDefectoCalidadIntrinsecaEquipo(eq.getId(), iTemporadaActual));
		}
		
		
		
    }
    
    private static void gestionarCambioDeTemporadaSiProcede(SQLiteDatabase con, Context ctx, TareaSegundoPlano task, float prcMin, float prcMax)
    {
    	task.publicarProgreso(prcMin);
    	if (hayCambioDeTemporada(con, ctx))
    		gestionarCambioDeTemporada(con, ctx, task, prcMin, prcMax);
    	task.publicarProgreso(prcMax);
    }
    
    
    
    
	private int MENU_ACTUALIZAR= 1;
	private int MENU_CHANGELOG= 2;
	private int MENU_ACERCA_DE= 3;
	private int MENU_SALIR= 4;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
 	    super.onCreateOptionsMenu(menu);
 	    int groupId = 0;	    

 	    menu.add(groupId, MENU_ACTUALIZAR, Menu.NONE, "Actualizar").setIcon(android.R.drawable.ic_menu_rotate);
 	    menu.add(groupId, MENU_CHANGELOG, Menu.NONE, "Cambios").setIcon(android.R.drawable.ic_menu_agenda);
 	    menu.add(groupId, MENU_ACERCA_DE, Menu.NONE, "Acerca de ...").setIcon(android.R.drawable.ic_menu_info_details);
 	    menu.add(groupId, MENU_SALIR, Menu.NONE, "Salir").setIcon(android.R.drawable.ic_menu_close_clear_cancel);
 	    
 	    return true;
    }
       
    @Override
    public boolean onOptionsItemSelected(MenuItem item) 
    {
   	   super.onOptionsItemSelected(item);
       boolean ret= false;
 	   if (item.getItemId() == MENU_ACTUALIZAR)
 	   {
 		   Notificaciones.actualizarNotificaciones();
 		   actualizarDatos(false);
 	       ret= true;
 	   }
 	   else if (item.getItemId() == MENU_CHANGELOG)
 	   {
       	   Intent i = new Intent(getApplicationContext(), Changelog.class);
   		   startActivity(i);
 	       ret= true;
 	   }
 	   else if (item.getItemId() == MENU_ACERCA_DE)
 	   {
       	   Intent i = new Intent(getApplicationContext(), AcercaDe.class);
   		   startActivity(i);
 	       ret= true;
 	   }
 	   else if (item.getItemId() == MENU_SALIR)
 	   {
 		   finish();
 	       ret= true;
 	   }
 	   else
 		   ret= false;
 	   return ret;
    }
  
    /*
     * qué hacer cuando se retorna de alguna de las sub-actividades
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) 
    {
	    super.onActivityResult(requestCode, resultCode, data);
    }    
 
/*    
    @Override
    protected void onPause()
    {
    	super.onPause();
		if (Log.isLoggable(Constantes.LOG_TAG, Log.VERBOSE))
			Log.v(Constantes.LOG_TAG, "### onPause");
    }
    
    @Override
    protected void onResume()
    {
    	super.onResume();
		if (Log.isLoggable(Constantes.LOG_TAG, Log.VERBOSE))
			Log.v(Constantes.LOG_TAG, "### onResume");
    } 
    
    @Override
    protected void onStop()
    {
    	super.onStop();
		if (Log.isLoggable(Constantes.LOG_TAG, Log.VERBOSE))
			Log.v(Constantes.LOG_TAG, "### onStop");
    }
    
    @Override
    protected void onDestroy()
    {
    	super.onDestroy();
		if (Log.isLoggable(Constantes.LOG_TAG, Log.VERBOSE))
			Log.v(Constantes.LOG_TAG, "### onDestroy");
    }
*/    
}