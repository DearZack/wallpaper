package com.moe.LiveVisualizer.inter;
import android.graphics.Canvas;
import android.graphics.Shader;
import com.moe.LiveVisualizer.LiveWallpaper;
import android.graphics.Matrix;

public interface Draw
{
	void draw(Canvas canvas);
	void onDraw(Canvas canvas,int color_mode);
	double[] getFft();
	float getDownSpeed();
	//上下渐变
	void setFade(Shader sgader);
	Shader getFade();
	
	LiveWallpaper.WallpaperEngine getEngine();
	void onBorderWidthChanged(int width);
	void onBorderHeightChanged(int height);
	void onSpaceWidthChanged(int space);
	void onDrawHeightChanged(float height);
	int size();
	int getColor();
	void setShader(Shader shader);
	Shader getShader();
	void setRound(boolean round);
	//void setCutImage(boolean cut);
	//Matrix getCenterScale();
	void setOffsetX(int x);
	void setOffsetY(int y);
	void notifySizeChanged();
}
