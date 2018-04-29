package io.github.tavisco.rvglassistant.objects;

/**
 * Created by Tavisco on 29/04/18.
 */
public enum ItemType {
    LEVEL("level", "levels", 4),
    CAR("car", "cars", 7),
    UNKNOWN("unknown", "", 0);

    private final String typeText;
    private final String typePath;
    private final int typeParameterNameLine;

    ItemType(String text, String path,int parameterNameLine){
        typeText = text;
        typePath = path;
        typeParameterNameLine = parameterNameLine;
    }

    public String getTypeText(){
        return typeText;
    }

    public String getTypePath() {
        return typePath;
    }

    public int getTypeParameterNameLine() {
        return typeParameterNameLine;
    }
}
