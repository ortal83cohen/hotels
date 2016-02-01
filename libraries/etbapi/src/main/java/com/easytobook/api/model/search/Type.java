package com.easytobook.api.model.search;

public abstract class Type {
    public final static String SPR = "spr"; // Single point radius
    public final static String POLYGON = "poly";
    public final static String VIEWPORT = "viewport";
    public final static String LIST = "list";
    private String mType;

    protected Type(String type) {
        mType = type;
    }

    protected Type() {
    }

    public String getType() {
        return mType;
    }

    public abstract String getContext();
}