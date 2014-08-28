package jm.q1x2;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;

@ReportsCrashes(formUri = "http://www.bugsense.com/api/acra?api_key=92d0fa07", formKey="") 
public class Belfy1x2App extends Application 
{
  @Override
  public void onCreate() 
  {
    // The following line triggers the initialization of ACRA
    super.onCreate();
    //[jm]ACRA.init(this);
  }
}