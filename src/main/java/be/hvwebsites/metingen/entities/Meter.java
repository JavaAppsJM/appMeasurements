package be.hvwebsites.metingen.entities;

import be.hvwebsites.libraryandroid4.helpers.IDNumber;
import be.hvwebsites.metingen.constants.ElectricityPriceType;
import be.hvwebsites.metingen.constants.HomeService;

public class Meter {
    private IDNumber entityId;
    private String entityName;
    private IDNumber meterLocationId;
    private HomeService homeServiceType;
    private ElectricityPriceType electricityPriceType;
    public static final String METER_LATEST_ID = "meterlatestid";


    public Meter() {
    }

    public Meter(String basedir, boolean b) {
        entityId = new IDNumber(basedir, METER_LATEST_ID);
        entityName = "";
    }

    public Meter(String fileLine){
        convertFromFileLine(fileLine);
    }

    public void setMeter(Meter inMeter){
        setEntityId(inMeter.getEntityId());
        setEntityName(inMeter.getEntityName());
        setMeterLocationId(inMeter.getMeterLocationId());
        setHomeServiceType(inMeter.getHomeServiceType());
        setElectricityPriceType(inMeter.getElectricityPriceType());
    }

    public void convertFromFileLine(String fileLine) {
        // Maakt een meter obv een fileline -
        // format: <key><102><meter><meternaam><location><11><homeservice><1><pricetype><0>
        // fileLine splitsen in argumenten
        String[] fileLineContent = fileLine.split("<");
        for (int i = 0; i < fileLineContent.length; i++) {
            if (fileLineContent[i].matches("key.*")){
                setEntityId(new IDNumber(fileLineContent[i+1].replace(">", "")));
            }
            if (fileLineContent[i].matches("meter.*")){
                setEntityName(fileLineContent[i+1].replace(">",""));
            }
            if (fileLineContent[i].matches("location.*")){
                setMeterLocationId(new IDNumber(fileLineContent[i+1].replace(">", "")));
            }
            if (fileLineContent[i].matches("homeservice.*")){
                HomeService water = HomeService.WATER;
                HomeService electricity = HomeService.ELECTRICITY;
                HomeService other = HomeService.OTHER;
                int hsInt = Integer.parseInt(fileLineContent[i+1].replace(">", ""));
                if (hsInt == water.ordinal()){
                    setHomeServiceType(water);
                }else if (hsInt == electricity.ordinal()){
                    setHomeServiceType(electricity);
                }else {
                    setHomeServiceType(other); // default
                }
            }
            if (fileLineContent[i].matches("pricetype.*")){
                ElectricityPriceType day = ElectricityPriceType.DAY;
                ElectricityPriceType night = ElectricityPriceType.NIGHT;
                ElectricityPriceType xnight = ElectricityPriceType.EXCLUSIVE_NIGHT;
                int ptInt = Integer.parseInt(fileLineContent[i+1].replace(">", ""));
                if (ptInt == day.ordinal()){
                    setElectricityPriceType(day);
                }else if (ptInt == night.ordinal()){
                    setElectricityPriceType(night);
                }else if (ptInt == xnight.ordinal()){
                    setElectricityPriceType(xnight);
                }else {
                    setElectricityPriceType(day); // default
                }
            }
        }
    }

    public String convertToFileLine() {
        // Maakt een fileline van een meter -
        // format: <key><102><meter><meternaam><location><11><homeservice><1><pricetype><0>
        String fileLine = "<key><" + getEntityId().getIdString()
                + "><meter><" + getEntityName()
                + "><location><" + getMeterLocationId().getIdString()
                + "><homeservice><" + getHomeServiceType().ordinal()
                + "><pricetype><" + getElectricityPriceType().ordinal()
                + ">";
        return fileLine;
    }

    public HomeService getHomeServiceType() {
        return homeServiceType;
    }

    public void setHomeServiceType(HomeService homeServiceType) {
        this.homeServiceType = homeServiceType;
    }

    public ElectricityPriceType getElectricityPriceType() {
        return electricityPriceType;
    }

    public void setElectricityPriceType(ElectricityPriceType electricityPriceType) {
        this.electricityPriceType = electricityPriceType;
    }

    public IDNumber getEntityId() {
        return entityId;
    }

    public void setEntityId(IDNumber entityId) {
        this.entityId = entityId;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public IDNumber getMeterLocationId() {
        return meterLocationId;
    }

    public void setMeterLocationId(IDNumber meterLocationId) {
        this.meterLocationId = meterLocationId;
    }
}
