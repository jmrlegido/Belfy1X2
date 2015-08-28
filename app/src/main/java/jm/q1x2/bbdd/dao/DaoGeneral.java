package jm.q1x2.bbdd.dao;

import android.database.sqlite.SQLiteDatabase;

public class DaoGeneral 
{
	protected SQLiteDatabase con= null;
	
	public DaoGeneral(SQLiteDatabase _con)
	{
		con= _con;
	}
}
