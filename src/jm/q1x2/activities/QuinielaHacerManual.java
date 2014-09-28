package jm.q1x2.activities;


import java.util.ArrayList;

import jm.q1x2.R;
import jm.q1x2.bbdd.Basedatos;
import jm.q1x2.bbdd.dao.EquipoDao;
import jm.q1x2.logneg.QuinielaOp;
import jm.q1x2.transobj.Partido;
import jm.q1x2.transobj.PartidoQuiniela;
import jm.q1x2.transobj.Quiniela;
import jm.q1x2.utils.Constantes;
import jm.q1x2.utils.Mensajes;
import jm.q1x2.utils.Notificaciones;
import jm.q1x2.utils.Preferencias;
import jm.q1x2.utils.Utils;
import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class QuinielaHacerManual extends Activity  
{
	private int SUBACTIVITY_GRABAR_QUINIELA= 1;
	
	private Quiniela quinielaHecha= null;

    boolean[] signo1_marcadas= new boolean[15];  // no tendré en cuenta el índice 0
    boolean[] signoX_marcadas= new boolean[15];  // no tendré en cuenta el índice 0
    boolean[] signo2_marcadas= new boolean[15];  // no tendré en cuenta el índice 0
    boolean signo_pleno15_local_0_marcado= false;
    boolean signo_pleno15_local_1_marcado= false;
    boolean signo_pleno15_local_2_marcado= false;
    boolean signo_pleno15_local_m_marcado= false;
    boolean signo_pleno15_visit_0_marcado= false;
    boolean signo_pleno15_visit_1_marcado= false;
    boolean signo_pleno15_visit_2_marcado= false;
    boolean signo_pleno15_visit_m_marcado= false;
	
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        for (int k=1; k<=14; k++)
        {
        	signo1_marcadas[k]= false;
        	signoX_marcadas[k]= false;
        	signo2_marcadas[k]= false;
        }
        
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
     		SQLiteDatabase con= Basedatos.getConexion(getApplicationContext(), Basedatos.LECTURA);
     		EquipoDao daoEq= new EquipoDao(con);     		
     		QuinielaOp quinOp= new QuinielaOp();
     		
     		setContentView(R.layout.lay_quiniela_manual);
     		
     		int iJornadaQuinielaEstaSemana= new Integer(notifQuin.trim()).intValue();
         	Quiniela quin= quinOp.getQuinielaEstaSemana(iJornadaQuinielaEstaSemana, con, getApplicationContext());

         	ArrayList<PartidoQuiniela> filasQuiniela= new ArrayList<PartidoQuiniela>();
         	
         	if (quin != null)  // si el usuario tiene descargada la quiniela de la jornada ...
         	{
	         	int idTemporada= Preferencias.getTemporadaActual(getApplicationContext());
	     		ArrayList<Partido> partidos= quin.getPartidos();
	     		quinielaHecha= new Quiniela();
	     		quinielaHecha.setTemporada(idTemporada);
	     		quinielaHecha.setJornada(iJornadaQuinielaEstaSemana);
	     		
	     		Partido par;
	     		for (int idx= 0; idx < 14; idx++)   // partidos 1 a 14
	     		{
		         	par= partidos.get(idx);
		         	quinielaHecha.annadirPartido(par);        	
		         	filasQuiniela.add(new PartidoQuiniela(daoEq.getNombreEquipo(par.getIdEquipoLocal()), daoEq.getNombreEquipo(par.getIdEquipoVisit()), resIdRivales[idx]));
	     		}

	     		// pleno al 15
	         	par= partidos.get(14);
	         	quinielaHecha.annadirPartido(par);   
	         	PartidoQuiniela pq= new PartidoQuiniela();
	         	pq.setEq1(daoEq.getNombreEquipo(par.getIdEquipoLocal()));
	         	pq.setEq2(daoEq.getNombreEquipo(par.getIdEquipoVisit()));
	         	filasQuiniela.add(pq);
	     		
         	}
     		con.close();
     		
     		PartidoQuiniela fila= null;
     		for (int k= 0; k< filasQuiniela.size()-1; k++)   // partidos 1 a 14
     		{
     			fila= filasQuiniela.get(k);
     			mostrarPartido(fila.getEq1(), fila.getEq2(), fila.getResIdRivales());
     		}
     		
     		// pleno al 15:
 			fila= filasQuiniela.get(filasQuiniela.size()-1);
 			mostrarPartidoPleno15(fila.getEq1(), fila.getEq2());
 		}
    }    

    private boolean quinielaDisponible()
    {
 		String notifQuin= Notificaciones.getNotificacion(Constantes.NOTIFICACION_QUINIELA_PROXIMA);
 		int quin= new Integer(notifQuin.trim()).intValue();
 		return (quin != QuinielaOp.QUINIELA_NO_DISPONIBLE);
    }


	public void grabar_quiniela(View v)
    {
	   /*
	    * Debo "inyectar" en los partidos del objeto 'quinielaHecha' los resultados,
	    * según lo que el usuario ha pinchado y está en las estructuras de datos signo1_marcadas[], signoX_marcadas[] y signo2_marcadas[]	
	    */
		
	   ArrayList<Partido> partidos= quinielaHecha.getPartidos();
	   Partido p= null;
	   for (int k=0; k<14; k++)   // todos menos el pleno al 15
	   {
		   p= partidos.get(k);
		   
		   int result= -1;
		   if (signo1_marcadas[k+1])
		   {
			   if (signoX_marcadas[k+1])
			   {
				   if (signo2_marcadas[k+1])
					   result= QuinielaOp.RES_1X2;
				   else
					   result= QuinielaOp.RES_1X;
			   }
			   else if (signo2_marcadas[k+1])
				   result= QuinielaOp.RES_12;
			   else
				   result= QuinielaOp.RES_1;
		   }
		   else if (signoX_marcadas[k+1])
		   {
			   if (signo2_marcadas[k+1])
				   result= QuinielaOp.RES_X2;
			   else
				   result= QuinielaOp.RES_X;
		   }
		   else if (signo2_marcadas[k+1])
			   result= QuinielaOp.RES_2;
		   
		   p.setResultadoQuiniela(result);
	   }

	   // pleno al 15:
	   p= partidos.get(14);
	   
	   if (signo_pleno15_local_0_marcado)
	   {
		   if (signo_pleno15_visit_0_marcado)
			   p.setResultadoQuiniela(QuinielaOp.RES_PLENO15_00);
		   else if (signo_pleno15_visit_1_marcado)
			   p.setResultadoQuiniela(QuinielaOp.RES_PLENO15_01);
		   else if (signo_pleno15_visit_2_marcado)
			   p.setResultadoQuiniela(QuinielaOp.RES_PLENO15_02);
		   else if (signo_pleno15_visit_m_marcado)
			   p.setResultadoQuiniela(QuinielaOp.RES_PLENO15_0M);
	   }
	   else if (signo_pleno15_local_1_marcado)
	   {
		   if (signo_pleno15_visit_0_marcado)
			   p.setResultadoQuiniela(QuinielaOp.RES_PLENO15_10);
		   else if (signo_pleno15_visit_1_marcado)
			   p.setResultadoQuiniela(QuinielaOp.RES_PLENO15_11);
		   else if (signo_pleno15_visit_2_marcado)
			   p.setResultadoQuiniela(QuinielaOp.RES_PLENO15_12);
		   else if (signo_pleno15_visit_m_marcado)
			   p.setResultadoQuiniela(QuinielaOp.RES_PLENO15_1M);
	   }
	   else if (signo_pleno15_local_2_marcado)
	   {
		   if (signo_pleno15_visit_0_marcado)
			   p.setResultadoQuiniela(QuinielaOp.RES_PLENO15_20);
		   else if (signo_pleno15_visit_1_marcado)
			   p.setResultadoQuiniela(QuinielaOp.RES_PLENO15_21);
		   else if (signo_pleno15_visit_2_marcado)
			   p.setResultadoQuiniela(QuinielaOp.RES_PLENO15_22);
		   else if (signo_pleno15_visit_m_marcado)
			   p.setResultadoQuiniela(QuinielaOp.RES_PLENO15_2M);
	   }
	   else if (signo_pleno15_local_m_marcado)
	   {
		   if (signo_pleno15_visit_0_marcado)
			   p.setResultadoQuiniela(QuinielaOp.RES_PLENO15_M0);
		   else if (signo_pleno15_visit_1_marcado)
			   p.setResultadoQuiniela(QuinielaOp.RES_PLENO15_M1);
		   else if (signo_pleno15_visit_2_marcado)
			   p.setResultadoQuiniela(QuinielaOp.RES_PLENO15_M2);
		   else if (signo_pleno15_visit_m_marcado)
			   p.setResultadoQuiniela(QuinielaOp.RES_PLENO15_MM);
	   }
	   
	   Intent i = new Intent(getApplicationContext(), QuinielaGrabar.class);			
	   i.putExtra(Constantes.QUINIELA_GRABAR, quinielaHecha);
	   startActivityForResult(i, SUBACTIVITY_GRABAR_QUINIELA);  
    }
    
    /*
     * qué hacer cuando se retorna de alguna de las sub-actividades
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) 
    {
	    super.onActivityResult(requestCode, resultCode, data);
	    if (requestCode == SUBACTIVITY_GRABAR_QUINIELA  &&  resultCode == RESULT_OK)
	    {
	    	Mensajes.alerta(getApplicationContext(), "Quiniela grabada correctamente");
	    	Button btn = (Button)findViewById(R.id.boton_grabar_quiniela);
	    	btn.setVisibility(View.INVISIBLE);
	    	finish();
	    }
    }    
    
    private void mostrarPartido(String eq1, String eq2, int resIdRivales)
    {    	
    	((TextView) findViewById(resIdRivales)).setText(eq1 + " - "+ eq2);
    }

    private void mostrarPartidoPleno15(String eq1, String eq2)
    {    	
    	((TextView) findViewById(R.id.p15_equipolocal)).setText("    "+eq1);
    	((TextView) findViewById(R.id.p15_equipovisitante)).setText("    "+eq2);
    }
    
    int[] resIdRivales= new int[]{R.id.rivales1,R.id.rivales2,R.id.rivales3,R.id.rivales4,R.id.rivales5,R.id.rivales6,R.id.rivales7,R.id.rivales8,R.id.rivales9,R.id.rivales10,R.id.rivales11,R.id.rivales12,R.id.rivales13,R.id.rivales14};
    int[] resIdSigno1= new int[]{R.id.p1_1,R.id.p2_1,R.id.p3_1,R.id.p4_1,R.id.p5_1,R.id.p6_1,R.id.p7_1,R.id.p8_1,R.id.p9_1,R.id.p10_1,R.id.p11_1,R.id.p12_1,R.id.p13_1,R.id.p14_1};
    int[] resIdSignoX= new int[]{R.id.p1_x,R.id.p2_x,R.id.p3_x,R.id.p4_x,R.id.p5_x,R.id.p6_x,R.id.p7_x,R.id.p8_x,R.id.p9_x,R.id.p10_x,R.id.p11_x,R.id.p12_x,R.id.p13_x,R.id.p14_x};
    int[] resIdSigno2= new int[]{R.id.p1_2,R.id.p2_2,R.id.p3_2,R.id.p4_2,R.id.p5_2,R.id.p6_2,R.id.p7_2,R.id.p8_2,R.id.p9_2,R.id.p10_2,R.id.p11_2,R.id.p12_2,R.id.p13_2,R.id.p14_2};
    
    private void _clk1(int fila)
    {
    	if (signo1_marcadas[fila])
    		((ImageView) findViewById(resIdSigno1[fila-1])).setImageResource(R.drawable.quin_1);
    	else
    		((ImageView) findViewById(resIdSigno1[fila-1])).setImageResource(R.drawable.quin_sel);
    	
    	signo1_marcadas[fila]= !signo1_marcadas[fila];
    	ponerVisibleBotonDeGrabarSiTodoOK();
    }
    private void _clkX(int fila)
    {    	
    	if (signoX_marcadas[fila])
    		((ImageView) findViewById(resIdSignoX[fila-1])).setImageResource(R.drawable.quin_x);
    	else
    		((ImageView) findViewById(resIdSignoX[fila-1])).setImageResource(R.drawable.quin_sel);
    	
    	signoX_marcadas[fila]= !signoX_marcadas[fila];
    	ponerVisibleBotonDeGrabarSiTodoOK();
    }
    private void _clk2(int fila)
    {    	
    	if (signo2_marcadas[fila])
    		((ImageView) findViewById(resIdSigno2[fila-1])).setImageResource(R.drawable.quin_2);
    	else
    		((ImageView) findViewById(resIdSigno2[fila-1])).setImageResource(R.drawable.quin_sel);
    	
    	signo2_marcadas[fila]= !signo2_marcadas[fila];
    	ponerVisibleBotonDeGrabarSiTodoOK();
    }
    
	public void clk1_1(View v)	{	_clk1(1);	}
	public void clk1_x(View v)	{	_clkX(1);	}
	public void clk1_2(View v)	{	_clk2(1);	}
	public void clk2_1(View v)	{	_clk1(2);	}
	public void clk2_x(View v)	{	_clkX(2);	}
	public void clk2_2(View v)	{	_clk2(2);	}
	public void clk3_1(View v)	{	_clk1(3);	}
	public void clk3_x(View v)	{	_clkX(3);	}
	public void clk3_2(View v)	{	_clk2(3);	}
	public void clk4_1(View v)	{	_clk1(4);	}
	public void clk4_x(View v)	{	_clkX(4);	}
	public void clk4_2(View v)	{	_clk2(4);	}
	public void clk5_1(View v)	{	_clk1(5);	}
	public void clk5_x(View v)	{	_clkX(5);	}
	public void clk5_2(View v)	{	_clk2(5);	}
	public void clk6_1(View v)	{	_clk1(6);	}
	public void clk6_x(View v)	{	_clkX(6);	}
	public void clk6_2(View v)	{	_clk2(6);	}
	public void clk7_1(View v)	{	_clk1(7);	}
	public void clk7_x(View v)	{	_clkX(7);	}
	public void clk7_2(View v)	{	_clk2(7);	}
	public void clk8_1(View v)	{	_clk1(8);	}
	public void clk8_x(View v)	{	_clkX(8);	}
	public void clk8_2(View v)	{	_clk2(8);	}
	public void clk9_1(View v)	{	_clk1(9);	}
	public void clk9_x(View v)	{	_clkX(9);	}
	public void clk9_2(View v)	{	_clk2(9);	}
	public void clk10_1(View v)	{	_clk1(10);	}
	public void clk10_x(View v)	{	_clkX(10);	}
	public void clk10_2(View v)	{	_clk2(10);	}
	public void clk11_1(View v)	{	_clk1(11);	}
	public void clk11_x(View v)	{	_clkX(11);	}
	public void clk11_2(View v)	{	_clk2(11);	}
	public void clk12_1(View v)	{	_clk1(12);	}
	public void clk12_x(View v)	{	_clkX(12);	}
	public void clk12_2(View v)	{	_clk2(12);	}
	public void clk13_1(View v)	{	_clk1(13);	}
	public void clk13_x(View v)	{	_clkX(13);	}
	public void clk13_2(View v)	{	_clk2(13);	}
	public void clk14_1(View v)	{	_clk1(14);	}
	public void clk14_x(View v)	{	_clkX(14);	}
	public void clk14_2(View v)	{	_clk2(14);	}
	public void clk15_local_0(View v)	
	{	
		desmarcarTodosLosGolesLocal();
    	((ImageView) findViewById(R.id.p15_equipolocal_0)).setImageResource(signo_pleno15_local_0_marcado ? R.drawable.quin_goles0 : R.drawable.quin_sel);
    	signo_pleno15_local_0_marcado= !signo_pleno15_local_0_marcado;
    	ponerVisibleBotonDeGrabarSiTodoOK();
	}
	public void clk15_local_1(View v)	
	{	
		desmarcarTodosLosGolesLocal();
    	((ImageView) findViewById(R.id.p15_equipolocal_1)).setImageResource(signo_pleno15_local_1_marcado ? R.drawable.quin_goles1 : R.drawable.quin_sel);
    	signo_pleno15_local_1_marcado= !signo_pleno15_local_1_marcado;
    	ponerVisibleBotonDeGrabarSiTodoOK();
	}
	public void clk15_local_2(View v)	
	{	
		desmarcarTodosLosGolesLocal();
    	((ImageView) findViewById(R.id.p15_equipolocal_2)).setImageResource(signo_pleno15_local_2_marcado ? R.drawable.quin_goles2 : R.drawable.quin_sel);
    	signo_pleno15_local_2_marcado= !signo_pleno15_local_2_marcado;
    	ponerVisibleBotonDeGrabarSiTodoOK();
	}
	public void clk15_local_m(View v)	
	{	
		desmarcarTodosLosGolesLocal();
    	((ImageView) findViewById(R.id.p15_equipolocal_m)).setImageResource(signo_pleno15_local_m_marcado ? R.drawable.quin_golesm : R.drawable.quin_sel);
    	signo_pleno15_local_m_marcado= !signo_pleno15_local_m_marcado;
    	ponerVisibleBotonDeGrabarSiTodoOK();
	}
	public void clk15_visit_0(View v)	
	{	
		desmarcarTodosLosGolesVisitante();
    	((ImageView) findViewById(R.id.p15_equipovisitante_0)).setImageResource(signo_pleno15_visit_0_marcado ? R.drawable.quin_goles0 : R.drawable.quin_sel);
    	signo_pleno15_visit_0_marcado= !signo_pleno15_visit_0_marcado;
    	ponerVisibleBotonDeGrabarSiTodoOK();
	}
	public void clk15_visit_1(View v)	
	{	
		desmarcarTodosLosGolesVisitante();
    	((ImageView) findViewById(R.id.p15_equipovisitante_1)).setImageResource(signo_pleno15_visit_1_marcado ? R.drawable.quin_goles1 : R.drawable.quin_sel);
    	signo_pleno15_visit_1_marcado= !signo_pleno15_visit_1_marcado;
    	ponerVisibleBotonDeGrabarSiTodoOK();
	}
	public void clk15_visit_2(View v)	
	{	
		desmarcarTodosLosGolesVisitante();
    	((ImageView) findViewById(R.id.p15_equipovisitante_2)).setImageResource(signo_pleno15_visit_2_marcado ? R.drawable.quin_goles2 : R.drawable.quin_sel);
    	signo_pleno15_visit_2_marcado= !signo_pleno15_visit_2_marcado;
    	ponerVisibleBotonDeGrabarSiTodoOK();
	}
	public void clk15_visit_m(View v)	
	{	
		desmarcarTodosLosGolesVisitante();
    	((ImageView) findViewById(R.id.p15_equipovisitante_m)).setImageResource(signo_pleno15_visit_m_marcado ? R.drawable.quin_golesm : R.drawable.quin_sel);
    	signo_pleno15_visit_m_marcado= !signo_pleno15_visit_m_marcado;
    	ponerVisibleBotonDeGrabarSiTodoOK();
	}
	
	private void desmarcarTodosLosGolesLocal()
	{
		((ImageView) findViewById(R.id.p15_equipolocal_0)).setImageResource(R.drawable.quin_goles0);
		((ImageView) findViewById(R.id.p15_equipolocal_1)).setImageResource(R.drawable.quin_goles1);
		((ImageView) findViewById(R.id.p15_equipolocal_2)).setImageResource(R.drawable.quin_goles2);
		((ImageView) findViewById(R.id.p15_equipolocal_m)).setImageResource(R.drawable.quin_golesm);
	}
	private void desmarcarTodosLosGolesVisitante()
	{
		((ImageView) findViewById(R.id.p15_equipovisitante_0)).setImageResource(R.drawable.quin_goles0);
		((ImageView) findViewById(R.id.p15_equipovisitante_1)).setImageResource(R.drawable.quin_goles1);
		((ImageView) findViewById(R.id.p15_equipovisitante_2)).setImageResource(R.drawable.quin_goles2);
		((ImageView) findViewById(R.id.p15_equipovisitante_m)).setImageResource(R.drawable.quin_golesm);
	}
	
	
    int maxDobles[]= new int[]{14, 13, 11, 10, 8, 7, 5, 3, 2, 0};  //el índice de la matriz es el número de triples
	/*
	 * Tabla de posibles valores. Para leer la tabla:  X -> Y se lee "si hay X triples, el número máximo de dobles es Y".
	 * 0 -> 14		1 -> 13		2 -> 11		3 -> 10		4 -> 8
	 * 5 -> 7		6 ->  5		7 ->  3		8 ->  2		9 -> 0
	 */
	
	private void ponerVisibleBotonDeGrabarSiTodoOK()
	{
		LinearLayout botonera= (LinearLayout) findViewById(R.id.quiniela_botonera);
		botonera.setVisibility(View.INVISIBLE);
		
		int triples= 0;
		int dobles= 0;
			
		int marcadasEnEstaFila;
		for (int k=1; k<=14; k++)  // todos excepto el pleno al 15
		{
			marcadasEnEstaFila= 0;
			if (signo1_marcadas[k]) marcadasEnEstaFila++;
			if (signoX_marcadas[k]) marcadasEnEstaFila++;
			if (signo2_marcadas[k]) marcadasEnEstaFila++;
			
			if (marcadasEnEstaFila == 0)
				return;  // se queda invisible y me salgo sin más
			else if (marcadasEnEstaFila == 2)
				dobles++;
			else if (marcadasEnEstaFila == 3)
				triples++;
		}		
		
		if (triples >= maxDobles.length)
			Mensajes.alerta(getApplicationContext(), "Como máximo se permiten 9 triples.");
		else if (dobles > maxDobles[triples])
			Mensajes.alerta(getApplicationContext(), "Con "+triples+" triples no se permiten más de "+maxDobles[triples]+" dobles.");			
		else
		{
			if ( (signo_pleno15_local_0_marcado || signo_pleno15_local_1_marcado || signo_pleno15_local_2_marcado || signo_pleno15_local_m_marcado)
				&&
				 (signo_pleno15_visit_0_marcado || signo_pleno15_visit_1_marcado || signo_pleno15_visit_2_marcado || signo_pleno15_visit_m_marcado) 
			   )
		    {
			   botonera.setVisibility(View.VISIBLE);
		    }
		}
	}
	
}
