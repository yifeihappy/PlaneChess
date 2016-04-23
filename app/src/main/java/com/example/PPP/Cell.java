package com.example.PPP;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Cell {
	private int x;
	private int y;

	public Cell()
	{

	}
	public Cell(int x0,int y0,int x1,int y1,Paint paint,Canvas canvas)
	{
		this.x=(x0+x1)/2;
		this.y=(y0+y1)/2;
		canvas.drawRect(x0,y0,x1,y1, paint);
		paint.setColor(Color.WHITE);
		canvas.drawCircle(x,y,11, paint);
	}
	public Cell(int x,int y,Paint paint,Canvas canvas)
	{
		this.x=x;
		this.y=y;
		paint.setColor(Color.WHITE);
		canvas.drawCircle(x,y,11, paint);
	}
    public int getX()
    {
    	return x;
    }
	public int getY()
	{
		return y;
	}
	public void setX(int x)
	{
		this.x = x;
	}
	public void setY(int y)
	{
		this.y = y;
	}
}
