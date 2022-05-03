package de.hskl.contol;

public class MaskDistanceController {
    private boolean masked;
    private boolean distance;

    public MaskDistanceController(boolean masked, boolean distance) {
        this.distance = distance;
        this.masked = masked;
    }

    public boolean isMasked() {
        return masked;
    }

    public void setMasked(boolean masked) {
        this.masked = masked;
    }

    public boolean isDistance() {
        return distance;
    }

    public void setDistance(boolean distance) {
        this.distance = distance;
    }

}
