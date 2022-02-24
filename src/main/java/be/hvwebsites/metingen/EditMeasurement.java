package be.hvwebsites.metingen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import be.hvwebsites.libraryandroid4.helpers.DateString;
import be.hvwebsites.libraryandroid4.helpers.IDNumber;
import be.hvwebsites.libraryandroid4.returninfo.ReturnInfo;
import be.hvwebsites.libraryandroid4.statics.StaticData;
import be.hvwebsites.metingen.constants.SpecificData;
import be.hvwebsites.metingen.entities.Location;
import be.hvwebsites.metingen.entities.Measurement;
import be.hvwebsites.metingen.entities.Meter;
import be.hvwebsites.metingen.fragments.DatePickerFragment;
import be.hvwebsites.metingen.viewmodels.EntitiesViewModel;

public class EditMeasurement extends AppCompatActivity implements NewDatePickerInterface{
    private EntitiesViewModel viewModel;
    private TextView instruction;
    private String entityType = "";
    private String action = "";
    private String locationSelection = "";
    private String meterSelection = "";
    private int iDToUpdate = StaticData.ITEM_NOT_FOUND;
    private Measurement msrmntToUpdate;
    private EditText dateView;
    private TextView labelValue1View;
    private EditText value1View;
    private TextView labelValue2View;
    private TextView locationValueView;
    private TextView meterValueView;
    private EditText meterstandView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_measurement);

        // Get a viewmodel from the viewmodelproviders
        viewModel = new ViewModelProvider(this).get(EntitiesViewModel.class);
        // Basis directory definitie
        String baseDir = getBaseContext().getExternalFilesDir(null).getAbsolutePath();
//        String baseDir = getBaseContext().getFilesDir().getAbsolutePath();
        // Initialize viewmodel mt basis directory (data wordt opgehaald in viewmodel)
        ReturnInfo viewModelStatus = viewModel.initializeViewModel(baseDir);
        if (viewModelStatus.getReturnCode() == 0) {
            // Files gelezen
        } else if (viewModelStatus.getReturnCode() == 100) {
            Toast.makeText(EditMeasurement.this,
                    viewModelStatus.getReturnMessage(),
                    Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(EditMeasurement.this,
                    "Ophalen data is mislukt",
                    Toast.LENGTH_LONG).show();
        }

        // Intent bekijken
        Intent editIntent = getIntent();
        action = editIntent.getStringExtra(StaticData.EXTRA_INTENT_KEY_ACTION);
        entityType = editIntent.getStringExtra(SpecificData.ENTITY_TYPE);
        locationSelection = editIntent.getStringExtra(SpecificData.LOCATION_SPIN);
        meterSelection = editIntent.getStringExtra(SpecificData.METER_SPIN);

        // Schermvelden Definities
        instruction = findViewById(R.id.instructionNewMsrmnt);
        locationValueView = findViewById(R.id.locationNameMsrmnt);
        meterValueView = findViewById(R.id.meterNameMsrmnt);
        dateView = findViewById(R.id.editMsrmntDate);
        meterstandView = findViewById(R.id.editMeterstand);
        Button saveButton = findViewById(R.id.buttonSaveMsrmnt);

        // Schermvelden invullen
        locationValueView.setText(locationSelection);
        meterValueView.setText(meterSelection);
        saveButton.setText(SpecificData.BUTTON_TOEVOEGEN);

        if (action.equals(StaticData.ACTION_NEW)) {
            setTitle(SpecificData.TITLE_NEW_ACTIVITY_T3);
            instruction.setText(StaticData.INSTRUCTION_ACTION_NEW);
        } else {
            setTitle(SpecificData.TITLE_UPDATE_ACTIVITY_T3);
            instruction.setText(StaticData.INSTRUCTION_ACTION_UPDATE);
            // Index uit intent halen om te weten welk element moet aangepast worden
            iDToUpdate = editIntent.getIntExtra(StaticData.EXTRA_INTENT_KEY_ID,
                    StaticData.ITEM_NOT_FOUND);
            // Bepaal geselecteerd item obv meegegeven ID
            msrmntToUpdate = (Measurement) viewModel.getMsrmntById(new IDNumber(iDToUpdate));
            // Locatie en meter
            Location locMsrmntToUpd = viewModel.getLocationById(msrmntToUpdate.getMeterLocationId());
            Meter meterMsrmntToUpd = viewModel.getMeterById(msrmntToUpdate.getMeterId());
            // Vul Scherm in met gegevens
            locationValueView.setText(locMsrmntToUpd.getEntityName());
            meterValueView.setText(meterMsrmntToUpd.getEntityName());
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
                Intent replyIntent = new Intent(EditMeasurement.this, MainActivity.class);
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
        FragmentManager dateFragmentMgr = getSupportFragmentManager();
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

        Toast.makeText(this, "Date: " + dateMessage, Toast.LENGTH_SHORT).show();
    }
}