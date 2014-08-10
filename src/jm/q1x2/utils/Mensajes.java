package jm.q1x2.utils;

import jm.q1x2.R;
import jm.q1x2.transobj.NotificacionDatos;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.widget.Toast;

public class Mensajes 
{
    public static void alerta(Context context, CharSequence texto)
    {
 	   	//Context context = getApplicationContext();
 	   	int duration = Toast.LENGTH_SHORT;
 	   	Toast toast = Toast.makeText(context, texto, duration);
 	   	toast.show();
    }    
    
    public static void dialogo(AlertDialog.Builder ad, String titulo, String mensaje)
    {
 	   	//AlertDialog.Builder ad = new AlertDialog.Builder(this);
 	   	ad.setTitle(titulo);
 	   	ad.setMessage(mensaje);
 	   	ad.show();
    }   
    
    public static void notificacion(Context ctx, NotificacionDatos datos)
    {
    	NotificationManager notifManager= (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
    	int icon = R.drawable.icono_belfy1x2_32x32;	          
 	    long when = System.currentTimeMillis(); 
 	    Notification notif= new Notification(icon, datos.getTextoTitulo(), when);
 	    
		PendingIntent pi = PendingIntent.getActivity(ctx, 0, datos.getIntent(), 0);		
		notif.setLatestEventInfo(ctx, datos.getTextoTitulo(), datos.getTextoContenido(), pi);
	    notif.flags |= Notification.FLAG_AUTO_CANCEL;
		
		final int NOTIFICATION_ID = new Integer(datos.getIdQuiniela()).intValue();
		notifManager.notify(NOTIFICATION_ID, notif); 	    
    }

}
