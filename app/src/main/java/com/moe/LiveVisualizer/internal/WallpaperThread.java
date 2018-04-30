package com.moe.LiveVisualizer.internal;
import com.moe.LiveVisualizer.LiveWallpaper;
import android.content.SharedPreferences;
import android.os.Looper;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;
import android.graphics.Canvas;
import android.util.TypedValue;
import android.graphics.Rect;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.Bitmap;
import android.graphics.PorterDuffXfermode;
import android.graphics.PorterDuff;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Movie;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.gifdecoder.GifDecoder;

public class WallpaperThread extends Thread
{

	private LiveWallpaper.WallpaperEngine engine;
	//private Handler handler;
	private ImageDraw imageDraw;
	private long oldTime;
	private byte[] buffer;
	public WallpaperThread(LiveWallpaper.WallpaperEngine engine)
	{

		this.engine = engine;
		imageDraw = ImageDrawCompat.getInstance(engine);
	}

	public void close()
	{
		imageDraw = null;
	}

	public void onUpdate(byte[] p2)
	{
		this.buffer = p2;
		/*if ( engine.isVisible() && handler != null )
		 {
		 handler.removeMessages(0);
		 handler.sendMessageDelayed(handler.obtainMessage(0, p2), 26);
		 }*/
	}

	@Override
	public void run()
	{

		/*	Looper.prepare();
		 handler = new Handler(){
		 public void handleMessage(Message msg)
		 {*/
		while ( imageDraw != null )
		{
			long delay=0;
			SurfaceHolder sh=engine.getSurfaceHolder();
			if ( sh != null && engine.isVisible() )
			{
				synchronized ( sh )
				{
					final Canvas canvas=sh.lockCanvas();
					if ( canvas != null )
					{
						/*if ( engine.getSharedPreferences().getBoolean("artwork", false) && engine.getArtwork() != null )
						 {
						 Bitmap buffer=engine.getArtwork();
						 Matrix matrix=new Matrix();
						 float scale=Math.max(((float)canvas.getWidth() / buffer.getWidth()), ((float)canvas.getHeight() / buffer.getHeight()));
						 matrix.setScale(scale, scale);
						 float offsetX=(buffer.getWidth() * scale - canvas.getWidth()) / 2;
						 float offsetY=(buffer.getHeight() * scale - canvas.getHeight()) / 2;
						 matrix.postTranslate(-offsetX, -offsetY);
						 canvas.drawBitmap(engine.getArtwork(), matrix, null);
						 }
						 else*/
						if ( engine.isGif() && engine.getMovie() != null )
						{
							canvas.drawColor(0xff000000);
							final GifDecoder movie=engine.getMovie();
							movie.advance();
							Bitmap bit=movie.getNextFrame();
							if ( bit == null )
							{movie.resetFrameIndex();
								bit = movie.getNextFrame();}
							if ( bit != null )
							{
								Matrix matrix=new Matrix();
								float scale=Math.min(canvas.getWidth() / (float)bit.getWidth(), canvas.getHeight() / (float)bit.getHeight());
								matrix.setScale(scale, scale);
								matrix.postTranslate((canvas.getWidth() - bit.getWidth() * scale) / 2.0f, (canvas.getHeight() - bit.getHeight() * scale) / 2.0f);
								canvas.drawBitmap(bit, matrix, null);
								bit.recycle();
								delay=movie.getDelay(movie.getCurrentFrameIndex());
							}
						}
						else if ( engine.getWallpaper() != null )
							canvas.drawBitmap(engine.getWallpaper(), 0, 0, null);
						else
							canvas.drawColor(0xff000000);
						if ( imageDraw != null )
						{
							ImageDraw draw=imageDraw.lockData(buffer);
							if ( draw != null )
							//try{
								draw.draw(canvas);
							//}catch(Exception e){}
						}
						try
						{
							sh.unlockCanvasAndPost(canvas);
						}
						catch (Exception E)
						{}
					}
				}
			}
			long blank=(System.nanoTime() - oldTime)/1000000;
			try
			{
				long space=delay==0?33:delay;
				sleep(blank > space?0: (space - blank));
				oldTime = System.nanoTime();
				
			}
			catch (InterruptedException e)
			{}

		}

		/*		}
		 };
		 Looper.loop();
		 */

	}

}
