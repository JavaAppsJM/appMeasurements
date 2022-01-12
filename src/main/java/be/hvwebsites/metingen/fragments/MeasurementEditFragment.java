package be.hvwebsites.metingen.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import be.hvwebsites.libraryandroid4.helpers.DateString;
import be.hvwebsites.libraryandroid4.helpers.IDNumber;
import be.hvwebsites.libraryandroid4.statics.StaticData;
import be.hvwebsites.metingen.MainActivity;
import be.hvwebsites.metingen.ManageEntities;
import be.hvwebsites.metingen.NewDatePickerInterface;
import be.hvwebsites.metingen.R;
import be.hvwebsites.metingen.constants.SpecificData;
import be.hvwebsites.metingen.entities.Location;
import be.hvwebsites.metingen.entities.Measurement;
import be.hvwebsites.metingen.viewmodels.EntitiesViewModel;

public class MeasurementEditFragment extends Fragment implements NewDatePickerInterface {
    private EntitiesViewModel viewModel;
    private int iDToUpdate = StaticData.ITEM_NOT_FOUND;
    private Measurement msrmntToUpdate;
    private String locationSelection = "";
    private String meterSelection = "";
    private TextView locationValueView;
    private TextView meterValueView;
    private EditText dateView;
    private EditText meterstandView;


    // Toegevoegd vanuit android tutorial
    public MeasurementEditFragment(){
        super(R.layout.fragment_measurement_edit);
    }

    private FragmentManager getSupportFragmentManager() {
        return null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_measurement_edit, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Schermvelden Definities
        locationValueView = view.findViewById(R.id.locationNameMsrmnt);
        meterValueView = view.findViewById(R.id.meterNameMsrmnt);
        dateView = view.findViewById(R.id.editMsrmntDate);
        meterstandView = view.findViewById(R.id.editMeterstand);
        Button saveButton = view.findViewById(R.id.buttonSaveMsrmnt);

        // Via het viewmodel uit de activity kan je over de data beschikken !
        viewModel = new ViewModelProvider(requireActivity()).get(EntitiesViewModel.class);

        // Wat zijn de argumenten die werden meegegeven
        locationSelection = requireArguments().getString(SpecificData.LOCATION_SPIN);
        meterSelection = requireArguments().getString(SpecificData.METER_SPIN);
        String action = requireArguments().getString(StaticData.EXTRA_INTENT_KEY_ACTION);

        // Schermvelden invullen
        locationValueView.setText(locationSelection);
        meterValueView.setText(meterSelection);
        saveButton.setText(SpecificData.BUTTON_TOEVOEGEN);

        // Update specific
        if (action.equals(StaticData.ACTION_UPDATE)){
            iDToUpdate = requireArguments().getInt(StaticData.EXTRA_INTENT_KEY_ID);
            // Bepaal geselecteerd item obv meegegeven ID
            msrmntToUpdate = (Measurement) viewModel.getMsrmntById(new IDNumber(iDToUpdate));
            // Vul Scherm in met gegevens
            meterstandView.setText(String.valueOf(msrmntToUpdate.getMeasurementValue()));
            dateView.setText(msrmntToUpdate.getMeasurementDate().getFormatDate());
            saveButton.setText(SpecificData.BUTTON_AANPASSEN);
        }

        // Als op datum veld geclickt wordt...
        dateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(v);
            }
        });

        // Als button ingedrukt wordt...
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Gegevens overnemen vh scherm
                // Datum binnenpakken
                DateString dateString = new DateString(dateView.getText().toString());
                // Meterstand binnenpakken
                Double meterstand = Double.parseDouble(String.valueOf(meterstandView.getText()));
                // Actie gebonden verwerking
                if (action.equals(StaticData.ACTION_UPDATE)){ // Update
                    // Index van aan te passen meting bepalen
                    int indexToUpdate = viewModel.getMsrmntIndexById(new IDNumber(iDToUpdate));
                    // Aanpassingen rechtstreeks in measurement list
                    viewModel.getMeasurementList().get(indexToUpdate).setMeasurementDate(dateString);
                    viewModel.getMeasurementList().get(indexToUpdate).setMeasurementValue(meterstand);
                }else { // New
                    // Nieuwe meting maken
                    Measurement newMsrmnt = new Measurement(viewModel.getBasedir(), false);
                    // Gegevens vh scherm invullen in de meting
                    newMsrmnt.setMeasurementDate(dateString);
                    newMsrmnt.setMeasurementValue(meterstand);
                    // Gegevens meegekregen invullen in de nieuewe meting
                    Location location = viewModel.getLocationByName(locationSelection);
                    newMsrmnt.setMeterLocationId(location.getEntityId());
                    newMsrmnt.setMeterId(viewModel
                            .getMeterByNameForLocation(meterSelection, location).getEntityId());
                    // meting toevoegen in de list
                    viewModel.getMeasurementList().add(newMsrmnt);
                }
                viewModel.storeMeasurements();
                // Ga terug nr Main
                Intent replyIntent = new Intent(getContext(), MainActivity.class);
                replyIntent.putExtra(SpecificData.ENTITY_TYPE, SpecificData.ENTITY_TYPE_3);
                startActivity(replyIntent);
            }
        });
    }

    @Override
    public void showDatePicker(View view) {
        // Toont de datum picker, de gebruiker kan nu de datum picken
        DialogFragment newFragment = new DatePickerFragment();
        Bundle bundle = new Bundle();
        bundle.putString("Caller", "MeasurementEditFragment");
        newFragment.setArguments(bundle);
        FragmentManager dateFragmentMgr = getParentFragmentManager();
        newFragment.show(dateFragmentMgr, "datePicker");

    }

    @Override
    public void processDatePickerResult(int year, int month, int day) {
        // Verwerkt de gekozen datum uit de picker
        String month_string = Integer.toString(month+1);
        String day_string = Integer.toString(day);
        String year_string = Integer.toString(year);
        String dateMessage = (day_string +
                "/" + month_string + "/" + year_string);

        dateView.setText(dateMessage);

        Toast.makeText(getContext(), "Date: " + dateMessage, Toast.LENGTH_SHORT).show();

    }
}