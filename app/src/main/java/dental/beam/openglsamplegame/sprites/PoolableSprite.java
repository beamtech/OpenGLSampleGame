package dental.beam.openglsamplegame.sprites;

import android.content.Context;

public interface PoolableSprite
{
	public void setContext(Context context);

	public boolean isInUse ();
	public void setInUse (boolean inUse);
}
