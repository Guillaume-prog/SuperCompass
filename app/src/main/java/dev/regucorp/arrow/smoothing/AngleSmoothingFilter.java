package dev.regucorp.arrow.smoothing;

public class AngleSmoothingFilter {

    private RollingAverage sinFilter;
    private RollingAverage cosFilter;

    public AngleSmoothingFilter(int size) {
        sinFilter = new RollingAverage(size);
        cosFilter = new RollingAverage(size);
    }

    public void add(float angleInDegree) {
        float radian = (float) Math.toRadians(angleInDegree);
        float sinValue = (float) Math.sin(radian);
        float cosValue = (float) Math.cos(radian);

        sinFilter.add(sinValue);
        cosFilter.add(cosValue);
    }

    public float getAverage() {
        float sinAverage = (float) sinFilter.getAverage();
        float cosAverage = (float) cosFilter.getAverage();

        float radian = (float) Math.atan2(sinAverage, cosAverage);
        return (float) Math.toDegrees(radian);
    }

}
