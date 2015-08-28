package jm.q1x2.activities;

import jm.q1x2.R;
import jm.q1x2.bbdd.Basedatos;
import jm.q1x2.bbdd.dao.ConfigDao;
import jm.q1x2.bbdd.dao.QuinielaDao;
import jm.q1x2.transobj.FactoresPesos;
import jm.q1x2.utils.Constantes;
import jm.q1x2.utils.Mensajes;
import jm.q1x2.utils.Preferencias;
import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

public class ConfigParametrosQuinielas extends Activity implements SeekBar.OnSeekBarChangeListener
{
	private int MENU_GRABAR_PATRON= 1;
	private int MENU_CARGAR_PATRON= 2;

	private int SUBACTIVITY_GRABAR_PATRON= 1;
	private int SUBACTIVITY_CARGAR_PATRON= 2;	
	
    int[] factores_barra= new int[] {R.id.pq_aleatoriedad_barra, R.id.pq_calidad_intrinseca_barra, R.id.pq_factor_campo_barra, R.id.pq_golaveraje_total_barra, R.id.pq_golaveraje_localvisit_barra, R.id.pq_puntos_partido_barra, R.id.pq_puntos_partido_localvisit_barra, R.id.pq_ultimos4_barra, R.id.pq_ultimos4_localvisit_barra};
    int[] factores_valor= new int[] {R.id.pq_aleatoriedad_valor, R.id.pq_calidad_intrinseca_valor, R.id.pq_factor_campo_valor, R.id.pq_golaveraje_total_valor, R.id.pq_golaveraje_localvisit_valor, R.id.pq_puntos_partido_valor, R.id.pq_puntos_partido_localvisit_valor, R.id.pq_ultimos4_valor, R.id.pq_ultimos4_localvisit_valor};
    SeekBar[] barras= null;

	public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lay_params_quinielas);
        final ScrollView listaParams = (ScrollView) findViewById(R.id.lista_params_quinielas);
        registerForContextMenu(listaParams);
        
        botones();
        rellenarFactoresConValoresActuales();

    }    

	private void rellenarFactoresConValoresActuales()
	{
		SQLiteDatabase con= Basedatos.getConexion(getApplicationContext(), Basedatos.LECTURA);
		ConfigDao dao= new ConfigDao(con);		
		FactoresPesos f= dao.getParamsConfigQuinielas(Preferencias.getUsuarioId(getApplicationContext()));
		Basedatos.cerrarConexion(con);  //[jm] con.close();
        int[] valores= new int[]{f.getAleatoriedad(), f.getCalidadIntrinseca(), f.getFactorCampo(), f.getGolaveraje(), f.getGolaverajeLocalVisit(), f.getPuntosPartido(), f.getPuntosPartidoLocalVisit(), f.getUltimos4(), f.getUltimos4LocalVisit()};
        
        barras= new SeekBar[valores.length];
        for (int k=0; k< valores.length; k++) 
        {
        	int valor= valores[k];
        	final SeekBar barraK = (SeekBar) findViewById(factores_barra[k]);
        	barras[k]= barraK;
        	final TextView valorK= (TextView) findViewById(factores_valor[k]);

        	valorK.setText(""+valor);
            barraK.setProgress(valor);
            barraK.setSecondaryProgress(valor);
            barraK.setOnSeekBarChangeListener(this);
		}        
	}
	
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) 
	{
		int idx= getIndice(seekBar);
		final TextView valorK= (TextView) findViewById(factores_valor[idx]);
		valorK.setText(""+progress); 
	}    

    private void botones()
    {
    	boton_aceptar();
    	boton_cancelar();
    }

    private void boton_aceptar()
    {
        Button btn_aceptar = (Button)findViewById(R.id.pq_boton_aceptar);
        btn_aceptar.setOnClickListener(new Button.OnClickListener() 
        {
        	public void onClick(View v) 
        	{
        		SQLiteDatabase con= Basedatos.getConexion(getApplicationContext(), Basedatos.ESCRITURA);
        		ConfigDao dao= new ConfigDao(con);
        		
        		FactoresPesos f= rellenarFactoresPesos();        		
        		dao.actualizarParamsConfigQuinielas(Preferencias.getUsuarioId(getApplicationContext()), f);
        		Basedatos.cerrarConexion(con);  //[jm] con.close();
        		
        		setResult(RESULT_OK);
        		finish();
        	}
        });        
    }

    private FactoresPesos rellenarFactoresPesos()
    {
		FactoresPesos f= new FactoresPesos();
		f.setAleatoriedad((new Integer(((TextView) findViewById(R.id.pq_aleatoriedad_valor)).getText().toString())).intValue());
		f.setCalidadIntrinseca((new Integer(((TextView) findViewById(R.id.pq_calidad_intrinseca_valor)).getText().toString())).intValue());
		f.setFactorCampo((new Integer(((TextView) findViewById(R.id.pq_factor_campo_valor)).getText().toString())).intValue());
		f.setGolaverajeLocalVisit((new Integer(((TextView) findViewById(R.id.pq_golaveraje_localvisit_valor)).getText().toString())).intValue());
		f.setGolaveraje((new Integer(((TextView) findViewById(R.id.pq_golaveraje_total_valor)).getText().toString())).intValue());
		f.setPuntosPartidoLocalVisit((new Integer(((TextView) findViewById(R.id.pq_puntos_partido_localvisit_valor)).getText().toString())).intValue());
		f.setPuntosPartido((new Integer(((TextView) findViewById(R.id.pq_puntos_partido_valor)).getText().toString())).intValue());
		f.setUltimos4((new Integer(((TextView) findViewById(R.id.pq_ultimos4_valor)).getText().toString())).intValue());
		f.setUltimos4LocalVisit((new Integer(((TextView) findViewById(R.id.pq_ultimos4_localvisit_valor)).getText().toString())).intValue());
		return f;
    }
    private void boton_cancelar()
    {
        Button btn_cancelar = (Button)findViewById(R.id.pq_boton_cancelar);
        btn_cancelar.setOnClickListener(new Button.OnClickListener() 
        {
        	public void onClick(View v) 
        	{
        		finish();
        	}
        });        
    }
    
    private int getIndice(SeekBar sb)
    {
    	for (int k=0; k< barras.length; k++)
    	{
    		if (sb == barras[k])
    			return k;
    	}
    	return -1;
    }
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
 	    super.onCreateOptionsMenu(menu);
 	    int groupId = 0;	    

 	    menu.add(groupId, MENU_GRABAR_PATRON, Menu.NONE, "grabar como patrón").setIcon(android.R.drawable.ic_menu_save);
 	    menu.add(groupId, MENU_CARGAR_PATRON, Menu.NONE, "cargar patrón").setIcon(android.R.drawable.ic_menu_revert);
 	    
 	    return true;
    }
       
    @Override
    public boolean onOptionsItemSelected(MenuItem item) 
    {
  	   super.onOptionsItemSelected(item);
       boolean ret= false;
 	   if (item.getItemId() == MENU_GRABAR_PATRON)
 	   { 	
 		   Intent i = new Intent(this, QuinielaGrabarPatron.class);
 		   FactoresPesos fac= rellenarFactoresPesos();
    	   i.putExtra(Constantes.QUINIELA_PATRON_GRABAR, fac);
		   startActivityForResult(i, SUBACTIVITY_GRABAR_PATRON);		   
 	       ret= true;
 	   }
 	   else if (item.getItemId() == MENU_CARGAR_PATRON)
 	   {
 			SQLiteDatabase con= Basedatos.getConexion(getApplicationContext(), Basedatos.LECTURA);
 			QuinielaDao qDao= new QuinielaDao(con);
 			int numPatrones= qDao.getNumPatrones(Preferencias.getUsuarioId(getApplicationContext()));
 			Basedatos.cerrarConexion(con);  //[jm] con.close();
 			if (numPatrones > 0)
 			{
 	 		   Intent i = new Intent(this, QuinielaCargarPatron.class);
 			   startActivityForResult(i, SUBACTIVITY_CARGAR_PATRON);		   
 			}
 			else
 				Mensajes.alerta(getApplicationContext(), "Este usuario no tiene ningún patrón grabado.");
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
	    if (requestCode == SUBACTIVITY_CARGAR_PATRON)
	    	rellenarFactoresConValoresActuales();
    }

	public void onStartTrackingTouch(SeekBar arg0) 
	{
	}

	public void onStopTrackingTouch(SeekBar seekBar) 
	{
	}    
   
}
