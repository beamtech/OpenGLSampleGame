package dental.beam.openglsamplegame.sprites;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class SpritePool<T extends PoolableSprite>
{
	private Class<T> _clazz;
	private List<T> _pool;
	private Context _context;
	private int _lastSpawnedIndex = 0;
	private int _maxSprites = 0;

	public SpritePool (Context context, int maxSprites, Class<T> clazz)
	{
		_context = context;
		_clazz = clazz;
		_maxSprites = maxSprites;
		_pool = new ArrayList<T>(maxSprites);

		for (int i = 0; i < maxSprites; i++)
		{
			_pool.add(createSprite(context));
		}
	}

	private T createSprite (Context context)
	{
		try
		{
			T t = _clazz.cast(_clazz.newInstance());
			t.setContext(context);
			return t;
		}
		catch (InstantiationException e)
		{
			Timber.e("instantiation", e);
		}
		catch (IllegalAccessException e)
		{
			Timber.e("illegal", e);
		}
		return null;
	}

	public T spawn ()
	{
		for (int i = _lastSpawnedIndex + 1; i < _maxSprites; i++)
		{
			T sprite = _pool.get(i);
			if (!sprite.isInUse())
			{
				sprite.setInUse(true);
				_lastSpawnedIndex = i;
				return sprite;
			}
		}
		for (int i = 0; i < _lastSpawnedIndex; i++)
		{
			T sprite = _pool.get(i);
			if (!sprite.isInUse())
			{
				sprite.setInUse(true);
				_lastSpawnedIndex = i;
				return sprite;
			}
		}

		//expand pool
		T t = createSprite(_context);
		t.setInUse(true);
		_lastSpawnedIndex = _pool.size();
		_pool.add(t);
		return t;
	}

	public void kill (PoolableSprite sprite)
	{
		sprite.setInUse(false);
	}

	public List<T> getSprites ()
	{
		return _pool;
	}

	public void clear ()
	{
		for (int i = 0; i < _pool.size(); i++)
		{
			_pool.get(i).setInUse(false);
		}
	}
}
