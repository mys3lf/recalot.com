package com.example.cars.more;

import com.example.cars.interfaces.ICar;

/**
 * @author Matthaeus.schmedding
 */
public class Astra implements ICar {
    @Override
    public String getDesign() {
        return "-/##\\-";
    }

    @Override
    public String getName() {
        return "Astra";
    }
}
