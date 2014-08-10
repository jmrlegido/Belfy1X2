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

    boolean[] signo1_marcadas= new boolean[16];  // no tendré en cuenta el índice 0
    boolean[] signoX_marcadas= new boolean[16];  // no tendré en cuenta el índice 0
    boolean[] signo2_marcadas= new boolean[16];  // no tendré en cuenta el índice 0
	
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        for (int k=1; k<=15; k++)
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
	     		for (int idx= 0; idx < 15; idx++)
	     		{
		         	par= partidos.get(idx);
		         	quinielaHecha.annadirPartido(par);        	
		         	filasQuiniela.add(new PartidoQuiniela(daoEq.getNombreEquipo(par.getIdEquipoLocal()), daoEq.getNombreEquipo(par.getIdEquipoVisit()), resIdRivales[idx]));
	     		}
	         	
         	}
     		con.close();
     		
          	for(PartidoQuiniela fila: filasQuiniela)
          		mostrarPartido(fila.getEq1(), fila.getEq2(), fila.getResIdRivales());
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
	   for (int k=0; k<15; k++)
	   {
		   Partido p= partidos.get(k);
		   
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
    
    int[] resIdRivales= new int[]{R.id.rivales1,R.id.rivales2,R.id.rivales3,R.id.rivales4,R.id.rivales5,R.id.rivales6,R.id.rivales7,R.id.rivales8,R.id.rivales9,R.id.rivales10,R.id.rivales11,R.id.rivales12,R.id.rivales13,R.id.rivales14,R.id.rivales15};
    int[] resIdSigno1= new int[]{R.id.p1_1,R.id.p2_1,R.id.p3_1,R.id.p4_1,R.id.p5_1,R.id.p6_1,R.id.p7_1,R.id.p8_1,R.id.p9_1,R.id.p10_1,R.id.p11_1,R.id.p12_1,R.id.p13_1,R.id.p14_1,R.id.p15_1};
    int[] resIdSignoX= new int[]{R.id.p1_x,R.id.p2_x,R.id.p3_x,R.id.p4_x,R.id.p5_x,R.id.p6_x,R.id.p7_x,R.id.p8_x,R.id.p9_x,R.id.p10_x,R.id.p11_x,R.id.p12_x,R.id.p13_x,R.id.p14_x,R.id.p15_x};
    int[] resIdSigno2= new int[]{R.id.p1_2,R.id.p2_2,R.id.p3_2,R.id.p4_2,R.id.p5_2,R.id.p6_2,R.id.p7_2,R.id.p8_2,R.id.p9_2,R.id.p10_2,R.id.p11_2,R.id.p12_2,R.id.p13_2,R.id.p14_2,R.id.p15_2};
    
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
	public void clk15_1(View v)	{	_clk1(15);	}
	public void clk15_x(View v)	{	_clkX(15);	}
	public void clk15_2(View v)	{	_clk2(15);	}

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

		/*
		 * pleno al 15
		 */
		int marcadasEnPleno15= 0;
		if (signo1_marcadas[15]) marcadasEnPleno15++;
		if (signoX_marcadas[15]) marcadasEnPleno15++;
		if (signo2_marcadas[15]) marcadasEnPleno15++;		
		if (marcadasEnPleno15 == 0)
			return;  // se queda invisible y me salgo sin más
		else if (marcadasEnPleno15 > 1)
		{
			Mensajes.alerta(getApplicationContext(), "El pleno al 15 no puede contener dobles ni triples.");
			return;
		}
		
		
		if (triples >= maxDobles.length)
			Mensajes.alerta(getApplicationContext(), "Como máximo se permiten 9 triples.");
		else if (dobles > maxDobles[triples])
			Mensajes.alerta(getApplicationContext(), "Con "+triples+" triples no se permiten más de "+maxDobles[triples]+" dobles.");			
		else
			botonera.setVisibility(View.VISIBLE);
	}
	
}
