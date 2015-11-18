package dental.beam.openglsamplegame.helpers;

public class RandomHelper
{
	public static float randomFloatBetween (float lower, float upper)
	{
		if (lower > upper)
		{
			float tmp = lower;
			lower = upper;
			upper = tmp;
		}
		float diff = upper - lower;
		return lower + ((float) Math.random() * diff);
	}
}
