package com.easytobook.api.model;

public class CancellationPolicy {
    public int policy;
    public int period;
    public Penalty penaltyLate;
    public int noShowType;
    public String text;

    public static class Penalty {
        int type;
        int value;
    }
}