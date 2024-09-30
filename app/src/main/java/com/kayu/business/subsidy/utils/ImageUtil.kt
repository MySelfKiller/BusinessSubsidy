package com.kayu.business.subsidy.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import coil.imageLoader
import coil.request.ImageRequest
import coil.request.ImageResult
import coil.request.SuccessResult

object ImageUtil {
    fun compoundBitmap(bitmapOne: Bitmap?, bitmapTwo: Bitmap?, x: Int, y: Int): Bitmap? {
        if (null == bitmapOne || null == bitmapTwo) return null
        val newBitmap: Bitmap
        newBitmap = bitmapOne.copy(Bitmap.Config.ARGB_8888, true)
        // newBitmap = Bitmap.createBitmap(bitmapOne.getWidth(),bitmapOne.getHeight(),bitmapOne.getConfig());
        val canvas = Canvas(newBitmap)
        val paint = Paint()
        val w = bitmapOne.width
        val h = bitmapOne.height
        val w_2 = bitmapTwo.width
        val h_2 = bitmapTwo.height
        // paint = new Paint();
//设置第二张图片的 左、上的位置坐标
        var litW = x
        var litH = y
        if (x == 0 || y == 0 ||x>w || y>h) {
            litW = (w - w_2) / 2
            litH = h - h_2 - 20
        }
        canvas.drawBitmap(
            bitmapTwo, litW.toFloat(),
            litH.toFloat(), paint
        )
        canvas.save()
        // 存储新合成的图片
        canvas.restore()
        return newBitmap
    } //    /**
    //     * 保存图片到指定路径
    //     *
    //     * @param context
    //     * @return
    //     */
    //    public static boolean saveImageToGallery(Context context, Bitmap bitmap, String fileName) {
    //        // 保存图片至指定路径
    //        String storePath = Utils.getEnaviBaseStorage(context)+ File.separator + Constants.PATH_IMG ;
    //        File appDir = new File(storePath);
    //        if (!appDir.exists()) {
    //            appDir.mkdir();
    //        }
    //        File file = new File(appDir, fileName);
    ////        if (file.exists()) {
    ////            return true;
    ////        }
    //        try {
    //            FileOutputStream fos = new FileOutputStream(file);
    //            //通过io流的方式来压缩保存图片(80代表压缩20%)
    //            boolean isSuccess = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
    //            fos.flush();
    //            fos.close();
    //
    //            //发送广播通知系统图库刷新数据
    //            Uri uri = Uri.fromFile(file);
    //            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
    //            if (isSuccess) {
    //                return true;
    //            } else {
    //                return false;
    //            }
    //        } catch (IOException e) {
    //            e.printStackTrace();
    //        }
    //        return false;
    //    }
    //    public static void sendToGallery(Context context, String filePath) {
    //        //发送广播通知系统图库刷新数据
    //        Uri uri = Uri.fromFile(new File(filePath));
    //        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
    //    }
}

/**
 * 从图片地址获取Bitmap
 * @receiver Context
 * @param url String
 * @return Bitmap?
 */
suspend fun Context.getImageBitmapByUrl(url: String): Bitmap? {
    val request = ImageRequest.Builder(this)
        .data(url)
        .allowHardware(false)
        .build()
    val result = (imageLoader.execute(request)).drawable
    return if(null == result ) null else (result as BitmapDrawable).bitmap
}