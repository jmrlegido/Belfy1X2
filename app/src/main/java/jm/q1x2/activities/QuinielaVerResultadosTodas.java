package jm.q1x2.activities;


import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import jm.q1x2.R;
import jm.q1x2.bbdd.Basedatos;
import jm.q1x2.bbdd.dao.QuinielaDao;
import jm.q1x2.logneg.QuinielaOp;
import jm.q1x2.transobj.Partido;
import jm.q1x2.transobj.Quiniela;
import jm.q1x2.transobj.vista.ObjetoIconoTextoTexto;
import jm.q1x2.utils.Constantes;
import jm.q1x2.utils.Mensajes;
import jm.q1x2.utils.Preferencias;
import jm.q1x2.utils.Utils;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class QuinielaVerResultadosTodas extends ListActivity
{
    private ProgressDialog dialog;
	private int SUBACTIVITY_QUINIELA_VER_PRONOSTICO= 1;	
    
	private ArrayList<Integer> id_elemsLista;
	LinearLayout layoutPrincipal;
	int temporadaActual;

	private int MENU_ELIMINAR= 1;
	private int MENU_VER_RESULTADOS= 2;
	
    private ArrayList<ObjetoIconoTextoTexto> m_quinielas = null;
    private AdaptadorEquipo m_adaptador;	

    Button botonRefrescar;
	boolean bHayAlgunaQuinielaSinCorregir= false;
	long iValorTimer= -1;
	long systemCurrentTimeEnTimerCuandoPantallaPierdeFoco= -1;

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
	
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        temporadaActual= Preferencias.getTemporadaActual(getApplicationContext());
        setContentView(R.layout.lay_quinielas_comprobar);        
        m_quinielas = new ArrayList<ObjetoIconoTextoTexto>();

        botonRefrescar= (Button) findViewById(R.id.btn_refrescar);
        
	    ListView lista = (ListView) findViewById(android.R.id.list);
	    registerForContextMenu(lista);

	    dialog = new ProgressDialog(this);
	    dialog.setMessage("Actualizando datos ...");
	    dialog.setTitle("Por favor, espere");
	    //dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
	    dialog.setCancelable(false);

	    rellenarLista();
	    if (bHayAlgunaQuinielaSinCorregir)
	    	controlActualizacionEnTiempoReal();
	    
    }
    
    private void rellenarLista()
    {
	    AsyncTask<String, Float, ArrayList<Quiniela>> plano2= new QuinielaVerResultadoSegundoPlano().execute("");
	    ArrayList<Quiniela> quins= new ArrayList<Quiniela>();
	    try
	    {
	    	quins= plano2.get();
	    }
	    catch(ExecutionException e) 
	    {
	    	quins= new ArrayList<Quiniela>();  // se deja vacío como si realmente no hubiera quinielas
	    } 
	    catch(InterruptedException e)
	    {
	    	quins= new ArrayList<Quiniela>();  // se deja vacío como si realmente no hubiera quinielas
	    } 
    	
	    actualizarListaQuinielas(temporadaActual, quins);    	
    }
    
    private void controlActualizacionEnTiempoReal()
    {
    	botonRefrescar.setVisibility(View.INVISIBLE);
	    if (Utils.estamosEnPeriodoDeTiempoTalQueLaJornadaHaEmpezadoPeroNoAcabado())
	    	botonRefrescar.setVisibility(View.VISIBLE);
    }

    public void refrescar(View v)
    {
    	botonRefrescar.setVisibility(View.INVISIBLE);

    	QuinielaOp.actualizarDatosEnSeguimientoTiempoReal(getApplicationContext());
    	rellenarLista();
    	if (bHayAlgunaQuinielaSinCorregir)
        	botonRefrescar.setVisibility(View.VISIBLE);
    	else
    	{
    		Mensajes.alerta(getApplicationContext(), "La quiniela ya está completa.");
    		setResult(Constantes.RESULT_CODE_SE_HAN_COMPLETADO_TODOS_LOS_PARTIDOS_EN_TIEMPO_REAL);
    		finish();
    	}
    }
    
    private class QuinielaVerResultadoSegundoPlano extends AsyncTask<String, Float, ArrayList<Quiniela>>
    {
	   	 protected void onPreExecute() 
	   	 {
	   		 dialog.setProgress(0);
	   		 dialog.setMax(100);
             dialog.show(); //Mostramos el diálogo antes de comenzar
         }

         protected ArrayList<Quiniela> doInBackground(String... urls) 
         {
        	 return getListaQuinielas(temporadaActual);
         }

         protected void onProgressUpdate (Float... valores) 
         {}

         protected void onPostExecute(ArrayList<Quiniela> bytes) 
         {
        	 if(dialog!=null && dialog.isShowing())
           		dialog.dismiss();              
         }
   }

    @Override
    protected void onPause() 
    {
    	super.onPause();
    	systemCurrentTimeEnTimerCuandoPantallaPierdeFoco= System.currentTimeMillis();
    }
    
    protected boolean presentarQuinielasEnTiempoReal_SiHay()
    {
    	return false;
    }
    
	private ArrayList<Quiniela> getListaQuinielas(int temporada)
	{
        SQLiteDatabase con= Basedatos.getConexion(this, Basedatos.LECTURA);                
		ArrayList<Quiniela> quinielasHechas= getListaQuinielas(temporada, con);
		Basedatos.cerrarConexion(con);  //[jm] con.close();
		return quinielasHechas;
	}
	private ArrayList<Quiniela> getListaQuinielas(int temporada, SQLiteDatabase con)
	{
		ArrayList<Quiniela> quinielasHechas= getQuinielas(temporada, con);
		return quinielasHechas;
	}
    
	private void actualizarListaQuinielas(int temporada)
	{
		ArrayList<Quiniela> quinielasHechas= getListaQuinielas(temporada);
		actualizarListaQuinielas(temporada, quinielasHechas);
	}
	private void actualizarListaQuinielas(int temporada, ArrayList<Quiniela> quinielasHechas)
	{		
		bHayAlgunaQuinielaSinCorregir= false;
		Quiniela quinCorregida= null;        
		if (quinielasHechas.size() == 0)
		{
			Mensajes.alerta(getApplicationContext(), "No hay ninguna quiniela grabada");
			//finish();
		}
		else
		{
	        id_elemsLista= new ArrayList<Integer>();
	        m_quinielas= new ArrayList<ObjetoIconoTextoTexto>();
	        SQLiteDatabase con= Basedatos .getConexion(this, Basedatos.LECTURA);
	        for (Quiniela q: quinielasHechas)
	        {
	        	int aciertos= -1;
	        	String txtAciertos= "-";
	        	if (q.haSidoCalculadoElNumeroDeAciertos())
	        	{
	        		aciertos= q.getAciertos();
	        		txtAciertos= ""+aciertos;
	        	}
	        	else
	        	{
		        	quinCorregida= QuinielaOp.getQuinielaCorregidaDeJornada(temporada, q.getJornada(), con);  // leo en BB.DD.
		        	if (quinCorregida != null)
		        	{ // está en BB.DD.
			        	aciertos= QuinielaOp.obtenerAciertosQuiniela(q, quinCorregida);			        	
			        	if (aciertos == Quiniela.ACIERTOS_VALOR_PARA_NO_CALCULADO)
			        		QuinielaOp.actualizarAciertosEnQuinielasHechasSobreEstaCorregida(q.getJornada(), quinCorregida, getApplicationContext(), con);
			        	txtAciertos= ""+aciertos;
		        	}
		        	else
		        	{
		        		/*
		        		 * no está en BB.DD.
		        		 * Quizá hay una corrección parcial.
		        		 */
		        		
		        		if (presentarQuinielasEnTiempoReal_SiHay())
		        		{
			        		bHayAlgunaQuinielaSinCorregir= true;
	
			        		if (Utils.estamosEnPeriodoDeTiempoTalQueLaJornadaHaEmpezadoPeroNoAcabado())
			        		{
			        			Quiniela quinParcialmenteCorregida= QuinielaOp.getQuinielaParcialmenteCorregida(getApplicationContext(), temporada, q.getJornada());
			        			if (quinParcialmenteCorregida != null)
			        			{
			        				aciertos= QuinielaOp.obtenerAciertosQuiniela(q, quinParcialmenteCorregida);
			    	        		int iNumPartidosCorregidos= getNumPartidosCorregidos(quinParcialmenteCorregida);
			    	        		txtAciertos= "("+aciertos+" de "+iNumPartidosCorregidos+")";
			        			}
			        		}
		        		}
		        	}
	        	}
	        	id_elemsLista.add(new Integer(q.getId()));

                if (aciertos >= Constantes.ACIERTOS_UMBRAL_PREMIO)
                	txtAciertos= "¡premiada! " + txtAciertos;
	        	
	        	m_quinielas.add(new ObjetoIconoTextoTexto(null, "["+Utils.getFecha(q.getJornada())+"] "+q.getNombre(), txtAciertos));
	        }
        	Basedatos.cerrarConexion(con);  //[jm] con.close();
        	
	        if (m_quinielas.size() > 0)
	        {
		        this.m_adaptador = new AdaptadorEquipo(this, R.layout.cada_quiniela_corregida, m_quinielas);
		        setListAdapter(this.m_adaptador);
	        }
	        else
	        {
	        	setContentView(R.layout.lay_mensaje);
	        	TextView msg= (TextView) findViewById(R.id.texto_mensaje);
	        	msg.setText("No está disponible aún la corrección de ninguna quiniela.");
	        }
		}
	}
    	
	private int getNumPartidosCorregidos(Quiniela q)
	{
		int ret= 0;
		ArrayList<Partido> partidos= q.getPartidos();
		for (Partido p :  partidos)
		{
			if (p.getResultadoQuiniela() != QuinielaOp.RES_NO_DISPONIBLE)
				ret++;
		}
		return ret;
	}

	/*
	 * @since 1.6
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) 
	{
	  super.onListItemClick(l, v, position, id);
      accionConCadaElementoLista(position);
	}
	
	
    /*
     * menú contextual de los elementos de la lista
     */
    @Override    
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) 
    {
      super.onCreateContextMenu(menu, v, menuInfo);
      final AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;      
      accionConCadaElementoLista(new Long(info.id).intValue());
    }

    public void accionConCadaElementoLista(final int pos) 
    {
      final int[] itemsId = {MENU_VER_RESULTADOS, MENU_ELIMINAR};
      final CharSequence[] itemsDesc = {"Ver pronóstico", "Eliminar"};
      AlertDialog.Builder builder = new AlertDialog.Builder(this);
  	  ObjetoIconoTextoTexto selec= m_quinielas.get(pos);
      builder.setTitle(selec.getTexto1());
      builder.setItems(itemsDesc, new DialogInterface.OnClickListener() 
      {
          public void onClick(DialogInterface dialog, int item) 
          {
        	  if (itemsId[item] == MENU_VER_RESULTADOS)
        	  {
	       		   Integer id= id_elemsLista.get(pos);
	       		   Intent i = new Intent(getApplicationContext(), QuinielaVerPronostico.class);
	       		   i.putExtra(Constantes.ID_QUINIELA, id.intValue());
	       		   startActivityForResult(i, SUBACTIVITY_QUINIELA_VER_PRONOSTICO);
        	  }
        	  else if (itemsId[item] == MENU_ELIMINAR)
        	  {
				   if (id_elemsLista.size() > pos)
				   {
					   Integer idQuin = id_elemsLista.get(pos);
					   SQLiteDatabase con = Basedatos.getConexion(getApplicationContext(), Basedatos.ESCRITURA);
					   QuinielaDao qDao = new QuinielaDao(con);
					   qDao.borrar(idQuin.intValue());
					   Basedatos.cerrarConexion(con);  //[jm] con.close();
					   actualizarListaQuinielas(temporadaActual);

					   Mensajes.alerta(getApplicationContext(), "Quiniela eliminada correctamente");
				   }
        	  }
          }
      });
      AlertDialog alert = builder.create();
      alert.show();
    }
    
    
    /*
     * este método es sobrescribible en caso de querer mostrar quinielas de la semana pasada, etc
     */
    protected ArrayList<Quiniela> getQuinielas(int temporada, SQLiteDatabase con)
    {
		QuinielaOp qOp= new QuinielaOp();
		return qOp.getTodasQuinielasDeTemporada(temporada, Preferencias.getUsuarioId(getApplicationContext()), con);		
    }
        
	private class AdaptadorEquipo extends ArrayAdapter<ObjetoIconoTextoTexto> 
	{
        private ArrayList<ObjetoIconoTextoTexto> items;

        public AdaptadorEquipo(Context context, int textViewResourceId, ArrayList<ObjetoIconoTextoTexto> items) 
        {
            super(context, textViewResourceId, items);
            this.items = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) 
        {
            View v = convertView;
            if (v == null) 
            {
                LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.cada_quiniela_corregida, null);
            }
            ObjetoIconoTextoTexto o = items.get(position);
            if (o != null) 
            {
                TextView tt = (TextView) v.findViewById(R.id.quiniela_nombre);
                if (tt != null)
                    tt.setText(o.getTexto1());
                TextView ci = (TextView) v.findViewById(R.id.quiniela_aciertos);                
                if (ci != null)
                    ci.setText(o.getTexto2());                
            }
            return v;
        }
	}	
	
}
