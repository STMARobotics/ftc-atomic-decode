package org.firstinspires.ftc.teamcode.Autos.Paths;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;

public class BackZoneSixPaths {

    public static PathChain Path1(Follower follower) {
        return follower.pathBuilder()
                .addPath(new com.pedropathing.paths.Path(new BezierLine(new Pose(51.153284671532845, 8.3, Math.toRadians(90.0)), new Pose(9.927007299270072, 8.3, Math.toRadians(180.0)))))
                .setLinearHeadingInterpolation(
                        Math.toRadians(90.0),
                        Math.toRadians(180.0))
                .build();
    }

    public static PathChain Path2(Follower follower) {
        return follower.pathBuilder()
                .addPath(new com.pedropathing.paths.Path(new BezierLine(new Pose(9.927007299270072, 8.3, Math.toRadians(180.0)), new Pose(53.95620437956204, 13.547445255474447, Math.toRadians(180.0)))))
                .setLinearHeadingInterpolation(
                        Math.toRadians(180.0),
                        Math.toRadians(180.0))
                .build();
    }

    public static PathChain Path3(Follower follower) {
        return follower.pathBuilder()
                .addPath(new com.pedropathing.paths.Path(new BezierLine(new Pose(53.95620437956204, 13.547445255474447, Math.toRadians(180.0)), new Pose(53.95620437956204, 13.547445255474447, Math.toRadians(0.0)))))
                .setLinearHeadingInterpolation(
                        Math.toRadians(180.0),
                        Math.toRadians(0.0))
                .build();
    }

}
