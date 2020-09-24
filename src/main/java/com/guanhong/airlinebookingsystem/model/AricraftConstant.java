package com.guanhong.airlinebookingsystem.model;

public class AricraftConstant {
    private static final int B737_CAPACITY = 169;
    private static final int B777_CAPACITY = 400;
    private static final int CRJ900_CAPACITY = 76;
    private static final int A320_CAPACITY = 146;
    private static final int CRJ200_CAPACITY = 50;

    public static int getCapacityByAircraft(int aircraftId) {
        switch (aircraftId) {
            case 737:
                return B737_CAPACITY;
            case 777:
                return B777_CAPACITY;
            case 900:
                return CRJ900_CAPACITY;
            case 320:
                return A320_CAPACITY;
            case 200:
                return CRJ200_CAPACITY;
            default:
                return 0;
        }
    }

    public static boolean validAircraftID(int aircraftId){
        switch (aircraftId) {
            case 737:
                return true;
            case 777:
                return true;
            case 900:
                return true;
            case 320:
                return true;
            case 200:
                return true;
            default:
                return false;
        }
    }
}
