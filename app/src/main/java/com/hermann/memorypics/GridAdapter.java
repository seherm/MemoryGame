package com.hermann.memorypics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.IOException;
import java.util.ArrayList;

import static com.hermann.memorypics.GridActivity.convertDpToPixel;

/**
 * Created by OkanO on 11/3/2016.
 */

public class GridAdapter extends BaseAdapter {
    private Context mContext;
    public int firstClick = -1, seccondClick = -1, level;

    public GridAdapter(Context c) {
        mContext = c;
    }

    public int getCount() {
        return mThumbIds.size();
    }

    public Object getItem(int position) {
        return mThumbIds.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.grid_element, parent, false);

            imageView = (ImageView) convertView.findViewById(R.id.imageView);

            WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int numberOfColumns;
            switch (level){
                case 1:
                    numberOfColumns = 4;
                    break;

                case 2:
                    numberOfColumns = 4;
                    break;

                case 3:
                    numberOfColumns = 4;
                    break;

                case 4:
                    numberOfColumns = 5;
                    break;

                default:
                    numberOfColumns = 1;
                    break;
            }

            Integer dp = convertDpToPixel(10, mContext);

            Integer dimension = (Math.min(size.x, size.y-2*dp) - (numberOfColumns + 1)*dp)/numberOfColumns;

            convertView.setLayoutParams(new GridView.LayoutParams(dimension, dimension));
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setPadding(8, 8, 8, 8);
            convertView.setTag(imageView);
        } else {
            imageView = (ImageView) convertView.getTag();
        }

        String resource;

        int color;
        int borderWidth;
        if (opened.contains(mThumbIds.get(position)) || firstClick == position || seccondClick == position){
            resource = mThumbIds.get(position);
            color = Color.parseColor("#00000000");
            borderWidth = 0;
        } else {
            resource = Main.resourceToUri(mContext, R.mipmap.cardsbackground1).toString();
            color = Color.parseColor("#ff000000");
            borderWidth = 3;
        }

        Uri uri = Uri.parse(resource);

        imageView.setImageBitmap(getRoundedCornerBitmap(uri, color, 10, borderWidth, mContext));
        return convertView;
    }

    // references to our images
    public ArrayList<String> mThumbIds;
    public ArrayList<String> opened;

    public static Bitmap getRoundedCornerBitmap(Uri imageUri, int color, int cornerDips, int borderDips, Context context) {

        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        if (height != width){
            int smallerDimension;
            int xOrigin = 0;
            int yOrigin = 0;
            if (width > height){
                smallerDimension = height;
                xOrigin = (width - height)/2;
            } else {
                smallerDimension = width;
                yOrigin = (height - width)/2;
            }

            bitmap = Bitmap.createBitmap(bitmap, xOrigin, yOrigin, smallerDimension, smallerDimension);
        }

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int borderSizePx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) borderDips,
                context.getResources().getDisplayMetrics());
        final int cornerSizePx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) cornerDips,
                context.getResources().getDisplayMetrics());
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        // prepare canvas for transfer
        paint.setAntiAlias(true);
        paint.setColor(0xFFFFFFFF);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawRoundRect(rectF, cornerSizePx, cornerSizePx, paint);

        // draw bitmap
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        // draw border
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth((float) borderSizePx);
        canvas.drawRoundRect(rectF, cornerSizePx, cornerSizePx, paint);

        return output;
    }

}
