package com.example.unitconvert;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.util.HashMap;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    // Declare UI components - these are the visual elements of the app that the user will interact with.
    private Spinner spinnerSource; // Spinner for selecting the source unit (e.g., Inch, Foot).
    private Spinner spinnerDestination; // Spinner for selecting the destination unit (e.g., Mile, Meter).
    private EditText editTextValue; // EditText field where the user will enter the value to be converted.
    private TextView textViewResult; // TextView to display the calculated result.

    // Array containing all available unit options - this is a list of all the units that can be selected in the spinners/dropdown.
    private final String[] units = {"Inch", "Foot", "Yard", "Mile", "Pound", "Ounce", "Ton", "Celsius", "Fahrenheit", "Kelvin"};

    // Create sets for each category of conversions - using sets to group units by their type for easy checking.
    private final Set<String> lengthUnits = new HashSet<>(Arrays.asList("Inch", "Foot", "Yard", "Mile")); // Set of length units.
    private final Set<String> weightUnits = new HashSet<>(Arrays.asList("Pound", "Ounce", "Ton")); // Set of weight units.
    private final Set<String> temperatureUnits = new HashSet<>(Arrays.asList("Celsius", "Fahrenheit", "Kelvin")); // Set of temperature units.

    // HashMap for length conversion factors (to meters) - used to store the conversion factors for each length unit relative to meters.
    private final HashMap<String, Double> lengthToMeter = new HashMap<>();

    // HashMap for weight conversion factors (to kilograms) - used to store the conversion factors for each weight unit relative to kilograms.
    private final HashMap<String, Double> weightToKg = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // The onCreate method is called when the activity is first created.
        super.onCreate(savedInstanceState);
        // Set the content view to the activity_main layout.
        setContentView(R.layout.activity_main);

        // Find all UI components by their ID - connecting the variables with the UI elements in the layout.
        spinnerSource = findViewById(R.id.spinner); // Get the source spinner.
        spinnerDestination = findViewById(R.id.spinner2); // Get the destination spinner.
        editTextValue = findViewById(R.id.editTextText); // Get the input field.
        Button buttonConvert = findViewById(R.id.button); // Get the convert button.
        textViewResult = findViewById(R.id.textView4); // Get the result text view.

        // Initialise length conversion factors - populate the lengthToMeter HashMap with conversion factors.
        lengthToMeter.put("Inch", 0.0254); // 1 inch is 0.0254 meters.
        lengthToMeter.put("Foot", 0.3048); // 1 foot is 0.3048 meters.
        lengthToMeter.put("Yard", 0.9144); // 1 yard is 0.9144 meters.
        lengthToMeter.put("Mile", 1609.34); // 1 mile is 1609.34 meters.

        // Initialise weight conversion factors - populate the weightToKg HashMap with conversion factors.
        weightToKg.put("Pound", 0.453592); // 1 pound is 0.453592 kilograms.
        weightToKg.put("Ounce", 0.0283495); // 1 ounce is 0.0283495 kilograms.
        weightToKg.put("Ton", 907.185); // 1 ton is 907.185 kilograms.

        // Set up ArrayAdapter for spinners - creating an adapter to populate the spinners with the unit options in the list/UI.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, units); // Create adapter with the list of units.
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // Set the layout for the dropdown.
        spinnerSource.setAdapter(adapter); // Assign the adapter to the source spinner.
        spinnerDestination.setAdapter(adapter); // Assign the adapter to the destination spinner.

        // Set onClickListener for the convert button - defining what happens when the convert button is clicked.
        buttonConvert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get user input and trim whitespace - get the text from the input field and remove any leading or trailing spaces.
                String inputString = editTextValue.getText().toString().trim();
                // Check if the input field is empty.
                if (inputString.isEmpty()) {
                    Toast.makeText(MainActivity.this, R.string.enter_value, Toast.LENGTH_SHORT).show(); // Show a message if no value is entered.
                    return; // Exit the method early since there's nothing to convert.
                }

                // Convert input to double - try to parse the input string to a double.
                double inputValue;
                try {
                    inputValue = Double.parseDouble(inputString); // Try to convert the string to a double.
                } catch (NumberFormatException e) {
                    Toast.makeText(MainActivity.this, R.string.invalid_number, Toast.LENGTH_SHORT).show(); // Show a message if the input is not a valid number.
                    return; // Exit the method early if the input is not a valid number.
                }

                // Get selected units - get the currently selected source and destination units from the spinners.
                String sourceUnit = spinnerSource.getSelectedItem().toString(); // Get the selected source unit.
                String destinationUnit = spinnerDestination.getSelectedItem().toString(); // Get the selected destination unit.

                // Check if units are of the same type - verify that the selected units are compatible for conversion.
                if ((lengthUnits.contains(sourceUnit) && lengthUnits.contains(destinationUnit)) || // Check if both units are length units.
                        (weightUnits.contains(sourceUnit) && weightUnits.contains(destinationUnit)) || // Check if both units are weight units.
                        (temperatureUnits.contains(sourceUnit) && temperatureUnits.contains(destinationUnit))) { // Check if both units are temperature units.
                    // Perform conversion - if the units are compatible, perform the conversion.
                    double result = convertUnits(sourceUnit, destinationUnit, inputValue); // Call the method to convert the units.
                    // Display result with 2 decimals - set the result in the text view, formatted to two decimal places.
                    textViewResult.setText(String.format("%.2f", result)); // Display the formatted result.
                } else {
                    Toast.makeText(MainActivity.this, R.string.incompatible_types, Toast.LENGTH_SHORT).show(); // Show a message if incompatible types are selected.
                }
            }
        });
    }

    /**
     * Converts the given input value from the source unit to the destination unit.
     *
     * @param sourceUnit      The unit to convert from.
     * @param destinationUnit The unit to convert to.
     * @param value           The numeric value to convert.
     * @return The converted value.
     */
    private double convertUnits(String sourceUnit, String destinationUnit, double value) {
        // If units are the same, return the original value - no conversion is needed.
        if (sourceUnit.equals(destinationUnit)) {
            return value; // Return the original value.
        }

        // Handle length conversions - if both units are length units, convert them.
        if (lengthUnits.contains(sourceUnit) && lengthUnits.contains(destinationUnit)) {
            double valueInMeters = value * lengthToMeter.get(sourceUnit); // Convert the source value to meters.
            return valueInMeters / lengthToMeter.get(destinationUnit); // Convert the value in meters to the destination unit.
        }

        // Handle weight conversions - if both units are weight units, convert them.
        if (weightUnits.contains(sourceUnit) && weightUnits.contains(destinationUnit)) {
            double valueInKg = value * weightToKg.get(sourceUnit); // Convert the source value to kilograms.
            return valueInKg / weightToKg.get(destinationUnit); // Convert the value in kilograms to the destination unit.
        }

        // Handle temperature conversions - if both units are temperature units, convert them.
        if (temperatureUnits.contains(sourceUnit) && temperatureUnits.contains(destinationUnit)) {
            double valueInCelsius = 0.0;
            switch (sourceUnit) {
                case "Celsius":
                    valueInCelsius = value;
                    break;
                case "Fahrenheit":
                    valueInCelsius = (value - 32) / 1.8;
                    break;
                case "Kelvin":
                    valueInCelsius = value - 273.15;
                    break;
            }

            switch (destinationUnit) {
                case "Celsius":
                    return valueInCelsius;
                case "Fahrenheit":
                    return (valueInCelsius * 1.8) + 32;
                case "Kelvin":
                    return valueInCelsius + 273.15;
                default:
                    return value; // Should not occur, but added for safety
            }
        }

        return value; // Should not occur, but added for safety just in case
    }
}