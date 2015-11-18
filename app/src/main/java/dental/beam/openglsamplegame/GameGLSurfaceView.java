package dental.beam.openglsamplegame;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import java.util.Map;

import dental.beam.openglsamplegame.engine.GameEngine;
import timber.log.Timber;

public class GameGLSurfaceView extends GLSurfaceView
{
	private GameGLRenderer _renderer;
	private GameEngine _engine;

	public GameGLSurfaceView (Context context)
	{
		super(context);
		init();
	}

	public GameGLSurfaceView (Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init();
	}

	private void init ()
	{
		Timber.d("init");

		setEGLContextClientVersion(2);

		setEGLConfigChooser(false);
		getHolder().setFormat(PixelFormat.RGBA_8888);

		if (!isInEditMode())
		{
			_renderer = new GameGLRenderer();
			setRenderer(_renderer);
			setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
		}

		_engine = new GameEngine(getContext());
		_renderer.setGameEngine(_engine);
	}

	public void exitGame ()
	{
		// run this on the glthread
		queueEvent(new Runnable()
		{
			@Override
			public void run ()
			{
				_engine.destroy();
			}
		});
	}

	public void setViewLocations (final Map<Integer, Rect> viewLocations)
	{
		// run this on the glthread
		queueEvent(new Runnable()
		{
			@Override
			public void run ()
			{
				_engine.setViewLocations(viewLocations);
			}
		});
	}

	public void startGame ()
	{
		// run this on the glthread
		queueEvent(new Runnable()
		{
			@Override
			public void run ()
			{
				_engine.startGame();
			}
		});
	}
}
