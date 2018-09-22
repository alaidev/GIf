package com.example.alairaner.gif.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import com.example.alairaner.gif.AnimatedGifEncoder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by Alairaner on 2018/8/9.
 */

public class GIFUtils {
    private String createStringGif(String filename, List<String> paths, int fps, int width, int height) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        AnimatedGifEncoder localAnimatedGifEncoder = new AnimatedGifEncoder();
        localAnimatedGifEncoder.start(baos);//start
        localAnimatedGifEncoder.setRepeat(0);//设置生成gif的开始播放时间。0为立即开始播放
        localAnimatedGifEncoder.setDelay(fps);
        if (paths.size() > 0) {
            for (int i = 0; i < paths.size(); i++) {
                Bitmap bitmap = BitmapFactory.decodeFile(paths.get(i));
                Bitmap resizeBm = ImgToStringImg(bitmap, width, height);
                localAnimatedGifEncoder.addFrame(resizeBm);
            }
        }
        localAnimatedGifEncoder.finish();//finish
        File file = new File(Environment.getExternalStorageDirectory().getPath() + "/LiliNote");
        if (!file.exists()) file.mkdir();
        String path = Environment.getExternalStorageDirectory().getPath() + "/LiliNote/" + filename + ".gif";
        Log.d("PATH",path);
        FileOutputStream fos = new FileOutputStream(path);
        baos.writeTo(fos);
        baos.flush();
        fos.flush();
        baos.close();
        fos.close();
        return path;
    }
    public static String createGif(String filename, List<String> paths, int fps, int width, int height) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        AnimatedGifEncoder localAnimatedGifEncoder = new AnimatedGifEncoder();
        localAnimatedGifEncoder.start(baos);//start
        localAnimatedGifEncoder.setRepeat(0);//设置生成gif的开始播放时间。0为立即开始播放
        localAnimatedGifEncoder.setDelay(fps);
        if (paths.size() > 0) {
            for (int i = 0; i < paths.size(); i++) {
                Bitmap bitmap = BitmapFactory.decodeFile(paths.get(i));
                Bitmap resizeBm = changeBitmapSize(bitmap, width, height);
                localAnimatedGifEncoder.addFrame(resizeBm);
            }
        }
        localAnimatedGifEncoder.finish();//finish
        File file = new File(Environment.getExternalStorageDirectory().getPath() + "/LiliNote");
        if (!file.exists()) file.mkdir();
        String path = Environment.getExternalStorageDirectory().getPath() + "/LiliNote/" + filename + ".gif";
        Log.d("PATH",path);
        FileOutputStream fos = new FileOutputStream(path);
        baos.writeTo(fos);
        baos.flush();
        fos.flush();
        baos.close();
        fos.close();
        return path;
    }

    public static Bitmap changeBitmapSize(Bitmap bitmap, int width, int height) {
        int oldwidth = bitmap.getWidth();
        int oldheight = bitmap.getHeight();
        Log.e("width","width:"+width);
        Log.e("height","height:"+height);
        //设置想要的大小
        int newWidth=width;
        int newHeight=height;
        //计算压缩的比率
        float scaleWidth=((float)newWidth)/oldwidth;
        float scaleHeight=((float)newHeight)/oldheight;
        //获取想要缩放的matrix
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth,scaleHeight);
        //获取新的bitmap
        bitmap=Bitmap.createBitmap(bitmap,0,0,oldwidth,oldheight,matrix,true);
        bitmap.getWidth();
        bitmap.getHeight();
        Log.e("newWidth","newWidth"+bitmap.getWidth());
        Log.e("newHeight","newHeight"+bitmap.getHeight());
        return bitmap;
    }
    public Bitmap ImgToStringImg(Bitmap bitmap,int width,int height){
        final String base = "@#&$%*o!;.";// 字符串由复杂到简单
        Bitmap mBitmap=Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
        Canvas canvas=new Canvas(mBitmap);
        canvas.drawColor(Color.WHITE);
        Paint mTextPaint = new Paint();
        mTextPaint.setColor(Color.BLACK);// 白色画笔
        mTextPaint.setTextSize(10f);// 设置字体大小
        for (int y = 0; y < height; y += 2){
            for (int x = 0; x < width; x++){
                final int pixel = bitmap.getPixel(x, y);
                final int r = (pixel & 0xff0000) >> 16, g = (pixel & 0xff00) >> 8, b = pixel & 0xff;
                final float gray = 0.299f * r + 0.578f * g + 0.114f * b;
                final int index = Math.round(gray * (base.length() + 1) / 255);
                canvas.drawText((index >= base.length() ? " " : String.valueOf(base.charAt(index)))
                        ,x,y,mTextPaint);
            }
        }
        return mBitmap;
    }
}
