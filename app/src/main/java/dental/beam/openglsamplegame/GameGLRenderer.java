package dental.beam.openglsamplegame;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import dental.beam.openglsamplegame.engine.GameEngine;
import dental.beam.openglsamplegame.sprites.TextureSprite;
import timber.log.Timber;

public class GameGLRenderer implements GLSurfaceView.Renderer
{
	// region gl code
	private final float[] _MVPMatrix = new float[16];
	private final float[] _projectionMatrix = new float[16];
	private final float[] _viewMatrix = new float[16];
	// endregion

	private GameEngine _engine;

	private long fpsTime = System.nanoTime();
	private int frames;

	@Override
	public void onSurfaceCreated (GL10 gl, EGLConfig config)
	{
		Timber.d("onSurface created");

		GLES20.glClearColor(0.09019f, 0.10588f, 0.13333f, 0.0f);

		GLES20.glEnable(GLES20.GL_BLEND);
		GLES20.glBlendEquation(GLES20.GL_FUNC_ADD);
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

		GLES20.glEnable(GLES20.GL_DEPTH_TEST);

		TextureSprite.initGlState();

		_engine.initSprites();
	}

	/**
	 * Called when width/height are known
	 * Use it to setup our coordinate range
	 *  x-axis goes from -1=left edge to 1=right edge
	 *  y-axis -X=bottom edge to X=top edge,
	 *  	where X is the ratio of height/width.
	 *  	On Nexus 6 it's around 1.7 but will vary by device
	 */
	@Override
	public void onSurfaceChanged (GL10 unused, int width, int height)
	{
		Timber.d("surface changed");

		// Adjust the viewport
		GLES20.glViewport(0, 0, width, height);

		float ratio = (float) height / width;

		_engine.setRatio(ratio, width, height);

		Matrix.orthoM(_projectionMatrix, 0, -1, 1, -ratio, ratio, 1, -1);

		// Set the camera position (View matrix)
		Matrix.setLookAtM(_viewMatrix, 0, 0, 0, 1, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

		// Calculate the projection and view transformation
		Matrix.multiplyMM(_MVPMatrix, 0, _projectionMatrix, 0, _viewMatrix, 0);
	}

	/**
	 * Draw a new frame of the game
	 */
	@Override
	public void onDrawFrame (GL10 unused)
	{
		// Draw background color
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

		_engine.drawFrame(_MVPMatrix);

		if (System.nanoTime() - fpsTime >= 1000000000)
		{
			Timber.d("fps: %d", frames);
			frames = 0;
			fpsTime = System.nanoTime();
		}
		else
		{
			frames++;
		}
	}


	public void setGameEngine (GameEngine engine)
	{
		_engine = engine;
	}

	/**
	 * Utility method for compiling a OpenGL shader.
	 *
	 * <p><strong>Note:</strong> When developing shaders, use the checkGlError()
	 * method to debug shader coding errors.</p>
	 *
	 * @param type       - Vertex or fragment shader type.
	 * @param shaderCode - String containing the shader code.
	 * @return - Returns an id for the shader.
	 */

	public static int loadShader (int type, String shaderCode)
	{
		// create a vertex shader type (GLES20.GL_VERTEX_SHADER)
		// or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
		int shader = GLES20.glCreateShader(type);

		// add the source code to the shader and compile it
		GLES20.glShaderSource(shader, shaderCode);
		GLES20.glCompileShader(shader);

		return shader;
	}

	/**
	 * Utility method for debugging OpenGL calls. Provide the name of the call
	 * just after making it:
	 *
	 * <pre>
	 * mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
	 * GameGLRenderer.checkGlError("glGetUniformLocation");</pre>
	 *
	 * If the operation is not successful, the check logs an error
	 *
	 * @param glOperation - Name of the OpenGL call to check.
	 */
	public static void checkGlError (String glOperation)
	{
		int error;
		while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR)
		{
			Timber.e(glOperation + ": glError %d", error);
		}
	}
}