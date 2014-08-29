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
import jm.q1x2.utils.Utils;
import android.app.Activity;
import android.app.ProgressDialog;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class QuinielaVerPronostico extends Activity 
{
    private ProgressDialog dialog;
    int idQuin;
	    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
    	idQuin= extras.getInt(Constantes.ID_QUINIELA);    	
    	presentarQuiniela();    	
    }    

    protected void accionesPresentacion()
    {
        setContentView(R.layout.lay_quiniela);
        LinearLayout bot= (LinearLayout) findViewById(R.id.quiniela_botonera);
        bot.setVisibility(View.INVISIBLE);
    }
    
    private void presentarQuiniela()
    {     	
	    dialog = new ProgressDialog(this);
	    dialog.setMessage("Recuperando pronósticos ...");
	    dialog.setTitle("Por favor, espere");
	    dialog.setCancelable(false);
	    new PresentarQuinielaSegundoPlano().execute("");
    }
       
    private void mostrarResultado(PartidoQuiniela partido, boolean bQuinielaCorregida)
    {    	
    	String eq1= partido.getEq1();
    	String eq2= partido.getEq2();
    	int resultado1x2= partido.getResultado1x2();
    	int resIdRivales= partido.getResIdRivales();
    	int resId1= partido.getResId1();
    	int resIdx= partido.getResIdx();
    	int resId2= partido.getResId2();
    	boolean bAcertada= partido.estaAcertado();
    	boolean bFinalizado= partido.esFinalizado();
    	
    	((TextView) findViewById(resIdRivales)).setText(eq1 + " - "+ eq2);
    	boolean marco1= resultado1x2==QuinielaOp.RES_1 || resultado1x2==QuinielaOp.RES_1X || resultado1x2==QuinielaOp.RES_1X2 || resultado1x2==QuinielaOp.RES_12;
    	boolean marcoX= resultado1x2==QuinielaOp.RES_X || resultado1x2==QuinielaOp.RES_1X || resultado1x2==QuinielaOp.RES_X2 || resultado1x2==QuinielaOp.RES_1X2;
    	boolean marco2= resultado1x2==QuinielaOp.RES_2 || resultado1x2==QuinielaOp.RES_X2 || resultado1x2==QuinielaOp.RES_1X2 || resultado1x2==QuinielaOp.RES_12;
    	((ImageView) findViewById(resId1)).setImageResource(marco1 ? R.drawable.quin_sel : R.drawable.quin_1);
    	((ImageView) findViewById(resIdx)).setImageResource(marcoX ? R.drawable.quin_sel : R.drawable.quin_x);
    	((ImageView) findViewById(resId2)).setImageResource(marco2 ? R.drawable.quin_sel : R.drawable.quin_2);
    	
    	if (bQuinielaCorregida  &&  bFinalizado)
			((TextView) findViewById(resIdRivales)).setTextColor(bAcertada?Color.GREEN:Color.RED);
    }
    private void mostrarResultadoPleno15(PartidoQuiniela partido, boolean bQuinielaCorregida)
    {    	
    	String eq1= partido.getEq1();
    	String eq2= partido.getEq2();
    	int res1x2= partido.getResultado1x2();
    	boolean bAcertada= partido.estaAcertado();
    	boolean bFinalizado= partido.esFinalizado();
    	
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
    	
    	if (bQuinielaCorregida  &&  bFinalizado)
    	{
			((TextView) findViewById(R.id.p15_equipolocal)).setTextColor(bAcertada?Color.GREEN:Color.RED);
			((TextView) findViewById(R.id.p15_equipovisitante)).setTextColor(bAcertada?Color.GREEN:Color.RED);
    	}
    }
    
    private class PresentarQuinielaSegundoPlano extends AsyncTask<String, Float, Integer>
    {
		 ArrayList<PartidoQuiniela> filasQuiniela= null;
		 boolean bQuinielaCorregida= false;
    	
	   	 protected void onPreExecute() 
	   	 {
	   		 dialog.setProgress(0);
	   		 dialog.setMax(100);
             dialog.show(); //Mostramos el diálogo antes de comenzar
         }

         protected Integer doInBackground(String... urls) 
         {                    	
      		QuinielaOp quinOp= new QuinielaOp();

      		SQLiteDatabase con= Basedatos.getConexion(getApplicationContext(), Basedatos.LECTURA);
     		EquipoDao equipDao= new EquipoDao(con);

     		Quiniela quinHecha= quinOp.getQuinielaHecha(idQuin, con);
     		Quiniela quinCorregida= null;
     		bQuinielaCorregida= quinHecha.haSidoCalculadoElNumeroDeAciertos();
     		if (bQuinielaCorregida)
     			quinCorregida= QuinielaOp.getQuinielaCorregidaDeJornada(quinHecha.getTemporada(), quinHecha.getJornada(), con);
     		else
     		{
     			// quizá está corregida parcialmente
     			quinCorregida= QuinielaOp.getQuinielaParcialmenteCorregida(getApplicationContext(), quinHecha.getTemporada(), quinHecha.getJornada());
     			if (quinCorregida != null)
     				bQuinielaCorregida= true;
     		}
     		
     		ArrayList<Partido> partidos= quinHecha.getPartidos();
     		
     		filasQuiniela= new ArrayList<PartidoQuiniela>();

     		int[] rivales= new int[]{R.id.rivales1, R.id.rivales2, R.id.rivales3, R.id.rivales4, R.id.rivales5, R.id.rivales6, R.id.rivales7, R.id.rivales8, R.id.rivales9, R.id.rivales10, R.id.rivales11, R.id.rivales12, R.id.rivales13, R.id.rivales14};
     		int[] partidos_1= new int[]{R.id.p1_1, R.id.p2_1, R.id.p3_1, R.id.p4_1, R.id.p5_1, R.id.p6_1, R.id.p7_1, R.id.p8_1, R.id.p9_1, R.id.p10_1, R.id.p11_1, R.id.p12_1, R.id.p13_1, R.id.p14_1};
     		int[] partidos_x= new int[]{R.id.p1_x, R.id.p2_x, R.id.p3_x, R.id.p4_x, R.id.p5_x, R.id.p6_x, R.id.p7_x, R.id.p8_x, R.id.p9_x, R.id.p10_x, R.id.p11_x, R.id.p12_x, R.id.p13_x, R.id.p14_x};
     		int[] partidos_2= new int[]{R.id.p1_2, R.id.p2_2, R.id.p3_2, R.id.p4_2, R.id.p5_2, R.id.p6_2, R.id.p7_2, R.id.p8_2, R.id.p9_2, R.id.p10_2, R.id.p11_2, R.id.p12_2, R.id.p13_2, R.id.p14_2};
     		
     		for (int k= 0; k<15; k++)
     		{
	         	Partido par= partidos.get(k);
	         	int iRes= par.getResultadoQuiniela();
	         	PartidoQuiniela parQuin= null;
	         	if (k<14)  // todos excepto pleno al 15
	         		parQuin= new PartidoQuiniela(equipDao.getNombreEquipo(par.getIdEquipoLocal()), equipDao.getNombreEquipo(par.getIdEquipoVisit()), iRes, rivales[k], partidos_1[k], partidos_x[k], partidos_2[k]);
	         	else
	         	{
	         		// pleno al 15
	         		parQuin= new PartidoQuiniela();
	         		parQuin.setEq1(equipDao.getNombreEquipo(par.getIdEquipoLocal()));
	         		parQuin.setEq2(equipDao.getNombreEquipo(par.getIdEquipoVisit()));
	         		parQuin.setResultado1x2(iRes);
	         	}
	         	
	         	if (bQuinielaCorregida)
	         	{
	         		boolean bFinalizado= quinCorregida.getPartidos().get(k).getResultadoQuiniela() != QuinielaOp.RES_NO_DISPONIBLE;
	         		parQuin.ponerFinalizado(bFinalizado);
	         		if (bFinalizado)
	         			parQuin.ponerAcertado(Utils.resultadoAcertado(quinHecha.getPartidos().get(k).getResultadoQuiniela() , quinCorregida.getPartidos().get(k).getResultadoQuiniela()));
	         	}
	         	filasQuiniela.add(parQuin);
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
            accionesPresentacion();

          	if (filasQuiniela == null)
          	{
         		Mensajes.alerta(getApplicationContext(), "No se ha posido cargar la quiniela.");
         		finish();          		
          	}
          	else
          	{
          		PartidoQuiniela fila= null;
	          	for(int k=0; k<filasQuiniela.size()-1; k++)   // todos los partidos excepto pleno al 15
	          	{
	          		fila= filasQuiniela.get(k); 
	          		mostrarResultado(fila, bQuinielaCorregida);
	          	}

	          	// pleno al 15
          		fila= filasQuiniela.get(filasQuiniela.size()-1); 
          		mostrarResultadoPleno15(fila, bQuinielaCorregida);

          	}
          	if(dialog!=null && dialog.isShowing())
          		dialog.dismiss();
         }
   }
    
}
