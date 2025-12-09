package org.firstinspires.ftc.teamcode.Autos.Paths;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;

public class BackZoneSixPaths {

    public static PathChain Path1(Follower follower) {
        return follower.pathBuilder()
                .addPath(new BezierLine(new Pose(53.51196172248804, 8.1, Math.toRadians(180.0)), new Pose(22.507177033492823, 8.1, Math.toRadians(180.0))))
                .setLinearHeadingInterpolation(
                        Math.toRadians(180.0),
                        Math.toRadians(180.0))
                .build();
    }

    public static PathChain Path2(Follower follower) {
        return follower.pathBuilder()
                .addPath(new BezierLine(new Pose(22.507177033492823, 8.1, Math.toRadians(180.0)), new Pose(22.507177033492823, 8.1, Math.toRadians(0.0))))
                .setLinearHeadingInterpolation(
                        Math.toRadians(180.0),
                        Math.toRadians(180.0))
                .build();
    }

    public static PathChain Path3(Follower follower) {
        return follower.pathBuilder()
                .addPath(new BezierLine(new Pose(53.95620437956204, 13.547445255474447, Math.toRadians(180.0)), new Pose(53.95620437956204, 13.547445255474447, Math.toRadians(0.0))))
                .setLinearHeadingInterpolation(
                        Math.toRadians(180.0),
                        Math.toRadians(0.0))
                .build();
    }

}
