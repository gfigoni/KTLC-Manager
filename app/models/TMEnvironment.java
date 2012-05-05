package models;

import java.util.ArrayList;
import java.util.List;

/**
 * Trackmania TMEnvironment
 * @author gehef
 */
public enum TMEnvironment {

    STADIUM, BAY, ISLAND, COAST, RALLY, SPEED, ALPINE, CANYON;

    public static TMEnvironment getEnvironment(String name) {
        return TMEnvironment.valueOf(name.toUpperCase());
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }
    
    public static List<TMEnvironment> getEnvironmentsTMU() {
    	List<TMEnvironment> enviros = new ArrayList<TMEnvironment>();
    	enviros.add(ALPINE);
    	enviros.add(SPEED);
    	enviros.add(BAY);
    	enviros.add(COAST);
    	enviros.add(ISLAND);
    	enviros.add(RALLY);
    	enviros.add(STADIUM);
    	
    	return enviros;
    }
    
    public static List<TMEnvironment> getEnvironmentsTM2() {
    	List<TMEnvironment> enviros = new ArrayList<TMEnvironment>();
    	enviros.add(CANYON);
    	
    	return enviros;
    }
}
