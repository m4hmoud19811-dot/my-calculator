package com.example.mycalculator;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

import java.util.Locale;

public class MainActivity extends Activity {
    private LinearLayout root, keypad;
    private TextView display, history, modeButton, themeButton, brand, subtitle, foot;
    private boolean scientific = false, degrees = true, resultShown = false;
    private String expression = "";
    private boolean lightTheme = false;
    private int bg, panel, emerald, deep, white, muted, keyFace;

    private void applyPalette() {
        if (lightTheme) {
            bg = Color.rgb(242, 247, 244); panel = Color.rgb(255, 255, 255);
            emerald = Color.rgb(0, 145, 96); deep = Color.rgb(0, 92, 68);
            white = Color.rgb(24, 47, 39); muted = Color.rgb(105, 126, 116);
            keyFace = Color.rgb(225, 241, 235);
        } else {
            bg = Color.rgb(10, 16, 15); panel = Color.rgb(22, 32, 29);
            emerald = Color.rgb(0, 190, 125); deep = Color.rgb(0, 103, 75);
            white = Color.WHITE; muted = Color.rgb(160, 178, 169);
            keyFace = Color.rgb(29, 43, 42);
        }
    }

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        lightTheme = getPreferences(0).getBoolean("light_theme", false);
        applyPalette();
        getWindow().setStatusBarColor(bg);
        getWindow().setNavigationBarColor(bg);
        buildScreen();
    }

    private GradientDrawable shape(int color, int radius) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(radius);
        return d;
    }

    // Layered translucent glass: the keypad keeps the parent surface visible,
    // with a bright upper reflection and a fine turquoise glass rim.
    private android.graphics.drawable.Drawable glassShape(int color, int radius) {
        int top = Color.argb(lightTheme ? 118 : 105,
                Color.red(color), Color.green(color), Color.blue(color));
        int bottom = Color.argb(lightTheme ? 165 : 148,
                Color.red(color), Color.green(color), Color.blue(color));
        GradientDrawable base = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM, new int[]{top, bottom});
        base.setCornerRadius(radius);
        base.setStroke(dp(1), Color.argb(lightTheme ? 190 : 220, 105, 255, 238));

        GradientDrawable reflection = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{Color.argb(lightTheme ? 78 : 66, 255, 255, 255),
                          Color.argb(0, 255, 255, 255)});
        reflection.setCornerRadius(radius);
        reflection.setStroke(dp(1), Color.argb(75, 255, 255, 255));

        android.graphics.drawable.LayerDrawable glass =
                new android.graphics.drawable.LayerDrawable(
                        new android.graphics.drawable.Drawable[]{base, reflection});
        glass.setLayerInset(1, dp(2), dp(2), dp(2), dp(18));
        return glass;
    }

    private void buildScreen() {
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(16), dp(18), dp(16), dp(16));
        root.setBackgroundColor(bg);

        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        brand = new TextView(this);
        brand.setText("LUX");
        brand.setTextColor(emerald);
        brand.setTextSize(25);
        brand.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        brand.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        header.addView(brand, new LinearLayout.LayoutParams(0, dp(42), 1));

        themeButton = new TextView(this);
        themeButton.setText("◐  ▾");
        themeButton.setGravity(Gravity.CENTER);
        themeButton.setTextSize(15);
        themeButton.setTypeface(null, Typeface.BOLD);
        themeButton.setTextColor(emerald);
        themeButton.setBackground(shape(panel, dp(14)));
        themeButton.setElevation(dp(3));
        header.addView(themeButton, new LinearLayout.LayoutParams(dp(54), dp(36)));
        root.addView(header, new LinearLayout.LayoutParams(-1, dp(42)));

        subtitle = new TextView(this);
        subtitle.setText("CALCULATOR  •  ماشین حساب");
        subtitle.setTextColor(muted);
        subtitle.setTextSize(11);
        subtitle.setGravity(Gravity.CENTER);
        root.addView(subtitle, new LinearLayout.LayoutParams(-1, dp(24)));

        modeButton = new TextView(this);
        modeButton.setGravity(Gravity.CENTER);
        modeButton.setTextSize(14);
        modeButton.setTypeface(null, Typeface.BOLD);
        modeButton.setTextColor(white);
        modeButton.setBackground(shape(deep, dp(18)));
        modeButton.setPadding(dp(12), 0, dp(12), 0);
        LinearLayout.LayoutParams mp = new LinearLayout.LayoutParams(-1, dp(46));
        mp.topMargin = dp(14);
        root.addView(modeButton, mp);
        themeButton.setOnClickListener(v -> {
            android.widget.PopupMenu menu = new android.widget.PopupMenu(MainActivity.this, themeButton);
            menu.getMenu().add("تم مشکی و فیروزه‌ای").setOnMenuItemClickListener(item -> {
                lightTheme = false;
                getPreferences(0).edit().putBoolean("light_theme", false).apply();
                applyPalette();
                refreshTheme();
                return true;
            });
            menu.getMenu().add("تم روشن و فیروزه‌ای").setOnMenuItemClickListener(item -> {
                lightTheme = true;
                getPreferences(0).edit().putBoolean("light_theme", true).apply();
                applyPalette();
                refreshTheme();
                return true;
            });
            menu.show();
        });
        modeButton.setOnClickListener(v -> {
            scientific = !scientific;
            updateMode();
            drawKeys();
        });

        history = new TextView(this);
        history.setTextColor(muted);
        history.setTextSize(14);
        history.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        history.setSingleLine(true);
        history.setPadding(dp(8), 0, dp(8), 0);
        LinearLayout.LayoutParams hp = new LinearLayout.LayoutParams(-1, dp(38));
        hp.topMargin = dp(14);
        root.addView(history, hp);

        display = new TextView(this);
        display.setText("0");
        display.setTextColor(white);
        display.setTextSize(38);
        display.setTypeface(null, Typeface.BOLD);
        display.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        display.setSingleLine(true);
        display.setPadding(dp(16), dp(8), dp(16), dp(8));
        display.setBackground(shape(panel, dp(22)));
        root.addView(display, new LinearLayout.LayoutParams(-1, dp(104)));

        keypad = new LinearLayout(this);
        keypad.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams kp = new LinearLayout.LayoutParams(-1, -2);
        kp.topMargin = dp(14);
        root.addView(keypad, kp);

        foot = new TextView(this);
        foot.setText("LUX CALCULATOR  •  SIMPLE & SCIENTIFIC");
        foot.setTextColor(Color.rgb(92, 116, 105));
        foot.setTextSize(9);
        foot.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams fp = new LinearLayout.LayoutParams(-1, dp(32));
        fp.topMargin = dp(8);
        root.addView(foot, fp);

        scroll.addView(root);
        setContentView(scroll);
        updateMode();
        updateThemeLabel();
        drawKeys();
    }

    private void updateMode() {
        modeButton.setText(scientific ? "حالت علمی فعال  •  بازگشت به ساده" : "حالت ساده  •  نمایش ابزارهای علمی");
    }

    private void updateThemeLabel() {
        themeButton.setText("◐  ▾");
        themeButton.setContentDescription(lightTheme ? "انتخاب تم روشن" : "انتخاب تم تیره");
    }

    private void refreshTheme() {
        root.setBackgroundColor(bg);
        brand.setTextColor(emerald);
        subtitle.setTextColor(muted);
        history.setTextColor(muted);
        display.setTextColor(white);
        display.setBackground(shape(panel, dp(22)));
        modeButton.setTextColor(Color.WHITE);
        modeButton.setBackground(shape(deep, dp(18)));
        themeButton.setTextColor(white);
        themeButton.setBackground(shape(panel, dp(16)));
        foot.setTextColor(muted);
        getWindow().setStatusBarColor(bg);
        getWindow().setNavigationBarColor(bg);
        drawKeys();
        updateThemeLabel();
    }

    private void drawKeys() {
        keypad.removeAllViews();
        String[][] rows = scientific ? new String[][] {
            {"sin","cos","tan","√"},
            {"x²","1/x","ln","log"},
            {"π","e","(",")"},
            {"AC","⌫","%","÷"},
            {"7","8","9","×"},
            {"4","5","6","−"},
            {"1","2","3","+"},
            {"±","0",".","="},
            {"DEG","RAD",""}
        } : new String[][] {
            {"AC","⌫","%","÷"},
            {"7","8","9","×"},
            {"4","5","6","−"},
            {"1","2","3","+"},
            {"±","0",".","="}
        };

        for (String[] row : rows) {
            LinearLayout line = new LinearLayout(this);
            line.setOrientation(LinearLayout.HORIZONTAL);
            for (String key : row) {
                if (key.isEmpty()) {
                    View spacer = new View(this);
                    line.addView(spacer, new LinearLayout.LayoutParams(0, dp(58), 1));
                    continue;
                }
                TextView button = new TextView(this);
                button.setText(key);
                button.setTextSize(key.length() > 2 ? 14 : 19);
                button.setTypeface(null, Typeface.BOLD);
                button.setGravity(Gravity.CENTER);
                int color = keyFace;
                int ink = lightTheme ? Color.rgb(10, 48, 39) : Color.WHITE;
                if ("÷×−+".contains(key) && key.length() == 1) {
                    color = lightTheme ? Color.rgb(0, 137, 112) : Color.rgb(0, 139, 125);
                    ink = Color.WHITE;
                }
                if (key.equals("=")) {
                    color = Color.rgb(0, 210, 190);
                    ink = Color.rgb(2, 35, 33);
                }
                if (key.equals("AC")) {
                    color = lightTheme ? Color.rgb(255, 224, 222) : Color.rgb(113, 48, 52);
                    ink = lightTheme ? Color.rgb(150, 35, 38) : Color.rgb(255, 224, 224);
                }
                if (key.equals("⌫")) {
                    color = lightTheme ? Color.rgb(202, 226, 219) : Color.rgb(48, 76, 74);
                    ink = lightTheme ? Color.rgb(15, 65, 57) : Color.WHITE;
                }
                if (key.equals("DEG") || key.equals("RAD")) {
                    color = lightTheme ? Color.rgb(0, 137, 112) : Color.rgb(0, 111, 105);
                    ink = Color.WHITE;
                }
                button.setTextColor(ink);
                button.setBackground(glassShape(color, dp(17)));
                button.setElevation(dp(3));
                LinearLayout.LayoutParams bp = new LinearLayout.LayoutParams(0, dp(58), 1);
                bp.setMargins(dp(4), dp(4), dp(4), dp(4));
                line.addView(button, bp);
                button.setOnClickListener(v -> press(key));
            }
            keypad.addView(line);
        }
    }

    private int dp(float value) {
        return (int)(value * getResources().getDisplayMetrics().density + 0.5f);
    }

    private void showExpression() {
        display.setText(expression.isEmpty() ? "0" : expression);
    }

    private void press(String key) {
        try {
            switch (key) {
                case "AC":
                    expression = "";
                    history.setText("");
                    resultShown = false;
                    break;
                case "⌫":
                    if (!expression.isEmpty()) expression = expression.substring(0, expression.length() - 1);
                    resultShown = false;
                    break;
                case "=":
                    if (!expression.trim().isEmpty()) {
                        String old = expression;
                        double value = new Parser(expression).parse();
                        expression = format(value);
                        history.setText(old + "  =");
                        resultShown = true;
                    }
                    break;
                case "DEG": degrees = true; history.setText("زاویه: درجه"); break;
                case "RAD": degrees = false; history.setText("زاویه: رادیان"); break;
                case "±":
                    if (expression.isEmpty()) expression = "-";
                    else if (resultShown) expression = format(-new Parser(expression).parse());
                    else expression = "-(" + expression + ")";
                    resultShown = false;
                    break;
                case "sin": case "cos": case "tan": case "√": case "x²": case "1/x": case "ln": case "log":
                    applyScientific(key);
                    break;
                case "π": appendValue("3.141592653589793"); break;
                case "e": appendValue("2.718281828459045"); break;
                default:
                    if (resultShown && (isDigit(key) || key.equals("."))) expression = "";
                    resultShown = false;
                    if (key.equals(".") && lastNumberHasDot()) break;
                    expression += key;
                    break;
            }
            showExpression();
        } catch (Exception ex) {
            history.setText("عبارت نامعتبر است");
            display.setText("خطا");
            expression = "";
            resultShown = false;
        }
    }

    private void appendValue(String value) {
        if (resultShown) expression = "";
        expression += value;
        resultShown = false;
    }

    private boolean isDigit(String s) { return s.length() == 1 && s.charAt(0) >= '0' && s.charAt(0) <= '9'; }

    private boolean lastNumberHasDot() {
        for (int i = expression.length() - 1; i >= 0; i--) {
            char c = expression.charAt(i);
            if (c == '.') return true;
            if (!(Character.isDigit(c))) break;
        }
        return false;
    }

    private void applyScientific(String fn) {
        if (expression.isEmpty()) return;
        double x = new Parser(expression).parse();
        double a = degrees ? Math.toRadians(x) : x;
        double y;
        switch (fn) {
            case "sin": y = Math.sin(a); break;
            case "cos": y = Math.cos(a); break;
            case "tan": y = Math.tan(a); break;
            case "√": if (x < 0) throw new ArithmeticException(); y = Math.sqrt(x); break;
            case "x²": y = x * x; break;
            case "1/x": if (x == 0) throw new ArithmeticException(); y = 1 / x; break;
            case "ln": if (x <= 0) throw new ArithmeticException(); y = Math.log(x); break;
            default: if (x <= 0) throw new ArithmeticException(); y = Math.log10(x); break;
        }
        history.setText(fn + "(" + format(x) + ")");
        expression = format(y);
        resultShown = true;
    }

    private String format(double n) {
        if (Double.isNaN(n) || Double.isInfinite(n)) throw new ArithmeticException();
        if (Math.abs(n) < 1e-10) n = 0;
        return String.format(Locale.US, "%.8f", n).replaceAll("0+$", "").replaceAll("\\.$", "");
    }

    // Recursive-descent calculator: parentheses, unary signs, %, and operator precedence.
    private static class Parser {
        private final String s;
        private int pos = 0;
        Parser(String input) { s = input.replace("×", "*").replace("÷", "/").replace("−", "-").replace(" ", ""); }
        double parse() {
            double value = expression();
            if (pos != s.length()) throw new IllegalArgumentException();
            if (Double.isNaN(value) || Double.isInfinite(value)) throw new ArithmeticException();
            return value;
        }
        double expression() {
            double x = term();
            while (pos < s.length()) {
                char op = s.charAt(pos);
                if (op != '+' && op != '-') break;
                pos++;
                double y = term();
                x = op == '+' ? x + y : x - y;
            }
            return x;
        }
        double term() {
            double x = factor();
            while (pos < s.length()) {
                char op = s.charAt(pos);
                if (op != '*' && op != '/') break;
                pos++;
                double y = factor();
                if (op == '/' && y == 0) throw new ArithmeticException();
                x = op == '*' ? x * y : x / y;
            }
            return x;
        }
        double factor() {
            if (pos >= s.length()) throw new IllegalArgumentException();
            char c = s.charAt(pos);
            if (c == '+') { pos++; return factor(); }
            if (c == '-') { pos++; return -factor(); }
            double x;
            if (c == '(') {
                pos++;
                x = expression();
                if (pos >= s.length() || s.charAt(pos) != ')') throw new IllegalArgumentException();
                pos++;
            } else {
                int start = pos;
                while (pos < s.length() && (Character.isDigit(s.charAt(pos)) || s.charAt(pos) == '.')) pos++;
                if (start == pos) throw new IllegalArgumentException();
                x = Double.parseDouble(s.substring(start, pos));
            }
            if (pos < s.length() && s.charAt(pos) == '%') { pos++; x /= 100.0; }
            return x;
        }
    }
}
