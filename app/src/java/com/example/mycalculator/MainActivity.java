package com.example.mycalculator;

import android.app.Activity;
import android.os.Bundle;
import android.widget.*;
import android.view.*;
import android.graphics.Color;

public class MainActivity extends Activity {

    TextView display;
    String current = "";
    double first = 0;
    String operator = "";

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(1);
        layout.setPadding(16,16,16,16);
        layout.setBackgroundColor(Color.rgb(20,25,35));

        display = new TextView(this);
        display.setText("0");
        display.setTextSize(42);
        display.setTextColor(Color.WHITE);
        display.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        display.setPadding(10,30,10,30);
        layout.addView(display,
            new LinearLayout.LayoutParams(-1,140));

        String[][] keys = {
            {"AC","⌫","%","÷"},
            {"7","8","9","×"},
            {"4","5","6","−"},
            {"1","2","3","+"},
            {"0",".","="}
        };

        for(String[] row : keys) {
            LinearLayout line = new LinearLayout(this);
            for(String key : row) {
                Button btn = new Button(this);
                btn.setText(key);
                btn.setTextSize(23);
                line.addView(btn,
                    new LinearLayout.LayoutParams(0,85,1));
                btn.setOnClickListener(v -> press(key));
            }
            layout.addView(line);
        }

        setContentView(layout);
    }

    void press(String key) {
        if(key.equals("AC")) {
            current = "";
            operator = "";
            first = 0;
        } else if(key.equals("⌫")) {
            if(!current.isEmpty())
                current = current.substring(0,current.length()-1);
        } else if(key.equals("=")) {
            if(!current.isEmpty() && !operator.isEmpty()) {
                double second = Double.parseDouble(current);
                double result = 0;
                switch(operator) {
                    case "+": result=first+second; break;
                    case "−": result=first-second; break;
                    case "×": result=first*second; break;
                    case "÷":
                        if(second==0) {
                            display.setText("خطا");
                            current="";
                            operator="";
                            return;
                        }
                        result=first/second;
                        break;
                }
                current = String.valueOf(result);
                operator="";
            }
        } else if("+-−×÷%".contains(key) && key.length()==1) {
            if(!current.isEmpty()) {
                first=Double.parseDouble(current);
                current="";
                operator=key;
                if(key.equals("%")) {
                    current=String.valueOf(first/100);
                    operator="";
                }
            }
        } else {
            current += key;
        }

        display.setText(current.isEmpty() ? "0" : current);
    }
}