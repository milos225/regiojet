package domain;

public enum VehicleType {

    BUS("BUS"),
    TRAIN("TRAIN");

    String vehicleType;

    VehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getVehicleType() {
        return vehicleType;
    }
}
