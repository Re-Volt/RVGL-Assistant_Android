package io.github.tavisco.rvglassistant.objects.enums;

/**
 * Created by Tavisco on 29/04/18.
 */
public enum ItemType {
    LEVEL("level", "levels", 4, "\'(.*?)\'", "\'"),
    CAR("car", "cars", 7, "\"(.*?)\"", "\""),
    UNKNOWN("unknown", "", 0, "", "");

    private final String typeText;
    private final String typePath;
    private final int typeParameterNameLine;
    private final String typeRegex;
    private final String typeReplacer;

    ItemType(String text, String path,int parameterNameLine, String regex, String replacer){
        typeText = text;
        typePath = path;
        typeParameterNameLine = parameterNameLine;
        typeRegex = regex;
        typeReplacer = replacer;
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

    public String getTypeRegex() {
        return typeRegex;
    }

    public String getTypeReplacer() {
        return typeReplacer;
    }
}
