package com.example.frank.rgbtest;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.AvoidXfermode;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;


public class MainActivity extends Activity {
    //1
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        //setContentView(R.layout.activity_main);
//
//        // Load both of our images from our application's resources.
//        Resources r = getResources();
//        Bitmap resource24 = BitmapFactory.decodeResource(r, R.raw.spectrum_gray_nodither_24);
//        Bitmap resource32 = BitmapFactory.decodeResource(r, R.raw.spectrum_gray_nodither_32);
//
//        // Print some log statements to show what pixel format these images were decoded with.
//        Log.d("FormatTest", "Resource24: " + resource24.getConfig()); // Resource24: RGB_565
//        Log.d("FormatTest", "Resource32: " + resource32.getConfig()); // Resource32: ARGB_8888
//
//        // Create two image views to show these bitmaps in.
//        ImageView image24 = new ImageView(this);
//        ImageView image32 = new ImageView(this);
//        image24.setImageBitmap(resource24);
//        image32.setImageBitmap(resource32);
//
//        //qian.chen
//        //Drawable drawable32 = image32.getDrawable();
//        //drawable32.setDither(true);
//
//        // Create a simple layout t
//        // o show these two image views side-by-side.
//        LayoutParams wrap = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//        LayoutParams fill = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
//        RelativeLayout.LayoutParams params24 = new RelativeLayout.LayoutParams(wrap);
//        params24.addRule(RelativeLayout.CENTER_VERTICAL);
//        params24.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
//        RelativeLayout.LayoutParams params32 = new RelativeLayout.LayoutParams(wrap);
//        params32.addRule(RelativeLayout.CENTER_VERTICAL);
//        params32.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//        RelativeLayout layout = new RelativeLayout(this);
//        layout.addView(image24, params24);
//        layout.addView(image32, params32);
//        layout.setBackgroundColor(Color.BLACK);
//
//        // Show this layout in our activity.
//        setContentView(layout, fill);
//    }

    //3
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load both of our images from our application's resources.
        Resources r = getResources();
        Bitmap resource24 = BitmapFactory.decodeResource(r, R.raw.spectrum_gray_nodither_24);
        Bitmap resource32 = BitmapFactory.decodeResource(r, R.raw.spectrum_gray_nodither_32);

        Log.d("FormatTest","Resource24: " + resource24.getConfig()); // Resource24: RGB_565
        Log.d("FormatTest","Resource32: " + resource32.getConfig()); // Resource32: ARGB_8888

        // Sadly, the images we have decoded from our resources are immutable. Since we want to
        // change them, we need to copy them into new mutable bitmaps, giving each of them the same
        // pixel format as their source.
        Bitmap bitmap24 = resource24.copy(resource24.getConfig(), true);
        Bitmap bitmap32 = resource32.copy(resource32.getConfig(), true);

        // Save the dimensions of these images.
        int width = bitmap24.getWidth();
        int height = bitmap24.getHeight();

        // Create a new paint object that we will use to manipulate our images. This will tell
        // Android that we want to replace any color in our image that is even remotely similar to
        // 0xFF307070 (a dark teal) with 0xFF000000 (black).
        Paint avoid1Paint = new Paint();
        avoid1Paint.setColor(0xFF000000);
        avoid1Paint.setXfermode(new AvoidXfermode(0xFF307070, 255, AvoidXfermode.Mode.TARGET));

        // Make another paint object, but this one will replace any color that is similar to a
        // 0xFF00C000 (green) with 0xFF0070D0 (skyish blue) instead.
        Paint avoid2Paint = new Paint();
        avoid2Paint.setColor(0xFF0070D0);
        avoid2Paint.setXfermode(new AvoidXfermode(0xFF00C000, 245, AvoidXfermode.Mode.TARGET));

        Paint fadePaint = new Paint();
        int[] fadeColors = {0x00000000, 0xFF000000, 0xFF000000, 0x00000000};
        fadePaint.setShader(new LinearGradient(0, 0, 0, height, fadeColors, null,
                LinearGradient.TileMode.CLAMP));
        fadePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));

        // Create a new canvas for our bitmaps, and draw a full-sized rectangle to each of them
        // which will apply the paint object we just created.
        Canvas canvas = new Canvas();
        canvas.setBitmap(bitmap24);
        canvas.drawRect(0, 0, width, height, avoid1Paint);
        canvas.drawRect(0, 0, width, height, avoid2Paint);
        canvas.drawRect(0, 0, width, height, fadePaint);
        canvas.setBitmap(bitmap32);
        canvas.drawRect(0, 0, width, height, avoid1Paint);
        canvas.drawRect(0, 0, width, height, avoid2Paint);
        canvas.drawRect(0, 0, width, height, fadePaint);

        // Create a 16-bit RGB565 bitmap that we will draw our 32-bit image to with dithering. We
        // only need to do this for our 32-bit image, and not our 24-bit image, because it is
        // already in the RGB565 format.
        Bitmap final32 = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

        // Create a new paint object that we will use to draw our bitmap with. This is how we tell
        // Android that we want to dither the 32-bit image when it gets drawn to our 16-bit final
        // bitmap.
        Paint ditherPaint = new Paint();
        ditherPaint.setDither(true);

        // Using our canvas from above, draw our 32-bit image to it with the paint object we just
        // created.
        canvas.setBitmap(final32);
        canvas.drawBitmap(bitmap32, 0, 0, ditherPaint);

        // Create two image views to show these bitmaps in.
        ImageView image24 = new ImageView(this);
        ImageView image32 = new ImageView(this);
        image24.setImageBitmap(bitmap24);
        image32.setImageBitmap(final32);

        // Create a simple layout to show these two image views side-by-side.
        LayoutParams wrap = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        LayoutParams fill = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        RelativeLayout.LayoutParams params24 = new RelativeLayout.LayoutParams(wrap);
        params24.addRule(RelativeLayout.CENTER_VERTICAL);
        params24.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        RelativeLayout.LayoutParams params32 = new RelativeLayout.LayoutParams(wrap);
        params32.addRule(RelativeLayout.CENTER_VERTICAL);
        params32.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        RelativeLayout layout = new RelativeLayout(this);
        layout.addView(image24, params24);
        layout.addView(image32, params32);
        layout.setBackgroundColor(Color.BLACK);

        // Show this layout in our activity.
        setContentView(layout, fill);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
