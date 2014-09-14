package jm.q1x2.activities;


import java.util.ArrayList;

import jm.q1x2.R;
import jm.q1x2.bbdd.Basedatos;
import jm.q1x2.bbdd.dao.ConfigDao;
import jm.q1x2.bbdd.dao.EquipoDao;
import jm.q1x2.logneg.QuinielaOp;
import jm.q1x2.transobj.FactoresPesos;
import jm.q1x2.transobj.Partido;
import jm.q1x2.transobj.PartidoQuiniela;
import jm.q1x2.transobj.Quiniela;
import jm.q1x2.transobj.ValoracionEquiposPartidoQuiniela;
import jm.q1x2.utils.Constantes;
import jm.q1x2.utils.Mensajes;
import jm.q1x2.utils.Notificaciones;
import jm.q1x2.utils.Preferencias;
import jm.q1x2.utils.Utils;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class QuinielaHacer extends Activity implements SeekBar.OnSeekBarChangeListener 
{
    private int SUBACTIVITY_PARAMS_QUINIELAS= 1;
	private int SUBACTIVITY_GRABAR_QUINIELA= 2;
	
    private ProgressDialog dialog;
	private Quiniela quinielaHecha= null;

	SeekBar barraTriples;	SeekBar barraDobles;
	TextView labelTriples;	TextView labelDobles;
	TextView valorTriples;	TextView valorDobles;
	
	private boolean bQuinielaYaGenerada= false;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lay_quiniela_hacer_pre);

        initDoblesTriples();
    	
        String notifQuin= Notificaciones.getNotificacion(Constantes.NOTIFICACION_QUINIELA_PROXIMA);
        if (notifQuin.compareTo(Utils.hoy()) <= 0)  // la quiniela que está en notificaciones es de una fecha anterior o igual al día de hoy
        {
     		Mensajes.alerta(getApplicationContext(), "Aún no está disponible la quiniela de esta semana ...");
     		finish();        	
        }        
        else if (!quinielaDisponible())
     	{
     		Mensajes.alerta(getApplicationContext(), "La quiniela no está disponible. Debe actualizar los datos.");
     		finish();
     	}
 		else
 		{
	        botones();
	        actualizarValores();
 		}
    }    

    private void initDoblesTriples()
    {
    	barraTriples= (SeekBar) findViewById(R.id.triples);
    	barraDobles= (SeekBar) findViewById(R.id.dobles);
        
    	int numDobles= Preferencias.getPreferenciaInt(getApplicationContext(), Constantes.PREFERENCIAS_QUINIELA_NUM_DOBLES, -1);
    	if (numDobles == -1)
    	{
    		numDobles= 0;
    		Preferencias.grabarPreferenciaInt(getApplicationContext(), Constantes.PREFERENCIAS_QUINIELA_NUM_DOBLES, numDobles);
    	}
    	int numTriples= Preferencias.getPreferenciaInt(getApplicationContext(), Constantes.PREFERENCIAS_QUINIELA_NUM_TRIPLES, -1);
    	if (numTriples == -1)
    	{
    		numTriples= 0;
    		Preferencias.grabarPreferenciaInt(getApplicationContext(), Constantes.PREFERENCIAS_QUINIELA_NUM_TRIPLES, numTriples);
    	}
    	
    	barraTriples.setProgress(numTriples);
    	barraDobles.setProgress(numDobles);

    	/*** valores ***/
    	valorTriples= (TextView) findViewById(R.id.triples_valor);
    	valorTriples.setText(""+numTriples);
    	valorDobles= (TextView) findViewById(R.id.dobles_valor);
    	valorDobles.setText(""+numDobles);
    	
    	
    	/*** label ***/
    	labelTriples= (TextView) findViewById(R.id.triples_label);
    	labelDobles= (TextView) findViewById(R.id.dobles_label);
    	labelTriples.setText("Triples (0-9): ");
    	labelDobles.setText("Dobles (0-14): ");
    	
        barraTriples.setOnSeekBarChangeListener(this);
        barraDobles.setOnSeekBarChangeListener(this);
    	
    }
        
    private boolean quinielaDisponible()
    {
 		String notifQuin= Notificaciones.getNotificacion(Constantes.NOTIFICACION_QUINIELA_PROXIMA);
 		int quin= new Integer(notifQuin.trim()).intValue();
 		return (quin != QuinielaOp.QUINIELA_NO_DISPONIBLE);
    }
    
    private void actualizarValores()
    {
		SQLiteDatabase con= Basedatos.getConexion(getApplicationContext(), Basedatos.LECTURA);
		ConfigDao dao= new ConfigDao(con);		
		FactoresPesos f= dao.getParamsConfigQuinielas(Preferencias.getUsuarioId(getApplicationContext()));
		con.close();	

		TextView t= (TextView) findViewById(R.id.aleatoriedad); t.setText(""+f.getAleatoriedad()+"%");
		t= (TextView) findViewById(R.id.calidad_intrinseca); t.setText(""+f.getCalidadIntrinseca()+"%");
		t= (TextView) findViewById(R.id.factor_campo); t.setText(""+f.getFactorCampo()+"%");
		t= (TextView) findViewById(R.id.golaveraje_total); t.setText(""+f.getGolaveraje()+"%");
		t= (TextView) findViewById(R.id.golaveraje_localvisit); t.setText(""+f.getGolaverajeLocalVisit()+"%");
		t= (TextView) findViewById(R.id.ptos_partido); t.setText(""+f.getPuntosPartido()+"%");
		t= (TextView) findViewById(R.id.ptos_partido_localvisit); t.setText(""+f.getPuntosPartidoLocalVisit()+"%");
		t= (TextView) findViewById(R.id.ult4partidos); t.setText(""+f.getUltimos4()+"%");
		t= (TextView) findViewById(R.id.ult4partidos_localvisit); t.setText(""+f.getUltimos4LocalVisit()+"%");
    }

    int maxDobles[]= new int[]{14, 13, 11, 10, 8, 7, 5, 3, 2, 0};  //el índice de la matriz es el número de triples
	/*
	 * Tabla de posibles valores. Para leer la tabla:  X -> Y se lee "si hay X triples, el número máximo de dobles es Y".
	 * 0 -> 14		1 -> 13		2 -> 11		3 -> 10		4 -> 8
	 * 5 -> 7		6 ->  5		7 ->  3		8 ->  2		9 -> 0
	 */
    
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) 
	{
		int triples= barraTriples.getProgress();
		int dobles= barraDobles.getProgress();
				
		if (dobles > maxDobles[triples])
		{
			barraDobles.setProgress(maxDobles[triples]);
			Mensajes.alerta(getApplicationContext(), "Con "+triples+" triples no se permiten más de "+maxDobles[triples]+" dobles.");			
		}
		
		actualizarDoblesTriples();
	}    

	/*
	 * Aquí se presupone que el SeekBar de dobles y triples tienen los valores correctos
	 */
	private void actualizarDoblesTriples()
	{
		int triples= barraTriples.getProgress();
		int dobles= barraDobles.getProgress();

		/*** label ***/
    	labelTriples.setText("Triples (0-9): ");    	 
    	if (maxDobles[triples] == 0)
    		labelDobles.setText("Dobles (0): ");
    	else
    		labelDobles.setText("Dobles (0-"+maxDobles[triples]+"): ");
		
		/*** valores ***/
    	valorTriples.setText(""+triples);
    	valorDobles.setText(""+dobles);
	}
	
    private void botones()
    {
    	boton_hacer_quiniela();
    	boton_cambiar_valores();
    }

    private void boton_hacer_quiniela()
    {    	
        Button btn_aceptar = (Button)findViewById(R.id.boton_hacer_quiniela);
        btn_aceptar.setOnClickListener(new Button.OnClickListener() 
        {
        	public void onClick(View v) 
        	{
     			bQuinielaYaGenerada= false;
     			
     	    	Preferencias.grabarPreferenciaInt(getApplicationContext(), Constantes.PREFERENCIAS_QUINIELA_NUM_DOBLES, barraDobles.getProgress());
     	    	Preferencias.grabarPreferenciaInt(getApplicationContext(), Constantes.PREFERENCIAS_QUINIELA_NUM_TRIPLES, barraTriples.getProgress());
     			
        		presentarQuiniela();
        	}
        });        
    }

    private void boton_cambiar_valores()
    {
        Button btn_cancelar = (Button)findViewById(R.id.boton_cambiar_valores);
        btn_cancelar.setOnClickListener(new Button.OnClickListener() 
        {
        	public void onClick(View v) 
        	{
   	 		   Intent i = new Intent(getApplicationContext(), ConfigParametrosQuinielas.class);			
  			   startActivityForResult(i, SUBACTIVITY_PARAMS_QUINIELAS);  
        	}
        });        
    }

    private void boton_grabar_quiniela()
    {
        Button btn = (Button)findViewById(R.id.boton_grabar_quiniela);
        btn.setOnClickListener(new Button.OnClickListener() 
        {
        	public void onClick(View v) 
        	{
	 		   Intent i = new Intent(getApplicationContext(), QuinielaGrabar.class);			
	    	   i.putExtra(Constantes.QUINIELA_GRABAR, quinielaHecha);
	    	   startActivityForResult(i, SUBACTIVITY_GRABAR_QUINIELA);  
        	}
        });        
    }
    
    /*
     * qué hacer cuando se retorna de alguna de las sub-actividades
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) 
    {
	    super.onActivityResult(requestCode, resultCode, data);
	    if (requestCode == SUBACTIVITY_PARAMS_QUINIELAS)
	    	actualizarValores();
	    else if (requestCode == SUBACTIVITY_GRABAR_QUINIELA  &&  resultCode == RESULT_OK)
	    {
	    	Mensajes.alerta(getApplicationContext(), "Quiniela grabada correctamente");
	    	Button btn = (Button)findViewById(R.id.boton_grabar_quiniela);
	    	if (btn != null)
	    		btn.setVisibility(View.INVISIBLE);
	    	finish();
	    }
    }    
    
    private void presentarQuiniela()
    {     	
	    dialog = new ProgressDialog(this);
	    dialog.setMessage("Calculando resultados de quiniela ...");
	    dialog.setTitle("Por favor, espere");
	    dialog.setCancelable(false);
	    new PresentarQuinielaSegundoPlano().execute("");
    }
       
    private void mostrarResultado(String eq1, String eq2, int res1x2, int resIdRivales, int resId1, int resIdx, int resId2)
    {    	
    	((TextView) findViewById(resIdRivales)).setText(eq1 + " - "+ eq2);
    	((ImageView) findViewById(resId1)).setImageResource(res1x2==QuinielaOp.RES_1 
    													 || res1x2==QuinielaOp.RES_1X 
    													 || res1x2==QuinielaOp.RES_1X2 
    													 ? R.drawable.quin_sel : R.drawable.quin_1);
    	((ImageView) findViewById(resIdx)).setImageResource(res1x2==QuinielaOp.RES_X
    													 || res1x2==QuinielaOp.RES_1X
    													 || res1x2==QuinielaOp.RES_X2
    	    	    									 || res1x2==QuinielaOp.RES_1X2
    													 ? R.drawable.quin_sel : R.drawable.quin_x);
    	((ImageView) findViewById(resId2)).setImageResource(res1x2==QuinielaOp.RES_2
    													 || res1x2==QuinielaOp.RES_X2
    													 || res1x2==QuinielaOp.RES_1X2
    													 ? R.drawable.quin_sel : R.drawable.quin_2);
    }

    private void mostrarResultadoPleno15(String eq1, String eq2, int res1x2)
    {    	
    	((TextView) findViewById(R.id.p15_equipolocal)).setText("      "+eq1);
    	((ImageView) findViewById(R.id.p15_equipolocal_0)).setImageResource(res1x2==QuinielaOp.RES_PLENO15_00 
    													 || res1x2==QuinielaOp.RES_PLENO15_01 
    													 || res1x2==QuinielaOp.RES_PLENO15_02
    													 || res1x2==QuinielaOp.RES_PLENO15_0M
    													 ? R.drawable.quin_sel : R.drawable.quin_goles0);
    	((ImageView) findViewById(R.id.p15_equipolocal_1)).setImageResource(res1x2==QuinielaOp.RES_PLENO15_10 
														 || res1x2==QuinielaOp.RES_PLENO15_11 
														 || res1x2==QuinielaOp.RES_PLENO15_12
														 || res1x2==QuinielaOp.RES_PLENO15_1M
														 ? R.drawable.quin_sel : R.drawable.quin_goles1);
    	((ImageView) findViewById(R.id.p15_equipolocal_2)).setImageResource(res1x2==QuinielaOp.RES_PLENO15_20 
														 || res1x2==QuinielaOp.RES_PLENO15_21 
														 || res1x2==QuinielaOp.RES_PLENO15_22
														 || res1x2==QuinielaOp.RES_PLENO15_2M
														 ? R.drawable.quin_sel : R.drawable.quin_goles2);
    	((ImageView) findViewById(R.id.p15_equipolocal_m)).setImageResource(res1x2==QuinielaOp.RES_PLENO15_M0 
														 || res1x2==QuinielaOp.RES_PLENO15_M1 
														 || res1x2==QuinielaOp.RES_PLENO15_M2
														 || res1x2==QuinielaOp.RES_PLENO15_MM
														 ? R.drawable.quin_sel : R.drawable.quin_golesm);

    	((TextView) findViewById(R.id.p15_equipovisitante)).setText("      "+eq2);
    	((ImageView) findViewById(R.id.p15_equipovisitante_0)).setImageResource(res1x2==QuinielaOp.RES_PLENO15_00 
    													 || res1x2==QuinielaOp.RES_PLENO15_10 
    													 || res1x2==QuinielaOp.RES_PLENO15_20
    													 || res1x2==QuinielaOp.RES_PLENO15_M0
    													 ? R.drawable.quin_sel : R.drawable.quin_goles0);
    	((ImageView) findViewById(R.id.p15_equipovisitante_1)).setImageResource(res1x2==QuinielaOp.RES_PLENO15_01 
														 || res1x2==QuinielaOp.RES_PLENO15_11 
														 || res1x2==QuinielaOp.RES_PLENO15_21
														 || res1x2==QuinielaOp.RES_PLENO15_M1
														 ? R.drawable.quin_sel : R.drawable.quin_goles1);
    	((ImageView) findViewById(R.id.p15_equipovisitante_2)).setImageResource(res1x2==QuinielaOp.RES_PLENO15_02 
														 || res1x2==QuinielaOp.RES_PLENO15_12 
														 || res1x2==QuinielaOp.RES_PLENO15_22
														 || res1x2==QuinielaOp.RES_PLENO15_M2
														 ? R.drawable.quin_sel : R.drawable.quin_goles2);
    	((ImageView) findViewById(R.id.p15_equipovisitante_m)).setImageResource(res1x2==QuinielaOp.RES_PLENO15_0M 
														 || res1x2==QuinielaOp.RES_PLENO15_1M 
														 || res1x2==QuinielaOp.RES_PLENO15_2M
														 || res1x2==QuinielaOp.RES_PLENO15_MM
														 ? R.drawable.quin_sel : R.drawable.quin_golesm);
    
    }
    
    int[] resIdRivales= new int[]{R.id.rivales1,R.id.rivales2,R.id.rivales3,R.id.rivales4,R.id.rivales5,R.id.rivales6,R.id.rivales7,R.id.rivales8,R.id.rivales9,R.id.rivales10,R.id.rivales11,R.id.rivales12,R.id.rivales13,R.id.rivales14};
    int[] resIdSigno1= new int[]{R.id.p1_1,R.id.p2_1,R.id.p3_1,R.id.p4_1,R.id.p5_1,R.id.p6_1,R.id.p7_1,R.id.p8_1,R.id.p9_1,R.id.p10_1,R.id.p11_1,R.id.p12_1,R.id.p13_1,R.id.p14_1};
    int[] resIdSignoX= new int[]{R.id.p1_x,R.id.p2_x,R.id.p3_x,R.id.p4_x,R.id.p5_x,R.id.p6_x,R.id.p7_x,R.id.p8_x,R.id.p9_x,R.id.p10_x,R.id.p11_x,R.id.p12_x,R.id.p13_x,R.id.p14_x};
    int[] resIdSigno2= new int[]{R.id.p1_2,R.id.p2_2,R.id.p3_2,R.id.p4_2,R.id.p5_2,R.id.p6_2,R.id.p7_2,R.id.p8_2,R.id.p9_2,R.id.p10_2,R.id.p11_2,R.id.p12_2,R.id.p13_2,R.id.p14_2};
    
    private class PresentarQuinielaSegundoPlano extends AsyncTask<String, Float, Integer>
    {
		 ArrayList<PartidoQuiniela> filasQuiniela= null;
    	
	   	 protected void onPreExecute() 
	   	 {
        	 if (!bQuinielaYaGenerada)
        	 {
		   		 dialog.setProgress(0);
		   		 dialog.setMax(100);
	             dialog.show(); //Mostramos el diálogo antes de comenzar
        	 }
         }

         protected Integer doInBackground(String... urls) 
         {             
        	if (bQuinielaYaGenerada)
        		return 0;
        	
          	int idUsuario= Preferencias.getUsuarioId(getApplicationContext());
        	
     		SQLiteDatabase con= Basedatos.getConexion(getApplicationContext(), Basedatos.LECTURA);
     		ConfigDao daoConf= new ConfigDao(con);
     		EquipoDao daoEq= new EquipoDao(con);
     		FactoresPesos fac= daoConf.getParamsConfigQuinielas(idUsuario);

     		QuinielaOp quinOp= new QuinielaOp();
     		String notifQuin= Notificaciones.getNotificacion(Constantes.NOTIFICACION_QUINIELA_PROXIMA);
     		int iJornadaQuinielaEstaSemana= new Integer(notifQuin.trim()).intValue();
         	Quiniela quin= quinOp.getQuinielaEstaSemana(iJornadaQuinielaEstaSemana, con, getApplicationContext());

         	if (quin != null)  // si el usuario tiene descargada la quiniela de la jornada ...
         	{
	         	int idTemporada= Preferencias.getTemporadaActual(getApplicationContext());
	     		ArrayList<Partido> partidos= quin.getPartidos();
	     		quinielaHecha= new Quiniela();
	     		quinielaHecha.setTemporada(idTemporada);
	     		quinielaHecha.setJornada(iJornadaQuinielaEstaSemana);
	     		
	     		filasQuiniela= new ArrayList<PartidoQuiniela>();
	     			    		
	     		ArrayList<ValoracionEquiposPartidoQuiniela> valoresLocalVisit= quinOp.getValoracionesEquiposSegunCriterios(partidos, fac, con, idTemporada, idUsuario, getApplicationContext());
	     		ArrayList<Partido> partidosConPronosticos1x2= quinOp.convertirValoracionesEnPronostico1X2(partidos, valoresLocalVisit, barraTriples.getProgress(), barraDobles.getProgress());
	     		
	     		Partido par;
	     		for (int idx= 0; idx < 14; idx++)  // todos excepto pleno al 15
	     		{
		         	par= partidosConPronosticos1x2.get(idx);
		         	quinielaHecha.annadirPartido(par);        	
		         	filasQuiniela.add(new PartidoQuiniela(daoEq.getNombreEquipo(par.getIdEquipoLocal()), daoEq.getNombreEquipo(par.getIdEquipoVisit()), par.getResultadoQuiniela(), resIdRivales[idx], resIdSigno1[idx], resIdSignoX[idx], resIdSigno2[idx]));
	     		}

	     		// pleno al 15
	         	par= partidosConPronosticos1x2.get(14);
	         	quinielaHecha.annadirPartido(par);   
	         	PartidoQuiniela ple15= new PartidoQuiniela();
	         	ple15.setEq1(daoEq.getNombreEquipo(par.getIdEquipoLocal()));
	         	ple15.setEq2(daoEq.getNombreEquipo(par.getIdEquipoVisit()));
	         	ple15.setResultado1x2(par.getResultadoQuiniela());
	         	filasQuiniela.add(ple15);
	     		
         	}
     		con.close();	    	        

            return 1;
         }

         protected void onProgressUpdate (Float... valores) 
         {
        	 // no lo usamos porque no muestra porcentaje de evolución: 74% ...
        	 /*
	       	  int p = Math.round(100*valores[0]);
	       	  dialog.setProgress(p);
	       	 */
         }

         protected void onPostExecute(Integer bytes) 
         {        	 
            setContentView(R.layout.lay_quiniela);
          	boton_grabar_quiniela();

          	if (filasQuiniela == null)
          	{
         		Mensajes.alerta(getApplicationContext(), "La quiniela no está descargada. Debe actualizar los datos.");
         		finish();          		
          	}
          	else
          	{
          		PartidoQuiniela fila= null;
	          	for(int k= 0; k< filasQuiniela.size()-1;k++)   // partidos 1 a 14
	          	{
		          	fila= filasQuiniela.get(k);
	          		mostrarResultado(fila.getEq1(), fila.getEq2(), fila.getResultado1x2(), fila.getResIdRivales(), fila.getResId1(), fila.getResIdx(), fila.getResId2());
	          	}
	          	
	          	//pleno al 15
	          	fila= filasQuiniela.get(filasQuiniela.size()-1);
	          	mostrarResultadoPleno15(fila.getEq1(), fila.getEq2(), fila.getResultado1x2());
	          	
		        bQuinielaYaGenerada= true;
          	}
          	if (dialog!=null && dialog.isShowing())
          		dialog.dismiss();
         }
   }

	public void onStartTrackingTouch(SeekBar arg0) 
	{
		// obligatorio por Interfaz. No quiero que haga nada.
	}

	public void onStopTrackingTouch(SeekBar arg0) 
	{
		// obligatorio por Interfaz. No quiero que haga nada.
	}

}
