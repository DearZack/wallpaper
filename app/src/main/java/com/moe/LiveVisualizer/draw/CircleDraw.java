package com.moe.LiveVisualizer.draw;
import com.moe.LiveVisualizer.internal.ImageDraw;
import com.moe.LiveVisualizer.LiveWallpaper;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Bitmap;
import android.graphics.PorterDuffXfermode;
import android.graphics.PorterDuff;
import android.graphics.Matrix;

public abstract class CircleDraw extends Draw
{
	private PointF point;
	public CircleDraw(ImageDraw draw,LiveWallpaper.WallpaperEngine engine){
		super(draw,engine);
		point=new PointF();
		point.x=engine.getSharedPreferences().getInt("offsetX",Math.min(engine.getDisplayWidth(),engine.getDisplayHeight())/2);
		point.y=engine.getSharedPreferences().getInt("offsetY",Math.max(engine.getDisplayHeight(),engine.getDisplayWidth())/2);
		if(engine.getDisplayWidth()>engine.getDisplayHeight()){
			float x=point.x;
			point.x=point.y;
			point.y=x;
		}
	}

	@Override
	public abstract void setRound(boolean round);

	@Override
	public void notifySizeChanged()
	{
		point.x=getEngine().getSharedPreferences().getInt("offsetX",Math.min(getEngine().getDisplayWidth(),getEngine().getDisplayHeight())/2);
		point.y=getEngine().getSharedPreferences().getInt("offsetY",Math.max(getEngine().getDisplayHeight(),getEngine().getDisplayWidth())/2);
		if(getEngine().getDisplayWidth()>getEngine().getDisplayHeight()){
			float x=point.x;
			point.x=point.y;
			point.y=x;
		}
	}



	
	@Override
	public void setOffsetX(int x)
	{
		point.x=x;
	}

	@Override
	public void setOffsetY(int y)
	{
		point.y=y;
	}

	
	@Override
	final public void onDrawHeightChanged(float height)
	{
		// TODO: Implement this method
	}

	public PointF getPointF(){
		return point;
	}
	
}
