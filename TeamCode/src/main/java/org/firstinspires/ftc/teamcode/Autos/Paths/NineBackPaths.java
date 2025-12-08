package org.firstinspires.ftc.teamcode.Autos.Paths;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;

public class NineBackPaths {

    public static PathChain Path1(Follower follower) {
        return follower.pathBuilder()
                .addPath(new com.pedropathing.paths.Path(new BezierLine(new Pose(43.5, 7.5, Math.toRadians(90.0)), new Pose(36.0, 29.5, Math.toRadians(135.0)))))
                .setLinearHeadingInterpolation(
                        Math.toRadians(90.0),
                        Math.toRadians(135.0))
                .build();
    }

    public static PathChain Path2(Follower follower) {
        return follower.pathBuilder()
                .addPath(new com.pedropathing.paths.Path(new BezierLine(new Pose(36.0, 29.5, Math.toRadians(135.0)), new Pose(15.5, 34.0, Math.toRadians(135.0)))))
                .setLinearHeadingInterpolation(
                        Math.toRadians(135.0),
                        Math.toRadians(135.0))
                .build();
    }

    public static PathChain Path3(Follower follower) {
        return follower.pathBuilder()
                .addPath(new com.pedropathing.paths.Path(new BezierLine(new Pose(15.5, 34.0, Math.toRadians(135.0)), new Pose(47.5, 9.0, Math.toRadians(180.0)))))
                .setLinearHeadingInterpolation(
                        Math.toRadians(135.0),
                        Math.toRadians(180.0))
                .build();
    }

    public static PathChain Path4(Follower follower) {
        return follower.pathBuilder()
                .addPath(new com.pedropathing.paths.Path(new BezierLine(new Pose(47.5, 9.0, Math.toRadians(180.0)), new Pose(12.0, 10.0, Math.toRadians(180.0)))))
                .setLinearHeadingInterpolation(
                        Math.toRadians(180.0),
                        Math.toRadians(180.0))
                .build();
    }

    public static PathChain Path7(Follower follower) {
        return follower.pathBuilder()
                .addPath(new com.pedropathing.paths.Path(new BezierLine(new Pose(12.0, 10.0, Math.toRadians(180.0)), new Pose(62.0, 23.0, Math.toRadians(180.0)))))
                .setLinearHeadingInterpolation(
                        Math.toRadians(180.0),
                        Math.toRadians(180.0))
                .build();
    }

    public static PathChain Path8(Follower follower) {
        return follower.pathBuilder()
                .addPath(new com.pedropathing.paths.Path(new BezierLine(new Pose(62.0, 23.0, Math.toRadians(180.0)), new Pose(62.0, 34.0, Math.toRadians(180.0)))))
                .setLinearHeadingInterpolation(
                        Math.toRadians(180.0),
                        Math.toRadians(180.0))
                .build();
    }

}
