package jm.q1x2.activities;

import java.util.ArrayList;

import jm.q1x2.R;
import jm.q1x2.bbdd.Basedatos;
import jm.q1x2.bbdd.dao.UsuarioDao;
import jm.q1x2.transobj.Usuario;
import jm.q1x2.transobj.vista.ObjetoIconoTexto;
import jm.q1x2.utils.Constantes;
import jm.q1x2.utils.Mensajes;
import jm.q1x2.utils.Preferencias;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class Usuarios extends ListActivity 
{
	private int SUBACTIVITY_USUARIO_NUEVO= 1;
	private int SUBACTIVITY_USUARIO_MODIFICAR= 2;	

	private int MENU_MODIFICAR= 1;
	private int MENU_ELIMINAR= 2;
	
	private ArrayList<Integer> id_elemsLista;
	private Context contextoUsuarios;
    private ArrayList<ObjetoIconoTexto> m_usuarios= new ArrayList<ObjetoIconoTexto>();;
    private AdaptadorEquipo m_adaptador;
    private boolean bEsElUsuarioActual;
	
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lay_usuarios);
            
        contextoUsuarios= this;
        
        SQLiteDatabase con= Basedatos.getConexion(this, Basedatos.ESCRITURA);
        UsuarioDao usuDao= new UsuarioDao(con);
        actualizarListaUsuarios(usuDao);
        con.close();
        
	    ListView lista = (ListView) findViewById(android.R.id.list);
	    registerForContextMenu(lista);
	    boton_anadir();
	    
	    lista.setTextFilterEnabled(true);
	    lista.setOnItemClickListener(new OnItemClickListener() 
	    {
	      public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
	      {
			   Integer idUsu= id_elemsLista.get(position);
			   ObjetoIconoTexto obj= m_usuarios.get(position);
	    	   Mensajes.alerta(getApplicationContext(), "Usuario seleccionado: " + obj.getTexto());
	    	   Preferencias.grabarPreferenciaInt(getApplicationContext(), Constantes.PREFERENCIAS_USUARIO_ID, idUsu.intValue());
	    	   Preferencias.grabarPreferenciaString(getApplicationContext(), Constantes.PREFERENCIAS_USUARIO_NOMBRE, obj.getTexto());
	    	   setTitle(obj.getTexto());  // esto hará que Jm1x2Principal.onChildTitleChanged() ponga este usuario en el título de la aplicación (a la derecha)
	    	   actualizarListaUsuarios();
	      }
	    });	    
    }    
    
    
	private void actualizarListaUsuarios()
	{
        SQLiteDatabase con= Basedatos.getConexion(this, Basedatos.ESCRITURA);
        UsuarioDao usuDao= new UsuarioDao(con);
        actualizarListaUsuarios(usuDao);
        con.close();
	}
	private void actualizarListaUsuarios(UsuarioDao usuDao)
	{
        ArrayList<Usuario> usus= usuDao.getUsuarios();
		id_elemsLista= new ArrayList<Integer>();             
        m_usuarios= new ArrayList<ObjetoIconoTexto>();
        for (Usuario usu : usus) 
        {
			id_elemsLista.add(usu.getId());
        	m_usuarios.add(new ObjetoIconoTexto(null, usu.getNombre()));
        }
        
        this.m_adaptador = new AdaptadorEquipo(this, R.layout.cada_usuario, m_usuarios);
        setListAdapter(this.m_adaptador);        
	}
			
	private class AdaptadorEquipo extends ArrayAdapter<ObjetoIconoTexto> 
	{
        private ArrayList<ObjetoIconoTexto> items;
        private String usuarioActual= null;

        public AdaptadorEquipo(Context context, int textViewResourceId, ArrayList<ObjetoIconoTexto> items) 
        {
            super(context, textViewResourceId, items);
            this.items = items;
            usuarioActual= Preferencias.getPreferenciaString(getApplicationContext(), Constantes.PREFERENCIAS_USUARIO_NOMBRE, "¿?");
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) 
        {
            View v = convertView;
            if (v == null) 
            {
                LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.cada_usuario, null);
            }
            ObjetoIconoTexto o = items.get(position);
            if (o != null) 
            {
                TextView tt = (TextView) v.findViewById(R.id.usuario_nombre);
                if (tt != null)
                    tt.setText(o.getTexto()+ (o.getTexto().equals(usuarioActual)?" (usuario actual)":""));
            }
            return v;
        }
	}		
	
    private void boton_anadir()
    {
        Button btn_aceptar = (Button)findViewById(R.id.boton_anadir_usuario);
        btn_aceptar.setOnClickListener(new Button.OnClickListener() 
        {
        	public void onClick(View v) 
        	{
    	 		   Intent i = new Intent(contextoUsuarios, UsuarioNuevo.class);			
    			   startActivityForResult(i, SUBACTIVITY_USUARIO_NUEVO);		   
        	}
        });        
    }
    

    
    
    /*
     * menú contextual de los elementos de la lista
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) 
    {
      super.onCreateContextMenu(menu, v, menuInfo);
      
      final AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
      final int[] itemsId = {MENU_MODIFICAR, MENU_ELIMINAR};
      final CharSequence[] itemsDesc = {"Modificar", "Eliminar"};
      AlertDialog.Builder builder = new AlertDialog.Builder(this);
  	  ObjetoIconoTexto selec= m_usuarios.get(new Long(info.id).intValue());
      builder.setTitle(selec.getTexto());
      builder.setItems(itemsDesc, new DialogInterface.OnClickListener() 
      {
          public void onClick(DialogInterface dialog, int item) 
          {
   		      int idUsuarioActual= Preferencias.getPreferenciaInt(getApplicationContext(), Constantes.PREFERENCIAS_USUARIO_ID, -1);
          	  Integer idUsuario= id_elemsLista.get(new Long(info.id).intValue());
          	  bEsElUsuarioActual= (idUsuario == idUsuarioActual);
        	  
          	  if (itemsId[item] == MENU_MODIFICAR)
        	  {
	       		   Intent i = new Intent(getApplicationContext(), UsuarioModificar.class);
	       		   Integer id= id_elemsLista.get(new Long(info.id).intValue());
	       		   i.putExtra(Constantes.ID_USUARIO_MODIF_BORRAR, id.intValue());
	       		   i.putExtra(Constantes.ES_USUARIO_ACTUAL, bEsElUsuarioActual);
	       		   startActivityForResult(i, SUBACTIVITY_USUARIO_MODIFICAR);
        	  }
        	  else if (itemsId[item] == MENU_ELIMINAR)
        	  {        		           		  
	           	   if (!bEsElUsuarioActual)
	           	   {
		       		   SQLiteDatabase con= Basedatos.getConexion(getApplicationContext(), Basedatos.ESCRITURA);
		       		   UsuarioDao usuDao= new UsuarioDao(con);
		       		   usuDao.borrar(idUsuario.intValue());
		       		   actualizarListaUsuarios(usuDao);
		       		   con.close();	           	  
		       		   Mensajes.alerta(getApplicationContext(), "Usuario eliminado correctamente");
	           	   }
	           	   else
	           		   Mensajes.alerta(getApplicationContext(), "No se puede eliminar el usuario actual. Seleccione otro como usuario actual antes de eliminarlo.");
        	  }
          }
      });
      AlertDialog alert = builder.create();
      alert.show();
    }

    /*
     * qué hacer cuando se retorna de alguna de las sub-actividades
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) 
    {
	    super.onActivityResult(requestCode, resultCode, data);
	    if (requestCode == SUBACTIVITY_USUARIO_NUEVO  ||  requestCode==SUBACTIVITY_USUARIO_MODIFICAR)
	    	actualizarListaUsuarios();
	    if (requestCode == SUBACTIVITY_USUARIO_MODIFICAR  && bEsElUsuarioActual)
	 	    setTitle(Preferencias.getPreferenciaString(getApplicationContext(), Constantes.PREFERENCIAS_USUARIO_NOMBRE, "-"));
    }    
}
