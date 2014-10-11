package jm.q1x2.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config 
{
	private static Properties propConfig= null;

    public static String getURLNotificaciones()
    {
    	return getDato("url_notificaciones");
    }
    
    public static String getURLCarpetaJornadas()
    {
    	return getDato("url_carpeta_jornadas");
    }

    public static String getURLCarpetaQuinielas()
    {
    	return getDato("url_carpeta_quinielas");
    }

    public static String getURLCarpetaQuinielasResultados()
    {
    	return getDato("url_carpeta_quinielas_resultados");
    }

    public static String getURLCarpetaMigracionesBBDD()
    {
    	return getDato("url_carpeta_migraciones_bbdd");
    }

    public static String getURLCarpetaMigracionesTemporadas()
    {
    	return getDato("url_carpeta_migraciones_temporadas");
    }

    public static String getURLCarpetaCambiosDatosGenerales()
    {
    	return getDato("url_carpeta_cambios_datos_generales");
    }
    
    public static String getURLNotificarDescarga()
    {
    	return getDato("url_notificar_descarga");
    }
   
    /*
     * @since v6.1
     */
	public static String getCarpetaJornadas()
	{
		return getDato("carpeta_jornadas");
	}
	
    /*
     * @since v6.1
     */
	public static String getCarpetaJornadasPartidosSuspendidos_o_Corregidos()
	{
		return getDato("carpeta_suspendidos_corregidos");
	}

    /*
     * @since v6.1
     */
	public static String getCarpetaQuinielas()
	{
		return getDato("carpeta_quinielas");
	}

    /*
     * @since v6.1
     */
	public static String getCarpetaQuinielasResultados()
	{
		return getDato("carpeta_quinielas_resultados");
	}

    /*
     * @since v6.1
     */
	public static String getURLLectorFicheros()
	{
		return getDato("url_lector_ficheros");
	}
	
    /*
     * @since v2.0
     */
    public static String getURLNoticias()
    {
    	return getDato("url_noticias");
    }

    /*
     * @since v4.4
     */
    public static String getURLInfoError()
    {
    	return getDato("url_info_error");
    }
    
    /*
     * @since 1.6
     */
    public static String getURLNotificarPremio()
    {
    	return getDato("url_notificar_premio");
    }

    /*
     * @since 4.6
     */
    public static String getTemporadaActual()
    {
    	return getDato("temporada_actual");
    }
    
    /*
     * @since 4.6
     */
    public static String getTemporadaLetrero()
    {
    	return getDato("temporada_letrero");
    }
    
    public static boolean esDesarrollo()
    {
    	return getDato("desarrollo").equals("1");
    }
    
    
    /*
     * Zona de métodos privados
     */
    private static String getDato(String key)     
    {
    	if (propConfig == null)
    		cargarConfig();
    	return propConfig.getProperty(key);
    }
	
	private static void cargarConfig()
	{
		try 
		{			
	 	     InputStream is = Config.class.getResourceAsStream("config.properties");
			 propConfig= new Properties();
			 propConfig.load(is);
		     is.close();
		}
		catch (IOException e) 
		{ 
			 propConfig= null;
		}
	}	
}
