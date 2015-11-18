package dental.beam.openglsamplegame.sprites;

import android.graphics.Color;
import android.opengl.Matrix;

import dental.beam.gltext.GLText;

public class TextSprite extends TextureSprite
{
	public static final int TEXT_ALIGN_LEFT = 1;
	public static final int TEXT_ALIGN_CENTER = 2;
	public static final int TEXT_ALIGN_RIGHT = 3;
	public static final int TEXT_NO_ALIGN = 4;

	private String _text;
	private int _textAlign;
	private int _textSize;
	private GLText _glText;
	private float _width;
	private float _margin;
	private float _color[];

	public void setText (String text, int textAlign, float yOffset, int color)
	{
		_text = text;
		setTextAlign(textAlign);
		_currentPos[1] += yOffset;
		_color = new float[4];
		_color[0] = Color.red(color) / 255f;
		_color[1] = Color.green(color) / 255f;
		_color[2] = Color.blue(color) / 255f;
		_color[3] = Color.alpha(color) / 255f;
	}

	public void setText(String text)
	{
		_text = text;
	}

	public void init (float ratio, float width, int textSize)
	{
		_width = width;
		_margin = width / 30f;
		_currentPos[0] = 0;
		_currentPos[1] = ratio;
		_currentPos[2] = 0.1f;

		_alive = false;

		_textSize = textSize;
		if (_glText == null)
		{
			_glText = new GLText(_context.getAssets());
			_glText.load("OpenSans-Light.ttf", textSize, 2, 2);
		}
	}

	private float textMatrix[] = new float[16];

	@Override
	public boolean update ()
	{
		return true;
	}

	@Override
	public void draw (float[] mvpMatrix)
	{
		System.arraycopy(mvpMatrix, 0, textMatrix, 0, 16);

		Matrix.translateM(textMatrix, 0, _currentPos[0], _currentPos[1], _currentPos[2]);
		//TODO see if can use gltext.setscale instead of this
		Matrix.scaleM(textMatrix, 0, 1 / _width * 2, 1 / _width * 2, 1); // the text code we're using assumes a view scaled by phone width/height, so scale it down

		float x = 0;
		if (_textAlign == TEXT_NO_ALIGN)
		{
			x = 0;
		}
		else if (_textAlign == TEXT_ALIGN_LEFT)
		{
			x = -_width / 2 + _margin;
		}
		else if (_textAlign == TEXT_ALIGN_RIGHT)
		{
			x = _width / 2 - _glText.getLength(_text) - _margin;
		}
		else if (_textAlign == TEXT_ALIGN_CENTER)
		{
			x = -_glText.getLength(_text) / 2;
		}

		_glText.begin(_color[0], _color[1], _color[2], 1.0f, textMatrix);
		_glText.draw(_text, x, 0, 0, 0);
		_glText.end();
	}

	@Override
	public void reloadTexture ()
	{
		if (_textSize > 0)
		{
			_glText = new GLText(_context.getAssets());
			_glText.load("OpenSans-Light.ttf", _textSize, 2, 2);
		}
	}

	public void setTextAlign (int textAlign)
	{
		_textAlign = textAlign;
		if (_textAlign != TEXT_NO_ALIGN)
		{
			_currentPos[0] = 0;
		}
	}
}
