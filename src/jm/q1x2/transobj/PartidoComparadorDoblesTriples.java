package jm.q1x2.transobj;

import java.math.BigDecimal;

public class PartidoComparadorDoblesTriples implements Comparable<PartidoComparadorDoblesTriples> 
{
	public static int COMPARADOR_DOBLES= 1;
	public static int COMPARADOR_TRIPLES= 2;
	
	private static BigDecimal _50= new BigDecimal(50);
	
	private int ordenEnQuiniela;  // se us
	private BigDecimal prcVisit;  //porcentaje del visitante en la puntuación teniendo en cuenta todos los criterios
	private BigDecimal umbral_1x;
	private BigDecimal umbral_x2;
	private int pronostico_1x2;
	private int tipoComparador;

	
	public int compareTo(PartidoComparadorDoblesTriples another) 
	{
		if (tipoComparador == COMPARADOR_TRIPLES)
			return compareTriplesTo(another);
		else if (tipoComparador == COMPARADOR_DOBLES)
			return compareDoblesTo(another);
		else
			return 0;
	}

	private int compareTriplesTo(PartidoComparadorDoblesTriples otro)
	{
		/*
		 * algoritmo:
		 * Los partidos elegidos en primer lugar serán aquellos con un prcVisit más próximo al 50
		 */		
		BigDecimal yo_dist50= this.getPrcVisit().subtract(_50).abs();
		BigDecimal otro_dist50= otro.getPrcVisit().subtract(_50).abs();
		
		return yo_dist50.compareTo(otro_dist50) ;
	}

	private int compareDoblesTo(PartidoComparadorDoblesTriples otro)
	{
		/*
		 * Sea  V   : porcentaje de puntuación del visitante. Si es < umbral_1X sería un 1; si es > umbral_X2 sería un 2; resto sería X.
		 * 		U_1X: umbral entre pronóstico 1 y X
		 * 		U_X2: umbral entre pronóstico X y 2
		 * algoritmo:
		 * Los partidos elegidos serán aquellos con Y ordenado de menor a mayor, siendo Y:
		 * 			Y= menor(A,B), siendo  A: distancia entre V y U_1X,  B: distancia entre V y U_X2
		 * 			(es decir, aquel partido cuyo V esté más próximo a un umbral cualquiera)
		 */		
		BigDecimal umbral_1x= this.getUmbral_1x();  // los 2 objetos tienen el mismo valor
		BigDecimal umbral_x2= this.getUmbral_x2();  // los 2 objetos tienen el mismo valor
		BigDecimal yo_V= this.getPrcVisit();
		BigDecimal otro_V= otro.getPrcVisit();
	
		BigDecimal yo_A= yo_V.subtract(umbral_1x).abs();
		BigDecimal yo_B= yo_V.subtract(umbral_x2).abs();
		BigDecimal yo_Y= yo_A.compareTo(yo_B) < 0 ? yo_A : yo_B;

		BigDecimal otro_A= otro_V.subtract(umbral_1x).abs();
		BigDecimal otro_B= otro_V.subtract(umbral_x2).abs();
		BigDecimal otro_Y= otro_A.compareTo(otro_B) < 0 ? otro_A : otro_B;
		
		return yo_Y.compareTo(otro_Y) ;
	}
	
	
	
	public BigDecimal getPrcVisit() {
		return prcVisit;
	}
	public void setPrcVisit(BigDecimal prcVisit) {
		this.prcVisit = prcVisit;
	}
	public BigDecimal getUmbral_1x() {
		return umbral_1x;
	}
	public void setUmbral_1x(BigDecimal umbral_1x) {
		this.umbral_1x = umbral_1x;
	}
	public BigDecimal getUmbral_x2() {
		return umbral_x2;
	}
	public void setUmbral_x2(BigDecimal umbral_x2) {
		this.umbral_x2 = umbral_x2;
	}




	public int getTipoComparador() {
		return tipoComparador;
	}




	public void setTipoComparador(int tipoComparador) {
		this.tipoComparador = tipoComparador;
	}




	public int getOrdenEnQuiniela() {
		return ordenEnQuiniela;
	}




	public void setOrdenEnQuiniela(int ordenEnQuiniela) {
		this.ordenEnQuiniela = ordenEnQuiniela;
	}




	public int getPronostico_1x2() {
		return pronostico_1x2;
	}




	public void setPronostico_1x2(int pronostico_1x2) {
		this.pronostico_1x2 = pronostico_1x2;
	}
	
	
}
