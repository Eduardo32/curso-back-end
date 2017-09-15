/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.curso.ws.model;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Eduardo
 */

@XmlRootElement
public class Temperature {
    
    private static Temperature INSTANCE = null;
    
    public static Temperature getInstace() {
        if(INSTANCE == null) {
            INSTANCE = new Temperature();
            System.out.println("Criou Singleton!");
        }
        return INSTANCE;
    }
    
    private float temperature = (float)0.0;
    
    private Temperature() {
        this.temperature = (float)0.0;
    }
    
    public Temperature(float temperature) {
        this.temperature = temperature;
    }
    
    public void setValue(float temperature) {
        this.temperature = temperature;
    }
    
    public float getValue() {
        return temperature;
    }
    
    public float getTemperature() {
        return temperature;
    }
    
    public void convertToFahrenheit() {
        temperature = (float) 1.8 * temperature + 32;
    }
}
