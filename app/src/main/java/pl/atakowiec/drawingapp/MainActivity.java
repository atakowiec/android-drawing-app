package pl.atakowiec.drawingapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private final static int[] COLORS = new int[] { Color.RED, Color.GREEN, Color.BLUE, Color.BLACK };
    private CanvasView canvasView;
    private ActivityResultLauncher<Intent> launcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.canvasView = findViewById(R.id.canvas);
        this.launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), x -> {});

        prepareColorButton(0, findViewById(R.id.colorBtn1));
        prepareColorButton(1, findViewById(R.id.colorBtn2));
        prepareColorButton(2, findViewById(R.id.colorBtn3));
        prepareColorButton(3, findViewById(R.id.colorBtn4));

        findViewById(R.id.clearBtn).setOnClickListener(v -> canvasView.clear());
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable("canvas", canvasView.onSaveInstanceState());
    }

    @Override
    public void onRestoreInstanceState(@Nullable Bundle savedInstanceState) {
        if(savedInstanceState == null) {
            return;
        }

        super.onRestoreInstanceState(savedInstanceState);

        if(savedInstanceState.getParcelable("canvas") == null) {
            return;
        }

        canvasView.onRestoreInstanceState(savedInstanceState.getParcelable("canvas"));
    }

    public void prepareColorButton(int buttonId, Button button) {
        int color = COLORS[buttonId];
        button.setBackgroundColor(color);
        button.setOnClickListener(v -> canvasView.setColor(color));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.save_painting) {
            canvasView.saveBitmap(this);
            return true;
        }
        if(item.getItemId() == R.id.show_saved) {
            Intent intent = new Intent(this, PaintingListActivity.class);
            launcher.launch(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}