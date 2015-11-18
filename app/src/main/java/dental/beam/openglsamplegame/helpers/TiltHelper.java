package dental.beam.openglsamplegame.helpers;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;

public abstract class TiltHelper
{
	public static TiltHelper getInstance (Context context)
	{
		SensorManager manager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		Sensor accelerometer = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

		if (accelerometer != null)
		{
			return new TiltHelperAccel(manager, accelerometer);
		}
		else
		{
			return null;
		}
	}

	/**
	 * @return Rotation of the device around the Y-axis in degrees
	 *
	 * | / - \ |
	 * -90  0  +90
	 */
	public abstract float getTilt ();
	public abstract void destroy ();
}
