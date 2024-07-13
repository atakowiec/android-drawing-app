package pl.atakowiec.drawingapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class CanvasView extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    private final static String DRAWING_DIRECTORY = "drawings";
    private final Object LOCK = new Object();
    private final SurfaceHolder surfaceHolder;
    private final Paint paint;
    private boolean drawingThreadRunning = false;
    private Point touchPoint = null;
    private Point previousPoint = null;
    private Bitmap bitmap;
    private Canvas bitmapCanvas;
    private Bitmap loadedBitmap = null;

    public CanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);

        paint = new Paint();
        paint.setColor(context.getColor(R.color.color1));
        paint.setStrokeWidth(10);
    }

    public void resumeDrawing() {
        drawingThreadRunning = true;
        new Thread(this).start();
    }

    public void pauseDrawing() {
        drawingThreadRunning = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();

        synchronized (LOCK) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touchPoint = new Point(x, y);
                    previousPoint = null;
                    draw(bitmapCanvas);
                    drawCircle(bitmapCanvas, x, y);
                    break;
                case MotionEvent.ACTION_MOVE:
                    setTouchPoint(x, y);
                    draw(bitmapCanvas);
                    break;
                case MotionEvent.ACTION_UP:
                    touchPoint = null;
                    previousPoint = null;
                    drawCircle(bitmapCanvas, x, y);
                    break;
            }
        }
        return true;
    }

    public void draw(Canvas canvas) {
        if (canvas == null || paint == null)
            return;

        if (touchPoint == null)
            return;

        if (previousPoint == null) {
            canvas.drawPoint(touchPoint.x, touchPoint.y, paint);
        } else {
            canvas.drawLine(previousPoint.x, previousPoint.y, touchPoint.x, touchPoint.y, paint);
        }
    }

    public void drawCircle(Canvas canvas, int x, int y) {
        if (canvas == null || paint == null)
            return;

        canvas.drawCircle(x, y, 20, paint);
    }

    public void setTouchPoint(int x, int y) {
        if (touchPoint != null) {
            previousPoint = new Point(touchPoint.x, touchPoint.y);
        }

        if (touchPoint == null) {
            touchPoint = new Point(x, y);
            previousPoint = null;
            return;
        }

        touchPoint.set(x, y);
    }

    @Override
    public void run() {
        while (drawingThreadRunning) {
            Canvas canvas = null;
            try {
                synchronized (surfaceHolder) {
                    if (!surfaceHolder.getSurface().isValid())
                        continue;

                    canvas = surfaceHolder.lockCanvas();
                    canvas.drawBitmap(bitmap, 0, 0, paint);
                }
            } finally {
                if (canvas != null) {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        int size = Math.max(getWidth(), getHeight());
        bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        bitmapCanvas = new Canvas(bitmap);
        bitmapCanvas.drawColor(Color.WHITE);

        if (loadedBitmap != null) {
            bitmapCanvas.drawBitmap(loadedBitmap, 0, 0, paint);
        }

        resumeDrawing();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        drawingThreadRunning = false;
    }

    public void setColor(int color) {
        paint.setColor(color);
    }

    public void clear() {
        bitmapCanvas.drawColor(Color.WHITE);
    }

    public void saveBitmap(MainActivity mainActivity, String name, boolean force) {
        File directory = new File(mainActivity.getFilesDir(), DRAWING_DIRECTORY);

        if (!directory.exists()) {
            directory.mkdirs();
        }

        File file = new File(directory, name + ".png");
        if(file.exists() && !force) {
            mainActivity.showConfirmationPopup(name);
            return;
        }

        mainActivity.setEnteredDrawingName("");

        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(mainActivity, mainActivity.getString(R.string.failed_to_save_drawing), Toast.LENGTH_SHORT).show();
            return;
        }

        try (FileOutputStream stream = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            Toast.makeText(mainActivity, mainActivity.getString(R.string.drawing_saved), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(mainActivity, mainActivity.getString(R.string.failed_to_save_drawing), Toast.LENGTH_SHORT).show();
        }
    }

    public byte[] getBitmapAsByteArray() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public void setBitmapFromByteArray(byte[] byteArray) {
        loadedBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("superState", super.onSaveInstanceState());
        bundle.putByteArray("bitmap", getBitmapAsByteArray());
        bundle.putInt("paint", paint.getColor());
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            byte[] bitmapArray = bundle.getByteArray("bitmap");
            int color = bundle.getInt("paint");
            paint.setColor(color);

            if (bitmapArray != null) {
                setBitmapFromByteArray(bitmapArray);
            }
            state = bundle.getParcelable("superState");
        }
        super.onRestoreInstanceState(state);
    }
}