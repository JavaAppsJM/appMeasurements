package be.hvwebsites.metingen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import be.hvwebsites.libraryandroid4.adapters.NothingSelectedSpinnerAdapter;
import be.hvwebsites.libraryandroid4.helpers.IDNumber;
import be.hvwebsites.libraryandroid4.helpers.ListItemHelper;
import be.hvwebsites.libraryandroid4.returninfo.ReturnInfo;
import be.hvwebsites.libraryandroid4.statics.StaticData;
import be.hvwebsites.metingen.adapters.TextItemListAdapter;
import be.hvwebsites.metingen.constants.SpecificData;
import be.hvwebsites.metingen.viewmodels.EntitiesViewModel;

public class ManageEntities extends AppCompatActivity {
    // Activiteit om meerdere entities te beheren (toevoegen, aanpassen, deleten)
    private EntitiesViewModel viewModel;
    private String entityType = "";
    private List<ListItemHelper> itemList = new ArrayList<>();
    private String locationSelection = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_entities);

        // Spinner deactiveren, wordt terug geactiveerd voor meter
        Spinner locationForMeterSpinner = (Spinner) findViewById(R.id.spinnerForMeter);
        locationForMeterSpinner.setVisibility(View.INVISIBLE);

        // Over welke entity gaat het --> intent nakijken
        Intent mgmtIntent = getIntent();
        entityType = mgmtIntent.getStringExtra(SpecificData.ENTITY_TYPE);
        if (entityType == null){
            entityType = SpecificData.ENTITY_TYPE_1; //Default entity type indien de back arrows gebruikt worden
        }

        // FloatingActionButton
        FloatingActionButton fab = findViewById(R.id.fab_manage_entities);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent manIntent = new Intent(ManageEntities.this, EditEntity.class);
                manIntent.putExtra(SpecificData.ENTITY_TYPE, entityType);
                manIntent.putExtra(StaticData.EXTRA_INTENT_KEY_ACTION, StaticData.ACTION_NEW);
                manIntent.putExtra(SpecificData.LOCATION_SPIN, locationSelection);
                startActivity(manIntent);
            }
        });

        // Data ophalen
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
//            Toast.makeText(ManageEntities.this,
//                    viewModelStatus.getReturnMessage(),
//                    Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(ManageEntities.this,
                    "Ophalen data is mislukt",
                    Toast.LENGTH_LONG).show();
        }

        // Recyclerview definieren
        RecyclerView recyclerView = findViewById(R.id.recycler_manage_entities);
        final TextItemListAdapter adapter = new TextItemListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        itemList.clear();

        // Entity specifics
        TextView labelColHead1 = findViewById(R.id.listHeader);
        // TODO: hier krijg ik soms een nullpointerexception ?
        switch (entityType){
            case SpecificData.ENTITY_TYPE_1:
                setTitle(SpecificData.TITLE_LIST_ACTIVITY_T1);
                labelColHead1.setText(SpecificData.HEAD_LIST_ACTIVITY_T1);
                itemList.addAll(viewModel.getLocationItemList());
                break;
            case SpecificData.ENTITY_TYPE_2:
                setTitle(SpecificData.TITLE_LIST_ACTIVITY_T2);
                // Column head invullen
                labelColHead1.setText(SpecificData.HEAD_LIST_ACTIVITY_T2);
                // Als het over meters gaat, dan moet eerst de location gekozen worden
                // in de spinner indien ze niet is meegegeven in de intent
                // Spinner activeren
                locationForMeterSpinner.setVisibility(View.VISIBLE);
                ArrayAdapter<String> locSpinAdapter = new ArrayAdapter(this,
                        android.R.layout.simple_spinner_item);
                locSpinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                // Spinner vullen met locations
                List<String> locationNameList = viewModel.getLocationNameList();
                if (locationNameList.size() == 0){
                    locationForMeterSpinner.setAdapter(new NothingSelectedSpinnerAdapter(
                            locSpinAdapter, R.layout.contact_spinner_nothing_selected, this));
                }else {
                    locationForMeterSpinner.setAdapter(locSpinAdapter);
                }
                locSpinAdapter.addAll(viewModel.getLocationNameList());
                // is location meegegeven via intent ?
                if (mgmtIntent.hasExtra(SpecificData.LOCATION_SPIN)){
                    locationSelection = mgmtIntent.getStringExtra(SpecificData.LOCATION_SPIN);
                    locationForMeterSpinner
                            .setSelection(viewModel.getLocationIndexByName(locationSelection));
                }
                // selection listener activeren
                locationForMeterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        locationSelection = String.valueOf(parent.getItemAtPosition(position));
                        viewModel.setSpinnerSelection(locationSelection);
                        if (viewModel.getLocationByName(locationSelection) != null){
                            itemList.clear();
                            itemList.addAll(viewModel.getMeterItemList(
                                    viewModel.getLocationByName(locationSelection)));
                            adapter.setEntityType(entityType);
                            adapter.setItemList(itemList);
                        }
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
                break;
        }

        // om te kunnen swipen in de recyclerview ; swippen == deleten
        ItemTouchHelper helper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(0,
                        ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView,
                                          @NonNull RecyclerView.ViewHolder viewHolder,
                                          @NonNull RecyclerView.ViewHolder target) {
                        return false;
                    }
                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                        Toast.makeText(ManageEntities.this,
                                "Deleting item ... ",
                                Toast.LENGTH_LONG).show();
                        int position = viewHolder.getAdapterPosition();
                        // Bepalen entity IDNumber to be deleted
                        IDNumber idNumberToBeDeleted = itemList.get(position).getItemID();
                        // Delete vlgns entity type
                        switch (entityType){
                            case SpecificData.ENTITY_TYPE_1:
                                viewModel.deleteLocationByID(idNumberToBeDeleted);
                                itemList.addAll(viewModel.getLocationItemList());
                                break;
                            case SpecificData.ENTITY_TYPE_2:
                                // delete ve meter
                                viewModel.deleteMeterByID(idNumberToBeDeleted);
                                // ophalen een lijst mt metrnamen
                                itemList.addAll(viewModel.getMeterItemList(
                                        viewModel.getLocationByName(locationSelection)
                                ));
                                break;
                        }
                        // Refresh recyclerview
                        adapter.setEntityType(entityType);
                        adapter.setItemList(itemList);
                    }
                });
        helper.attachToRecyclerView(recyclerView);
        adapter.setEntityType(entityType);
        adapter.setItemList(itemList);
    }
}