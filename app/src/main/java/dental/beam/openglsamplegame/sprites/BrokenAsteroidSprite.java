package dental.beam.openglsamplegame.sprites;

import dental.beam.openglsamplegame.R;
import dental.beam.openglsamplegame.helpers.RandomHelper;

public class BrokenAsteroidSprite extends AsteroidSprite
{
	private static final float SCALE_DELTA = 0.0005f;

	public void init (float ratio, float position[], float vector[], float scale)
	{
		_textureDataHandle = loadGLTexture(R.drawable.asteroid);
		_ratio = ratio;
		_currentScale[0] = scale;
		_currentScale[1] = _currentScale[0] / IMAGE_RATIO;
		_currentPos = position;
		_vector = vector;
		_rotationZ = RandomHelper.randomFloatBetween(-90, 90);
		_rotationDelta = RandomHelper.randomFloatBetween(-2, 2);
		_alive = true;
	}

	@Override
	public boolean update ()
	{
		// broken asteroids move down and scale down to 0 to make them look like they are moving away
		_currentScale[0] -= SCALE_DELTA;
		_currentScale[1] -= SCALE_DELTA;
		if (_currentScale[0] <= 0 || _currentScale[1] <= 0)
		{
			return false;
		}
		else
		{
			return super.update();
		}
	}
}
