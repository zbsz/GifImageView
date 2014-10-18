package com.felipecsl.gifimageview.library;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v8.renderscript.*;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.util.Log;

import java.nio.ByteBuffer;

/**
 */
public class RsLzwDecoder {

    private final Context context;
    private final RenderScript _rs;

    private final byte[] inputData;
    private final Allocation inAllocation;
    private final Allocation dummy;
    private final Allocation pixelsAllocation;
    private final ScriptC_decode decodeScript;
    private final Allocation colorAllocation;
    private final Int4 frame;

    public RsLzwDecoder(Context context, int imageWidth, int imageHeight, int maxImageDataSize, Bitmap image) {
        this.context = context.getApplicationContext();
        _rs = RenderScript.create(this.context);

        dummy = Allocation.createSized(_rs, Element.U32(_rs), 1, Allocation.USAGE_SCRIPT);

        inputData = new byte[maxImageDataSize];
        inAllocation = Allocation.createSized(_rs, Element.U8(_rs), maxImageDataSize, Allocation.USAGE_SCRIPT);
        colorAllocation = Allocation.createSized(_rs, Element.RGBA_8888(_rs), 256, Allocation.USAGE_SCRIPT);
//        pixelsAllocation = Allocation.createSized(_rs, Element.RGBA_5551(_rs), imageWidth * imageHeight, Allocation.USAGE_SCRIPT);
//        pixelsAllocation = Allocation.createSized(_rs, Element.RGBA_8888(_rs), imageWidth * imageHeight, Allocation.USAGE_SCRIPT);
        pixelsAllocation = Allocation.createFromBitmap(_rs, image);

        decodeScript = new ScriptC_decode(_rs, context.getResources(), R.raw.decode);
        decodeScript.set_image(inAllocation);
        decodeScript.set_pixels(pixelsAllocation);
        decodeScript.set_colors(colorAllocation);

        decodeScript.set_width(imageWidth);
        decodeScript.set_height(imageHeight);

        frame = new Int4(0, 0, imageWidth, imageHeight);
        decodeScript.set_fx(0);
        decodeScript.set_fy(0);
        decodeScript.set_fw(imageWidth);
        decodeScript.set_fh(imageHeight);
    }

    public void decode(ByteBuffer input, int limit, int fx, int fy, int width, int height, int[] colors, Bitmap dst) {

        long time = System.nanoTime();

        input.get(inputData, 0, limit);
        inAllocation.copyFromUnchecked(inputData);
        colorAllocation.copyFromUnchecked(colors);

        if (fx != frame.x) {
            frame.x = fx;
            decodeScript.set_fx(fx);
        }
        if (fy != frame.y) {
            frame.y = fy;
            decodeScript.set_fy(fy);
        }
        if (width != frame.x) {
            frame.z = width;
            decodeScript.set_fw(width);
        }
        if (height != frame.w) {
            frame.w = height;
            decodeScript.set_fh(height);
        }

        long time1 = System.nanoTime();
        decodeScript.forEach_decode(dummy);
//        try {
//            Thread.sleep(5);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        long time2 = System.nanoTime();

        pixelsAllocation.copyTo(dst);
        long time3 = System.nanoTime();

        Log.d("RsLzwDecoder", "rs prepare time: " + ((time1 - time) / 1000) + " decode time: " + ((time2 - time1) / 1000) + " bitmap time: " + ((time3 - time2) / 1000));
    }

    public void destroy() {
        inAllocation.destroy();
        pixelsAllocation.destroy();
        dummy.destroy();
    }

//    public Bitmap blur(Bitmap original, float radius) {
//        int width = original.getWidth();
//        int height = original.getHeight();
//        Bitmap blurred = original.copy(Bitmap.Config.ARGB_8888, true);
//
//        ScriptC_blur blurScript = new ScriptC_blur(_rs, context.getResources(), R.raw.blur);
//
//        Allocation inAllocation = Allocation.createFromBitmap(_rs, blurred, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
//
//        blurScript.set_gIn(inAllocation);
//        blurScript.set_width(width);
//        blurScript.set_height(height);
//        blurScript.set_radius((int) radius);
//
//        int[] row_indices = new int[height];
//        for (int i = 0; i < height; i++) {
//            row_indices[i] = i;
//        }
//
//        Allocation rows = Allocation.createSized(_rs, Element.U32(_rs), height, Allocation.USAGE_SCRIPT);
//        rows.copyFrom(row_indices);
//
//        row_indices = new int[width];
//        for (int i = 0; i < width; i++) {
//            row_indices[i] = i;
//        }
//
//        Allocation columns = Allocation.createSized(_rs, Element.U32(_rs), width, Allocation.USAGE_SCRIPT);
//        columns.copyFrom(row_indices);
//
//        blurScript.forEach_blur_h(rows);
//        blurScript.forEach_blur_v(columns);
//        inAllocation.copyTo(blurred);
//
//        return blurred;
//    }
}
