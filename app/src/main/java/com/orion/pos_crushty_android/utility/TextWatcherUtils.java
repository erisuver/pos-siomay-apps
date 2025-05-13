package com.orion.pos_crushty_android.utility;

import android.text.Editable;
import android.text.TextWatcher;

import com.google.android.material.textfield.TextInputEditText;
import com.orion.pos_crushty_android.globals.Global;

public class TextWatcherUtils {
    public interface AfterTextChangedListener {
        void afterTextChanged(Editable editable);
    }

    public static TextWatcher formatAmountTextWatcher(final TextInputEditText editText) {
        return new TextWatcher() {
            private boolean isEditing = false;
            private int newTextCount = 0;
            private int textCount = 0;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // No action needed
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String input = charSequence.toString();
                if (input.equals("")) {
                    editText.setText("0");
                    editText.selectAll();
                }

                textCount = input.length();
                if (textCount != newTextCount) {
                    try {
                        String newPrice = Global.FloatToStrFmt(Global.StrFmtToFloat(input));
                        editText.removeTextChangedListener(this); // Prevent infinite loop
                        if (newPrice.length() >= 11) {
                            newPrice = "99.999.999";
                        }
                        editText.setText(newPrice);
                        editText.setSelection(newPrice.length()); // Move cursor to end of string
                        editText.addTextChangedListener(this);
                    } catch (NumberFormatException nfe) {
                        nfe.printStackTrace();
                    }
                }
                newTextCount = textCount;
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
    }

    public static TextWatcher formatAmountTextWatcher(final TextInputEditText editText, final AfterTextChangedListener listener) {
        return new TextWatcher() {
            private boolean isEditing = false;
            private int newTextCount = 0;
            private int textCount = 0;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // No action needed
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String input = charSequence.toString();
                if (input.equals("")) {
                    editText.setText("0");
                    editText.selectAll();
                }
                input = input.replaceAll("[^\\d]", "");
                textCount = input.length();
                if (textCount != newTextCount) {
                    try {
                        String newPrice = Global.FloatToStrFmt(Global.StrFmtToFloat(input));
                        editText.removeTextChangedListener(this); // Prevent infinite loop
                        if (newPrice.length() < 11) {
                            editText.setText(newPrice);
                        } else {
                            newPrice = "99.999.999";
                            editText.setText(newPrice);
                        }
                        editText.setSelection(newPrice.length()); // Move cursor to end of string
                        editText.addTextChangedListener(this);
                    } catch (NumberFormatException nfe) {
                        nfe.printStackTrace();
                    }
                }
                newTextCount = textCount;
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Call the listener's afterTextChanged method if provided
                if (listener != null) {
                    listener.afterTextChanged(editable);
                }
            }
        };
    }
    public static TextWatcher formatNumberDecimalTextWatcher(final TextInputEditText editText, final AfterTextChangedListener listener) {
        return new TextWatcher() {
            private boolean isEditing = false;
            private int newTextCount = 0;
            private int textCount = 0;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // No action needed
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String input = charSequence.toString();
                if (input.equals("")) {
                    editText.setText("0,0");
                    editText.selectAll();
                }

                input = input.replaceAll("[^\\d]", "");
                textCount = input.length();
                if (textCount != newTextCount) {
                    try {
                        String newPrice = Global.FloatToStrFmt2(Global.StrFmtToFloatInput(input));
                        editText.removeTextChangedListener(this); // Prevent infinite loop
                        if (newPrice.length() < 11) {
                            editText.setText(newPrice);
                        } else {
                            newPrice = "99.999.999";
                            editText.setText(newPrice);
                        }
                        editText.setSelection(newPrice.length()); // Move cursor to end of string
                        editText.addTextChangedListener(this);
                    } catch (NumberFormatException nfe) {
                        nfe.printStackTrace();
                    }
                }
                newTextCount = textCount;
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Call the listener's afterTextChanged method if provided
                if (listener != null) {
                    listener.afterTextChanged(editable);
                }
            }
        };
    }
}

