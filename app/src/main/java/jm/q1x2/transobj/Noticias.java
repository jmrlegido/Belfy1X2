package jm.q1x2.transobj;

import java.util.ArrayList;

public class Noticias 
{
	private String ultima;
	private ArrayList<Noticia> noticias;
	
	public Noticias()
	{
		noticias= new ArrayList<Noticia>();
	}
	
	public void anadirNoticia(Noticia not)
	{
		noticias.add(not);
	}
	
	public String getUltima() {
		return ultima;
	}
	public void setUltima(String ultima) {
		this.ultima = ultima;
	}
	public ArrayList<Noticia> getNoticias() {
		return noticias;
	}
	public void setNoticias(ArrayList<Noticia> noticias) {
		this.noticias = noticias;
	}
	
	
}
