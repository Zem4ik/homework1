package ru.ifmo.android_2016.calc;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

/**
 * Created by alexey.nikitin on 13.09.16.
 */

public final class CalculatorActivity extends Activity {
    private boolean err_flag = false;
    private boolean e_flag = false;
    private boolean dotFlag = false;
    private boolean resultCounted = false;
    private char sign = ' ';
    private StringBuilder current_number = new StringBuilder();
    private String last_result = "";
    private TextView output_result;
    private TextView output_history;
    private HorizontalScrollView historyScrollView;
    private HorizontalScrollView resultScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);
        output_result = (TextView) findViewById(R.id.output_result);
        output_history = (TextView) findViewById(R.id.output_history);
        historyScrollView = (HorizontalScrollView) findViewById(R.id.history_scrollview);
        resultScrollView = (HorizontalScrollView) findViewById(R.id.result_scrollview);
    }

    private void clear() {
        dotFlag = false;
        e_flag = false;
        current_number.setLength(0);
        sign = ' ';
        last_result = "";
    }

    private void clearCurrentNumber() {
        last_result = current_number.toString().replaceFirst("^0*", "");
        current_number.setLength(0);
        dotFlag = false;
        e_flag = false;
    }

    public void onClick(View v) {
        if (err_flag) {
            clear();
            err_flag = false;
        }
        if (resultCounted) {
            last_result = "";
            resultCounted = false;
        }
        switch (v.getId()) {
            case (R.id.d0):
            case (R.id.d1):
            case (R.id.d2):
            case (R.id.d3):
            case (R.id.d4):
            case (R.id.d5):
            case (R.id.d6):
            case (R.id.d7):
            case (R.id.d8):
            case (R.id.d9):
                if (current_number.length() > 20) break;
                current_number.append(((Button) v).getText());
                break;
            case (R.id.clear):
                clear();
                break;
            case (R.id.delete_one_symbol):
                if (current_number.length() > 0) {
                    if (current_number.charAt(current_number.length() - 1) == '.')
                        dotFlag = false;
                    if (current_number.charAt(current_number.length() - 1) == 'E')
                        e_flag = false;
                    current_number.deleteCharAt(current_number.length() - 1);
                }
                break;
            case (R.id.mul):
            case (R.id.div):
            case (R.id.sub):
            case (R.id.add):
                Button button = (Button) v;
                if (current_number.length() == 0 && output_history.getText().length() != 0) {
                    sign = button.getText().charAt(0);
                    break;
                }
                if (current_number.length() == 0) break;
                clearCurrentNumber();
                sign = button.getText().charAt(0);
                break;
            case (R.id.plus_minus):
                if (current_number.length() == 0) break;
                if (current_number.charAt(0) != '-') {
                    current_number = new StringBuilder('-' + current_number.toString());
                } else {
                    current_number.deleteCharAt(0);
                }
                break;
            case (R.id.dot):
                if (current_number.length() > 20) break;
                if (!dotFlag) {
                    current_number.append('.');
                    dotFlag = true;
                }
                break;
            case (R.id.exponential):
                if (current_number.length() > 20) break;
                if (!e_flag) {
                    current_number.append('E');
                    e_flag = true;
                }
                break;
            case (R.id.result):
                if (last_result.equals("")) break;
                countResult();
                break;
        }
        update_screen();
    }

    private void update_screen() {
        output_history.setText(last_result + ' ' + sign);
        output_result.setText(current_number.toString());
        resultScrollView.post(new Runnable() {
            public void run() {
                resultScrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
            }
        });
        historyScrollView.post(new Runnable() {
            public void run() {
                historyScrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
            }
        });
    }

    public void countResult() {
        double a;
        double b;
        double result = 0;
        try {
            a = Double.parseDouble(last_result);
            b = Double.parseDouble(current_number.toString());
        } catch (NumberFormatException e) {
            clear();
            current_number.append("Wrong number format");
            err_flag = true;
            return;
        }
        last_result += " " + sign + " " + current_number.toString() + " =";
        current_number.setLength(0);
        switch (sign) {
            case '+': {
                result = a + b;
                break;
            }
            case '-': {
                result = a - b;
                break;
            }
            case 'X': {
                result = a * b;
                break;
            }
            case '/': {
                if (b == 0) {
                    last_result = "";
                    current_number.setLength(0);
                    current_number.append("Division by zero");
                    err_flag = true;
                    sign = ' ';
                    return;
                }
                result = a / b;
                break;
            }
        }
        if (Double.isInfinite(result)) {
            err_flag = true;
        }
        if ((result == Math.floor(result)) && !Double.isInfinite(result) && result < Integer.MAX_VALUE && result > Integer.MIN_VALUE) {
            current_number.append((int) result);
        } else {
            current_number.append(result);
        }
        sign = ' ';
        resultCounted = true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("err_flag", err_flag);
        outState.putBoolean("e_flag", e_flag);
        outState.putBoolean("dotFlag", dotFlag);
        outState.putBoolean("resultCounted", resultCounted);
        outState.putChar("sign", sign);
        outState.putCharSequence("current_number", current_number);
        outState.putString("last_result", last_result);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        err_flag = savedInstanceState.getBoolean("err_flag");
        e_flag = savedInstanceState.getBoolean("e_flag");
        dotFlag = savedInstanceState.getBoolean("dotFlag");
        resultCounted = savedInstanceState.getBoolean("resultCounted");
        sign = savedInstanceState.getChar("sign");
        current_number = (StringBuilder) savedInstanceState.getCharSequence("current_number");
        last_result = savedInstanceState.getString("last_result");
        update_screen();
    }
}
