
package com.example.mycalculator;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.widget.*;
import java.util.Locale;

public class MainActivity extends Activity {

    TextView display, history;
    LinearLayout root, keysLayout;

    String current = "";
    double first = 0;
    String operator = "";
    boolean professional = false;
    boolean degrees = true;
    boolean newNumber = true;

    int background = Color.rgb(12, 19, 18);
    int green = Color.rgb(0, 180, 115);
    int darkGreen = Color.rgb(0, 100, 72);
    int white = Color.WHITE;
    int greenText = Color.rgb(0, 115, 80);

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        buildScreen();
    }

    GradientDrawable rounded(int color, int radius) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(radius);
        return d;
    }

    void buildScreen() {

        ScrollView scroll = new ScrollView(this);

        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(12, 15, 12, 12);
        root.setBackgroundColor(background);

        TextView title = new TextView(this);
        title.setText("LUX CALCULATOR");
        title.setTextColor(green);
        title.setTextSize(21);
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        root.addView(title, new LinearLayout.LayoutParams(-1, 55));

        TextView subtitle = new TextView(this);
        subtitle.setText("ماشین حساب لوکس");
        subtitle.setTextColor(white);
        subtitle.setTextSize(15);
        subtitle.setGravity(Gravity.CENTER);
        root.addView(subtitle, new LinearLayout.LayoutParams(-1, 30));

        Button mode = new Button(this);
        mode.setText("حالت ساده  |  حالت حرفه‌ای");
        mode.setTextColor(white);
        mode.setTextSize(15);
        mode.setAllCaps(false);
        mode.setBackground(rounded(darkGreen, 22));

        root.addView(mode, new LinearLayout.LayoutParams(-1, 50));

        mode.setOnClickListener(v -> {
            professional = !professional;
            mode.setText(professional ?
                "حالت حرفه‌ای فعال  |  بازگشت به ساده" :
                "حالت ساده  |  حالت حرفه‌ای");
            drawKeys();
        });

        history = new TextView(this);
        history.setTextColor(green);
        history.setTextSize(16);
        history.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        history.setPadding(10, 5, 10, 5);
        root.addView(history, new LinearLayout.LayoutParams(-1, 40));

        display = new TextView(this);
        display.setText("0");
        display.setTextColor(white);
        display.setTextSize(40);
        display.setTypeface(null, Typeface.BOLD);
        display.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        display.setPadding(15, 10, 15, 10);
        display.setBackground(rounded(Color.rgb(25, 38, 34), 22));

        root.addView(display, new LinearLayout.LayoutParams(-1, 110));

        keysLayout = new LinearLayout(this);
        keysLayout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams kp =
            new LinearLayout.LayoutParams(-1, -2);
        kp.topMargin = 15;
        root.addView(keysLayout, kp);

        scroll.addView(root);
        setContentView(scroll);

        drawKeys();
    }

    void drawKeys() {

        keysLayout.removeAllViews();

        String[][] keys;

        if (professional) {
            keys = new String[][] {
                {"sin", "cos", "tan", "√"},
                {"x²", "1/x", "ln", "log"},
                {"π", "e", "(", ")"},
                {"AC", "⌫", "%", "÷"},
                {"7", "8", "9", "×"},
                {"4", "5", "6", "−"},
                {"1", "2", "3", "+"},
                {"±", "0", ".", "="},
                {"DEG", "RAD", "ساده", "پاک"}
            };
        } else {
            keys = new String[][] {
                {"AC", "⌫", "%", "÷"},
                {"7", "8", "9", "×"},
                {"4", "5", "6", "−"},
                {"1", "2", "3", "+"},
                {"±", "0", ".", "="}
            };
        }

        for (String[] row : keys) {

            LinearLayout line = new LinearLayout(this);

            for (String key : row) {

                Button btn = new Button(this);
                btn.setText(key);
                btn.setTextSize(18);
                btn.setTypeface(null, Typeface.BOLD);
                btn.setAllCaps(false);
                btn.setPadding(0, 0, 0, 0);

                int color = white;
                int textColor = greenText;

                if ("÷×−+=".contains(key) && key.length() == 1) {
                    color = darkGreen;
                    textColor = white;
                }

                if (key.equals("AC") || key.equals("=")) {
                    color = green;
                    textColor = Color.BLACK;
                }

                if (key.equals("⌫")) {
                    color = Color.rgb(55, 75, 68);
                    textColor = white;
                }

                if (key.equals("DEG") || key.equals("RAD")
                    || key.equals("ساده") || key.equals("پاک")) {
                    color = darkGreen;
                    textColor = white;
                }

                btn.setTextColor(textColor);
                btn.setBackground(rounded(color, 18));

                LinearLayout.LayoutParams bp =
                    new LinearLayout.LayoutParams(0, 62, 1);

                bp.setMargins(4, 5, 4, 5);
                line.addView(btn, bp);

                btn.setOnClickListener(v -> press(key));
            }

            keysLayout.addView(line);
        }
    }

    String format(double n) {

        if (Double.isNaN(n) || Double.isInfinite(n))
            return "خطا";

        if (Math.abs(n) < 0.000000001)
            n = 0;

        String s = String.format(Locale.US, "%.8f", n);

        while (s.contains(".") && s.endsWith("0"))
            s = s.substring(0, s.length() - 1);

        if (s.endsWith("."))
            s = s.substring(0, s.length() - 1);

        return s;
    }

    void press(String key) {

        try {

            if (key.equals("AC") || key.equals("پاک")) {
                current = "";
                operator = "";
                first = 0;
                history.setText("");
                newNumber = true;
            }

            else if (key.equals("⌫")) {
                if (!current.isEmpty()) {
                    current = current.substring(0, current.length() - 1);
                }
            }

            else if (key.equals("ساده")) {
                professional = false;
                drawKeys();
            }

            else if (key.equals("DEG")) {
                degrees = true;
                history.setText("حالت درجه فعال");
            }

            else if (key.equals("RAD")) {
                degrees = false;
                history.setText("حالت رادیان فعال");
            }

            else if (key.equals("π")) {
                current = format(Math.PI);
                newNumber = false;
            }

            else if (key.equals("e")) {
                current = format(Math.E);
                newNumber = false;
            }

            else if (key.equals("±")) {
                if (!current.isEmpty())
                    current = format(-Double.parseDouble(current));
            }

            else if (key.equals("sin") || key.equals("cos")
                    || key.equals("tan") || key.equals("√")
                    || key.equals("x²") || key.equals("1/x")
                    || key.equals("ln") || key.equals("log")) {

                scientific(key);
            }

            else if (key.equals("=")) {
                calculate();
            }

            else if ("+-−×÷".contains(key) && key.length() == 1) {

                if (!current.isEmpty()) {
                    if (!operator.isEmpty()) calculate();

                    first = Double.parseDouble(current);
                    operator = key;
                    current = "";
                    newNumber = true;
                }
            }

            else if (key.equals("%")) {
                if (!current.isEmpty())
                    current = format(Double.parseDouble(current) / 100);
            }

            else if (key.equals("(") || key.equals(")")) {
                history.setText("پرانتز در نسخه بعدی تکمیل می‌شود");
            }

            else if (key.equals(".")) {
                if (newNumber) {
                    current = "0.";
                    newNumber = false;
                } else if (!current.contains(".")) {
                    current += ".";
                }
            }

            else {
                if (newNumber) {
                    current = key;
                    newNumber = false;
                } else if (current.length() < 16) {
                    current += key;
                }
            }

            display.setText(current.isEmpty() ? "0" : current);

        } catch (Exception e) {
            current = "";
            operator = "";
            display.setText("خطا");
        }
    }

    void scientific(String key) {

        double x = current.isEmpty() ? 0 :
            Double.parseDouble(current);

        double angle = degrees ? Math.toRadians(x) : x;
        double result = 0;

        switch (key) {

            case "sin":
                result = Math.sin(angle);
                break;

            case "cos":
                result = Math.cos(angle);
                break;

            case "tan":
                result = Math.tan(angle);
                break;

            case "√":
                if (x < 0) throw new ArithmeticException();
                result = Math.sqrt(x);
                break;

            case "x²":
                result = x * x;
                break;

            case "1/x":
                if (x == 0) throw new ArithmeticException();
                result = 1 / x;
                break;

            case "ln":
                if (x <= 0) throw new ArithmeticException();
                result = Math.log(x);
                break;

            case "log":
                if (x <= 0) throw new ArithmeticException();
                result = Math.log10(x);
                break;
        }

        history.setText(key + " (" + format(x) + ")");
        current = format(result);
        newNumber = true;
    }

    void calculate() {

        if (current.isEmpty() || operator.isEmpty())
            return;

        double second = Double.parseDouble(current);
        double result = 0;

        history.setText(format(first) + " " +
            operator + " " + format(second));

        switch (operator) {

            case "+":
                result = first + second;
                break;

            case "−":
                result = first - second;
                break;

            case "×":
                result = first * second;
                break;

            case "÷":
                if (second == 0) throw new ArithmeticException();
                result = first / second;
                break;
        }

        current = format(result);
        operator = "";
        newNumber = true;
        display.setText(current);
    }
}
