package dental.beam.openglsamplegame.engine;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.SystemClock;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dental.beam.openglsamplegame.R;
import dental.beam.openglsamplegame.helpers.TiltHelper;
import dental.beam.openglsamplegame.sprites.AsteroidSprite;
import dental.beam.openglsamplegame.sprites.BrokenAsteroidSprite;
import dental.beam.openglsamplegame.sprites.ChickenSprite;
import dental.beam.openglsamplegame.sprites.PlayerSprite;
import dental.beam.openglsamplegame.sprites.SpritePool;
import dental.beam.openglsamplegame.sprites.TextSprite;
import dental.beam.openglsamplegame.sprites.TextureSprite;
import timber.log.Timber;

public class GameEngine
{
	Context _context;

	private TiltHelper _tiltHelper;

	//region opengl drawable objects
	private PlayerSprite _player;
	private SpritePool<AsteroidSprite> _asteroidPool;
	private List<AsteroidSprite> _asteroids = new ArrayList<>(100);
	private SpritePool<BrokenAsteroidSprite> _brokenAsteroidPool;
	private List<BrokenAsteroidSprite> _brokenAsteroids = new ArrayList<>(20);
	private SpritePool<ChickenSprite> _chickenPool;
	private List<ChickenSprite> _chickens = new ArrayList<>(20);
	private AsteroidSprite _asteroidIcon;
	private TextSprite _asteroidCountText;
	private TextSprite _gameOverText;
	//endregion

	//region game state
	private boolean _playing = false;
	private int _asteroidCount;
	private int _frames;
	private int _framesBetweenAddingAsteroid = 64;
	//endregion

	//region opengl stuff
	private float _ratio;
	private float _width;
	private float _height;

	Map<Integer, Rect> _viewLocations;
	//endregion

	public GameEngine (Context context)
	{
		_context = context;
		_asteroidPool = new SpritePool<>(context, 100, AsteroidSprite.class);
		_brokenAsteroidPool = new SpritePool<>(context, 100, BrokenAsteroidSprite.class);
		_chickenPool = new SpritePool<>(context, 5, ChickenSprite.class);

		startGame();
	}

	public void initSprites ()
	{
		TextureSprite.clearTextureCache();

		int asteroidCount = _asteroidPool.getSprites().size();
		for (int i = 0; i < asteroidCount; i++)
		{
			AsteroidSprite asteroidSprite = _asteroidPool.getSprites().get(i);
			asteroidSprite.reloadTexture();
		}
		int chickenCount = _chickenPool.getSprites().size();
		for (int i = 0; i < chickenCount; i++)
		{
			ChickenSprite chickenSprite = _chickenPool.getSprites().get(i);
			chickenSprite.reloadTexture();
		}
		if (_player == null)
		{
			_tiltHelper = TiltHelper.getInstance(_context);
			_player = new PlayerSprite(_context, R.drawable.ship, _tiltHelper);
		}
		else
		{
			_player.reloadTexture();
		}

		if (_asteroidIcon == null)
		{
			_asteroidIcon = spawnAsteroid();
		}
		else
		{
			_asteroidIcon.reloadTexture();
		}

		if (_asteroidCountText == null)
		{
			_asteroidCountText = new TextSprite();
			_asteroidCountText.setContext(_context);
		}
		else
		{
			_asteroidCountText.reloadTexture();
		}

		if (_gameOverText == null)
		{
			_gameOverText = new TextSprite();
			_gameOverText.setContext(_context);
			_gameOverText.setText("Game Over", TextSprite.TEXT_ALIGN_CENTER, 0, Color.WHITE);
		}
		else
		{
			_gameOverText.reloadTexture();
		}
	}

	/** ratio is height/width of screen which affects where in Y coordinate to place sprites */
	public void setRatio (float ratio, float width, float height)
	{
		_ratio = ratio;
		_width = width;
		_height = height;

		_player.setRatio(ratio);

		int chickenCount = _chickenPool.getSprites().size();
		for (int i = 0; i < chickenCount; i++)
		{
			ChickenSprite chickenSprite = _chickenPool.getSprites().get(i);
			chickenSprite.setRatio(ratio);
		}
		_gameOverText.init(ratio, _width, Math.round(_context.getResources().getDimension(R.dimen.game_over_text_size)));
		_gameOverText.getPosition()[1] = 0;

		setViewLocations(_viewLocations);
	}

	public void drawFrame (float matrix[])
	{
		if (_chickens.isEmpty())
		{
			gameOver();
		}
		else
		{
			update();
		}

		drawAsteroids(matrix);
		drawChickens(matrix);

		_player.draw(matrix);

		_asteroidIcon.draw(matrix);
		_asteroidCountText.draw(matrix);

		if (!_playing)
		{
			_gameOverText.draw(matrix);
		}
	}

	public void update()
	{
		updateAsteroids();
		updateChickens();

		if (_frames++ % _framesBetweenAddingAsteroid == 0)
		{
			AsteroidSprite asteroidSprite = spawnAsteroid();
			asteroidSprite.initRandom();
			addAsteroid(asteroidSprite);
		}

		if (_frames % 100 == 0 && _framesBetweenAddingAsteroid > 15)
		{
			_framesBetweenAddingAsteroid--;
		}

		_player.update();
	}

	public void startGame()
	{
		if (_playing)
		{
			return;
		}

		_playing = true;
		_asteroidCount = 0;
		_frames = 0;
		_framesBetweenAddingAsteroid = 64;

		for (int i = 0; i < 5; i++)
		{
			ChickenSprite chicken = _chickenPool.spawn();
			chicken.init();
			chicken.setRatio(_ratio);
			_chickens.add(chicken);
		}
	}

	public void gameOver()
	{
		_playing = false;
	}

	public AsteroidSprite spawnAsteroid ()
	{
		return _asteroidPool.spawn();
	}

	public void addAsteroid (AsteroidSprite asteroid)
	{
		asteroid.setRatio(_ratio);
		_asteroids.add(asteroid);
	}

	private void updateAsteroids()
	{
		for (int i = 0; i < _asteroids.size(); i++)
		{
			AsteroidSprite asteroid = _asteroids.get(i);
			boolean collided = false;
			if (asteroid.collidesWith(_player))
			{
				collided = true;
				_asteroidCountText.setText(Integer.toString(++_asteroidCount));
				breakAsteroid(asteroid);
				_asteroidPool.kill(asteroid);
				_asteroids.remove(i--);
			}
			else
			{
				for (int j = 0; j < _chickens.size(); j++)
				{
					ChickenSprite chicken = _chickens.get(j);
					if (asteroid.collidesWith(chicken))
					{
						_chickenPool.kill(chicken);
						_chickens.remove(j--);
					}
				}
			}

			if (!collided && !asteroid.update())
			{
				_asteroidPool.kill(asteroid);
				_asteroids.remove(i--);
			}
		}

		for (int i = 0; i < _brokenAsteroids.size(); i++)
		{
			BrokenAsteroidSprite brokenAsteroid = _brokenAsteroids.get(i);
			if (!brokenAsteroid.update())
			{
				_brokenAsteroidPool.kill(brokenAsteroid);
				_brokenAsteroids.remove(i--);
			}
		}
	}

	private void drawAsteroids (float matrix[])
	{
		if (_asteroids.size() > 0)
		{
			_asteroids.get(0).batchDraw(matrix, (List<TextureSprite>) ((Object) _asteroids));
		}

		if (_brokenAsteroids.size() > 0)
		{
			_brokenAsteroids.get(0).batchDraw(matrix, (List<TextureSprite>) ((Object) _brokenAsteroids));
		}
	}

	private void breakAsteroid (AsteroidSprite asteroid)
	{
		float origPosition[] = asteroid.getPosition();
		float origVector[] = asteroid.getVector();
		float origScale[] = asteroid.getScale();
		float newScale = origScale[0] / 4;
		float p[] = new float[] {
			origScale[1] * 1 / 4,
			origScale[1] * 1 / 2,
			origScale[1],
			origScale[1] * 1 / 2,
			origScale[1] * 1 / 4
		};
		for (int i = 0; i < 5; i++)
		{
			float newPosition[] = new float[3];
			float newVector[] = new float[3];
			newPosition[1] = origPosition[1] + p[i]; //move it up a bit
			newVector[1] = (float) (origVector[1] * (0.8f + 0.2f * Math.random())); //slow it's speed a bit

			if (i < 2)
			{
				newPosition[0] = origPosition[0] - (float) (0.06f * Math.random());
				newVector[0] = -(float) (Math.random() * .01f);
			}
			else if (i == 2)
			{
				newPosition[0] = origPosition[0];
				newVector[0] = 0;
			}
			else
			{
				newPosition[0] = origPosition[0] + (float) (0.06f * Math.random());
				newVector[0] = (float) (Math.random() * .01f);
			}

			BrokenAsteroidSprite piece = _brokenAsteroidPool.spawn();
			piece.init(_ratio, newPosition, newVector, newScale);
			_brokenAsteroids.add(piece);
		}
	}

	private void updateChickens()
	{
		for (int i = 0; i < _chickens.size(); i++)
		{
			ChickenSprite chicken = _chickens.get(i);

			if (!chicken.update())
			{
				_chickenPool.kill(chicken);
				_chickens.remove(i--);
			}
		}
	}

	private void drawChickens (float matrix[])
	{
		if (_chickens.size() > 0)
		{
			_chickens.get(0).batchDraw(matrix, (List<TextureSprite>) ((Object) _chickens));
		}
	}

	public void setViewLocations (Map<Integer, Rect> viewLocations)
	{
		_viewLocations = viewLocations;
		if (_ratio != 0)
		{
			initHUDIcon(_asteroidIcon, _ratio, R.id.asteroid_icon);
			initHUDText(_asteroidCountText, Integer.toString(_asteroidCount),
				TextSprite.TEXT_NO_ALIGN, _ratio, R.id.asteroid_count_text);
		}
	}

	private void initHUDIcon (AsteroidSprite sprite, float ratio, int id)
	{
		float coords[] = new float[2];
		float scale[] = new float[2];

		sprite.setRatio(ratio);
		Rect position = _viewLocations.get(id);
		convertAndroidLocationToGLCentered(coords, position);
		convertAndroidLocationToGLScale(scale, position);
		sprite.initIcon(coords[0], coords[1], scale[0], scale[1]);
	}

	private void initHUDText (TextSprite textSprite, String text, int textAlign, float ratio, int id)
	{
		float coords[] = new float[2];
		textSprite.init(ratio, _width, Math.round(_context.getResources().getDimension(R.dimen.asteroid_text_size)));
		textSprite.setText(text, textAlign, 0, Color.WHITE);
		convertAndroidLocationToGL(coords, _viewLocations.get(id));
		if (textAlign == TextSprite.TEXT_NO_ALIGN)
		{
			textSprite.getPosition()[0] = coords[0];
		}
		textSprite.getPosition()[1] = coords[1];
	}

	private void convertAndroidLocationToGLCentered (float glPos[], Rect androidLocation)
	{
		glPos[0] = androidLocation.centerX() / _width * 2 - 1;
		float yPercentage = androidLocation.centerY() / _height;
		float yPercentageGl = yPercentage * 2 * _ratio;
		glPos[1] = -(yPercentageGl - _ratio);
	}

	private void convertAndroidLocationToGL (float glPos[], Rect androidLocation)
	{
		glPos[0] = androidLocation.left / _width * 2 - 1;
		float yPercentage = androidLocation.bottom / _height;
		float yPercentageGl = yPercentage * 2 * _ratio;
		glPos[1] = -(yPercentageGl - _ratio);
	}

	private void convertAndroidLocationToGLScale (float glScale[], Rect androidLocation)
	{
		glScale[0] = androidLocation.width() / _width;
		glScale[1] = androidLocation.height() / _width;
	}

	public void destroy ()
	{
		if (_tiltHelper != null)
		{
			_tiltHelper.destroy();
		}
	}

	//region timing debug
	private long _now;
	private long _lastUpdate;
	private float _totalDiffs;
	private float _totalLens;
	private long _statFrames;

	private void timingDebugStart ()
	{
		_now = SystemClock.elapsedRealtime();

		if (_lastUpdate != 0)
		{
			_statFrames++;
			long timeDiff = _now - _lastUpdate;
			_totalDiffs += timeDiff;
			if (timeDiff > 20)
			{
				Timber.d("long frame %d, %d millis", _statFrames, timeDiff);
			}
			if (_statFrames % 128 == 0)
			{
				Timber.d("avg = %f %f %d", (_totalDiffs / _statFrames), _totalDiffs, _statFrames);
			}
		}

		_lastUpdate = _now;
	}

	private void timingDebugEnd ()
	{
		long len = SystemClock.elapsedRealtime() - _now;
		if (len > 20)
		{
			Timber.d("long len %d, %d millis", _statFrames, len);
		}
		_totalLens += len;
		if (_statFrames % 128 == 0)
		{
			Timber.d("avg len = %f %f %d", (_totalLens / _statFrames), _totalLens, _statFrames);
		}
	}
	//endregion
}