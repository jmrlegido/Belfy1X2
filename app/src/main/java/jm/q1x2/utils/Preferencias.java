package jm.q1x2.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public final class Preferencias 
{
	public static void grabarPreferenciaInt(Context ctx, String nombre, int valor)
	{
		SharedPreferences mySharedPreferences = ctx.getSharedPreferences(Constantes.PREFERENCIAS_NOMBRE, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = mySharedPreferences.edit();
		editor.putInt(nombre, valor);
		editor.commit();
	}

	public static void grabarPreferenciaString(Context ctx, String nombre, String valor)
	{
		SharedPreferences mySharedPreferences = ctx.getSharedPreferences(Constantes.PREFERENCIAS_NOMBRE, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = mySharedPreferences.edit();
		editor.putString(nombre, valor);
		editor.commit();
	}
	
	public static int getPreferenciaInt(Context ctx, String nombre, int valorDefecto) 
	{
		SharedPreferences pref = ctx.getSharedPreferences(Constantes.PREFERENCIAS_NOMBRE, Activity.MODE_PRIVATE);
		return pref.getInt(nombre, valorDefecto);
	}

	public static String getPreferenciaString(Context ctx, String nombre, String valorDefecto) 
	{
		SharedPreferences pref = ctx.getSharedPreferences(Constantes.PREFERENCIAS_NOMBRE, Activity.MODE_PRIVATE);
		return pref.getString(nombre, valorDefecto);
	}
	
	
	public static int getUsuarioId(Context ctx)
	{
		return getPreferenciaInt(ctx, Constantes.PREFERENCIAS_USUARIO_ID, -1);
	}

	public static int getTemporadaActual(Context ctx)
	{
		return getPreferenciaInt(ctx, Constantes.PREFERENCIAS_TEMPORADA_ACTUAL, -1);
	}

	public static int getClienteId(Context ctx)
	{
		int x= getPreferenciaInt(ctx, Constantes.PREFERENCIAS_CLIENTE_ID, -1);
		if (x == -1)
		{
			x= Utils.generarClienteId();
			grabarPreferenciaInt(ctx, Constantes.PREFERENCIAS_CLIENTE_ID, x);
		}
		return x;
	}

	
}
