package be.hvwebsites.metingen.entities;

import be.hvwebsites.libraryandroid4.helpers.DateString;
import be.hvwebsites.libraryandroid4.helpers.IDNumber;

public class Measurement {
    private IDNumber entityId;
    private IDNumber meterLocationId;
    private IDNumber meterId;
    private DateString measurementDate;
    private Double measurementValue;
    public static final String MEAS_LATEST_ID = "measlatestid";

    public Measurement() {
    }

    public Measurement(String basedir, boolean b) {
        entityId = new IDNumber(basedir, MEAS_LATEST_ID);
    }

    public Measurement(String fileLine){
        super();
        convertFromFileLine(fileLine);
    }

    public void setMeasurement(Measurement inMeasurement){
        setEntityId(inMeasurement.getEntityId());
        setMeterLocationId(inMeasurement.getMeterLocationId());
        setMeterId(inMeasurement.getMeterId());
        setMeasurementDate(inMeasurement.getMeasurementDate());
        setMeasurementValue(inMeasurement.getMeasurementValue());
    }

    public void convertFromFileLine(String fileLine) {
        // Maakt een meting obv een fileline -
        // format: <key><102526><locatie><11><meter><101><mdate><31122021><mvalue><99999,99>
        // fileLine splitsen in argumenten
        String[] fileLineContent = fileLine.split("<");
        for (int i = 0; i < fileLineContent.length; i++) {
            if (fileLineContent[i].matches("key.*")){
                setEntityId(new IDNumber(fileLineContent[i+1].replace(">", "")));
            }
            if (fileLineContent[i].matches("location.*")){
                setMeterLocationId(new IDNumber(fileLineContent[i+1].replace(">", "")));
            }
            if (fileLineContent[i].matches("meter.*")){
                setMeterId(new IDNumber(fileLineContent[i+1].replace(">", "")));
            }
            if (fileLineContent[i].matches("mdate.*")){
                setMeasurementDate(new DateString(fileLineContent[i+1].replace(">", "")));
            }
            if (fileLineContent[i].matches("mvalue.*")){
                setMeasurementValue(Double.parseDouble((fileLineContent[i+1].replace(">",""))));
            }
        }
    }

    public String convertToFileLine() {
        // Maakt een fileline van een meting -
        // format: <key><102526><locatie><11><meter><101><mdate><31122021><mvalue><99999,99>
        String fileLine = "<key><" + getEntityId().getIdString()
                + "><location><" + getMeterLocationId().getIdString()
                + "><meter><" + getMeterId().getIdString()
                + "><mdate><" + getMeasurementDate().getDateString()
                + "><mvalue><" + getMeasurementValue()
                + ">";
        return fileLine;
    }

    public String composeDisplayLine(Measurement mPrevious){
        // Maak een display line voor de current meting obv de current meting en de voorgaande
        double consumption = 0;
        double consumptionPerDay = 0;
        String resultString = getMeasurementDate().getDateString().concat(" ")
                .concat(String.valueOf(getMeasurementValue()));
        // Bereken het aantal dagen tssn current en vorige meting
        int nmbrOfDays = getMeasurementDate().calculateDateDifference(
                mPrevious.getMeasurementDate());
        if (nmbrOfDays > 0){
            // Bereken het verbruik en het verbruik per dag
            consumption = getMeasurementValue() - mPrevious.getMeasurementValue();
            consumptionPerDay = consumption/nmbrOfDays;
        }
        // Stel samen in resultstring
        resultString = resultString.concat(" ")
                .concat(String.valueOf(consumption)).concat(" ")
                .concat(String.valueOf(consumptionPerDay)).concat(" ")
                .concat(String.valueOf(nmbrOfDays));

        return resultString;

    }

    public IDNumber getEntityId() {
        return entityId;
    }

    public void setEntityId(IDNumber entityId) {
        this.entityId = entityId;
    }

    public IDNumber getMeterLocationId() {
        return meterLocationId;
    }

    public void setMeterLocationId(IDNumber meterLocationId) {
        this.meterLocationId = meterLocationId;
    }

    public IDNumber getMeterId() {
        return meterId;
    }

    public void setMeterId(IDNumber meterId) {
        this.meterId = meterId;
    }

    public DateString getMeasurementDate() {
        return measurementDate;
    }

    public void setMeasurementDate(DateString measurementDate) {
        this.measurementDate = measurementDate;
    }

    public Double getMeasurementValue() {
        return measurementValue;
    }

    public void setMeasurementValue(Double measurementValue) {
        this.measurementValue = measurementValue;
    }
}
