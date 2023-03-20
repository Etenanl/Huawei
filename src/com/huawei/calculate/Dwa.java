package com.huawei.calculate;

public class Dwa {
    public void createDynamicWindow(Velocity velocity, Config config, DynamicWindow dynamicWindow) {
        double minV = Math.max(config.minSpeed, velocity.linearVelocity - config.maxAccel * config.dt);
        double maxV = Math.min(config.maxSpeed, velocity.linearVelocity + config.maxAccel * config.dt);
        double minW = Math.max(-config.maxYawrate, velocity.angularVelocity - config.maxdYawrate * config.dt);
        double maxW = Math.min(config.maxYawrate, velocity.angularVelocity + config.maxdYawrate * config.dt);

        int nPossibleV =(int) ((maxV - minV) / config.velocityResolution);
        int nPossibleW =(int)((maxW - minW) / config.yawrateResolution);


        dynamicWindow.nPossibleV = nPossibleV;
        dynamicWindow.nPossibleW = nPossibleW;
        dynamicWindow.possibleV = new double[nPossibleV];
        dynamicWindow.possibleW = new double[nPossibleW];
        for(int i=0; i < nPossibleV; i++) {
            dynamicWindow.possibleV[i] = minV + (double)i * config.velocityResolution;
        }

        for(int i=0; i < nPossibleW; i++) {
            dynamicWindow.possibleW[i] = minW + (double)i * config.yawrateResolution;
        }
    }

    public PointCloud createPointCloud(int size){
        PointCloud pointCloud = new PointCloud();
        pointCloud.points = new Point[size];
        pointCloud.size = size;
        return pointCloud;
    }

    public Pose motion(Pose pose, Velocity velocity, double dt){
        Pose new_pose = new Pose();
        new_pose.yaw = pose.yaw + velocity.angularVelocity * dt;
        new_pose.point.x = pose.point.x + velocity.linearVelocity * Math.cos(new_pose.yaw) * dt;
        new_pose.point.y = pose.point.y + velocity.linearVelocity * Math.sin(new_pose.yaw) * dt;
        return new_pose;
    }

    public double calculateVelocityCost(Velocity velocity, Config config) {
        return config.maxSpeed - velocity.linearVelocity;
    }
    public double calculateHeadingCost(Pose pose, Point goal) {
        double dx = goal.x - pose.point.x;
        double dy = goal.y - pose.point.y;
        double angleError = Math.atan2(dy, dx);
        double angleCost = angleError - pose.yaw;
        return Math.abs(Math.atan2(Math.sin(angleCost), Math.cos(angleCost)));
    }

    public double calculateClearanceCost (Pose pose, Velocity velocity, PointCloud pointCloud, Config config) {
        Pose pPose = pose;
        double time = 0.0;
        double minr = Double.MAX_VALUE;
        double r;
        double dx;
        double dy;
        double x;
        double y;

        while (time < config.predictTime) {
            pPose = motion(pPose, velocity, config.dt);

            for(int i = 0; i < pointCloud.size; ++i) {
                dx = pPose.point.x - pointCloud.points[i].x;
                dy = pPose.point.y - pointCloud.points[i].y;
                x = -dx * Math.cos(pPose.yaw) + -dy * Math.sin(pPose.yaw);
                y = -dx * -Math.sin(pPose.yaw) + -dy * Math.cos(pPose.yaw);
                if (x <= config.base.xmax &&
                        x >= config.base.xmin &&
                        y <= config.base.ymax &&
                        y >= config.base.ymin){
                    return Double.MAX_VALUE;
                }
                r = Math.sqrt(dx*dx + dy*dy);
                if (r < minr)
                    minr = r;
            }
            time += config.dt;
        }
        return 1.0 / minr;
    }

    public Velocity planning(Pose pose, Velocity velocity, Point goal, PointCloud pointCloud, Config config) {
        DynamicWindow dw = new DynamicWindow();
        createDynamicWindow(velocity, config, dw);
        Velocity pVelocity = new Velocity();
        Pose pPose = pose;
        double total_cost = Double.MAX_VALUE;
        double cost;
        Velocity bestVelocity = new Velocity();
        for (int i = 0; i < dw.nPossibleV; ++i) {
            for (int j = 0; j < dw.nPossibleW; ++j) {
                pPose = pose;
                pVelocity.linearVelocity = dw.possibleV[i];
                pVelocity.angularVelocity = dw.possibleW[j];
                pPose = motion(pPose, pVelocity, config.predictTime);
                cost = config.velocity * calculateVelocityCost(pVelocity, config) +
                                config.heading * calculateHeadingCost(pPose, goal) +
                                config.clearance * calculateClearanceCost(pose, pVelocity,
                                        pointCloud, config);
                if (cost < total_cost) {
                    total_cost = cost;
                    bestVelocity = pVelocity;
                }
            }
        }
        //freeDynamicWindow(dw);
        return bestVelocity;
    }

}
