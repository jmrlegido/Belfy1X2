package jm.q1x2.utils;


public class Constantes 
{
	public static String TITULO_APLICACION= "Belfy1X2";
	
	public static String LOG_TAG= "jm1x2";

	public static String ID_USUARIO_MODIF_BORRAR= "usu_id";
	public static String ID_QUINIELA= "quin_id";
	public static String EQUIPO_SELEC_ID= "equipo_id";
	public static String EQUIPO_SELEC_NOMBRE= "equipo_desc";
	public static String EQUIPO_SELEC_CALIDAD_INTRINSECA= "equipo_calidadintrin";
	public static String QUINIELA_GRABAR= "quiniela_grabar";
	public static String QUINIELA_PATRON_GRABAR= "quiniela_patron_grabar";
	public static String ES_USUARIO_ACTUAL= "es_usuario_actual";
	public static int 	 ACIERTOS_UMBRAL_PREMIO= 10;  // mínimo número de aciertos para que la quiniela esté premiada
	
	public static String PESTANA_USUARIOS_ID= "idUsuarios";
	public static String PESTANA_QUINIELAS_ID= "idQuinielas";
	public static String PESTANA_EQUIPOS_ID= "idEquipos";
	public static String PESTANA_CONFIG_ID= "idConfiguracion";

	public static String PESTANA_USUARIOS_TIT= "Usuarios";
	public static String PESTANA_QUINIELAS_TIT= "Quinielas";
	public static String PESTANA_EQUIPOS_TIT= "Equipos";
	public static String PESTANA_CONFIG_TIT= "Configuración";

	/* notificaciones */   /* los valores deben coincidir con contenido de fichero jm1x2_notifs.properties (en web) */
	public static String NOTIFICACION_TEMPORADA_ACTUAL= "temporada_actual";
	public static String NOTIFICACION_TEMPORADA_LETRERO= "temporada_letrero";
	public static String NOTIFICACION_JORNADAS_NODISPONIBLES= "jornadas_no_disponibles";  /* obsoleta desde v1.6 */
	public static String NOTIFICACION_JORNADAS_NODISPONIBLES_DIV1= "jornadas_no_disponibles_div1";
	public static String NOTIFICACION_JORNADAS_NODISPONIBLES_DIV2= "jornadas_no_disponibles_div2";
	public static String NOTIFICACION_JORNADA= "jornada";  /* obsoleta desde v1.6 */
	public static String NOTIFICACION_JORNADA_DIV1= "jornada_div1";
	public static String NOTIFICACION_JORNADA_DIV2= "jornada_div2";
	public static String NOTIFICACION_QUINIELA_PROXIMA= "quiniela_proxima";
	public static String NOTIFICACION_VERSIONES_BBDD= "versiones_bbdd";
	public static String NOTIFICACION_CAMBIOS_DATOS_GENERALES= "cambios_datos_generales";
	public static String NOTIFICACION_VERSIONCODE_MINIMO= "versioncode_minimo";

	/* preferencias */
	public static String PREFERENCIAS_NOMBRE = "jm1x2_preferencias";
	public static String PREFERENCIAS_USUARIO_ID = "jm1x2.pref.usuId";
	public static String PREFERENCIAS_USUARIO_NOMBRE = "jm1x2.pref.usuNombre";
	public static String PREFERENCIAS_TEMPORADA_ACTUAL = "jm1x2.pref.temporadaActual";
	public static String PREFERENCIAS_PRIMERA_PRESENTACION = "jm1x2.pref.1era_invoc";
	public static String PREFERENCIAS_CLIENTE_ID = "jm1x2.pref.cliId";
	public static String PREFERENCIAS_QUINIELA_NUM_DOBLES = "jm1x2.pref.quin.dobles";
	public static String PREFERENCIAS_QUINIELA_NUM_TRIPLES = "jm1x2.pref.quin.triples";
	public static String PREFERENCIAS_ULTIMO_CAMBIO_DATOS_GENERALES = "jm1x2.pref.ultimoCambioDatosGenerales";
	
	//public static String PREFERENCIAS_HAY_QUE_MOSTRAR_MENSAJE_INICIAL_NUEVA_VERSION = "jm1x2.pref.msg.";  
	// ojo!!! es el prefijo. Contenido real "jm1x2.pref.msg.1.5", "jm1x2.pref.msg.1.6"
	// OBSOLETO desde versión 2.0
	
	public static String PREFERENCIAS_NOTICIAS_ULTIMA = "jm1x2.noticias.ultima";
	
	public static int RESULT_CODE_SE_HAN_COMPLETADO_TODOS_LOS_PARTIDOS_EN_TIEMPO_REAL = 10;
}
