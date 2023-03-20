package com.huawei.calculate;

public class Config {
    double maxSpeed;
    double minSpeed;
    double maxYawrate;
    double maxAccel;
    double maxdYawrate;
    double velocityResolution;
    double yawrateResolution;
    double dt;
    double predictTime;
    double heading;
    double clearance;
    double velocity;
    Rect base;

    public Config(double maxSpeed, double minSpeed, double maxYawrate,
                  double maxAccel, double maxdYawrate, double velocityResolution,
                  double yawrateResolution, double dt, double predictTime,
                  double heading, double clearance, double velocity, Rect base) {
        this.maxSpeed = maxSpeed;
        this.minSpeed = minSpeed;
        this.maxYawrate = maxYawrate;
        this.maxAccel = maxAccel;
        this.maxdYawrate = maxdYawrate;
        this.velocityResolution = velocityResolution;
        this.yawrateResolution = yawrateResolution;
        this.dt = dt;
        this.predictTime = predictTime;
        this.heading = heading;
        this.clearance = clearance;
        this.velocity = velocity;
        this.base = base;
    }
    public Config(){}
}
