package dental.beam.openglsamplegame.sprites;

import dental.beam.openglsamplegame.R;
import dental.beam.openglsamplegame.helpers.RandomHelper;

public class ChickenSprite extends MovingSprite
{
	protected static final float IMAGE_RATIO = 204f / 328f;
	private static float SCALE = 0.07f;

	public ChickenSprite ()
	{
		_drawableResourceId = R.drawable.chicken;
	}

	public void init ()
	{
		_textureDataHandle = loadGLTexture(_drawableResourceId);
		_currentScale[0] = SCALE;
		_currentScale[1] = _currentScale[0] / IMAGE_RATIO;
		_currentPos[0] = RandomHelper.randomFloatBetween(-1, 1);
		_currentPos[1] = 0;
		_currentPos[2] = 0.2f;

		_vector[0] = RandomHelper.randomFloatBetween(-0.05f, 0.05f);
		_alive = true;
	}

	public void setRatio (float ratio)
	{
		super.setRatio(ratio);

		// position at bottom of screen
		_currentPos[1] = -_ratio + 2 * _currentScale[1];
	}

	@Override
	public boolean update ()
	{
		super.update();

		// if hit wall move back opposite direction
		if (_currentPos[0] < -1)
		{
			_vector[0] = -_vector[0];
		}
		else if (_currentPos[0] > 1)
		{
			_vector[0] = -_vector[0];
		}

		return true;
	}
}
