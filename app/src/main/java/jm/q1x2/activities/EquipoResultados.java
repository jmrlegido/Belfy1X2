package jm.q1x2.activities;

import android.app.ListActivity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import jm.q1x2.R;
import jm.q1x2.bbdd.Basedatos;
import jm.q1x2.bbdd.dao.EquipoDao;
import jm.q1x2.transobj.Partido;
import jm.q1x2.transobj.vista.ResultadoPartido;
import jm.q1x2.utils.Constantes;
import jm.q1x2.utils.Mensajes;
import jm.q1x2.utils.Preferencias;

public class EquipoResultados extends ListActivity 
{
	LinearLayout layoutPrincipal;
    private ArrayList<ResultadoPartido> m_partidos = null;
    private AdaptadorResultados m_adaptador;	
	
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lay_equipo_resultados);
        m_partidos = new ArrayList<ResultadoPartido>();

        Bundle extras = getIntent().getExtras();
        if (extras == null)
        {
            Mensajes.alerta(getApplicationContext(), "No es posible ver los resultados en estos momentos ...");
            finish();
        }
        else
        {
            String idEquipo = extras.getString(Constantes.EQUIPO_SELEC_ID);
            String descEquipo = extras.getString(Constantes.EQUIPO_SELEC_NOMBRE);

            SQLiteDatabase con = Basedatos.getConexion(this, Basedatos.ESCRITURA);
            EquipoDao eqDao = new EquipoDao(con);
            ArrayList<Partido> partidos = eqDao.getPartidos(Preferencias.getTemporadaActual(getApplicationContext()), idEquipo);
            Basedatos.cerrarConexion(con);  //[jm] con.close();

            ImageView img = (ImageView) findViewById(R.id.icono_equipo);
            if (img != null)
                img.setImageDrawable(getResources().getDrawable(getResources().getIdentifier("esc_" + idEquipo, "drawable", getPackageName())));
            TextView tt = (TextView) findViewById(R.id.nombre_equipo);
            if (tt != null)
                tt.setText(descEquipo);


            m_partidos = new ArrayList<ResultadoPartido>();
            for (Partido par : partidos)
                m_partidos.add(new ResultadoPartido(par));

            this.m_adaptador = new AdaptadorResultados(this, R.layout.cada_resultado, m_partidos);
            setListAdapter(this.m_adaptador);
        }
	}
    
	private class AdaptadorResultados extends ArrayAdapter<ResultadoPartido> 
	{
        private ArrayList<ResultadoPartido> items;

        public AdaptadorResultados(Context context, int textViewResourceId, ArrayList<ResultadoPartido> items) 
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
                    v = vi.inflate(R.layout.cada_resultado, null);
                }
                ResultadoPartido o = items.get(position);
                if (o != null) 
                {
                    TextView jor = (TextView) v.findViewById(R.id.jornada);
                    if (jor != null)
                        jor.setText("Jornada "+o.getJornada());
                    TextView tt = (TextView) v.findViewById(R.id.resultado);
                    if (tt != null)
                        tt.setText(o.getEqLocal() + " " + o.getGolesLocal() + " - " + o.getGolesVisit() + " "+ o.getEqVisit());
                }
                return v;
        }
	}	
		
}
