package org.firstinspires.ftc.teamcode.Autos;

import com.seattlesolvers.solverslib.command.CommandOpMode;
import com.seattlesolvers.solverslib.command.SequentialCommandGroup;
import com.seattlesolvers.solverslib.pedroCommand.FollowPathCommand;
import com.pedropathing.follower.Follower;
import org.firstinspires.ftc.teamcode.Autos.Paths.NineBackPaths;
import org.firstinspires.ftc.teamcode.PedroPathing.Constants;

public class NineBack extends CommandOpMode {

    @Override
    public void initialize() {
        // TODO: Replace with your follower initialization
        Follower follower = Constants.createFollower(hardwareMap);

        schedule(
                new SequentialCommandGroup(
                        new FollowPathCommand(follower, NineBackPaths.Path1(follower)),
                        new FollowPathCommand(follower, NineBackPaths.Path2(follower)),
                        new FollowPathCommand(follower, NineBackPaths.Path3(follower)),
                        new FollowPathCommand(follower, NineBackPaths.Path4(follower)),
                        new FollowPathCommand(follower, NineBackPaths.Path7(follower)),
                        new FollowPathCommand(follower, NineBackPaths.Path8(follower))
                )
        );
    }
}
