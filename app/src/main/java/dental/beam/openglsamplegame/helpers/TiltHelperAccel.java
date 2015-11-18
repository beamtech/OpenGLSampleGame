package dental.beam.openglsamplegame.helpers;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import timber.log.Timber;

public class TiltHelperAccel extends TiltHelper implements SensorEventListener
{
	private static final float TILT_MAX = 90.0f;

	private final SensorManager _sensorManager;
	private final Sensor _accelerometer;
	private float[] _accel;

	protected TiltHelperAccel (SensorManager sensorManager, Sensor accelerometer)
	{
		_sensorManager = sensorManager;
		_accelerometer = accelerometer;

		_sensorManager.registerListener(this, _accelerometer, SensorManager.SENSOR_DELAY_GAME);
	}

	@Override
	public void onSensorChanged (SensorEvent event)
	{
		if (event.values == null)
		{
			Timber.w("event.values is null");
			return;
		}

		int sensorType = event.sensor.getType();
		switch (sensorType)
		{
			case Sensor.TYPE_ACCELEROMETER:
				_accel = event.values;
				break;
			default:
				Timber.w("Unknown sensor type %d", sensorType);
				return;
		}
	}

	@Override
	public void onAccuracyChanged (Sensor sensor, int accuracy)
	{
		// Not really sure what we should be doing here, so for the sake of efficiency, we'll do nothing!
	}

	public float getTilt ()
	{
		if (_accel == null)
		{
			Timber.w("_accel is null");
			return 0;
		}

		// We're going to assume that the user isn't waving their phone around like a maniac so the
		// only force acting on it should be gravity. This means that the sum of the absolute values
		// of all axes should be roughly equal to 9.8 so we should be able to see how much fo that
		// is taken up by the y axis. What ever is left is available for the x/z tilt we're interested in.
		// Then it just becomes a simple ratio to see how much of the space we have left is taken up
		// by the pull of gravity on the x axis.
		float availableRange = _accel[1] - _accelerometer.getMaximumRange();
		float amountOfTile = _accel[0] / availableRange;
		return amountOfTile * TILT_MAX;
	}

	public void destroy ()
	{
		_sensorManager.unregisterListener(this);
	}
}
