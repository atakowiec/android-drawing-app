package pl.atakowiec.drawingapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private final static String POPUP_SHOWN_KEY = "popupShown";
    private final static String CONFIRMATION_SHOWN_KEY = "confirmationShown";
    private final static String CANVAS_KEY = "canvas";
    private final static String ENTERED_NAME_KEY = "enteredName";
    private int[] COLORS;
    private CanvasView canvasView;
    private ActivityResultLauncher<Intent> launcher;
    private boolean confirmationPopupShown = false;
    private boolean popupShown = false;
    private String enteredDrawingName = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.COLORS = new int[]{
                getColor(R.color.color1),
                getColor(R.color.color2),
                getColor(R.color.color3),
                getColor(R.color.color4)};

        setContentView(R.layout.activity_main);

        this.canvasView = findViewById(R.id.canvas);
        this.launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), x -> {
        });

        prepareColorButton(0, findViewById(R.id.colorBtn1));
        prepareColorButton(1, findViewById(R.id.colorBtn2));
        prepareColorButton(2, findViewById(R.id.colorBtn3));
        prepareColorButton(3, findViewById(R.id.colorBtn4));

        findViewById(R.id.clearBtn).setOnClickListener(v -> canvasView.clear());
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(CANVAS_KEY, canvasView.onSaveInstanceState());
        outState.putBoolean(POPUP_SHOWN_KEY, popupShown);
        outState.putString(ENTERED_NAME_KEY, enteredDrawingName);
        outState.putBoolean(CONFIRMATION_SHOWN_KEY, confirmationPopupShown);
    }

    @Override
    public void onRestoreInstanceState(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            return;
        }

        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState.getParcelable(CANVAS_KEY) != null) {
            canvasView.onRestoreInstanceState(savedInstanceState.getParcelable(CANVAS_KEY));
        }
        enteredDrawingName = savedInstanceState.getString(ENTERED_NAME_KEY, "");

        if(savedInstanceState.getBoolean(POPUP_SHOWN_KEY)) {
            showEnterNamePopup();
        }

        if(savedInstanceState.getBoolean(CONFIRMATION_SHOWN_KEY)) {
            showConfirmationPopup(enteredDrawingName);
        }
    }

    public void prepareColorButton(int buttonId, Button button) {
        int color = COLORS[buttonId];
        button.setBackgroundColor(color);
        button.setOnClickListener(v -> canvasView.setColor(color));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.save_painting) {
            enteredDrawingName = "";
            showEnterNamePopup();
            return true;
        }
        if (item.getItemId() == R.id.show_saved) {
            Intent intent = new Intent(this, PaintingListActivity.class);
            launcher.launch(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showEnterNamePopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.insert_name_popup, null);
        builder.setView(view);

        builder.setOnCancelListener((v) -> popupShown = false);

        Button confirmButton = view.findViewById(R.id.save_btn);
        EditText editText = view.findViewById(R.id.insert_name_et);
        editText.setText(enteredDrawingName);

        AlertDialog dialog = builder.create();
        dialog.show();
        popupShown = true;

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                enteredDrawingName = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        confirmButton.setOnClickListener(v -> {
            if(editText.getText().toString().isEmpty()){
                editText.setError(getString(R.string.empty_name_error));
                return;
            }
            popupShown = false;

            String name = editText.getText().toString();

            dialog.dismiss();
            canvasView.saveBitmap(this, name, false);
        });
    }

    public void showConfirmationPopup(String name) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.confirm_override_popup, null);
        builder.setView(view);

        builder.setOnCancelListener((v) -> confirmationPopupShown = false);

        Button confirmButton = view.findViewById(R.id.confirm_override_btn);

        AlertDialog dialog = builder.create();
        dialog.show();
        confirmationPopupShown = true;

        confirmButton.setOnClickListener((v) -> {
            canvasView.saveBitmap(this, name, true);
            dialog.dismiss();
            confirmationPopupShown = false;
        });
    }

    public void setEnteredDrawingName(String enteredDrawingName) {
        this.enteredDrawingName = enteredDrawingName;
    }
}