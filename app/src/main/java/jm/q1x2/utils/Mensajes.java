package jm.q1x2.utils;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.widget.Toast;

import jm.q1x2.R;
import jm.q1x2.transobj.NotificacionDatos;

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
		int icon = R.drawable.icono_belfy1x2_32x32;
		Notification notification = new Notification.Builder(ctx)
				.setContentTitle("Title").setContentText("Text")
				.setSmallIcon(icon).getNotification();

		NotificationManager mNotificationManager =
				(NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);

		final int NOTIFICATION_ID = new Integer(datos.getIdQuiniela()).intValue();
		mNotificationManager.notify(NOTIFICATION_ID, notification);
	}
}
