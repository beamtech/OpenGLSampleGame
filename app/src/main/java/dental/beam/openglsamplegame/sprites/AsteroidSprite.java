package dental.beam.openglsamplegame.sprites;

import dental.beam.openglsamplegame.R;
import dental.beam.openglsamplegame.helpers.RandomHelper;

public class AsteroidSprite extends MovingSprite
{
	public static final float MAX_SCALE = 0.25f;
	public static final float MIN_SCALE = 0.04f;
	protected static final float IMAGE_RATIO = 408f / 384f;

	public float _rotationDelta;

	public AsteroidSprite ()
	{
		_drawableResourceId = R.drawable.asteroid;
	}

	public void initRandom ()
	{
		_textureDataHandle = loadGLTexture(_drawableResourceId);
		_currentScale[0] = RandomHelper.randomFloatBetween(MIN_SCALE, MAX_SCALE);
		_currentScale[1] = _currentScale[0] / IMAGE_RATIO;
		_currentPos[0] = RandomHelper.randomFloatBetween(-1, 1);
		_currentPos[1] = 0;
		_currentPos[2] = 0.1f;

		_vector[0] = RandomHelper.randomFloatBetween(-0.002f, 0.002f);
		_vector[1] = RandomHelper.randomFloatBetween(-0.03f, -0.02f);
		_rotationZ = RandomHelper.randomFloatBetween(-90, 90);
		_rotationDelta = RandomHelper.randomFloatBetween(-2, 2);
		_alive = true;
	}

	public void initIcon (float posX, float posY, float scaleX, float scaleY)
	{
		_textureDataHandle = loadGLTexture(_drawableResourceId);
		_currentScale[0] = scaleX;
		_currentScale[1] = scaleY;
		_currentPos[0] = posX;
		_currentPos[1] = posY;
		_currentPos[2] = 0.2f;

		_vector[0] = _vector[1] = _vector[2] = 0;
		_rotationZ = 0;
		_alive = true;
	}

	public void setRatio (float ratio)
	{
		super.setRatio(ratio);

		// start asteroid at top of screen
		_currentPos[1] = _ratio + _currentScale[1];
	}

	@Override
	public boolean update ()
	{
		_rotationZ += _rotationDelta;

		return super.update();
	}
}
