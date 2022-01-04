package be.hvwebsites.metingen.entities;

import be.hvwebsites.libraryandroid4.helpers.IDNumber;

public class Location {
    private IDNumber entityId;
    private String entityName;
    public static final String LOCATION_LATEST_ID = "locationlatestid";

    public Location() {
    }

    public Location(String basedir, boolean b) {
        entityId = new IDNumber(basedir, LOCATION_LATEST_ID);
        entityName = "";
    }

    public Location(String fileLine){
        convertFromFileLine(fileLine);
    }

    public void setLocation(Location inLocation){
        setEntityId(inLocation.getEntityId());
        setEntityName(inLocation.getEntityName());
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

    public void convertFromFileLine(String fileLine) {
        // Maakt een location obv een fileline - format: <key><102><location><kruisheide>
        // fileLine splitsen in argumenten
        String[] fileLineContent = fileLine.split("<");
        for (int i = 0; i < fileLineContent.length; i++) {
            if (fileLineContent[i].matches("key.*")){
                setEntityId(new IDNumber(fileLineContent[i+1].replace(">", "")));
            }
            if (fileLineContent[i].matches("location.*")){
                setEntityName(fileLineContent[i+1].replace(">",""));
            }
        }
    }

    public String convertToFileLine() {
        String fileLine = "<key><" + getEntityId().getIdString()
                + "><location><" + getEntityName() + ">";
        return fileLine;
    }


}
