package be.hvwebsites.metingen.entities;

import be.hvwebsites.libraryandroid4.helpers.DateString;
import be.hvwebsites.libraryandroid4.helpers.IDNumber;
import java.lang.Math;

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
        String dagverbruikString = "";
        String verbruikString = "";

        // Voeg datum toe
        String resultString = getMeasurementDate().getFormatDate().concat(" ");

        // Voeg meterstand toe
        double tempDouble1 = roundDouble(getMeasurementValue(), 2);
        String tempString1 = String.valueOf(tempDouble1);
        String meterstandString = formatString(tempString1, 10, 'r', 2);
        resultString = resultString.concat(meterstandString);

        // Bereken het aantal dagen tssn current en vorige meting
        int nmbrOfDays = getMeasurementDate().calculateDateDifference(
                mPrevious.getMeasurementDate());
        String periodeString = formatString(String.valueOf(nmbrOfDays), 3,'r', 0);
        if (nmbrOfDays > 0){
            // Bereken het verbruik en het verbruik per dag
            consumption = getMeasurementValue() - mPrevious.getMeasurementValue();
            verbruikString = formatString(String.valueOf(
                    roundDouble(consumption, 2)), 8, 'r', 2);
            consumptionPerDay = consumption/nmbrOfDays;
            dagverbruikString = formatString(String.valueOf(
                    roundDouble(consumptionPerDay, 4)), 8, 'r', 4);
        }

        // Voeg verbruik, verbruik/dag en periode toe in resultstring
        resultString = resultString.concat(" ")
                .concat(formatString(String.valueOf(
                        roundDouble(consumption, 2)), 8, 'r', 2))
                .concat(" ")
                .concat(formatString(String.valueOf(
                        roundDouble(consumptionPerDay, 4)), 8, 'r', 4))
                .concat(" ")
                .concat(formatString(String.valueOf(nmbrOfDays), 4, 'r', 0));

        return resultString;
    }

    private double roundDouble(double inDouble, int nmbrOfDecimals){
        double tempDouble = 0;
        int decimalFactor = (int) Math.pow(10, nmbrOfDecimals);

        tempDouble = Math.floor(inDouble * decimalFactor)/decimalFactor;

        return tempDouble;
    }

    private String formatString(String inString, int stringLength, char alignChar, int nmbrDecimals){
        // Formatteert het bedrag in inString
        String outString = "                              "; // 30 lang
        String fillZero = "000000000000000000000000000";
        int inStringLength = inString.length();
        String intPart;
        String decPart;

        // Bepaal decimal part
        if (inString.contains(".")){
            // Splitsen in int part en dec part indien inString een . bevat
            String[] nmbrString = inString.split("\\.");
            intPart = nmbrString[0];
            decPart = nmbrString[1];

            if (decPart.length() < nmbrDecimals){
                // Aanvullen decimal part indien nodig
                int aantalZeroToAdd = nmbrDecimals - decPart.length();
                String aanvulString = fillZero.substring(0, aantalZeroToAdd);
                decPart = decPart.concat(aanvulString);
            }
            decPart = ",".concat(decPart);
        }else {
            // er is geen decimaal gedeelte
            intPart = inString;
            decPart = "";
        }

        // Samenstellen intpart
        if (intPart.length() < (stringLength-decPart.length())){
            // aanvullen int part indien nodig
            int aantalSpaceToFill = stringLength - decPart.length() - intPart.length();
            String spaceToFill = outString.substring(0, aantalSpaceToFill);

            // outstring samenstellen
            switch (alignChar){
                case 'r':
                    outString = spaceToFill.concat(intPart).concat(decPart);
                    break;
                case 'l':
                    outString = intPart.concat(decPart).concat(spaceToFill);
                    break;
                case 'm':
                    spaceToFill = outString.substring(0, (aantalSpaceToFill/2));
                    outString = spaceToFill.concat(intPart).concat(decPart).concat(spaceToFill);
                    break;
            }
        }else {
            // TODO: als input string langer is dan de plaats voorzien ?
            outString = inString;
        }
        return outString;
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
