package jm.q1x2.activities;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.math.BigDecimal;

import jm.q1x2.R;
import jm.q1x2.bbdd.Basedatos;
import jm.q1x2.bbdd.dao.EquipoDao;
import jm.q1x2.transobj.EstadisticasEquipo;
import jm.q1x2.utils.Constantes;
import jm.q1x2.utils.Mensajes;
import jm.q1x2.utils.Preferencias;

public class EquipoEstadisticas extends Activity 
{
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lay_equipo_estadisticas);

        Bundle extras = getIntent().getExtras();
        if (extras == null)
        {
            Mensajes.alerta(getApplicationContext(), "No es posible ver las estadísticas en estos momentos ...");
            finish();
        }
        else
        {
            String idEquipo = extras.getString(Constantes.EQUIPO_SELEC_ID);
            String descEquipo = extras.getString(Constantes.EQUIPO_SELEC_NOMBRE);

            ImageView img = (ImageView) findViewById(R.id.icono_equipo);
            if (img != null)
                img.setImageDrawable(getResources().getDrawable(getResources().getIdentifier("esc_" + idEquipo, "drawable", getPackageName())));
            TextView tt = (TextView) findViewById(R.id.nombre_equipo);
            if (tt != null)
                tt.setText(descEquipo);

            presentarEstadisticas(idEquipo);
        }
	}
    
    private void presentarEstadisticas(String idEquipo)
    {
        SQLiteDatabase con= Basedatos.getConexion(this, Basedatos.ESCRITURA);
        EquipoDao eqDao= new EquipoDao(con);       
        EstadisticasEquipo e= eqDao.getEstadisticas(Preferencias.getTemporadaActual(getApplicationContext()), idEquipo);
        Basedatos.cerrarConexion(con);  //[jm] con.close();
    	
        ponerValor(R.id.partidos_jugados, e.getPartidosJugados());
        ponerValor(R.id.partidos_ganados_empatados_perdidos, ""+e.getPartidosGanados() + " / " + e.getPartidosEmpatados()+" / "+e.getPartidosPerdidos());
        ponerValor(R.id.partidos_ganados_empatados_perdidos_local, ""+e.getPartidosLocalGanados() + " / " + e.getPartidosLocalEmpatados()+" / "+e.getPartidosLocalPerdidos());
        ponerValor(R.id.partidos_ganados_empatados_perdidos_visitante, ""+e.getPartidosVisitanteGanados() + " / " + e.getPartidosVisitanteEmpatados()+" / "+e.getPartidosVisitantePerdidos());
        ponerValor(R.id.ptos_por_partido, e.getPtosPorPartido());
        ponerValor(R.id.ptos_por_partido_local_visit, ""+e.getPtosPorPartidoLocal()+" / "+e.getPtosPorPartidoVisit());
        ponerValor(R.id.goles_por_partido, e.getGolesPorPartido());
        ponerValor(R.id.goles_por_partido_local_visit, ""+e.getGolesPorPartidoLocal()+" / "+e.getGolesPorPartidoVisit());
    }
    
    private void ponerValor(int resId, int valor)
    {
        ponerValor(resId, ""+valor);
    }
    private void ponerValor(int resId, BigDecimal valor)
    {
        ponerValor(resId, valor.toString());
    }

    private void ponerValor(int resId, String valor)
    {
        TextView t = (TextView) findViewById(resId);
        t.setText(valor);
    }
}
