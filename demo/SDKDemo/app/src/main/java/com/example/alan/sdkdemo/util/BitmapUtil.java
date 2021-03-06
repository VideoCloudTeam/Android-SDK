package com.example.alan.sdkdemo.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.shockwave.pdfium.PdfDocument;
import com.shockwave.pdfium.PdfiumCore;
import com.vcrtc.utils.RealPathUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ricardo
 * 2020/1/9.
 */
public class BitmapUtil {
    private Context context;

    public BitmapUtil(Context context) {
        this.context = context;
    }

    /**
     * 根据PDF文件的uri，获得一组图片的路径数组
     *
     * @param uri
     * @return WritableNativeArray
     * @throws Exception
     */
    public List<String> pdfToImgs(Uri uri) throws Exception {
        String pdfPath = RealPathUtil.getRealPathFromURI(context, uri);
        if (pdfPath == null) {
            return null;
        }

        //获取pdf文件的字节数据
        byte[] buffer = null;
        File file = new File(pdfPath);
        FileInputStream fis = new FileInputStream(file);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] b = new byte[1024];
        int n;
        while ((n = fis.read(b)) != -1) {
            bos.write(b, 0, n);
        }
        fis.close();
        bos.close();
        buffer = bos.toByteArray();

        //pdf文件转为多个bitmap
        PdfiumCore pdfCore = new PdfiumCore(context);
        PdfDocument pdfDoc = pdfCore.newDocument(buffer);

        int pageCount = pdfCore.getPageCount(pdfDoc);
        pdfCore.openPage(pdfDoc, 0, pageCount - 1);

        List<String> list = new ArrayList();
        for (int i = 0; i < pageCount; i++) {
            int w = pdfCore.getPageWidthPoint(pdfDoc, i);
            int h = pdfCore.getPageHeightPoint(pdfDoc, i);
            Bitmap bitmap = Bitmap.createBitmap(1920, 1080, Bitmap.Config.ARGB_8888);
            pdfCore.renderPageBitmap(pdfDoc, bitmap, i, 0, 0, w, h);

            String path = getNewImgPath(pdfPath, i);
            compressAndWrite(bitmap, path);

            list.add(path);
        }
        pdfCore.closeDocument(pdfDoc);
        return list;
    }

    public String getNewImgPath(String oldPath, int page) {

        //原文件的[文件名+修改时间+页数]作为新的文件名。
        File file = new File(oldPath);
        String fullName = file.getName();
        String fileName = fullName.substring(0, fullName.lastIndexOf(".")); //去掉文件后缀的名称
        long l = file.lastModified() / 1000;   //文件修改时间

        String newName = fileName + "_" + l + "_" + page + ".jpg";

//        String dirPath = getPdfImageDir().getAbsolutePath();
        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "pdf";
        File temp = new File(dirPath);
        if (!temp.exists()) {
            temp.mkdirs();
        }
        String path = dirPath + File.separator + newName;
        return path;
    }

    public static Bitmap getPictureBitmap(String imagePath) {
        FileInputStream fis = null;

        try {
            fis = new FileInputStream(imagePath);
        } catch (FileNotFoundException var17) {
            var17.printStackTrace();
        }

        return BitmapFactory.decodeStream(fis);
    }

    /**
     * 质量压缩方法
     *
     * @param image
     * @return
     */
    public void compressAndWrite(Bitmap image, String path) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
//        int options = 100;
//        while (baos.toByteArray().length / 1024 > 300) {  //循环判断如果压缩后图片是否大于300kb,大于继续压缩
//            baos.reset();//重置baos即清空baos
//            //第一个参数 ：图片格式 ，第二个参数： 图片质量，100为最高，0为最差  ，第三个参数：保存压缩后的数据的流
//            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
//            options -= 10;//每次都减少10
//        }
        baos.close();
        if (!image.isRecycled()) {
            image.recycle();
        }

        FileOutputStream fos = new FileOutputStream(path);
        fos.write(baos.toByteArray());
        fos.flush();
        fos.close();

    }

    private ParcelFileDescriptor mDescriptor;
    private PdfRenderer mRenderer;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public Bitmap openRender(Uri uri) throws IOException {
        String pdfPath = RealPathUtil.getRealPathFromURI(context, uri);
        if (pdfPath == null) {
            return null;
        }
        File file = new File(pdfPath);
//初始化PdfRender
        mDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
        if (mDescriptor != null) {
            mRenderer = new PdfRenderer(mDescriptor);
        }
        PdfRenderer.Page currentPage = mRenderer.openPage(0);
        Bitmap whiteBitmap = Bitmap.createBitmap(1920, 1080, Bitmap.Config
                .ARGB_8888);
        Canvas canvasWhite = new Canvas(whiteBitmap);
        canvasWhite.drawColor(Color.BLACK);
        Rect rect = new Rect(0, 0, currentPage.getWidth(), currentPage.getHeight());
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        canvasWhite.drawRect(rect, paint);
        currentPage.render(whiteBitmap, rect, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

        return whiteBitmap;
    }

    /**
     * bitmap转化成16：9的尺寸
     *
     * @param
     * @return
     */
    public static Bitmap formatBitmap16_9(Bitmap bitmap, int expectWidth, int expectHeight) {


        Bitmap sourceBitmap = Bitmap.createBitmap(bitmap);

        int width = expectWidth;
        int height = expectHeight;
        //画bitmap的起点坐标
        int x, y;
        //缩放比


        if (sourceBitmap.getWidth() / sourceBitmap.getHeight() > 16 / 9) {
            width = sourceBitmap.getWidth();
            height = width * 9 / 16;
            x = 0;
            y = (height - sourceBitmap.getHeight()) / 2;
        } else {
            height = sourceBitmap.getHeight();
            width = height * 16 / 9;
            x = (width - sourceBitmap.getWidth()) / 2;
            y = 0;
        }

        Bitmap bitmap2 = Bitmap.createBitmap(width / 2 * 2, height / 2 * 2, Bitmap.Config.RGB_565);
        bitmap2.eraseColor(Color.BLACK);
        Canvas canvas = new Canvas(bitmap2);
        canvas.drawBitmap(sourceBitmap, x, y, null);

        if (!sourceBitmap.isRecycled()) {
            sourceBitmap.recycle();
        }


        return bitmap2;
    }

    /**
     * bitmap转化成16：9的尺寸
     *
     * @param
     * @return
     */
    public static Bitmap formatBitmap16_9_test(Bitmap bitmap, int expectWidth, int expectHeight) {

        Bitmap sourceBitmap = Bitmap.createBitmap(bitmap);

        int width = expectWidth;
        int height = expectHeight;
        //画bitmap的起点坐标
        int x, y;
        //缩放比
        x = (width - sourceBitmap.getWidth()) / 2;
        y = 0;
        Log.d("honorRunnable", "formatBitmap16_9:  width: " + width + "height: " + height);

        Bitmap bitmap2 = Bitmap.createBitmap(width / 2 * 2, height / 2 * 2, Bitmap.Config.RGB_565);
        bitmap2.eraseColor(Color.BLACK);
        Canvas canvas = new Canvas(bitmap2);
        canvas.drawBitmap(sourceBitmap, x, y, null);

        if (!sourceBitmap.isRecycled()) {
            sourceBitmap.recycle();
        }


        return bitmap2;
    }

    /**
     * bitmap转化成16：9的尺寸
     *
     * @param
     * @return
     */
    public static Bitmap formatBitmap16_9_No_recycle(Bitmap bitmap, int expectWidth, int expectHeight) {

        Bitmap sourceBitmap = Bitmap.createBitmap(bitmap);

        int width = expectWidth;
        int height = expectHeight;
        //画bitmap的起点坐标
        int x, y;
        //缩放比


        if (sourceBitmap.getWidth() / sourceBitmap.getHeight() > 16 / 9) {
            width = sourceBitmap.getWidth();
            height = width * 9 / 16;
            x = 0;
            y = (height - sourceBitmap.getHeight()) / 2;
        } else {
            height = sourceBitmap.getHeight();
            width = height * 16 / 9;
            x = (width - sourceBitmap.getWidth()) / 2;
            y = 0;
        }

        Bitmap bitmap2 = Bitmap.createBitmap(width / 2 * 2, height / 2 * 2, Bitmap.Config.RGB_565);
        bitmap2.eraseColor(Color.BLACK);
        Canvas canvas = new Canvas(bitmap2);
        canvas.drawBitmap(sourceBitmap, x, y, null);

//        if (!sourceBitmap.isRecycled()) {
//            sourceBitmap.recycle();
//        }


        return bitmap2;
    }



    /**
     * 压缩图片
     *
     * @param bitmap
     *          被压缩的图片
     *          大小限制
     * @return
     *          压缩后的图片
     */
    public static Bitmap compressBitmap(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int quality = 100;
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        long size = (baos.toByteArray().length / 2);
        Log.d("bitmapUtils", "pre: " + size);
        Log.d("bitmapUtils", "pre bitmap: " + bitmap.getByteCount());
        // 循环判断压缩后图片是否超过限制大小
        while(baos.toByteArray().length > size) {
            // 清空baos
            baos.reset();
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
            quality -= 10;
            Log.d("bitmapUtils", "while: ");

        }
        Log.d("bitmapUtils", "after: " + baos.toByteArray().length);

        Bitmap newBitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(baos.toByteArray()), null, null);
        Log.d("bitmapUtils", "after bitmap: " + newBitmap.getByteCount());


        return newBitmap;
    }


    public static Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Bitmap.CompressFormat Type= Bitmap.CompressFormat.JPEG;
        //image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        image.compress(Type, 70, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 300) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }
    private static void saveSignImage(Context context, Bitmap bitmap, String name) {
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, name);
//        contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/");
//        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/JPEG");
//        Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
//        if (uri != null) {
//            //若生成了uri，则表示该文件添加成功
//            //使用流将内容写入该uri中即可
//            try {
//                OutputStream outputStream = context.getContentResolver().openOutputStream(uri);
//                if (outputStream != null) {
//                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
//
//                    outputStream.flush();
//                    outputStream.close();
//
//
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
    }
}
