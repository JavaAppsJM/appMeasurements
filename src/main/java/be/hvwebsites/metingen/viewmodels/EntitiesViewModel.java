package be.hvwebsites.metingen.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import be.hvwebsites.libraryandroid4.helpers.IDNumber;
import be.hvwebsites.libraryandroid4.helpers.ListItemHelper;
import be.hvwebsites.libraryandroid4.repositories.FlexiRepository;
import be.hvwebsites.libraryandroid4.returninfo.ReturnInfo;
import be.hvwebsites.libraryandroid4.statics.StaticData;
import be.hvwebsites.metingen.constants.SpecificData;
import be.hvwebsites.metingen.entities.Location;
import be.hvwebsites.metingen.entities.Measurement;
import be.hvwebsites.metingen.entities.Meter;

public class EntitiesViewModel extends AndroidViewModel {
    private FlexiRepository repository;
    private String basedir;
    // File declaraties
    File locationFile;
    File meterFile;
    File measurementFile;
    // File names constants
    public static final String LOCATION_FILE = "location.txt";
    public static final String METER_FILE = "meter.txt";
    public static final String MEASUREMENT_FILE = "measurement.txt";
    // Lijsten om data in te zetten
    private List<Location> locationList = new ArrayList<>();
    private List<Meter> meterList = new ArrayList<>();
    private List<Measurement> measurementList = new ArrayList<>();
    // Spinner selecties om te onthouden
    private String spinnerSelection = "";


    public EntitiesViewModel(@NonNull Application application) {
        super(application);
    }

    public ReturnInfo initializeViewModel(String basedir){
        ReturnInfo returnInfo = new ReturnInfo(0);
        this.basedir = basedir;
        // Filedefinities
        locationFile = new File(basedir, LOCATION_FILE);
        meterFile = new File(basedir, METER_FILE);
        measurementFile = new File(basedir, MEASUREMENT_FILE);
        // Ophalen Locaties
        repository = new FlexiRepository(locationFile);
        locationList.addAll(getLocationsFromDataList(repository.getDataList()));
        if (locationList.size() == 0){
            returnInfo.setReturnCode(100);
            returnInfo.setReturnMessage(SpecificData.NO_LOCATIONS_YET);
        } else {
            // Ophalen meters
            repository = new FlexiRepository(meterFile);
            meterList.addAll(getMetersFromDataList(repository.getDataList()));
            if (meterList.size() == 0){
                returnInfo.setReturnCode(100);
                returnInfo.setReturnMessage(SpecificData.NO_METERS_YET);
            }else {
                // Ophalen metingen
                repository = new FlexiRepository(measurementFile);
                measurementList.addAll(getMeasurementsFromDataList(repository.getDataList()));
                if (measurementList.size() == 0){
                    returnInfo.setReturnCode(100);
                    returnInfo.setReturnMessage(SpecificData.NO_MEASUREMENTS_YET);
                }
            }
        }
        return returnInfo;
    }

    public List<ListItemHelper> getLocationItemList(){
        // bepaalt een lijst met location namen en ID's obv locationlist
        List<ListItemHelper> displayList = new ArrayList<>();
        for (int i = 0; i < locationList.size(); i++) {
            displayList.add(new ListItemHelper(
                    locationList.get(i).getEntityName(), "",
                    locationList.get(i).getEntityId()
            ));
        }
        return displayList;
    }

    public List<String> getLocationNameList(){
        // bepaalt een lijst met location namen obv locationlist voor spinners !!!
        List<String> displayList = new ArrayList<>();
        for (int i = 0; i < locationList.size(); i++) {
            displayList.add(locationList.get(i).getEntityName());
        }
        return displayList;
    }

    public Location getLocationByName(String inName){
        // bepaalt een locatie obv entityname
        for (int i = 0; i < locationList.size(); i++) {
            if (locationList.get(i).getEntityName().equals(inName)){
                return locationList.get(i);
            }
        }
        return null;
    }

    public int getLocationIndexByName(String inName){
        // bepaalt een locatie index obv entityname
        for (int i = 0; i < locationList.size(); i++) {
            if (locationList.get(i).getEntityName().equals(inName)){
                return i;
            }
        }
        return StaticData.ITEM_NOT_FOUND;
    }

    private List<Location> getLocationsFromDataList(List<String> dataList){
        // Converteert een datalist met locaties in een locationlist
        List<Location> locations = new ArrayList<>();
        for (int j = 0; j < dataList.size(); j++) {
            locations.add(new Location(dataList.get(j)));
        }
        return locations;
    }

    public Location getLocationById(IDNumber locationId){
        for (int j = 0; j < locationList.size(); j++) {
            if (locationList.get(j).getEntityId().getId() == locationId.getId()){
                return locationList.get(j);
            }
        }
        return null;
    }

    public List<String> getMeterNameList(Location inLoc){
        // bepaalt een lijst met meter namen obv meterlist en een location
        List<String> displayList = new ArrayList<>();
        for (int i = 0; i < meterList.size(); i++) {
            if (meterList.get(i).getMeterLocationId().getId() ==
                    inLoc.getEntityId().getId()){
                displayList.add(meterList.get(i).getEntityName());
            }
        }
        return displayList;
    }

    public List<ListItemHelper> getMeterItemList(Location inloc){
        // bepaalt een lijst met meter namen en ID's obv meterlist
        List<ListItemHelper> displayList = new ArrayList<>();
        for (int i = 0; i < meterList.size(); i++) {
            if (meterList.get(i).getMeterLocationId().getId() ==
                    inloc.getEntityId().getId()){
                displayList.add(new ListItemHelper(
                        meterList.get(i).getEntityName(), "",
                        meterList.get(i).getEntityId()
                ));
            }
        }
        return displayList;
    }

    private List<Meter> getMetersFromDataList(List<String> dataList){
        // Converteert een datalist met meters in een meterlist
        List<Meter> meters = new ArrayList<>();
        for (int j = 0; j < dataList.size(); j++) {
            meters.add(new Meter(dataList.get(j)));
        }
        return meters;
    }

    public Meter getMeterById(IDNumber meterID){
        for (int j = 0; j < meterList.size(); j++) {
            if (meterList.get(j).getEntityId().getId() == meterID.getId()){
                return meterList.get(j);
            }
        }
        return null;
    }

    public Meter getMeterByNameForLocation(String inName, Location inLoc){
        if (inLoc != null){
            for (int j = 0; j < meterList.size(); j++) {
                if ((meterList.get(j).getEntityName().equals(inName)) &&
                        (meterList.get(j).getMeterLocationId().getId() == inLoc.getEntityId().getId())) {
                    return meterList.get(j);
                }
            }
        }
        return null;
    }

    public int getMeterIndexById(IDNumber meterId){
        for (int j = 0; j < meterList.size(); j++) {
            if (meterList.get(j).getEntityId().getId() == meterId.getId()){
                return j;
            }
        }
        return StaticData.ITEM_NOT_FOUND;
    }

    public List<ListItemHelper> getMeasurementsforMeter(Location inLoc, Meter inMeter){
        // Eerst gaan we de metinglijst voor de opgegeven locatie, meter bepalen
        List<Measurement> mList = new ArrayList<>();
        if ((inLoc != null) && (inMeter != null)){
            for (int j = 0; j < measurementList.size(); j++) {
                if ((measurementList.get(j).getMeterLocationId().getId() == inLoc.getEntityId().getId() ) &&
                        (measurementList.get(j).getMeterId().getId() == inMeter.getEntityId().getId())) {
                    mList.add(measurementList.get(j));
                }
            }
        }
        // Display list bepalen voor geselecteerde measurements
        List<ListItemHelper> mDisplayList = new ArrayList<>();
        for (int i = 0; i < mList.size() ; i++) {
            if (i == mList.size()-1){
                // Voor de laatste meting kan je geen volgende meting doorgeven !!
                mDisplayList.add(new ListItemHelper(
                        mList.get(i).composeDisplayLine(mList.get(i)),
                        "", mList.get(i).getEntityId()
                ));
            }else {
                mDisplayList.add(new ListItemHelper(
                        mList.get(i).composeDisplayLine(mList.get(i+1)),
                        "", mList.get(i).getEntityId()
                ));
            }
        }
        return mDisplayList;
    }

    public Measurement getMsrmntById(IDNumber msrmntID){
        for (int j = 0; j < measurementList.size(); j++) {
            if (measurementList.get(j).getEntityId().getId() == msrmntID.getId()){
                return measurementList.get(j);
            }
        }
        return null;
    }

    public int getMsrmntIndexById(IDNumber msrmntID){
        for (int j = 0; j < measurementList.size(); j++) {
            if (measurementList.get(j).getEntityId().getId() == msrmntID.getId()){
                return j;
            }
        }
        return StaticData.ITEM_NOT_FOUND;
    }

    private List<Measurement> getMeasurementsFromDataList(List<String> dataList){
        // Converteert een datalist met metingen in een measurementlist
        List<Measurement> measurements = new ArrayList<>();
        for (int j = 0; j < dataList.size(); j++) {
            measurements.add(new Measurement(dataList.get(j)));
        }
        return measurements;
    }

    public ReturnInfo storeLocations(){
        // Bewaart de locationlist
        // Eerst de locationlist alfabetisch sorteren
        sortLocationList(locationList);
        ReturnInfo returnInfo = new ReturnInfo(0);
        repository.storeData(locationFile, convertLocationListinDataList(locationList));
        return returnInfo;
    }

    private void sortLocationList(List<Location> locList){
        // Sorteert een locationlist op entityname alfabetisch
        Location tempLoc = new Location();
        for (int i = locList.size() ; i > 0; i--) {
            for (int j = 1; j < i ; j++) {
                if (locList.get(j).getEntityName().compareToIgnoreCase(locList.get(j-1).getEntityName()) < 0){
                    tempLoc.setLocation(locList.get(j));
                    locList.get(j).setLocation(locList.get(j-1));
                    locList.get(j-1).setLocation(tempLoc);
                }
            }
        }
    }

    private List<String> convertLocationListinDataList(List<Location> inLocList){
        // Converteert een locationlist in een datalist voor bewaard te worden in een bestand
        List<String> lineList = new ArrayList<>();
        for (int i = 0; i < inLocList.size(); i++) {
            lineList.add(inLocList.get(i).convertToFileLine());
        }
        return lineList;
    }

    public void deleteLocationByID(IDNumber inIDNumber){
        for (int i = 0; i < locationList.size(); i++) {
            if (locationList.get(i).getEntityId().getId() ==
            inIDNumber.getId()){
                deleteLocation(i);
            }
        }
    }

    private ReturnInfo deleteLocation(int position){
        // Verwijdert een location
        ReturnInfo returnInfo = new ReturnInfo(0);
        locationList.remove(position);
        // Alle location relevante items moeten voor die location ook verwijderd worden

        // Bewaar nieuwe toestand locaties
        storeLocations();
        return returnInfo;
    }

    public ReturnInfo storeMeters(){
        // Bewaart de meterlist
        // Eerst alfabetisch sorteren
        sortMeterList(meterList);
        ReturnInfo returnInfo = new ReturnInfo(0);
        repository.storeData(meterFile, convertMeterListinDataList(meterList));
        return returnInfo;
    }

    private void sortMeterList(List<Meter> inMeterList){
        // Sorteert een list op entityname alfabetisch
        Meter tempMeter = new Meter();
        for (int i = inMeterList.size() ; i > 0; i--) {
            for (int j = 1; j < i ; j++) {
                if (inMeterList.get(j).getEntityName().compareToIgnoreCase(inMeterList.get(j-1).getEntityName()) < 0){
                    tempMeter.setMeter(inMeterList.get(j));
                    inMeterList.get(j).setMeter(inMeterList.get(j-1));
                    inMeterList.get(j-1).setMeter(tempMeter);
                }
            }
        }
    }

    private List<String> convertMeterListinDataList(List<Meter> inList){
        // Converteert een meterlist in een datalist voor bewaard te worden in een bestand
        List<String> lineList = new ArrayList<>();
        for (int i = 0; i < inList.size(); i++) {
            lineList.add(inList.get(i).convertToFileLine());
        }
        return lineList;
    }

    public void deleteMeterByID(IDNumber inIDNumber){
        for (int i = 0; i < meterList.size(); i++) {
            if (meterList.get(i).getEntityId().getId() ==
                    inIDNumber.getId()){
                deleteMeter(i);
            }
        }
    }

    private ReturnInfo deleteMeter(int position){
        // Verwijdert een meter
        ReturnInfo returnInfo = new ReturnInfo(0);
        meterList.remove(position);
        // Alle meter relevante items moeten voor die meter ook verwijderd worden

        // Bewaar nieuwe toestand meters
        storeMeters();
        return returnInfo;
    }

    public ReturnInfo storeMeasurements(){
        // Bewaart de measurementlist
        ReturnInfo returnInfo = new ReturnInfo(0);

        // Measurements omgekeerd chronologisch sorteren
        sortMeasurements();
        repository.storeData(measurementFile, convertMsrmntListinDataList(measurementList));
        return returnInfo;
    }

    private void sortMeasurements(){
        // Measurements worden omgekeerd chronologisch gesorteerd
        Measurement tempMsrmnt = new Measurement();
        int currentDate;
        int previousDate;

        for (int i = measurementList.size() ; i > 0; i--) {
            for (int j = 1; j < i ; j++) {
                currentDate = measurementList.get(j).getMeasurementDate().getIntDate();
                previousDate = measurementList.get(j-1).getMeasurementDate().getIntDate();
                if (currentDate > previousDate){
                    tempMsrmnt.setMeasurement(measurementList.get(j));
                    measurementList.get(j).setMeasurement(measurementList.get(j-1));
                    measurementList.get(j-1).setMeasurement(tempMsrmnt);
                }
            }
        }
    }

    private List<String> convertMsrmntListinDataList(List<Measurement> inList){
        // Converteert een measurementlist in een datalist voor bewaard te worden in een bestand
        List<String> lineList = new ArrayList<>();
        for (int i = 0; i < inList.size(); i++) {
            lineList.add(inList.get(i).convertToFileLine());
        }
        return lineList;
    }

    public void deleteMsrmntByID(IDNumber inIDNumber){
        for (int i = 0; i < measurementList.size(); i++) {
            if (measurementList.get(i).getEntityId().getId() ==
                    inIDNumber.getId()){
                deleteMsrmnt(i);
            }
        }
    }

    private ReturnInfo deleteMsrmnt(int position){
        // Verwijdert een meter
        ReturnInfo returnInfo = new ReturnInfo(0);
        measurementList.remove(position);
        // Alle relevante items moeten voor die measurement ook verwijderd worden

        // Bewaar nieuwe toestand meters
        storeMeasurements();
        return returnInfo;
    }

    public String getBasedir() {
        return basedir;
    }

    public void setSpinnerSelection(String spinnerSelection) {
        this.spinnerSelection = spinnerSelection;
    }

    public List<Location> getLocationList() {
        return locationList;
    }

    public List<Meter> getMeterList() {
        return meterList;
    }

    public List<Measurement> getMeasurementList() {
        return measurementList;
    }
}
