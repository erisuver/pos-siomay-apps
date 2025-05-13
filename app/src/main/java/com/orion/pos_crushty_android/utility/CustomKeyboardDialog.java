package com.orion.pos_crushty_android.utility;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputEditText;
import com.orion.pos_crushty_android.R;

public class CustomKeyboardDialog extends DialogFragment {

    private static CustomKeyboardDialog instance;
    private TextInputEditText targetEditText;

    public CustomKeyboardDialog(TextInputEditText editText) {
        this.targetEditText = editText;
    }

    public static CustomKeyboardDialog getInstance(TextInputEditText editText) {
        if (instance == null) {
            instance = new CustomKeyboardDialog(editText);
        }
        return instance;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        instance = null; // Reset instance ketika dialog ditutup
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_keyboard);

        // Atur dialog agar muncul di bawah layar
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.BOTTOM);
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }

        setupKeyboardButtons(dialog);

        return dialog;
    }
    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            Window window = getDialog().getWindow();
            WindowManager.LayoutParams params = window.getAttributes();
            params.dimAmount = 0f; // Menghilangkan efek redup
            window.setAttributes(params);
        }
    }


    private void setupKeyboardButtons(Dialog dialog) {
        int[] buttonIds = {
                R.id.btn_0, R.id.btn_1, R.id.btn_2, R.id.btn_3, R.id.btn_4,
                R.id.btn_5, R.id.btn_6, R.id.btn_7, R.id.btn_8, R.id.btn_9,
                R.id.btn_00, R.id.btn_000
        };

        for (int id : buttonIds) {
            Button button = dialog.findViewById(id);
            button.setOnClickListener(v -> {
                String currentText = targetEditText.getText().toString();
                String buttonText = button.getText().toString();
                targetEditText.setText(currentText + buttonText);
                targetEditText.setSelection(targetEditText.getText().length());
            });
        }

        Button btnBackspace = dialog.findViewById(R.id.btn_backspace);
        btnBackspace.setOnClickListener(v -> {
            String currentText = targetEditText.getText().toString();
            if (!currentText.isEmpty()) {
                targetEditText.setText(currentText.substring(0, currentText.length() - 1));
                targetEditText.setSelection(targetEditText.getText().length());
            }
        });

        Button btnOK = dialog.findViewById(R.id.btn_done);
        btnOK.setOnClickListener(v -> dismiss());
    }
}
