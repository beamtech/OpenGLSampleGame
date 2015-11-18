package dental.beam.openglsamplegame.sprites;

import android.content.Context;

import dental.beam.openglsamplegame.helpers.TiltHelper;

public class PlayerSprite extends TextureSprite
{
	private static final float TILT_MIN = -25;
	private static final float TILT_MAX = 25;
	private static final float TILT_SCALE = 2.0f;
	private static float TILT_SLOP = 1f;
	private static float SCALE = 0.15f;

	private TiltHelper _tiltHelper;

	private float _tilt;
	private float _ratio;

	public PlayerSprite (Context context, int drawableResourceId, TiltHelper tiltHelper)
	{
		super();
		setContext(context);
		setDrawable(drawableResourceId);

		_tiltHelper = tiltHelper;
	}

	public void setRatio (float ratio)
	{
		_ratio = ratio;
		_currentScale[0] = SCALE;
		_currentScale[1] = SCALE / _imageRatio;
		_currentPos[0] = 0;
		_currentPos[1] = -_ratio + _currentScale[1] * 3;
		_currentPos[2] = 0.1f;
		_vector[0] = _vector[1] = _vector[2] = 0;
		_alive = true;
	}

	public boolean update ()
	{
		float tilt = _tiltHelper.getTilt() * TILT_SCALE;
		if (tilt < TILT_MIN) { tilt = TILT_MIN; }
		if (tilt > TILT_MAX) { tilt = TILT_MAX; }

		float tiltDiff = Math.abs(tilt - _tilt);

		// ignore small player movements
		if (tiltDiff > TILT_SLOP)
		{
			// slow down player movement a bit
			if (tilt < _tilt)
			{
				_tilt -= (tiltDiff / 5f);
			}
			else
			{
				_tilt += (tiltDiff / 5f);
			}

			if (tilt < 0)
			{
				_currentPos[0] = -_tilt / TILT_MIN;
			}
			else
			{
				_currentPos[0] = _tilt / TILT_MAX;
			}
		}

		return true;
	}
}
