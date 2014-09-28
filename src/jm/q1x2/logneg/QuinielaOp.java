package jm.q1x2.logneg;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import jm.q1x2.activities.Jm1x2Principal.TareaSegundoPlano;
import jm.q1x2.activities.QuinielaVerPronosticoNotif;
import jm.q1x2.bbdd.Basedatos;
import jm.q1x2.bbdd.dao.EquipoDao;
import jm.q1x2.bbdd.dao.QuinielaDao;
import jm.q1x2.transobj.Equipo;
import jm.q1x2.transobj.FactoresPesos;
import jm.q1x2.transobj.NotificacionDatos;
import jm.q1x2.transobj.Partido;
import jm.q1x2.transobj.PartidoComparadorDoblesTriples;
import jm.q1x2.transobj.PatronFactoresPesos;
import jm.q1x2.transobj.Quiniela;
import jm.q1x2.transobj.ValoracionEquiposPartidoQuiniela;
import jm.q1x2.utils.CargaFichero;
import jm.q1x2.utils.Constantes;
import jm.q1x2.utils.Mensajes;
import jm.q1x2.utils.Notificaciones;
import jm.q1x2.utils.Preferencias;
import jm.q1x2.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class QuinielaOp 
{
	public static int RES_1=   1;
	public static int RES_X=   3;
	public static int RES_2=   2;
	public static int RES_1X=  4;
	public static int RES_X2=  5;
	public static int RES_1X2= 6;
	public static int RES_12= 7;
	public static int RES_NO_DISPONIBLE= 9;
	public static int RES_PLENO15_00= 10;
	public static int RES_PLENO15_01= 11;
	public static int RES_PLENO15_02= 12;
	public static int RES_PLENO15_0M= 13;
	public static int RES_PLENO15_10= 14;
	public static int RES_PLENO15_11= 15;
	public static int RES_PLENO15_12= 16;
	public static int RES_PLENO15_1M= 17;
	public static int RES_PLENO15_20= 18;
	public static int RES_PLENO15_21= 19;
	public static int RES_PLENO15_22= 20;
	public static int RES_PLENO15_2M= 21;
	public static int RES_PLENO15_M0= 22;
	public static int RES_PLENO15_M1= 23;
	public static int RES_PLENO15_M2= 24;
	public static int RES_PLENO15_MM= 25;
	
	private static BigDecimal UMBRAL_PRONOSTICO_1X= new BigDecimal(43.6);  // es un porcentaje. Por debajo de ese valor será "1".
	private static BigDecimal UMBRAL_PRONOSTICO_X2= new BigDecimal(50.4);  // es un porcentaje. Por encima de ese valor será "2". 

	private static BigDecimal cero= new BigDecimal(0);
	private static BigDecimal uno= new BigDecimal(1);
	private static BigDecimal dos= new BigDecimal(2);
	private static BigDecimal unTercio= new BigDecimal(0.3333);
	private static BigDecimal dosTercios= new BigDecimal(0.6666);
	
	public static int QUINIELA_NO_DISPONIBLE= 0; // será el valor que se pondrá en el fichero de notificaciones (quiniela_proxima= 0) si aún no está disponible la próxima quiniela

	BigDecimal getUmbralPronostico1X()
	{
		return UMBRAL_PRONOSTICO_1X;
	}
	BigDecimal getUmbralPronosticoX2()
	{
		return UMBRAL_PRONOSTICO_X2;
	}
	
	public ArrayList<Quiniela> getQuinielasHechasJornada(int temporada, int jornada, long idUsuario, SQLiteDatabase con)
	{
    	return new QuinielaDao(con).getQuinielasHechasJornada(temporada, jornada, idUsuario);
	}
	
	public ArrayList<Quiniela> getTodasQuinielasDeTemporada(int temporada, int idUsuario, SQLiteDatabase con)
	{
    	return new QuinielaDao(con).getTodasQuinielasDeTemporada(temporada, idUsuario);
	}
	
	public Quiniela getQuinielaHecha(int idQuiniela, SQLiteDatabase con)
	{
    	return new QuinielaDao(con).getQuinielaHecha(idQuiniela);
	}
	
	public ArrayList<PatronFactoresPesos> getPatronesUsuarioIdNombre(int idUsuario, SQLiteDatabase con)
	{
    	return new QuinielaDao(con).getPatronesUsuarioIdNombre(idUsuario);
	}
	
	public void borrarPatron(int idPatron, SQLiteDatabase con)
	{
		new QuinielaDao(con).borrarPatron(idPatron);
	}

	public int getNumPatrones(int idUsuario, SQLiteDatabase con)
	{
    	return new QuinielaDao(con).getNumPatrones(idUsuario);
	}

	
	public Quiniela getQuinielaEstaSemana(int iJornadaQuinielaEstaSemana, SQLiteDatabase con, Context ctx)
	{
		Quiniela ret= new Quiniela();
		Quiniela q= getQuiniela(Preferencias.getTemporadaActual(ctx), iJornadaQuinielaEstaSemana, ctx);
		if (q != null)
			ret.setPartidos(q.getPartidos());
		else
			ret= null;
		return ret;
	}

	public static Quiniela getQuinielaCorregidaDeJornada(int temporada, int jornada, SQLiteDatabase con)
	{
		QuinielaDao qDao= new QuinielaDao(con);
    	return qDao.getDefinicionQuinielaCorregidaJornada(temporada, jornada);
	}
	
	public static void actualizarAciertos(int quinId, int aciertos, SQLiteDatabase con)
	{
		QuinielaDao qDao= new QuinielaDao(con);
    	qDao.actualizarAciertos(quinId, aciertos);
	}

	public static int getNumDobles(Quiniela qHecha)
	{
		int ret= 0;
		ArrayList<Partido> partidos= qHecha.getPartidos();
		for (Partido p : partidos)
		{
			if (p.getResultadoQuiniela() == RES_1X  ||  p.getResultadoQuiniela() == RES_X2)
				ret++;
		}
		return ret;
	}

	public static int getNumTriples(Quiniela qHecha)
	{
		int ret= 0;
		ArrayList<Partido> partidos= qHecha.getPartidos();
		for (Partido p : partidos)
		{
			if (p.getResultadoQuiniela() == RES_1X2)
				ret++;
		}
		return ret;
	}
	
	public static int obtenerAciertosQuiniela(Quiniela qHecha, Quiniela qResultados)
	{
		int ret= 0;
		ArrayList<Partido> partidosACorregir= qHecha.getPartidos();
		ArrayList<Partido> partidosOK= qResultados.getPartidos();
		Partido partidoACorregir= null;
		Partido partidoOK= null;
		for (int k=0; k< partidosACorregir.size()-1; k++)
		{
			// para los 14 primeros (sin pleno al 15)
			partidoACorregir= partidosACorregir.get(k);
			partidoOK= partidosOK.get(k);
			if (Utils.resultadoAcertado(partidoACorregir.getResultadoQuiniela(), partidoOK.getResultadoQuiniela()))
				ret++;
		}

		// pleno al 15
		partidoACorregir= partidosACorregir.get(partidosACorregir.size()-1);
		partidoOK= partidosOK.get(partidosOK.size()-1);
		if (ret == 14  &&  Utils.resultadoAcertado(partidoACorregir.getResultadoQuiniela(), partidoOK.getResultadoQuiniela()))
			ret++;				

		return ret;
	}
	
	private int getValorAleatoriedad(int pesoFactorAleatoriedad)
	{
		/*
		 * Si por ejemplo el umbral entre 1x es 40, y entre x2 es 50, entonces el 40% de las veces va a devolver un 1
		 * y el 50% un 2, con lo cual el X sólo tiene un 10% de probabilidades.
		 * Sin embargo, por pura aleatoriedad los 3 resultados deberían ser equiprobables. 
		 * La idea de este método es corregir este hecho, y para ello debo ponderar para conseguir esa equiprobabilidad.
		 */
		BigDecimal _ret= null;
		int iValorEntre_0_100= Utils.getNumAleatorio(100);
		BigDecimal valorEntre_0_100= new BigDecimal(iValorEntre_0_100);
		int iValorEntre_0_100_escaladoUnTercio= iValorEntre_0_100;
		if (iValorEntre_0_100 > 66)
			iValorEntre_0_100_escaladoUnTercio= iValorEntre_0_100 - 66;
		else if (iValorEntre_0_100 > 33)
			iValorEntre_0_100_escaladoUnTercio= iValorEntre_0_100 - 33;
		BigDecimal valorEntre_0_100_escaladoUnTercio= new BigDecimal(iValorEntre_0_100_escaladoUnTercio);

		BigDecimal prcDentroDeRango= valorEntre_0_100_escaladoUnTercio.divide(new BigDecimal(33), 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100.0));
		if (valorEntre_0_100.compareTo(unTercio.multiply(new BigDecimal(100))) < 0)
		{
			// es un 1
			BigDecimal rango= getUmbralPronostico1X();
			BigDecimal valorEnRango= rango.multiply(prcDentroDeRango).divide(new BigDecimal(100), 4, RoundingMode.HALF_UP);
			_ret= valorEnRango;
		}
		else if (valorEntre_0_100.compareTo(dosTercios.multiply(new BigDecimal(100))) > 0)
		{
			// es un 2
			BigDecimal rango= new BigDecimal(100).subtract(getUmbralPronosticoX2());
			BigDecimal valorEnRango= rango.multiply(prcDentroDeRango).divide(new BigDecimal(100), 4, RoundingMode.HALF_UP);
			_ret= getUmbralPronosticoX2().add(valorEnRango);
		}
		else
		{
			// es un X
			BigDecimal rango= getUmbralPronosticoX2().subtract(getUmbralPronostico1X());
			BigDecimal valorEnRango= rango.multiply(prcDentroDeRango).divide(new BigDecimal(100), 4, RoundingMode.HALF_UP);
			_ret= getUmbralPronostico1X().add(valorEnRango);
		}
		return _ret.multiply(new BigDecimal(pesoFactorAleatoriedad)).divide(new BigDecimal(100), 4, RoundingMode.HALF_UP).setScale(0, RoundingMode.HALF_UP).intValue();
	}	
	
	public ArrayList<ValoracionEquiposPartidoQuiniela> getValoracionesEquiposSegunCriterios(ArrayList<Partido> partidos, FactoresPesos fac, SQLiteDatabase con, int idTemporada, int idUsuario, Context ctx)
	{
		ArrayList<ValoracionEquiposPartidoQuiniela> ret= new ArrayList<ValoracionEquiposPartidoQuiniela>();

		String idLocal= null;
		String idVisit= null;

		/*
		 * Primero los partidos 1 al 14.
		 * El pleno al 15 (se hace después), que se calcula de distinta forma
		 */
		for (int k=0; k< partidos.size()-1; k++)  
		{
			Partido par= partidos.get(k);
			
			idLocal= par.getIdEquipoLocal();
			idVisit= par.getIdEquipoVisit();
		
//			StringBuffer resumen= null;
			BigDecimal local_puntos= new BigDecimal(0);
			BigDecimal visit_puntos= new BigDecimal(0);
			BigDecimal valorFactorEnLocal, valorFactorEnVisit;
			
			EquipoDao eqDao= new EquipoDao(con);
	
//			if (Log.isLoggable(Constantes.LOG_TAG, Log.DEBUG))
//			{
//				Log.d(Constantes.LOG_TAG, "[pronóstico] Partido: "+idLocal+" - "+idVisit);
//				resumen= new StringBuffer("[pronóstico] ").append(idLocal).append(" - ").append(idVisit).append(": "); 
//			}
			
			// 1: aleatoriedad
			int r= getValorAleatoriedad(fac.getAleatoriedad());
			valorFactorEnLocal= new BigDecimal(r);
			valorFactorEnVisit= new BigDecimal(fac.getAleatoriedad()-r);
			local_puntos= local_puntos.add(valorFactorEnLocal);
			visit_puntos= visit_puntos.add(valorFactorEnVisit);
//			if (Log.isLoggable(Constantes.LOG_TAG, Log.DEBUG))
//				Log.d(Constantes.LOG_TAG, "[pronóstico] (1) aleatoriedad ("+fac.getAleatoriedad()+") - local: "+valorFactorEnLocal.toString()+" ; visit: "+valorFactorEnVisit.toString());
	
			// 2: calidad intrínseca
			int local_calidadIntrinseca= getCalidadIntrinseca(idLocal, eqDao, idTemporada, idUsuario, ctx);
			int visit_calidadIntrinseca= getCalidadIntrinseca(idVisit, eqDao, idTemporada, idUsuario, ctx);
			valorFactorEnLocal= ponderarValor(local_calidadIntrinseca, visit_calidadIntrinseca, fac.getCalidadIntrinseca());
			valorFactorEnVisit= ponderarValor(visit_calidadIntrinseca, local_calidadIntrinseca, fac.getCalidadIntrinseca());
			local_puntos= local_puntos.add(valorFactorEnLocal);
			visit_puntos= visit_puntos.add(valorFactorEnVisit);
//			if (Log.isLoggable(Constantes.LOG_TAG, Log.DEBUG))
//				Log.d(Constantes.LOG_TAG, "[pronóstico] (2) calidad intrínseca ("+fac.getCalidadIntrinseca()+") - local ("+local_calidadIntrinseca+"): "+valorFactorEnLocal.toString()+" ; visit ("+visit_calidadIntrinseca+"): "+valorFactorEnVisit.toString());
	
			// 3: factor campo 
			BigDecimal facCampo= new BigDecimal(fac.getFactorCampo());
			valorFactorEnLocal= facCampo.multiply(dosTercios).round(new MathContext(4));  	// 2/3 para el local
			valorFactorEnVisit= facCampo.multiply(unTercio).round(new MathContext(4));		// 1/3 para el visitante
			local_puntos= local_puntos.add(valorFactorEnLocal);  
			visit_puntos= visit_puntos.add(valorFactorEnVisit);    
//			if (Log.isLoggable(Constantes.LOG_TAG, Log.DEBUG))
//				Log.d(Constantes.LOG_TAG, "[pronóstico] (3) factor campo ("+fac.getFactorCampo()+") - local: "+valorFactorEnLocal.toString()+" ; visit: "+valorFactorEnVisit.toString());
			
			// 4: golaveraje
		    int local_numPartidosLocal= eqDao.getNumPartidos(idTemporada, idLocal, EquipoOp.EQUIPO_LOCAL);
		    int local_numPartidosVisit= eqDao.getNumPartidos(idTemporada, idLocal, EquipoOp.EQUIPO_VISITANTE);
		    int visit_numPartidosLocal= eqDao.getNumPartidos(idTemporada, idVisit, EquipoOp.EQUIPO_LOCAL);
		    int visit_numPartidosVisit= eqDao.getNumPartidos(idTemporada, idVisit, EquipoOp.EQUIPO_VISITANTE);
			BigDecimal local_golaverajePorPartido= eqDao.getGolaverajePorPartido(idTemporada, idLocal, EquipoOp.EQUIPO_LOCAL_Y_VISITANTE, local_numPartidosLocal+local_numPartidosVisit);
			BigDecimal visit_golaverajePorPartido= eqDao.getGolaverajePorPartido(idTemporada, idVisit, EquipoOp.EQUIPO_LOCAL_Y_VISITANTE, visit_numPartidosLocal+visit_numPartidosVisit);
			valorFactorEnLocal= ponderarValorPorTramos(local_golaverajePorPartido, fac.getGolaveraje(), -2, 3);
			valorFactorEnVisit= ponderarValorPorTramos(visit_golaverajePorPartido, fac.getGolaveraje(), -2, 3);
			local_puntos= local_puntos.add(valorFactorEnLocal);
			visit_puntos= visit_puntos.add(valorFactorEnVisit);
//			if (Log.isLoggable(Constantes.LOG_TAG, Log.DEBUG))
//				Log.d(Constantes.LOG_TAG, "[pronóstico] (4) golaveraje ("+fac.getGolaveraje()+") - local ("+local_golaverajePorPartido.round(new MathContext(4))+"): "+valorFactorEnLocal.toString()+" ; visit ("+visit_golaverajePorPartido.round(new MathContext(4))+"): "+valorFactorEnVisit.toString());
			
			// 5: golaveraje como local/visitante
			BigDecimal local_golaverajePorPartidoLocal= eqDao.getGolaverajePorPartido(idTemporada, idLocal, EquipoOp.EQUIPO_LOCAL, local_numPartidosLocal);
			BigDecimal visit_golaverajePorPartidoVisit= eqDao.getGolaverajePorPartido(idTemporada, idVisit, EquipoOp.EQUIPO_VISITANTE, visit_numPartidosVisit);
			valorFactorEnLocal= ponderarValorPorTramos(local_golaverajePorPartidoLocal, fac.getGolaverajeLocalVisit(), -2, 3);
			valorFactorEnVisit= ponderarValorPorTramos(visit_golaverajePorPartidoVisit, fac.getGolaverajeLocalVisit(), -2, 3);
			local_puntos= local_puntos.add(valorFactorEnLocal);
			visit_puntos= visit_puntos.add(valorFactorEnVisit);
//			if (Log.isLoggable(Constantes.LOG_TAG, Log.DEBUG))
//				Log.d(Constantes.LOG_TAG, "[pronóstico] (5) golaveraje local/visit ("+fac.getGolaverajeLocalVisit()+") - local ("+local_golaverajePorPartidoLocal.round(new MathContext(4))+"): "+valorFactorEnLocal.toString()+" ; visit ("+visit_golaverajePorPartidoVisit.round(new MathContext(4))+"): "+valorFactorEnVisit.toString());
	
			// 6: puntos por partido
			BigDecimal local_ptosPorPartido= eqDao.getPuntosPorPartido(idTemporada, idLocal, EquipoOp.EQUIPO_LOCAL_Y_VISITANTE, local_numPartidosLocal+local_numPartidosVisit);
			BigDecimal visit_ptosPorPartido= eqDao.getPuntosPorPartido(idTemporada, idVisit, EquipoOp.EQUIPO_LOCAL_Y_VISITANTE, visit_numPartidosLocal+visit_numPartidosVisit);
			valorFactorEnLocal= ponderarValorPorTramos(local_ptosPorPartido, fac.getPuntosPartido(), 0, 3);
			valorFactorEnVisit= ponderarValorPorTramos(visit_ptosPorPartido, fac.getPuntosPartido(), 0, 3);
			local_puntos= local_puntos.add(valorFactorEnLocal);
			visit_puntos= visit_puntos.add(valorFactorEnVisit);
//			if (Log.isLoggable(Constantes.LOG_TAG, Log.DEBUG))
//				Log.d(Constantes.LOG_TAG, "[pronóstico] (6) puntos por partido ("+fac.getPuntosPartido()+") - local ("+local_ptosPorPartido.round(new MathContext(4))+"): "+valorFactorEnLocal.toString()+" ; visit ("+visit_ptosPorPartido.round(new MathContext(4))+"): "+valorFactorEnVisit.toString());
			
			// 7: puntos por partido como local/visitante
			BigDecimal local_ptosPorPartidoLocal= eqDao.getPuntosPorPartido(idTemporada, idLocal, EquipoOp.EQUIPO_LOCAL, local_numPartidosLocal);
			BigDecimal visit_ptosPorPartidoVisit= eqDao.getPuntosPorPartido(idTemporada, idVisit, EquipoOp.EQUIPO_VISITANTE, visit_numPartidosVisit);
			valorFactorEnLocal= ponderarValorPorTramos(local_ptosPorPartidoLocal, fac.getPuntosPartidoLocalVisit(), 0, 3);
			valorFactorEnVisit= ponderarValorPorTramos(visit_ptosPorPartidoVisit, fac.getPuntosPartidoLocalVisit(), 0, 3);
			local_puntos= local_puntos.add(valorFactorEnLocal);
			visit_puntos= visit_puntos.add(valorFactorEnVisit);
//			if (Log.isLoggable(Constantes.LOG_TAG, Log.DEBUG))
//				Log.d(Constantes.LOG_TAG, "[pronóstico] (7) puntos por partido local/visit ("+fac.getPuntosPartidoLocalVisit()+") - local ("+local_ptosPorPartidoLocal.round(new MathContext(4))+"): "+valorFactorEnLocal.toString()+" ; visit ("+visit_ptosPorPartidoVisit.round(new MathContext(4))+"): "+valorFactorEnVisit.toString());
			
			// 8: últimos 4 partidos
			int local_ptosUltimos4= eqDao.getPuntosUltimos4Partidos(idTemporada, idLocal, EquipoOp.EQUIPO_LOCAL_Y_VISITANTE);
			int visit_ptosUltimos4= eqDao.getPuntosUltimos4Partidos(idTemporada, idVisit, EquipoOp.EQUIPO_LOCAL_Y_VISITANTE);
			/* Puede darse el caso de que aún no haya disputado 4 partidos.
			 * Aquí habrá que escalar este valor. */
			int local_partidosJugadosUltimasJornadas= eqDao.getPartidosJugadosUltimasXJornadas(idTemporada, idLocal, 4, EquipoOp.EQUIPO_LOCAL_Y_VISITANTE);
			int visit_partidosJugadosUltimasJornadas= eqDao.getPartidosJugadosUltimasXJornadas(idTemporada, idVisit, 4, EquipoOp.EQUIPO_LOCAL_Y_VISITANTE);
			if (local_partidosJugadosUltimasJornadas > 0)
				local_ptosUltimos4 *= 4/local_partidosJugadosUltimasJornadas;
			if (visit_partidosJugadosUltimasJornadas > 0)
				visit_ptosUltimos4 *= 4/visit_partidosJugadosUltimasJornadas;
			valorFactorEnLocal= ponderarValorPorTramos(local_ptosUltimos4, fac.getUltimos4(), 0, 4*3);
			valorFactorEnVisit= ponderarValorPorTramos(visit_ptosUltimos4, fac.getUltimos4(), 0, 4*3);
			local_puntos= local_puntos.add(valorFactorEnLocal);
			visit_puntos= visit_puntos.add(valorFactorEnVisit);
//			if (Log.isLoggable(Constantes.LOG_TAG, Log.DEBUG))
//				Log.d(Constantes.LOG_TAG, "[pronóstico] (8) últimos 4 partidos ("+fac.getUltimos4()+") - local ("+local_ptosUltimos4+"): "+valorFactorEnLocal.toString()+" ; visit ("+visit_ptosUltimos4+"): "+valorFactorEnVisit.toString());
			
			// 9: últimos 4 partidos como local/visitante
			int local_ptosUltimos4Local= eqDao.getPuntosUltimos4Partidos(idTemporada, idLocal, EquipoOp.EQUIPO_LOCAL);
			int visit_ptosUltimos4Visit= eqDao.getPuntosUltimos4Partidos(idTemporada, idVisit, EquipoOp.EQUIPO_VISITANTE);
			/* Puede darse el caso de que aún no haya disputado 4 partidos.
			 * Aquí habrá que escalar este valor. */
			int local_partidosJugadosUltimasJornadasLocal= eqDao.getPartidosJugadosUltimasXJornadas(idTemporada, idLocal, 4, EquipoOp.EQUIPO_LOCAL);
			int visit_partidosJugadosUltimasJornadasVisit= eqDao.getPartidosJugadosUltimasXJornadas(idTemporada, idVisit, 4, EquipoOp.EQUIPO_VISITANTE);
			if (local_partidosJugadosUltimasJornadasLocal > 0)
				local_ptosUltimos4Local *= 4/local_partidosJugadosUltimasJornadasLocal;
			if (visit_partidosJugadosUltimasJornadasVisit > 0)
				visit_ptosUltimos4Visit *= 4/visit_partidosJugadosUltimasJornadasVisit;
			valorFactorEnLocal= ponderarValorPorTramos(local_ptosUltimos4Local, fac.getUltimos4LocalVisit(), 0, 4*3);
			valorFactorEnVisit= ponderarValorPorTramos(visit_ptosUltimos4Visit, fac.getUltimos4LocalVisit(), 0, 4*3);
			local_puntos= local_puntos.add(valorFactorEnLocal);
			visit_puntos= visit_puntos.add(valorFactorEnVisit);
//			if (Log.isLoggable(Constantes.LOG_TAG, Log.DEBUG))
//				Log.d(Constantes.LOG_TAG, "[pronóstico] (9) últimos 4 partidos local/visit ("+fac.getUltimos4LocalVisit()+") - local ("+local_ptosUltimos4Local+"): "+valorFactorEnLocal.toString()+" ; visit ("+visit_ptosUltimos4Visit+"): "+valorFactorEnVisit.toString());
			
			ret.add(new ValoracionEquiposPartidoQuiniela(local_puntos, visit_puntos));
		}

		
		
		
		/*
		 * ###################################################################################################################
		 * Pleno al 15.
		 * Este no va con 1, x, 2 como el resto, sino con 0, 1, 2, M para cada equipo (siendo el número de goles; M: más de 2)
		 * 
		 * Aquí no entrarán en juego todos los criterios. Pongo + en los que sí entrarán y - en los que no:
		 * 		+ aleatoriedad
		 * 		+ calidad intrínseca
		 * 		- factor campo 
		 * 		+ golaveraje
		 * 		+ golaveraje como local/visitante
		 * 		- puntos por partido
		 * 		- puntos por partido como local/visitante
		 * 		+ últimos 4 partidos
		 * 		+ últimos 4 partidos como local/visitante
		 * 
		 * Lo que se acabará retornando como elemento 15 del array es:
		 * 		ret.add(new ValoracionEquiposPartidoQuiniela(A, B));
		 * A serán los goles del equipo local, y B los del visitante.
		 * Si este valor es menor que 3 será ese el pronóstico (0, 1 ó 2). Si es 3 o superior, el pronóstico será M
		 * 
		 * Algoritmo que seguiré:
		 * Tenemos varios criterios (c1, c2, ... ck) y varios pesos de esos criterios (p1, p2, ... pk)
		 * El número resultante será  N/D, siendo:
		 * 		N: c1*p1 + c2*p2 + ... + ck*pk
		 * 		D: p1+p2+...+pk
		 * (NOTA: c1, c2, ... , ck  tendrán como posibles valores 0,1,2,3.    El 3 representa M. )
		 */
		Partido par= partidos.get(partidos.size()-1);
	
		idLocal= par.getIdEquipoLocal();
		idVisit= par.getIdEquipoVisit();
	
//			StringBuffer resumen= null;
		BigDecimal local_numerador= new BigDecimal(0);
		BigDecimal local_denominador= new BigDecimal(0);
		BigDecimal visit_numerador= new BigDecimal(0);
		BigDecimal visit_denominador= new BigDecimal(0);
		
		EquipoDao eqDao= new EquipoDao(con);

//			if (Log.isLoggable(Constantes.LOG_TAG, Log.DEBUG))
//			{
//				Log.d(Constantes.LOG_TAG, "[pronóstico (pleno15)] Partido: "+idLocal+" - "+idVisit);
//				resumen= new StringBuffer("[pronóstico (pleno15)] ").append(idLocal).append(" - ").append(idVisit).append(": "); 
//			}
		
		// 1: aleatoriedad
		int local_valorAleatorioEntre0y3 = (new Random()).nextInt(3+1);   
		local_numerador= local_numerador.add(new BigDecimal(local_valorAleatorioEntre0y3).multiply(new BigDecimal(fac.getAleatoriedad())));
		local_denominador= local_denominador.add(new BigDecimal(fac.getAleatoriedad()));
		
		int visit_valorAleatorioEntre0y3 = (new Random()).nextInt(3+1);   
		visit_numerador= visit_numerador.add(new BigDecimal(visit_valorAleatorioEntre0y3).multiply(new BigDecimal(fac.getAleatoriedad())));
		visit_denominador= visit_denominador.add(new BigDecimal(fac.getAleatoriedad()));
		
		if (Log.isLoggable(Constantes.LOG_TAG, Log.DEBUG))
			Log.d(Constantes.LOG_TAG, "[pronóstico (pleno15)] (1) aleatoriedad ("+fac.getAleatoriedad()+") - local: "+local_valorAleatorioEntre0y3+" ; visit: "+visit_valorAleatorioEntre0y3);
		
		// 2: calidad intrínseca
		int local_calidadIntrinseca= getCalidadIntrinseca(idLocal, eqDao, idTemporada, idUsuario, ctx);
		if (local_calidadIntrinseca < 40) local_calidadIntrinseca= 0;   else if (local_calidadIntrinseca < 60) local_calidadIntrinseca= 1; 
																		else if (local_calidadIntrinseca < 80) local_calidadIntrinseca= 2; 
																		else local_calidadIntrinseca= 3; 
		local_numerador= local_numerador.add(new BigDecimal(local_calidadIntrinseca).multiply(new BigDecimal(fac.getCalidadIntrinseca())));
		local_denominador= local_denominador.add(new BigDecimal(fac.getCalidadIntrinseca()));
		
		int visit_calidadIntrinseca= getCalidadIntrinseca(idVisit, eqDao, idTemporada, idUsuario, ctx);
		if (visit_calidadIntrinseca < 40) visit_calidadIntrinseca= 0;   else if (visit_calidadIntrinseca < 60) visit_calidadIntrinseca= 1; 
																		else if (visit_calidadIntrinseca < 80) visit_calidadIntrinseca= 2; 
																		else visit_calidadIntrinseca= 3; 
		visit_numerador= visit_numerador.add(new BigDecimal(visit_calidadIntrinseca).multiply(new BigDecimal(fac.getCalidadIntrinseca())));
		visit_denominador= visit_denominador.add(new BigDecimal(fac.getCalidadIntrinseca()));
		
		if (Log.isLoggable(Constantes.LOG_TAG, Log.DEBUG))
			Log.d(Constantes.LOG_TAG, "[pronóstico (pleno15)] (2) calidad intrínseca ("+fac.getCalidadIntrinseca()+") - local: "+local_calidadIntrinseca+"); visit: "+visit_calidadIntrinseca+")");

		// 3: factor campo 		===> criterio que no se tendrá en cuenta en el pleno al 15
		
		// 4: golaveraje
	    int local_numPartidosLocal= eqDao.getNumPartidos(idTemporada, idLocal, EquipoOp.EQUIPO_LOCAL);
	    int local_numPartidosVisit= eqDao.getNumPartidos(idTemporada, idLocal, EquipoOp.EQUIPO_VISITANTE);
	    int visit_numPartidosLocal= eqDao.getNumPartidos(idTemporada, idVisit, EquipoOp.EQUIPO_LOCAL);
	    int visit_numPartidosVisit= eqDao.getNumPartidos(idTemporada, idVisit, EquipoOp.EQUIPO_VISITANTE);
		BigDecimal local_golaverajePorPartido= eqDao.getGolaverajePorPartido(idTemporada, idLocal, EquipoOp.EQUIPO_LOCAL_Y_VISITANTE, local_numPartidosLocal+local_numPartidosVisit);
		BigDecimal visit_golaverajePorPartido= eqDao.getGolaverajePorPartido(idTemporada, idVisit, EquipoOp.EQUIPO_LOCAL_Y_VISITANTE, visit_numPartidosLocal+visit_numPartidosVisit);
		if (local_golaverajePorPartido.compareTo(cero) < 0)  local_golaverajePorPartido= cero;
		if (visit_golaverajePorPartido.compareTo(cero) < 0)  visit_golaverajePorPartido= cero;
		
		local_numerador= local_numerador.add(local_golaverajePorPartido.multiply(new BigDecimal(fac.getGolaveraje())));
		local_denominador= local_denominador.add(new BigDecimal(fac.getGolaveraje()));

		visit_numerador= visit_numerador.add(visit_golaverajePorPartido.multiply(new BigDecimal(fac.getGolaveraje())));
		visit_denominador= visit_denominador.add(new BigDecimal(fac.getGolaveraje()));
		
		if (Log.isLoggable(Constantes.LOG_TAG, Log.DEBUG))
			Log.d(Constantes.LOG_TAG, "[pronóstico (pleno15)] (4) golaveraje ("+fac.getGolaveraje()+") - local: "+local_golaverajePorPartido+" ; visit: "+visit_golaverajePorPartido);
		
		// 5: golaveraje como local/visitante
		BigDecimal local_golaverajePorPartidoLocal= eqDao.getGolaverajePorPartido(idTemporada, idLocal, EquipoOp.EQUIPO_LOCAL, local_numPartidosLocal);
		BigDecimal visit_golaverajePorPartidoVisit= eqDao.getGolaverajePorPartido(idTemporada, idVisit, EquipoOp.EQUIPO_VISITANTE, visit_numPartidosVisit);
		if (local_golaverajePorPartidoLocal.compareTo(cero) < 0)  local_golaverajePorPartidoLocal= cero;
		if (visit_golaverajePorPartidoVisit.compareTo(cero) < 0)  visit_golaverajePorPartidoVisit= cero;

		local_numerador= local_numerador.add(local_golaverajePorPartidoLocal.multiply(new BigDecimal(fac.getGolaverajeLocalVisit())));
		local_denominador= local_denominador.add(new BigDecimal(fac.getGolaverajeLocalVisit()));

		visit_numerador= visit_numerador.add(visit_golaverajePorPartidoVisit.multiply(new BigDecimal(fac.getGolaverajeLocalVisit())));
		visit_denominador= visit_denominador.add(new BigDecimal(fac.getGolaverajeLocalVisit()));

		
		if (Log.isLoggable(Constantes.LOG_TAG, Log.DEBUG))
			Log.d(Constantes.LOG_TAG, "[pronóstico (pleno15)] (5) golaveraje local/visit ("+fac.getGolaverajeLocalVisit()+") - local:"+local_golaverajePorPartidoLocal+" ; visit: "+visit_golaverajePorPartidoVisit);

		// 6: puntos por partido 						===> criterio que no se tendrá en cuenta en el pleno al 15
		// 7: puntos por partido como local/visitante   ===> criterio que no se tendrá en cuenta en el pleno al 15
		
		// 8: últimos 4 partidos
		int local_ptosUltimos4= eqDao.getPuntosUltimos4Partidos(idTemporada, idLocal, EquipoOp.EQUIPO_LOCAL_Y_VISITANTE);
		int visit_ptosUltimos4= eqDao.getPuntosUltimos4Partidos(idTemporada, idVisit, EquipoOp.EQUIPO_LOCAL_Y_VISITANTE);
		/* Puede darse el caso de que aún no haya disputado 4 partidos.
		 * Aquí habrá que escalar este valor. */
		int local_partidosJugadosUltimasJornadas= eqDao.getPartidosJugadosUltimasXJornadas(idTemporada, idLocal, 4, EquipoOp.EQUIPO_LOCAL_Y_VISITANTE);
		int visit_partidosJugadosUltimasJornadas= eqDao.getPartidosJugadosUltimasXJornadas(idTemporada, idVisit, 4, EquipoOp.EQUIPO_LOCAL_Y_VISITANTE);
		if (local_partidosJugadosUltimasJornadas > 0)
			local_ptosUltimos4 *= 4/local_partidosJugadosUltimasJornadas;
		if (visit_partidosJugadosUltimasJornadas > 0)
			visit_ptosUltimos4 *= 4/visit_partidosJugadosUltimasJornadas;
		if (local_ptosUltimos4  < 0)  local_ptosUltimos4= 0;
		if (visit_ptosUltimos4  < 0)  visit_ptosUltimos4= 0;

		local_numerador= local_numerador.add(new BigDecimal(local_ptosUltimos4).multiply(new BigDecimal(fac.getUltimos4())));
		local_denominador= local_denominador.add(new BigDecimal(fac.getUltimos4()));
 
		visit_numerador= visit_numerador.add(new BigDecimal(visit_ptosUltimos4).multiply(new BigDecimal(fac.getUltimos4())));
		visit_denominador= visit_denominador.add(new BigDecimal(fac.getUltimos4()));
		
		if (Log.isLoggable(Constantes.LOG_TAG, Log.DEBUG))
			Log.d(Constantes.LOG_TAG, "[pronóstico (pleno15)] (8) últimos 4 partidos ("+fac.getUltimos4()+") - local: "+local_ptosUltimos4+" ; visit: "+visit_ptosUltimos4);
		
		// 9: últimos 4 partidos como local/visitante
		int local_ptosUltimos4Local= eqDao.getPuntosUltimos4Partidos(idTemporada, idLocal, EquipoOp.EQUIPO_LOCAL);
		int visit_ptosUltimos4Visit= eqDao.getPuntosUltimos4Partidos(idTemporada, idVisit, EquipoOp.EQUIPO_VISITANTE);
		/* Puede darse el caso de que aún no haya disputado 4 partidos.
		 * Aquí habrá que escalar este valor. */
		int local_partidosJugadosUltimasJornadasLocal= eqDao.getPartidosJugadosUltimasXJornadas(idTemporada, idLocal, 4, EquipoOp.EQUIPO_LOCAL);
		int visit_partidosJugadosUltimasJornadasVisit= eqDao.getPartidosJugadosUltimasXJornadas(idTemporada, idVisit, 4, EquipoOp.EQUIPO_VISITANTE);
		if (local_partidosJugadosUltimasJornadasLocal > 0)
			local_ptosUltimos4Local *= 4/local_partidosJugadosUltimasJornadasLocal;
		if (visit_partidosJugadosUltimasJornadasVisit > 0)
			visit_ptosUltimos4Visit *= 4/visit_partidosJugadosUltimasJornadasVisit;
		if (local_ptosUltimos4Local < 0)  local_ptosUltimos4Local= 0;
		if (visit_ptosUltimos4Visit < 0)  visit_ptosUltimos4Visit= 0;

		local_numerador= local_numerador.add(new BigDecimal(local_ptosUltimos4Local).multiply(new BigDecimal(fac.getUltimos4LocalVisit())));
		local_denominador= local_denominador.add(new BigDecimal(fac.getUltimos4LocalVisit()));

		visit_numerador= visit_numerador.add(new BigDecimal(visit_ptosUltimos4Visit).multiply(new BigDecimal(fac.getUltimos4LocalVisit())));
		visit_denominador= visit_denominador.add(new BigDecimal(fac.getUltimos4LocalVisit()));

		if (Log.isLoggable(Constantes.LOG_TAG, Log.DEBUG))
			Log.d(Constantes.LOG_TAG, "[pronóstico (pleno15)] (9) últimos 4 partidos local/visit ("+fac.getUltimos4LocalVisit()+") - local: "+local_ptosUltimos4Local+"; visit: "+visit_ptosUltimos4Visit);
		
		if (local_denominador.equals(cero))
			local_denominador= local_numerador;
		if (visit_denominador.equals(cero))
			visit_denominador= local_numerador;
		
		ret.add(new ValoracionEquiposPartidoQuiniela(local_numerador.divide(local_denominador, 4, RoundingMode.HALF_UP), visit_numerador.divide(visit_denominador, 4, RoundingMode.HALF_UP)));
		
		return ret;
	}

	public ArrayList<Partido> convertirValoracionesEnPronostico1X2(ArrayList<Partido> partidos, ArrayList<ValoracionEquiposPartidoQuiniela> valoresLocalVisit, int numTriples, int numDobles)
	{		
		ArrayList<Partido> ret= new ArrayList<Partido>();
		
		if (numTriples == 0  && numDobles == 0)
		{
			for (int k=0; k< partidos.size()-1; k++)    // cogerá todos excepto pleno al 15
			{
				Partido parK= partidos.get(k);
				ValoracionEquiposPartidoQuiniela val= valoresLocalVisit.get(k);
				int pron= convertirValoracionEnPronostico1X2(val.valorLocal, val.valorVisit);
				parK.setResultadoQuiniela(pron);
				ret.add(parK);
			}
		}
		else
		{
			ArrayList<Partido> partidosSinPlenoAl15= (ArrayList<Partido>) partidos.clone();
			partidosSinPlenoAl15.remove(partidos.size()-1);
			
			/*
			 * algoritmo:
			 * Sea  V   : porcentaje de puntuación del visitante. Si es < umbral_1X sería un 1; si es > umbral_X2 sería un 2; resto sería X.
			 * 		U_1X: umbral entre pronóstico 1 y X
			 * 		U_X2: umbral entre pronóstico X y 2
			 * 1.- Primero resuelvo los triples. Los partidos elegidos serán aquellos con X ordenado de menor a mayor, siendo X:
			 * 			X= A + B , siendo A: distancia entre V y U_1X,  B: distancia entre V y U_X2
			 * 			(es decir, aquel partido cuya suma de distancias entre V y U_1X, y V y U_X2   sea menor)
			 * 			En caso de empate, prevalece aquel partido con V más próximo a la media de los 2 umbrales
			 * 2.- En el resto de partidos, resuelvo los dobles. Los partidos elegidos serán aquellos con Y ordenado de menor a mayor, siendo Y:
			 * 			Y= menor(A,B), siendo  A: distancia entre V y U_1X,  B: distancia entre V y U_X2
			 * 			(es decir, aquel partido cuyo V esté más próximo a un umbral cualquiera)
			 */
			int numPartidos= partidosSinPlenoAl15.size();
			Partido par;
			ValoracionEquiposPartidoQuiniela valoracion;
	
			
			/*
			 * Triples
			 */
			List<PartidoComparadorDoblesTriples> partidosCompTriples= new ArrayList<PartidoComparadorDoblesTriples>();
			for (int idx=0; idx< numPartidos; idx++)
			{
				par= partidosSinPlenoAl15.get(idx);
				valoracion= valoresLocalVisit.get(idx);
				BigDecimal prcVisit= damePorcentajeValoracionVisitante(valoracion.valorLocal, valoracion.valorVisit);
				
				PartidoComparadorDoblesTriples parComp= new PartidoComparadorDoblesTriples();
				parComp.setOrdenEnQuiniela(idx);
				parComp.setPrcVisit(prcVisit);
				parComp.setTipoComparador(PartidoComparadorDoblesTriples.COMPARADOR_TRIPLES);
				parComp.setUmbral_1x(getUmbralPronostico1X());
				parComp.setUmbral_x2(getUmbralPronosticoX2());
				
				partidosCompTriples.add(parComp);
								
			}
			if (numTriples > 0)
			{
				Collections.sort(partidosCompTriples);
				// asigno el pronóstico 1X2 (triple) a los 'numTriples' primeros de esta lista
				for (int k=0; k<numTriples; k++)
					partidosCompTriples.get(k).setPronostico_1x2(QuinielaOp.RES_1X2);
			}
			
			
			/*
			 * Dobles
			 */
			List<PartidoComparadorDoblesTriples> partidosCompDoblesYSimples= new ArrayList<PartidoComparadorDoblesTriples>();
			
			// selecciono aquellos que ni tienen no triple (no tiene sentido que entren en la ordenación si ya se sabe que tienen un triple)
			PartidoComparadorDoblesTriples parDob;
			for (int k= numTriples; k<partidosCompTriples.size(); k++)
			{
				parDob= partidosCompTriples.get(k);
				parDob.setTipoComparador(PartidoComparadorDoblesTriples.COMPARADOR_DOBLES);  // les cambio el campo 'tipoComparador' para que pase a ser DOBLES
				partidosCompDoblesYSimples.add(parDob);
			}
			if (numDobles > 0)
			{
				Collections.sort(partidosCompDoblesYSimples);
				// asigno el pronóstico 1X2 (triple) a los 'numTriples' primeros de esta lista
				for (int k=0; k<numDobles; k++)
				{
					parDob= partidosCompDoblesYSimples.get(k);
					decidirPronosticoDoble(parDob);
				}
			}

			
			/*
			 * En este punto, la variable 'partidosCompDobles' tiene todos los partidos que no han sido TRIPLES.
			 * Dentro de esta variable: 
			 * 		- entre el idx 0 y numDobles, son los que se resuelven con dobles
			 * 		- entre el idx numDobles y partidosCompDobles.size(), son los que no son SIMPLES (ni DOBLES ni TRIPLES)
			 */
			
			
			/*
			 * faltan por tratar aquellos partidos que no han recibido pronóstico de doble ni triple
			 */
			for (int k=numDobles; k<partidosCompDoblesYSimples.size(); k++)
			{
				int orden= partidosCompDoblesYSimples.get(k).getOrdenEnQuiniela();
				ValoracionEquiposPartidoQuiniela val= valoresLocalVisit.get(orden);
				int pron= convertirValoracionEnPronostico1X2(val.valorLocal, val.valorVisit);
				partidosCompDoblesYSimples.get(k).setPronostico_1x2(pron);				
			}

			
			
			/*
			 * Finalmente, debo devolver la lista de 15 partidos con sus pronósticos, ordenada según su campo 'ordenEnQuiniela'
			 * ya que será según este orden como se mostrarán por pantalla.
			 */
			for (int k=0; k< partidos.size()-1; k++)    // cogerá todos excepto pleno al 15 (que se añadirá al final)
				ret.add(null);
			
			Partido partidoRet;
			for (int k=0; k< numTriples; k++)  // del array 'partidosCompTriples' sólo cojo los numTriples primeros
			{
				PartidoComparadorDoblesTriples tri= partidosCompTriples.get(k);
				int ordenEnQuiniela= tri.getOrdenEnQuiniela();
				partidoRet= partidosSinPlenoAl15.get(ordenEnQuiniela);
				partidoRet.setResultadoQuiniela(tri.getPronostico_1x2());
				ret.set(ordenEnQuiniela, partidoRet);
			}
			for (PartidoComparadorDoblesTriples dob : partidosCompDoblesYSimples)  // del array 'partidosCompDoblesYSimples' cojo todos (los primeros serán dobles; el resto, simples)
			{
				int ordenEnQuiniela= dob.getOrdenEnQuiniela();
				partidoRet= partidosSinPlenoAl15.get(ordenEnQuiniela);
				partidoRet.setResultadoQuiniela(dob.getPronostico_1x2());
				ret.set(ordenEnQuiniela, partidoRet);
			}
						
		}
		
		// pleno al 15
		int idx= partidos.size()-1;
		Partido parPleno15= partidos.get(idx);
		ValoracionEquiposPartidoQuiniela val= valoresLocalVisit.get(idx);
		int pron= convertirValoracionEnPronostico1X2_plenoAl15(val.valorLocal, val.valorVisit);
		parPleno15.setResultadoQuiniela(pron);
		ret.add(parPleno15);		
		
		return ret;
	}

	
	/*
	 * decidirá si para ese partido el doble es 1X  ó  X2
	 */
	private void decidirPronosticoDoble(PartidoComparadorDoblesTriples par)
	{
		/*
		 * algoritmo: 
		 * Sea V= par.prcVisit;
		 * 	Si:
		 * 		- V <= umbral_1X ==> 1X
		 * 		- V >= umbral_X2 ==> X2
		 * 		- umbral_1X < V < umbral_X2 ==> 1X si está más próximo al umbral_1X, o bien X2 si está más próximo al umbral_X2
		 */
		int pron;
		if (par.getPrcVisit().compareTo(par.getUmbral_1x()) <= 0)
			pron= RES_1X;
		else if (par.getPrcVisit().compareTo(par.getUmbral_1x()) >= 0)
			pron= RES_X2;
		else
		{
			BigDecimal dist1x= par.getPrcVisit().subtract(par.getUmbral_1x());
			BigDecimal distx2= par.getUmbral_x2().subtract(par.getPrcVisit());
			if (dist1x.compareTo(distx2) <= 0)
				pron= RES_1X;
			else
				pron= RES_X2;
		}
		par.setPronostico_1x2(pron);
	}
	
	private BigDecimal damePorcentajeValoracionVisitante(BigDecimal local_puntos, BigDecimal visit_puntos)
	{
		BigDecimal total_puntos= local_puntos.add(visit_puntos);
		return visit_puntos.divide(total_puntos, 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100.0));
	}
	
	private int convertirValoracionEnPronostico1X2(BigDecimal local_puntos, BigDecimal visit_puntos)
	{
		int ret= RES_X;
		BigDecimal total_puntos= local_puntos.add(visit_puntos);
		if (total_puntos.intValue() > 0)
		{
			BigDecimal umbral1x= getUmbralPronostico1X();
			BigDecimal umbralx2= getUmbralPronosticoX2();
			BigDecimal prcVisit= visit_puntos.divide(total_puntos, 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100.0));
			if (prcVisit.compareTo(umbral1x) < 0)
				ret= RES_1;
			else if (prcVisit.compareTo(umbralx2) > 0)
				ret= RES_2;
		}
		
//		if (Log.isLoggable(Constantes.LOG_TAG, Log.DEBUG))
//		{
//			BigDecimal cien= new BigDecimal(100);
//			Log.d(Constantes.LOG_TAG, "[pronóstico] puntos %local/%visit: "+ local_puntos.divide(total_puntos, 4, RoundingMode.HALF_UP).multiply(cien).toString() + "% / " + visit_puntos.divide(total_puntos, 4, RoundingMode.HALF_UP).multiply(cien).toString() + "%");
//			String res= signoQuinielaInt2String(ret);
//			Log.d(Constantes.LOG_TAG, "[pronóstico] Resultado del pronóstico: " + res);
//			resumen.append(local_puntos.divide(total_puntos, 4, RoundingMode.HALF_UP).multiply(cien).toString()).append("% --> ").append(res);
//			Log.d(Constantes.LOG_TAG, "[pronóstico] resumen: " + resumen.toString());
//		}
		return ret;
	}

	/*
	 * Aquí, a partir de la temporada 2014/15 el pronóstico puede tener 4 posibles valores: 0, 1, 2, M
	 * Significa el número de goles, siendo M más de 2
	 * Se hace para cada uno de los 2 equipos del partido del pleno al 15
	 */
	private int convertirValoracionEnPronostico1X2_plenoAl15(BigDecimal local_puntos, BigDecimal visit_puntos)
	{
	    local_puntos = local_puntos.setScale(0, BigDecimal.ROUND_HALF_UP);
	    visit_puntos = visit_puntos.setScale(0, BigDecimal.ROUND_HALF_UP);
		
	    if (local_puntos.equals(cero))
	    {
	    	if (visit_puntos.equals(cero)) 		return RES_PLENO15_00;
	    	else if (visit_puntos.equals(uno)) 	return RES_PLENO15_01;
	    	else if (visit_puntos.equals(dos)) 	return RES_PLENO15_02;
	    	else 								return RES_PLENO15_0M;
	    }
	    else if (local_puntos.equals(uno))
	    {
	    	if (visit_puntos.equals(cero)) 		return RES_PLENO15_10;
	    	else if (visit_puntos.equals(uno)) 	return RES_PLENO15_11;
	    	else if (visit_puntos.equals(dos)) 	return RES_PLENO15_12;
	    	else 								return RES_PLENO15_1M;
	    }
	    else if (local_puntos.equals(dos))
	    {
	    	if (visit_puntos.equals(cero)) 		return RES_PLENO15_20;
	    	else if (visit_puntos.equals(uno)) 	return RES_PLENO15_21;
	    	else if (visit_puntos.equals(dos)) 	return RES_PLENO15_22;
	    	else 								return RES_PLENO15_2M;
	    }
	    else
	    {
	    	if (visit_puntos.equals(cero)) 		return RES_PLENO15_M0;
	    	else if (visit_puntos.equals(uno)) 	return RES_PLENO15_M1;
	    	else if (visit_puntos.equals(dos)) 	return RES_PLENO15_M2;
	    	else 								return RES_PLENO15_MM;
	    }
	}
	
	/*
	 * Si p.e. valorAPonderar= 20;  sumaDeValores= 30 y peso= 60, se hace este razonamiento:
	 * Si alguno de los 2 tuviera el valor de suma (30) eso sería un 100%, y como el peso es 60, entonces obtendría 18
	 * Con el valor a Ponderar, su valor (20) es el 40% de la suma. Por lo tanto, si fuera 100 el peso, valdría 40.
	 * Como el peso es 60, su valor sería 24
	 */
	public BigDecimal ponderarValor(int valorAPonderar, int otroValor, int peso)
	{
		return ponderarValor(new BigDecimal(valorAPonderar), new BigDecimal(otroValor), peso);
	}
	public BigDecimal ponderarValor(BigDecimal valorAPonderar, BigDecimal otroValor, int peso)
	{		
		/*
		 * Aquí hay que tener en cuenta varias circunstancias particulares:
		 * 1.- Si valorAPonderar (A) y otroValor (B) son negativos, el valor ponderado de A deberá ser el que tuviera B si ambos fueran positivos.
		 * 		Por ejemplo: ponderarValor(-7, -3, 50) --> 35
		 * 					 ponderarValor(-3, -7, 50) --> 15
		 * 		Se favorecería a A cuando era menor que B (-7 < -3). En ese caso se permuta, y lo que se hará es:
		 * 		             ponderarValor(-7, -3, 50) --> 15
		 * 					 ponderarValor(-3, -7, 50) --> 35
		 * 2.- Si A es positivo y B negativo, hay que saber primero: ¿A cuántas veces es B?
		 * 		Si fuera por ejemplo A=8 y B=-2, A es 5 veces B (porque B para llegar a A haría: -2 -> 0 -> 2 -> 4 -> 6 -> 8
		 * 		Para intentar expresarlo de manera formal: B sería X y A sería 5 X. Pero ¿cuánto es X?
		 * 		La fórmula de X es:   X= peso / (((|A| + |B|) / |B|) + 1)
		 * 		Si fuera por ejemplo A=8 y B=-2, con peso 80:   X= 80/((10/2)+1) = 80/6= 13.3 ==> B= 13.3 y A= 5*13.3= 66.7
		 * 		Dicho de otra forma, el valor ponderado de A sería: peso - X
		 * 2.- Si A es negativo y B positivo: ponderarValor(Aneg,Bpos,peso)= peso-ponderar(Bpos,Aneg,peso) 
		 */
		BigDecimal ret= null;
		if (valorAPonderar.compareTo(cero) < 0   &&   otroValor.compareTo(cero) < 0)
			ret= ponderarValor(otroValor.abs(), valorAPonderar.abs(), peso);
		else if (valorAPonderar.compareTo(cero) < 0)
			ret= new BigDecimal(peso).subtract(ponderarValor(otroValor, valorAPonderar, peso));
		else if (otroValor.compareTo(cero) < 0)
		{
			BigDecimal p= new BigDecimal(peso);
			BigDecimal divisor= (valorAPonderar.abs().add(otroValor.abs())).divide(otroValor.abs(), 4, RoundingMode.HALF_UP).add(new BigDecimal(1));
			BigDecimal x= p.divide(divisor, 4, RoundingMode.HALF_UP);
			ret= p.subtract(x);
		}
		else
			ret= valorAPonderar.divide(valorAPonderar.add(otroValor), 4, RoundingMode.HALF_UP).multiply(new BigDecimal(peso));

		return ret;
	}

	public BigDecimal ponderarValorPorTramos(int valorAPonderar, int peso, int min, int max)
	{
		return ponderarValorPorTramos(new BigDecimal(valorAPonderar), peso, min, max);
	}
	/*
	 * Aquí se ponderará sin seguir un criterio proporcional (como hace "ponderarValor(...)").
	 * El criterio será por diferencia de valores (entre "valorAPonderar" y "otroValor").
	 * Esa diferencia se valorará por tramos, de esta forma:
	 * Sea A=valorAPonderar,  B=otroValor,    dif=A-B
	 * 	- siempre debería ser  dif >= min  y   dif <= max
	 * 	- si min < dif < max, se devolverá el valor que indique la proporción de dif entre min y max
	 * 		ejemplos: 	ponderarValorPorTramos(0, 2, 80, -3, 3) : dif=-2 ==> se devuelve 80*1/6 
	 * 					ponderarValorPorTramos(1, 2, 80, -3, 3) : dif=-1 ==> se devuelve 80*2/6
	 * 					ponderarValorPorTramos(1, 0, 80, -3, 3) : dif= 1 ==> se devuelve 80*4/6
	 * 		Formulado matemáticamente: retorno=abs( (valorAPonderar-min) / (max-min))*peso
	 */
	public BigDecimal ponderarValorPorTramos(BigDecimal valorAPonderar, int peso, int min, int max)
	{
		BigDecimal ret= null;
		if (valorAPonderar.compareTo(new BigDecimal(min)) <= 0)
			ret= cero;
		else if (valorAPonderar.compareTo(new BigDecimal(max)) >= 0)
			ret= new BigDecimal(peso);
		else
		{
			BigDecimal numerador= valorAPonderar.subtract(new BigDecimal(min));
			BigDecimal denominador= new BigDecimal(max).subtract(new BigDecimal(min));
			ret= numerador.divide(denominador, 4, RoundingMode.HALF_UP).abs().multiply(new BigDecimal(peso));
		}
		return ret;
	}
		
	private int getCalidadIntrinseca(String idEq, EquipoDao eqDao, int idTemporada, int idUsuario, Context ctx)
	{
		Equipo eq= eqDao.getEquipo(idEq, idTemporada, idUsuario);
		if (eq == null)  
		{
			// algo que no debería pasar nunca, pero como aparecen NullPointerExcepcion en "eq.getCalidadIntrinseca()" intento ver por qué
			AplicacionOp.mandarInfoError("QuinielaOp.getCalidadIntriseca(): objeto equipo nulo. Datos: idEq:"+idEq+", idTemp:"+idTemporada+", idUsu:"+idUsuario, ctx) ;
			return 50; // un valor medio cualquiera
		}
		else
			return eq.getCalidadIntrinseca();
	}
	
	public void grabarQuiniela(Quiniela q, int idUsuario, Context ctx)
	{
    	SQLiteDatabase con= Basedatos.getConexion(ctx, Basedatos.ESCRITURA);    	
    	new QuinielaDao(con).grabarQuiniela(q, idUsuario);
        con.close();
	}
	
	private Quiniela getQuiniela(int temporada, int _jornada, Context ctx) 
	{
		Quiniela ret= null;
		
    	SQLiteDatabase con= Basedatos.getConexion(ctx, Basedatos.LECTURA);
    	QuinielaDao qDao= new QuinielaDao(con);
    	Quiniela qBBDD= qDao.getDefinicionQuinielaJornada(temporada, _jornada);
        con.close();
        
        if (qBBDD != null)   // se grabó previamente en BB.DD y ahora se coge
        	ret= qBBDD;
        else
        {
			try
			{
			    String fich= "temp"+temporada+"_quin_jor"+_jornada+".json";
				String datosJornada = CargaFichero.getContenidoFicheroQuiniela(fich);		
				if (datosJornada == null)
					return null;
					
		    	JSONObject jObject = new JSONObject(datosJornada);
				JSONObject quiniela = jObject.getJSONObject("quiniela");
				int temp = quiniela.getInt("temporada");
				int jornadaNum = quiniela.getInt("jornada");
				/* no leo el campo 'corregida' porque debería ser siempre  Quiniela.QUINIELA_CORREGIDA_NO */
				if (temp!=temporada || jornadaNum!=_jornada)
				{
					Log.e(Constantes.LOG_TAG, "El fichero de quiniela de la temporada "+temporada+", jornada "+_jornada+" indica en sus datos que la temporada es "+temp+" y la jornada es "+jornadaNum);
					return null;
				}
		
				Quiniela qui= new Quiniela(temp, jornadaNum);
				JSONArray partidos = quiniela.getJSONArray("partidos");
				for (int k= 0; k< partidos.length(); k++) 
				{
					Partido par= new Partido();
					JSONArray partido= partidos.getJSONArray(k);  //  	[ 'sev', 'gij' ]
					par.setIdEquipoLocal(partido.getString(0));
					par.setIdEquipoVisit(partido.getString(1));
					par.setPartidoNum(k+1);
					qui.annadirPartido(par);
				}
				ret= qui;
				
		    	con= Basedatos.getConexion(ctx, Basedatos.ESCRITURA);
		    	qDao= new QuinielaDao(con);
		    	qDao.grabarDefinicionQuinielaJornada(qui);
		        con.close();
		        
		        Notificaciones.notificarDescarga(ctx, "quiniela: "+ _jornada);				
			}
			catch(JSONException e)
			{
				Log.e(Constantes.LOG_TAG, "Error leyendo fichero de quiniela de la jornada "+_jornada , e);
				return null;
			}
        }
        return ret;
	}   

    public static int getUltimaJornadaQuinielaCorregidaCargada(SQLiteDatabase con, int temporada)
    {
    	// se mirará en BB.DD.
    	int ultima = 0;
        QuinielaDao qDao= new QuinielaDao(con);
        ultima= qDao.getUltimaJornadaQuinielaCorregidaCargada(temporada);
    	return ultima;
    }
    
    public static boolean hayDatosQueActualizar(int temporada, SQLiteDatabase con, Context ctx)
    {
    	/* 
    	 * se mirará en BB.DD. si en la tabla 'quinielas_disponibles_server' hay algún registro con disponible_correccion=0
    	 * 	y que además la fecha de la quiniela sea superior a hoy
    	 */
    	
    	boolean ret= false;
        QuinielaDao quinDao= new QuinielaDao(con);
        ret= quinDao.hayDefinicionesDeQuinielasSinCorreccionDisponible(temporada, Preferencias.getUsuarioId(ctx), Utils.hoy());
    	return ret;
    }

    public static boolean actualizarDatosEnSeguimientoTiempoReal(Context ctx)
    {
    	SQLiteDatabase con= Basedatos.getConexion(ctx, Basedatos.ESCRITURA);
    	boolean ret= actualizarDatos(ctx, con, null, -1, -1);
        con.close();
        return ret;
    }
    
    
    /*
     * sólo carga/actualiza las quinielas corregidas, no las disponibles para una jornada.
     */
    public static boolean actualizarDatos(Context ctx, SQLiteDatabase con, TareaSegundoPlano task, float prcMin, float prcMax)
    {
    	if (task != null)
    		task.publicarProgreso(prcMin);
    	/*
    	 * Proceso:
    	 * 1.- Coger en BB.DD. aquellas definiciones de quinielas (tabla 'quinielas_disponibles_server') con disponible_correccion= Quiniela.QUINIELA_CORREGIDA_NO
    	 * 		(eso no quiere decir que en el servidor ya está disponible la corrección, pero lo que sí que se hará es intentarlo)
    	 * 2.- Para cada elem. de paso 1: intentar descargarla de internet.
    	 * 		Si el fichero .json del servidor con esta definición de quiniela tiene el campo 'corregida' con valor Quiniela.QUINIELA_CORREGIDA_NO,
    	 * 			indicará que aún sigue sin estar su corrección. Entonces, no se hará nada.
    	 * 		Si este fichero tiene 'corregida' con valor Quiniela.QUINIELA_CORREGIDA_SI, se coge y se graba en BB.DD.  
    	 * 		Si este fichero tiene 'corregida' con valor Quiniela.QUINIELA_CORREGIDA_PARCIAL, se coge pero no se graba en BB.DD.
    	 */
    	StringBuffer ok= null;
    	StringBuffer error= null;

        QuinielaDao quinDao= new QuinielaDao(con);
        EquipoDao eqDao= new EquipoDao(con);
        
        /*  (paso 1)  */
        ArrayList<Integer> fechas= quinDao.getFechasDeDefinicionQuinielasSinCorreccionDisponible(Preferencias.getTemporadaActual(ctx), Preferencias.getUsuarioId(ctx));
        
        /*  (paso 2)  */
        int iNumFechas= fechas.size();
        int contadorFechas= 0;
        for(Integer _fecha : fechas)
    	{
        	contadorFechas++;
        	int fecha= _fecha.intValue();
    		Quiniela quinJor= getQuinielaCorregida(Preferencias.getTemporadaActual(ctx), fecha, con);

    		if ( quinJor != null  && EquipoOp.existenEquipos(quinJor.getTemporada(), Equipo.DIVISION_1_Y_2, quinJor.getPartidos(), ctx, eqDao))
    		{    		
	    		if (quinJor.getCorregida()==Quiniela.QUINIELA_CORREGIDA_SI  &&  !quinDao.estaGrabadaQuinielaCorregida(quinJor))
	    			quinDao.grabarQuinielaCorregida(quinJor);  // no se graban las quinielas corregidas parcialmente
	    		
	    		actualizarAciertosEnQuinielasHechasSobreEstaCorregida(fecha, quinJor, ctx, con);
	    		
        		if (ok == null)
        			ok= new StringBuffer().append(fecha);
        		else
        			ok.append(", ").append(fecha);
    		}
        	else
        	{
        		if (error == null)
        			error= new StringBuffer().append(fecha);
        		else
        			error.append(", ").append(fecha);
        	}
    		
    		if (task != null)
        		task.publicarProgresoEnRango(prcMin, prcMax, (float) (contadorFechas*100.0/iNumFechas));
    	}
		if (Log.isLoggable(Constantes.LOG_TAG, Log.INFO))
			Log.i(Constantes.LOG_TAG, (ok==null?"":"Se han cargado las quinielas corregidas de jornadas: "+ok.toString()) + 
									  (error==null?"":". No se han podido cargar (quizá aún no estén disponibles) las quinielas corregidas de jornadas: "+error.toString()));
		if (ok != null)
			Notificaciones.notificarDescarga(ctx, "quinielas: "+ok.toString());

		if (task != null)
    		task.publicarProgreso(prcMax);
		
    	return true;  // no quiero que el usuario se entere de posibles errores
    }

    public static Quiniela getQuinielaParcialmenteCorregida(Context ctx, int temporada, int jornada)
    {
		return getQuinielaParcialmenteCorregida(Preferencias.getTemporadaActual(ctx), jornada);
    }
    
    /*
     * Cogerá todas aquellas quinielas que el usuario haya hecho para esa fecha/jornada,
     * y actualizará su campo 'aciertos'
     */
    public static void actualizarAciertosEnQuinielasHechasSobreEstaCorregida(int fecha, Quiniela quinCorregida, Context ctx, SQLiteDatabase con)
    {
    	QuinielaOp qOp= new QuinielaOp();
    	int temporada= Preferencias.getTemporadaActual(ctx);
    	int idUsuario= Preferencias.getUsuarioId(ctx);
    	ArrayList<Quiniela> quinielasSinCorregir= qOp.getQuinielasHechasJornada(temporada, fecha, idUsuario, con);
    	for (Quiniela q: quinielasSinCorregir)
    	{
	    	int aciertos= QuinielaOp.obtenerAciertosQuiniela(q, quinCorregida);
	    	QuinielaOp.actualizarAciertos(q.getId(), aciertos, con);
	    	q.setAciertos(aciertos);
	    	
        	/* Si la quiniela está premiada, lo notificaremos y mandamos una notificación al usuarios */
        	if (aciertos >= Constantes.ACIERTOS_UMBRAL_PREMIO)
        	{
    	    	int dobles= QuinielaOp.getNumDobles(q);
    	    	int triples= QuinielaOp.getNumTriples(q);
        		Notificaciones.notificarPremio(ctx, q.getJornada(), q.getAciertos(), dobles, triples);
      		    NotificacionDatos notifDatos= new NotificacionDatos();
     		    notifDatos.setIdQuiniela(q.getId());
     		    notifDatos.setAciertos(q.getAciertos());
     		    notifDatos.setTextoTitulo("Belfy1X2 ¡quiniela premiada!");
     		    notifDatos.setTextoContenido(""+q.getAciertos() + " aciertos en ["+q.getJornada()+"] "+q.getNombre()+" ...");			     		   
     		    Intent intent = new Intent(ctx, QuinielaVerPronosticoNotif.class);
     		    intent.putExtra(Constantes.ID_QUINIELA, q.getId());
     		    notifDatos.setIntent(intent);
     		    Mensajes.notificacion(ctx, notifDatos);			        		
        	}
    	}
    }
    
    /*
     * devolverá null en cualquiera de estos casos:
     * 	- no existe en el servidor su fichero de datos
     * 	- dentro del fichero de datos, está el dato 'corregida' con valor 0
     */
	private static Quiniela getQuinielaCorregida(int temporada, int _jornada, SQLiteDatabase con)
	{
		Quiniela ret= null;
		
    	QuinielaDao qDao= new QuinielaDao(con);
    	Quiniela quinBBDD= qDao.getDefinicionQuinielaCorregidaJornada(temporada, _jornada);
        
        if (quinBBDD != null)   // se grabó previamente en BB.DD y ahora se coge
        	ret= quinBBDD;
        else
        {
			try
			{
			    String fich= "temp"+temporada+"_quin_jor"+_jornada+".json";
				String datosJornada = CargaFichero.getContenidoFicheroQuinielaResultados(fich);		
				if (datosJornada == null)
					return null;

				Quiniela quin= fromStringJson2Quiniela(datosJornada);
				
				if (quin.getCorregida() != Quiniela.QUINIELA_CORREGIDA_SI)
					return null;
				else if (quin.getTemporada()!=temporada || quin.getJornada()!=_jornada)
				{
					Log.e(Constantes.LOG_TAG, "El fichero de quiniela de la temporada "+temporada+", jornada "+_jornada+" indica en sus datos que la temporada es "+quin.getTemporada()+" y la jornada es "+quin.getJornada());
					return null;
				}
		
				ret= quin;
			}
			catch(JSONException e)
			{
				Log.e(Constantes.LOG_TAG, "No se cargó quiniela de la jornada "+_jornada , e);
				return null;
			}
        }
        return ret;
	}   

    /*
     * devolverá null si y sólo si:
     * 	- no existe en el servidor su fichero de datos
     */
	private static Quiniela getQuinielaParcialmenteCorregida(int temporada, int _jornada)
	{
		Quiniela ret= null;
		
		try
		{
		    String fich= "temp"+temporada+"_quin_jor"+_jornada+".json";
			String datosJornada = CargaFichero.getContenidoFicheroQuinielaResultados(fich);		
			if (datosJornada == null)
				return null;
				
			Quiniela quin= fromStringJson2Quiniela(datosJornada);
			
			if (quin.getCorregida() != Quiniela.QUINIELA_CORREGIDA_PARCIAL)
				return null;
			else if (quin.getTemporada()!=temporada || quin.getJornada()!=_jornada)
			{
				Log.e(Constantes.LOG_TAG, "El fichero de quiniela de la temporada "+temporada+", jornada "+_jornada+" indica en sus datos que la temporada es "+quin.getTemporada()+" y la jornada es "+quin.getJornada());
				return null;
			}
	
			ret= quin;						
		}
		catch(JSONException e)
		{
			Log.e(Constantes.LOG_TAG, "No se cargó quiniela de la jornada "+_jornada , e);
			return null;
		}

        return ret;
	}   

	public static boolean hayAlgunaQuinielaDeEstaSemanaSinCorregir(Context ctx)
	{
 		String notifQuin= Notificaciones.getNotificacion(Constantes.NOTIFICACION_QUINIELA_PROXIMA);
 		int iJornadaQuinielaEstaSemana= new Integer(notifQuin.trim()).intValue();
 		int temp= Preferencias.getTemporadaActual(ctx);
 		int idUsu= Preferencias.getUsuarioId(ctx);
 		
    	SQLiteDatabase con= Basedatos.getConexion(ctx, Basedatos.LECTURA);
    	QuinielaDao qDao= new QuinielaDao(con);
    	boolean ret= qDao.hayAlgunaQuinielaDeEstaSemanaSinCorregir(temp, iJornadaQuinielaEstaSemana, idUsu);
        con.close();
        
        return ret;
	}
		
	public ArrayList<Quiniela> getQuinielasDeEstaSemanaSinCorregir(Context ctx, int temporada)
	{
 		String notifQuin= Notificaciones.getNotificacion(Constantes.NOTIFICACION_QUINIELA_PROXIMA);
 		int iJornadaQuinielaEstaSemana= new Integer(notifQuin.trim()).intValue();
 		int idUsu= Preferencias.getUsuarioId(ctx);
 		
    	SQLiteDatabase con= Basedatos.getConexion(ctx, Basedatos.LECTURA);
    	QuinielaDao qDao= new QuinielaDao(con);
    	ArrayList<Quiniela> ret= qDao.getQuinielasDeEstaSemanaSinCorregir(temporada, iJornadaQuinielaEstaSemana, idUsu);
        con.close();
        
        return ret;
	}
	
	private static Quiniela fromStringJson2Quiniela(String contenidoFicheroJson) throws JSONException
	{
		Quiniela quin= null;
    	JSONObject jObject = new JSONObject(contenidoFicheroJson);
		JSONObject quiniela = jObject.getJSONObject("quiniela");
		int temp = quiniela.getInt("temporada");
		int jornadaNum = quiniela.getInt("jornada");
		int corregida= quiniela.getInt("corregida");
		
		quin= new Quiniela(temp, jornadaNum);
		quin.setCorregida(corregida);
		JSONArray partidos = quiniela.getJSONArray("partidos");
		for (int k= 0; k< partidos.length(); k++) 
		{
			Partido par= new Partido();
			JSONArray partido= partidos.getJSONArray(k);  //  	[ 'sev', 'gij', 'x' ]
			par.setIdEquipoLocal(partido.getString(0));
			par.setIdEquipoVisit(partido.getString(1));
			if (partido.getString(2).length() > 0)   // en el caso de quiniela corregida parcialmente, y este partido aún no se ha jugado
				par.setResultadoQuiniela(signoQuinielaString2Int(partido.getString(2)));
			else
				par.setResultadoQuiniela(QuinielaOp.RES_NO_DISPONIBLE);
			par.setPartidoNum(k+1);
			quin.annadirPartido(par);
		}
		return quin;								
	}
	
	public boolean existeQuinielaConNombre(String nombreQuin, int usuId, int jornada, Context ctx)
	{
    	boolean ret= false;

    	SQLiteDatabase con= Basedatos.getConexion(ctx, Basedatos.LECTURA);
        ret= new QuinielaDao(con).existeQuinielaConNombre(nombreQuin, usuId, jornada);
        con.close();
    	return ret;		
	}
	
	public static String signoQuinielaInt2String(int par)
	{
	   String res= null;
	   if (par == RES_1)
		   res= "1";
	   else if (par == RES_2)
		   res= "2";
	   else if (par == RES_X)
		   res= "x";
	   else if (par == RES_1X)
		   res= "1x";
	   else if (par == RES_X2)
		   res= "x2";
	   else if (par == RES_12)
		   res= "12";
	   else if (par == RES_1X2)
		   res= "1x2";
	   else if (par == RES_PLENO15_00)
		   res= "p00";
	   else if (par == RES_PLENO15_01)
		   res= "p01";
	   else if (par == RES_PLENO15_02)
		   res= "p02";
	   else if (par == RES_PLENO15_0M)
		   res= "p0m";
	   else if (par == RES_PLENO15_10)
		   res= "p10";
	   else if (par == RES_PLENO15_11)
		   res= "p11";
	   else if (par == RES_PLENO15_12)
		   res= "p12";
	   else if (par == RES_PLENO15_1M)
		   res= "p1m";
	   else if (par == RES_PLENO15_20)
		   res= "p20";
	   else if (par == RES_PLENO15_21)
		   res= "p21";
	   else if (par == RES_PLENO15_22)
		   res= "p22";
	   else if (par == RES_PLENO15_2M)
		   res= "p2m";
	   else if (par == RES_PLENO15_M0)
		   res= "pm0";
	   else if (par == RES_PLENO15_M1)
		   res= "pm1";
	   else if (par == RES_PLENO15_M2)
		   res= "pm2";
	   else if (par == RES_PLENO15_MM)
		   res= "pmm";
	   else
		   res= "-";
	   return res;
	}

	public static int signoQuinielaString2Int(String par)
	{
	   int res= RES_NO_DISPONIBLE;
	   if (par.equals("1"))
		   res= RES_1;
	   else if (par.equals("2"))
		   res= RES_2;
	   else if (par.equalsIgnoreCase("x"))		   
		   res= RES_X;
	   else if (par.equalsIgnoreCase("1x"))		   
		   res= RES_1X;
	   else if (par.equalsIgnoreCase("12"))		   
		   res= RES_12;
	   else if (par.equalsIgnoreCase("x2"))		   
		   res= RES_X2;
	   else if (par.equalsIgnoreCase("1x2"))		   
		   res= RES_1X2;
	   else if (par.equalsIgnoreCase("p00"))		   
		   res= RES_PLENO15_00;
	   else if (par.equalsIgnoreCase("p01"))		   
		   res= RES_PLENO15_01;
	   else if (par.equalsIgnoreCase("p02"))		   
		   res= RES_PLENO15_02;
	   else if (par.equalsIgnoreCase("p0m"))		   
		   res= RES_PLENO15_0M;
	   else if (par.equalsIgnoreCase("p10"))		   
		   res= RES_PLENO15_10;
	   else if (par.equalsIgnoreCase("p11"))		   
		   res= RES_PLENO15_11;
	   else if (par.equalsIgnoreCase("p12"))		   
		   res= RES_PLENO15_12;
	   else if (par.equalsIgnoreCase("p1m"))		   
		   res= RES_PLENO15_1M;
	   else if (par.equalsIgnoreCase("p20"))		   
		   res= RES_PLENO15_20;
	   else if (par.equalsIgnoreCase("p21"))		   
		   res= RES_PLENO15_21;
	   else if (par.equalsIgnoreCase("p22"))		   
		   res= RES_PLENO15_22;
	   else if (par.equalsIgnoreCase("p2m"))		   
		   res= RES_PLENO15_2M;
	   else if (par.equalsIgnoreCase("pm0"))		   
		   res= RES_PLENO15_M0;
	   else if (par.equalsIgnoreCase("pm1"))		   
		   res= RES_PLENO15_M1;
	   else if (par.equalsIgnoreCase("pm2"))		   
		   res= RES_PLENO15_M2;
	   else if (par.equalsIgnoreCase("pmm"))		   
		   res= RES_PLENO15_MM;
	   return res;
	}
}
