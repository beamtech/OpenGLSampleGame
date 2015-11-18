package dental.beam.openglsamplegame;

import android.app.Application;

import timber.log.Timber;

public class GameApplication extends Application
{
	@Override
	public void onCreate ()
	{
		super.onCreate();

		if (BuildConfig.DEBUG)
		{
			Timber.plant(new Timber.DebugTree());
		}
	}
}
