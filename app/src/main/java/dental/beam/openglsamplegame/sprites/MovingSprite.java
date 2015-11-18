package dental.beam.openglsamplegame.sprites;

/**
 * Used for sprites that are moving down the screen and eventually go off screen and can be removed
 */
public abstract class MovingSprite extends TextureSprite
{
	float _ratio;

	public void setRatio (float ratio)
	{
		_ratio = ratio;
	}

	public boolean update ()
	{
		if (!_alive)
		{
			return false;
		}

		// move sprite
		_currentPos[0] = _currentPos[0] + _vector[0];
		_currentPos[1] = _currentPos[1] + _vector[1];

		// if sprite moved off bottom of screen we can remove sprite
		if (_currentPos[1] < -_ratio)
		{
			_alive = false;
		}

		return true;
	}
}
