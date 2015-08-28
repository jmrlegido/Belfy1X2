package jm.q1x2.transobj;

import android.content.Intent;

/*
 * Esta clase contendrá los datos que se enviarán en las notificaciones (barra superior de android).
 * Serán los datos que le llegarán a la aplicación que se inicie cuando se pinche en la notificación.
 */
public class NotificacionDatos 
{
	private int idQuiniela;
	private int aciertos;
	private int fecha;
	private String textoTitulo;
	private String textoContenido;
	private Intent intent;
	
	
	public int getIdQuiniela() {
		return idQuiniela;
	}
	public void setIdQuiniela(int idQuiniela) {
		this.idQuiniela = idQuiniela;
	}
	public int getAciertos() {
		return aciertos;
	}
	public void setAciertos(int aciertos) {
		this.aciertos = aciertos;
	}
	public int getFecha() {
		return fecha;
	}
	public void setFecha(int fecha) {
		this.fecha = fecha;
	}
	public String getTextoTitulo() {
		return textoTitulo;
	}
	public void setTextoTitulo(String textoTitulo) {
		this.textoTitulo = textoTitulo;
	}
	public String getTextoContenido() {
		return textoContenido;
	}
	public void setTextoContenido(String textoContenido) {
		this.textoContenido = textoContenido;
	}
	public Intent getIntent() {
		return intent;
	}
	public void setIntent(Intent intent) {
		this.intent = intent;
	}
	
	
}
