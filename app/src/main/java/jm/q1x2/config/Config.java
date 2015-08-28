package jm.q1x2.config;

public class Config
{
    private final static String temporada_actual= "1516";
    private final static String temporada_letrero= "2015/16";
    private final static String _base= "http://jmrlegido.es/proyectos/belfy1x2";
    private final static String url_notificaciones= _base + "/jm1x2_notifs.php";
    private final static String url_carpeta_jornadas= _base + "/jornadas";
    private final static String url_carpeta_quinielas= _base + "/quinielas";
    private final static String url_carpeta_quinielas_resultados= _base + "/quinielas";
    private final static String url_carpeta_migraciones_bbdd= _base + "/migraciones_bbdd";
    private final static String url_carpeta_migraciones_temporadas= _base + "/migraciones_temporadas";
    private final static String url_carpeta_cambios_datos_generales= _base + "/cambios_datos_generales";
    private final static String url_notificar_descarga= _base + "/descargado.php";
    private final static String url_notificar_premio= _base + "/premiado.php";
    private final static String url_lector_ficheros= _base + "/lee_fichero.php";
    private final static String url_noticias= _base + "/jm1x2_noticias.php?ult={ultima}&versioncode={versioncode}";
    private final static String url_info_error= _base + "/mandarInfoError.php?infoerror={infoerror}&versioncode={versioncode}";
    private final static String carpeta_jornadas= "jornadas";
    private final static String carpeta_suspendidos_corregidos= "suspendidos_corregidos";
    private final static String carpeta_quinielas= "quinielas";
    private final static String carpeta_quinielas_resultados= "quinielas";
    private final static boolean desarrollo= false;

    
    public static String getURLNotificaciones()
    {
        return url_notificaciones;
    }

    public static String getURLCarpetaJornadas()
    {
        return url_carpeta_jornadas;
    }

    public static String getURLCarpetaQuinielas()
    {
        return url_carpeta_quinielas;
    }

    public static String getURLCarpetaQuinielasResultados()
    {
        return url_carpeta_quinielas_resultados;
    }

    public static String getURLCarpetaMigracionesBBDD()
    {
        return url_carpeta_migraciones_bbdd;
    }

    public static String getURLCarpetaMigracionesTemporadas()
    {
        return url_carpeta_migraciones_temporadas;
    }

    public static String getURLCarpetaCambiosDatosGenerales()
    {
        return url_carpeta_cambios_datos_generales;
    }

    public static String getURLNotificarDescarga()
    {
        return url_notificar_descarga;
    }

    /*
     * @since v6.1
     */
    public static String getCarpetaJornadas()
    {
        return carpeta_jornadas;
    }

    /*
     * @since v6.1
     */
    public static String getCarpetaJornadasPartidosSuspendidos_o_Corregidos()
    {
        return carpeta_suspendidos_corregidos;
    }

    /*
     * @since v6.1
     */
    public static String getCarpetaQuinielas()
    {
        return carpeta_quinielas;
    }

    /*
     * @since v6.1
     */
    public static String getCarpetaQuinielasResultados()
    {
        return carpeta_quinielas_resultados;
    }

    /*
     * @since v6.1
     */
    public static String getURLLectorFicheros()
    {
        return url_lector_ficheros;
    }

    /*
     * @since v2.0
     */
    public static String getURLNoticias()
    {
        return url_noticias;
    }

    /*
     * @since v4.4
     */
    public static String getURLInfoError()
    {
        return url_info_error;
    }

    /*
     * @since 1.6
     */
    public static String getURLNotificarPremio()
    {
        return url_notificar_premio;
    }

    /*
     * @since 4.6
     */
    public static String getTemporadaActual()
    {
        return temporada_actual;
    }

    /*
     * @since 4.6
     */
    public static String getTemporadaLetrero()
    {
        return temporada_letrero;
    }

    public static boolean esDesarrollo()
    {
        return desarrollo;
    }

}
