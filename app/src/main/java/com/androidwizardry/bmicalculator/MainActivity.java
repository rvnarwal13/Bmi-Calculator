package com.androidwizardry.bmicalculator;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.AppCompatTextView;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    AppCompatEditText height, weight;
    AppCompatTextView result, tips;
    AppCompatButton calculate, clear;
    AppCompatSpinner heightSpinner, weightSpinner;
    HeightUnits heightUnits;
    WeightUnits weightUnits;
    BMICustomProgressBar bmiCustomProgressBar;
    float heightValue;
    float weightValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        height = findViewById(R.id.height);
        weight = findViewById(R.id.weight);
        result = findViewById(R.id.your_bmi);
        calculate = findViewById(R.id.calculate_bmi);
        heightSpinner = findViewById(R.id.height_units);
        weightSpinner = findViewById(R.id.weight_units);
        clear = findViewById(R.id.clear_bmi);
        bmiCustomProgressBar = findViewById(R.id.bmi_progress_bar);
        tips = findViewById(R.id.bmi_tips);

        ArrayAdapter<CharSequence> heightAdapter = ArrayAdapter.createFromResource(this, R.array.height_dropdown, R.layout.spinner_item);
        ArrayAdapter<CharSequence> weightAdapter = ArrayAdapter.createFromResource(this, R.array.weight_dropdown, R.layout.spinner_item);

        heightAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        weightAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        heightSpinner.setAdapter(heightAdapter);
        weightSpinner.setAdapter(weightAdapter);

        int defaultHeightUnit = heightAdapter.getPosition("centimeter");
        int defaultWeightUnit = weightAdapter.getPosition("kilogram");

        heightSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                heightUnits = HeightUnits.values()[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        weightSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                weightUnits = WeightUnits.values()[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        heightSpinner.setSelection(defaultHeightUnit);
        weightSpinner.setSelection(defaultWeightUnit);

        calculate.setOnClickListener(v -> calculateBmi());
        clear.setOnClickListener(v -> clearBmi());
    }

    private void calculateBmi() {
        closeKeyboard();
        if (!Objects.requireNonNull(height.getText()).toString().isEmpty() && !Objects.requireNonNull(weight.getText()).toString().isEmpty()) {
            heightValue = Float.parseFloat(height.getText().toString());
            weightValue = Float.parseFloat(weight.getText().toString());

            convertUnits();

            float bmi = weightValue / (heightValue * heightValue);

            ObjectAnimator animator = ObjectAnimator.ofInt(bmiCustomProgressBar, "bmiValue", 0, (int) bmi);
            animator.setDuration(500);
            animator.start();

            result.setText("BMI: " + Math.round(bmi*100)/100.0f + " (" + getStatus(bmi) + ")");
        }
    }

    private String getStatus(float bmi) {
        if (bmi < 18.5) {
            tips.setText(R.string.underweight_tip);
            return "Underweight";
        }
        if (bmi < 24.9) {
            tips.setText(R.string.normal_tip);
            return "Normal";
        }
        if (bmi < 29.9) {
            tips.setText(R.string.overweight_tip);
            return "Overweight";
        }
        tips.setText(R.string.obese_tip);
        return "Obese";
    }

    private void convertUnits() {
        if(heightUnits == HeightUnits.CENTIMETER) {
            heightValue = heightValue / 100;
        } else if(heightUnits == HeightUnits.FEET) {
            heightValue = heightValue * 0.3048F;
        } else if(heightUnits == HeightUnits.INCHES) {
            heightValue = heightValue * 0.0254F;
        }

        if(weightUnits == WeightUnits.GRAM) {
            weightValue = weightValue / 1000;
        } else if(weightUnits == WeightUnits.POUND) {
            weightValue = weightValue * 0.453592F;
        } else if(weightUnits == WeightUnits.OUNCE) {
            weightValue = weightValue * 0.0283495F;
        } else if(weightUnits == WeightUnits.STONE) {
            weightValue = weightValue * 6.35029F;
        }
    }

    private void clearBmi() {
        height.setText("");
        weight.setText("");
        result.setText("Your BMI will be visible here");
        tips.setText("");
        bmiCustomProgressBar.setBmiValue(0);
    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if(view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            view.clearFocus();
        }
    }
}