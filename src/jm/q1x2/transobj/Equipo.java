package jm.q1x2.transobj;

public class Equipo 
{
	public static int DIVISION_1 = 1;
	public static int DIVISION_2 = 2;
	public static int DIVISION_1_Y_2 = 3;
	
	private String id;
	private String nombre;
	private int division;
	private int calidadIntrinseca;
	
	public static int ORDEN_NOMBRE= 1;
	public static int ORDEN_CALIDAD_INTRINSECA= 2;
	public static int ORDEN_NINGUNO= -1;
	
	public Equipo(String _id)
	{
		id= _id;
	}
	public Equipo(String _id, String _nombre)
	{
		id= _id;
		nombre= _nombre;
	}
	
	public String getId() 
	{
		return id;
	}
	public void setId(String codigo) 
	{
		this.id = codigo;
	}
	
	public String getNombre() 
	{
		return nombre;
	}
	public void setNombre(String nombre) 
	{
		this.nombre = nombre;
	}

	public int getDivision() 
	{
		return division;
	}
	public void setDivision(int div) 
	{
		this.division= div;
	}

	public int getCalidadIntrinseca() 
	{
		return calidadIntrinseca;
	}
	public void setCalidadIntrinseca(int ci) 
	{
		this.calidadIntrinseca= ci;
	}
	
	@Override
	public String toString() 
	{
		return nombre;
	}
	
}
