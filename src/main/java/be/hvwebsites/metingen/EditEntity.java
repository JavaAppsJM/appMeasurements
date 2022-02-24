package be.hvwebsites.metingen;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import be.hvwebsites.libraryandroid4.returninfo.ReturnInfo;
import be.hvwebsites.libraryandroid4.statics.StaticData;
import be.hvwebsites.metingen.constants.SpecificData;
import be.hvwebsites.metingen.fragments.LocationEditFragment;
import be.hvwebsites.metingen.fragments.MeterEditFragment;
import be.hvwebsites.metingen.viewmodels.EntitiesViewModel;

public class EditEntity extends AppCompatActivity {
    private EntitiesViewModel viewModel;
    private TextView instruction;
    private String entityType = "";
    private String action = "";
    private String locationSelection = "";
    private String meterSelection = "";
    private int idToUpdate;
    // TODO: nog te customiseren
    private EditText dateView;
    private TextView labelValue1View;
    private EditText value1View;
    private TextView labelValue2View;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_entity);

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
            Toast.makeText(EditEntity.this,
                    viewModelStatus.getReturnMessage(),
                    Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(EditEntity.this,
                    "Ophalen data is mislukt",
                    Toast.LENGTH_LONG).show();
        }

        // Intent bekijken vr action en entity
        Intent editIntent = getIntent();
        action = editIntent.getStringExtra(StaticData.EXTRA_INTENT_KEY_ACTION);
        entityType = editIntent.getStringExtra(SpecificData.ENTITY_TYPE);
        locationSelection = editIntent.getStringExtra(SpecificData.LOCATION_SPIN);
        meterSelection = editIntent.getStringExtra(SpecificData.METER_SPIN);

        // Instruction invullen
        instruction = (TextView) findViewById(R.id.instructionNewItem);

        // Bundle voorbereiden om mee te geven aan fragment
        Bundle fragmentBundle = new Bundle();
        fragmentBundle.putString(StaticData.EXTRA_INTENT_KEY_ACTION, action);

        switch (entityType) {
            case SpecificData.ENTITY_TYPE_1:
                if (action.equals(StaticData.ACTION_NEW)) {
                    setTitle(SpecificData.TITLE_NEW_ACTIVITY_T1);
                    instruction.setText(StaticData.INSTRUCTION_ACTION_NEW);
                } else {
                    setTitle(SpecificData.TITLE_UPDATE_ACTIVITY_T1);
                    instruction.setText(StaticData.INSTRUCTION_ACTION_UPDATE);
                    // ID uit intent halen om te weten welk element moet aangepast worden
                    idToUpdate = editIntent.getIntExtra(StaticData.EXTRA_INTENT_KEY_ID,
                            StaticData.ITEM_NOT_FOUND);
                    // in bundle steken om mee te geven aan fragment
                    fragmentBundle.putInt(StaticData.EXTRA_INTENT_KEY_ID, idToUpdate);

                }
                // Creeer fragment_location
                if (savedInstanceState == null) {
                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .add(R.id.fragmentEntity, LocationEditFragment.class, fragmentBundle)
                            .commit();
                }
                break;
            case SpecificData.ENTITY_TYPE_2:
                fragmentBundle.putString(SpecificData.LOCATION_SPIN, locationSelection);
                if (action.equals(StaticData.ACTION_NEW)) {
                    setTitle(SpecificData.TITLE_NEW_ACTIVITY_T2);
                    instruction.setText(StaticData.INSTRUCTION_ACTION_NEW);
                } else {
                    setTitle(SpecificData.TITLE_UPDATE_ACTIVITY_T2);
                    instruction.setText(StaticData.INSTRUCTION_ACTION_UPDATE);
                    // Index uit intent halen om te weten welk element moet aangepast worden
                    idToUpdate = editIntent.getIntExtra(StaticData.EXTRA_INTENT_KEY_ID,
                            StaticData.ITEM_NOT_FOUND);
                    // in bundle steken om mee te geven aan fragment
                    fragmentBundle.putInt(StaticData.EXTRA_INTENT_KEY_ID, idToUpdate);
                }
                // Creeer fragment_meter
                if (savedInstanceState == null) {
                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .add(R.id.fragmentEntity, MeterEditFragment.class, fragmentBundle)
                            .commit();
                }
                break;
        }
    }
}