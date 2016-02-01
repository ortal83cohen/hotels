package com.easytobook.api.model;

import java.util.HashMap;

/**
 * @author user
 * @date 2015-09-07
 */
public class TaxesAndFees {
    public String name;
    public int type;
    //    public HashMap<String, Double> value; API problem
    public HashMap<String, Double> totalValue;
    public boolean prepaid;
}


