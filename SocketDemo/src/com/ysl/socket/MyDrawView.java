package com.ysl.socket;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.Log;
import android.view.View;

public class MyDrawView extends View
{

	public MyDrawView(Context context)
	{
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	//其次才是绘制
	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		Log.i("ysl", "onDraw（）------");
		 /*
         * 方法 说明 drawRect绘制矩形   drawCircle绘制圆形  drawOval绘制椭圆   drawPath绘制任意多边形
         * drawLine绘制直线   drawPoin绘制点
         */
		Paint paint = new Paint();
		paint.setColor(Color.RED);
		
		canvas.drawText("画圆：",10,20, paint);
		canvas.drawCircle(60, 20, 10, paint);
		paint.setAntiAlias(true);
		canvas.drawCircle(120, 20, 20, paint);
		
		canvas.drawText("画线及弧线：", 10, 60, paint);
		paint.setColor(Color.GREEN);
		canvas.drawLine(60, 40, 100, 40, paint); //直线
		canvas.drawLine(110, 40, 190, 80, paint);
		//画笑脸弧线
		paint.setStyle(Paint.Style.STROKE); //空心
		RectF ovall = new RectF(150, 20, 180, 40);
		canvas.drawArc(ovall, 180, 180, false, paint);
		ovall.set(190, 20, 220, 40);
		canvas.drawArc(ovall, 180, 180, false, paint);
		ovall.set(160,30,210,60);
		canvas.drawArc(ovall, 0,180, true, paint);
		//画正方形、矩形
		paint.setColor(Color.BLUE);
		canvas.drawText("画矩形：", 10, 80, paint);
		paint.setColor(Color.GRAY);
		paint.setStyle(Paint.Style.FILL);
		canvas.drawRect(60, 60, 80, 80, paint); //左 上 右 下   ---- 左右相距20  上下相距20 = 正方形
		canvas.drawRect(60, 90, 160, 120, paint); //左右相距100  上下相距30
		//画扇形和椭圆
		canvas.drawText("画扇形和椭圆", 10, 140, paint);
		/* 设置渐变色 */
		Shader mShader = new LinearGradient(0, 0, 100, 100, new int[] { Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW,
                Color.LTGRAY }, null, Shader.TileMode.REPEAT);
		paint.setShader(mShader);
		
		RectF oval2 = new RectF(60, 100, 200, 240);
		canvas.drawArc(oval2, 200, 130, true, paint);
		
		//画椭圆
		oval2.set(210,100,300,130);
		canvas.drawOval(oval2, paint);
		
		//画三角形
		canvas.drawText("画三角形", 10, 180, paint);
		Path path = new Path();
		path.moveTo(80, 200);// 此点为多边形的起点
        path.lineTo(120, 250);
        path.lineTo(80, 250);
		path.close();
		canvas.drawPath(path, paint);
		
		paint.reset();
		paint.setColor(Color.LTGRAY);
		paint.setStyle(Paint.Style.STROKE);
		Path path2 = new Path();
		path2.moveTo(180, 200);
		path2.lineTo(200, 200);
		path2.lineTo(210, 210);
		path2.lineTo(200, 220);
        path2.lineTo(180, 220);
        path2.lineTo(170, 210);
        path2.close();//封闭
        canvas.drawPath(path2,paint);
		
	    /*
         * Path类封装复合(多轮廓几何图形的路径
         * 由直线段*、二次曲线,和三次方曲线，也可画以油画。drawPath(路径、油漆),要么已填充的或抚摸
         * (基于油漆的风格),或者可以用于剪断或画画的文本在路径。
         */
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.YELLOW);
		paint.setAntiAlias(true);
		canvas.drawText("画圆角矩形", 10, 250, paint);
		RectF oval3 = new RectF(80,260,300,300);
		canvas.drawRoundRect(oval3, 20, 15, paint);
		
		canvas.drawText("画贝塞尔曲线:", 10, 400, paint);
		paint.reset();
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(Color.GREEN);
		Path path3 = new Path();
		path3.moveTo(200, 320);
		path3.quadTo(250,310,270,400); //设置贝塞尔曲线的控制点坐标和终点坐标
		canvas.drawPath(path3, paint);
		
		//画点
		canvas.drawText("画点:",10, 450, paint);
		canvas.drawPoint(40, 450, paint);
		 canvas.drawPoints(new float[]{45,450,55,450,70,400}, paint);//画多个点
	
		 //画图片 就是贴图
		 Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
		 canvas.drawBitmap(bitmap, 200,450, paint); //距离左边 距离顶部
	}
	
	//最先运行计算
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		Log.i("ysl", "onMeasure（）------");
	}
}
