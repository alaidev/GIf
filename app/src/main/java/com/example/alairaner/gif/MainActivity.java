package com.example.alairaner.gif;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.alairaner.gif.Adapter.MyAdapter;
import com.example.alairaner.gif.Utils.GIFUtils;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private MyAdapter adapter;
    private RecyclerView recyclerView;
    private Button add,togif,tostring;
    private List<Bitmap> bitmapList=new ArrayList<>();
    private static List<Bitmap> bitmaps=new ArrayList<>();
    private ImageView imageView;
    private List<String> listpath=new ArrayList<>();
    String path="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WindowManager wm1 = this.getWindowManager();
        add=findViewById(R.id.add);
        togif=findViewById(R.id.togif);
        tostring=findViewById(R.id.toString);
        imageView=findViewById(R.id.gif);
        recyclerView=findViewById(R.id.recycleview);
        adapter=new MyAdapter(this,bitmapList);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        recyclerView.setAdapter(adapter);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.GONE);
                listpath.clear();
                bitmapList.clear();
                OpenPic();
            }
        });
        togif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                recyclerView.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);
                bitmaps.clear();
                try {
                    ToGIF(listpath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        tostring.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);
                try {
                    ToStringGIF();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void OpenPic(){
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())
                .minSelectNum(1)
                .maxSelectNum(3)
                .imageSpanCount(3)// 每行显示个数 int
                .selectionMode(PictureConfig.MULTIPLE)
                .previewImage(true)
                .enableCrop(true)
                .withAspectRatio(1,1)
                .freeStyleCropEnabled(true)
                .showCropGrid(true)
                .showCropFrame(true)
                .previewImage(true)
                .compress(false)
                .glideOverride(160, 160)
                .previewEggs(true)
                .forResult(PictureConfig.CHOOSE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==PictureConfig.CHOOSE_REQUEST&&resultCode==RESULT_OK){
            List<LocalMedia> localMedia=PictureSelector.obtainMultipleResult(data);
            for (LocalMedia localMedia1:localMedia){
                Log.d("LOCAL",localMedia1.getPath());
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2;
                Bitmap img = BitmapFactory.decodeFile(localMedia1.getPath(),options);
                bitmapList.add(img);
                adapter.notifyDataSetChanged();
                listpath.add(localMedia1.getPath());
            }
        }
    }
    private void ToGIF(final List<String> listpath) throws IOException {
        path=createGif(String.valueOf(Math.random()),listpath,200,160,160);
        Glide.with(this).load(path).into(imageView);
    }

    private void ToStringGIF() throws IOException {
        path=createStringGif(String.valueOf(Math.random()),bitmaps,200);
        Glide.with(this).load(path).into(imageView);
    }

    private String createStringGif(String filename, List<Bitmap> paths,int fps) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        AnimatedGifEncoder localAnimatedGifEncoder = new AnimatedGifEncoder();
        localAnimatedGifEncoder.start(baos);//start
        localAnimatedGifEncoder.setRepeat(0);//设置生成gif的开始播放时间。0为立即开始播放
        localAnimatedGifEncoder.setDelay(fps);
        if (paths.size() > 0) {
            for (int i = 0; i < paths.size(); i++) {
                Bitmap resizeBm = ImgToStringImg(paths.get(i));
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
                bitmaps.add(resizeBm);
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
    public Bitmap ImgToStringImg(Bitmap bitmap){
        final String base = "$@B%8&WM#*oahkbdpqwmZO0QLCJUYXzcvunxrjft/\\|()1{}[]?-_+~<>i!lI;:,\\\"^`'.";// 字符串由复杂到简单
        int width=bitmap.getWidth();
        int height=bitmap.getHeight();
        Bitmap mBitmap=Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
        Canvas canvas=new Canvas(mBitmap);
        canvas.drawColor(Color.WHITE);
        Paint mTextPaint = new Paint();
        mTextPaint.setColor(Color.BLACK);// 白色画笔
        mTextPaint.setTextSize(10f);// 设置字体大小
        for (int y = 0; y < height; y += 1){
            for (int x = 0; x < width; x+=1){
                final int pixel = bitmap.getPixel(x, y);
                final int r = (pixel & 0xff0000) >> 16, g = (pixel & 0xff00) >> 8, b = pixel & 0xff;
                final float gray = 0.2126f * r + 0.7152f * g + 0.0722f * b;
                final int index = Math.round(gray /((256 + 1) / base.length()));
                canvas.drawText((index >= base.length() ? " " : String.valueOf(base.charAt(index)))
                        ,x,y,mTextPaint);
            }
        }
        return mBitmap;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
