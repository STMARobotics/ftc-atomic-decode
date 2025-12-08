#!/usr/bin/env python3
"""
pedro_pp_to_java.py

Versioned Pedro Pathing -> Java generator.

v0.3.1
- Generates:
  - Paths class (BezierLine/BezierCurve + PathChain) using Pedro 2.x APIs
  - SolversLib CommandOpMode auto that:
      * has @Autonomous annotation
      * schedules a SequentialCommandGroup in initialize()
      * includes a Javadoc block with setup instructions
- Supports:
  - FTC 144x144 field mirroring (blue <-> red) across vertical center line
  - Custom paths class name (e.g. NineBackPaths, TwelveBackPaths)
"""

import json
import re
from dataclasses import dataclass
from pathlib import Path
from typing import List

__version__ = "0.3.1"

# ==============================
# Data models
# ==============================

@dataclass
class Point:
    x: float
    y: float
    heading_deg: float  # degrees


@dataclass
class PathSpec:
    name: str
    points: List[Point]


@dataclass
class PPExport:
    paths: List[PathSpec]


# ==============================
# Name sanitizing (for Java)
# ==============================

def sanitize_name(name: str) -> str:
    # Trim whitespace
    name = name.strip()
    # Remove spaces
    name = name.replace(" ", "")
    # Remove any characters that aren't letters, digits, or underscore
    name = re.sub(r"[^0-9a-zA-Z_]", "", name)
    # Ensure it doesn't start with a digit and isn't empty
    if not name or name[0].isdigit():
        name = "P" + name
    return name


# ==============================
# Load .pp (Pedro visualizer format)
# ==============================

def load_pp(path: Path) -> PPExport:
    """
    Expected structure (simplified Pedro visualizer export):

    {
      "startPoint": {
        "x": ...,
        "y": ...,
        "heading": "linear",
        "startDeg": 90,
        "endDeg": 180
      },
      "lines": [
        {
          "name": "Path 1",
          "endPoint": {
            "x": ...,
            "y": ...,
            "heading": "linear",
            "startDeg": 90,
            "endDeg": 180
          },
          "controlPoints": [
            {"x": ..., "y": ...},
            ...
          ],
          "color": "#568AB5"
        },
        ...
      ]
    }

    Each line becomes one PathSpec:
      [startPoint/previousEnd, controlPoints..., endPoint]
    """
    raw = json.loads(path.read_text())

    # Initial start point
    sp = raw["startPoint"]
    start_deg = float(sp.get("startDeg", sp.get("endDeg", 0.0)))
    current_start = Point(
        x=float(sp["x"]),
        y=float(sp["y"]),
        heading_deg=start_deg,
    )
    current_heading = start_deg

    lines = raw["lines"]
    paths: List[PathSpec] = []

    for idx, line in enumerate(lines):
        raw_name = line.get("name") or f"path{idx}"
        name = sanitize_name(raw_name)

        points: List[Point] = []

        # Start of this line = previous end
        start_pt = Point(
            x=current_start.x,
            y=current_start.y,
            heading_deg=current_heading,
        )
        points.append(start_pt)

        # Control points (if any), reuse current heading
        for cp in line.get("controlPoints", []):
            points.append(
                Point(
                    x=float(cp["x"]),
                    y=float(cp["y"]),
                    heading_deg=current_heading,
                )
            )

        # End point
        ep = line["endPoint"]
        end_heading = float(ep.get("endDeg", current_heading))
        end_pt = Point(
            x=float(ep["x"]),
            y=float(ep["y"]),
            heading_deg=end_heading,
        )
        points.append(end_pt)

        paths.append(PathSpec(name=name, points=points))

        # Next line starts here
        current_start = end_pt
        current_heading = end_heading

    return PPExport(paths=paths)


# ==============================
# Mirroring for alliance swap
# ==============================

# FTC field default: 144" x 144"
FIELD_WIDTH = 144.0
FIELD_LENGTH = 144.0

def mirror_point(p: Point,
                 field_width: float = FIELD_WIDTH,
                 field_length: float = FIELD_LENGTH) -> Point:
    """
    Mirror across the vertical center line x = field_width / 2
    Coordinate system: x+ right, y+ up, 0° facing right.
    Reflection:
      x' = field_width - x
      y' = y
      heading' = (180 - heading) mod 360
    """
    return Point(
        x=field_width - p.x,
        y=p.y,
        heading_deg=(180.0 - p.heading_deg) % 360.0,
    )


def mirror_path(spec: PathSpec,
                field_width: float = FIELD_WIDTH,
                field_length: float = FIELD_LENGTH,
                suffix: str = "Red") -> PathSpec:
    """
    Create a mirrored copy of a path for the opposite alliance.
    e.g. Path1 -> Path1Red
    """
    new_name = sanitize_name(spec.name + suffix)
    return PathSpec(
        name=new_name,
        points=[mirror_point(p, field_width, field_length) for p in spec.points],
    )


# ==============================
# Java generation helpers
# ==============================

def pose_literal(p: Point) -> str:
    # Pose(x, y, headingRadians)
    return f"new Pose({p.x}, {p.y}, Math.toRadians({p.heading_deg}))"


def bezier_constructor(points: List[Point]) -> str:
    """
    If 2 points: new BezierLine(p0, p1)
    If >=3 points: new BezierCurve(p0, p1, p2, ...)
    """
    pose_args = ", ".join(pose_literal(pt) for pt in points)
    if len(points) == 2:
        return f"new BezierLine({pose_args})"
    else:
        return f"new BezierCurve({pose_args})"


def path_method_for(spec: PathSpec) -> str:
    """
    Build body of a static method that returns a PathChain
    using follower.pathBuilder().

    We fully-qualify Path to avoid conflicts with java.nio.file.Path etc.
    """
    bezier_expr = bezier_constructor(spec.points)
    start = spec.points[0]
    end = spec.points[-1]

    return f"""    public static PathChain {spec.name}(Follower follower) {{
        return follower.pathBuilder()
                .addPath(new com.pedropathing.paths.Path({bezier_expr}))
                .setLinearHeadingInterpolation(
                        Math.toRadians({start.heading_deg}),
                        Math.toRadians({end.heading_deg}))
                .build();
    }}
"""


# ==============================
# Java files
# ==============================

def generate_paths_java(
    export: PPExport,
    package: str,
    class_name: str,
) -> str:
    methods = "\n".join(path_method_for(p) for p in export.paths)

    return f"""package {package};

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;

public class {class_name} {{

{methods}
}}
"""


def generate_auto_java(
    export: PPExport,
    package: str,
    paths_package: str,
    paths_class: str,
    class_name: str,
) -> str:
    """
    Generate a CommandOpMode-based autonomous template that:
      - Uses SolversLib CommandOpMode and SequentialCommandGroup
      - Uses com.seattlesolvers.solverslib.pedroCommand.FollowPathCommand
      - Schedules the sequence in initialize()
      - Includes a Javadoc/guide at the top of the file
      - Runs ONLY base paths (no *Red mirrored ones)
    """
    command_list = ",\n                ".join(
        f"new FollowPathCommand(follower, {paths_class}.{p.name}(follower))"
        for p in export.paths
        if not p.name.endswith("Red")
    )

    guide_comment = f"""/** 
 * AUTO TEMPLATE GENERATED BY Pedro PP to Java v{__version__}
 *
 * HOW TO SET THIS UP:
 * 1) Ensure build.gradle has:
 *      implementation "com.pedropathing:ftc:2.x.x"
 *      implementation "com.pedropathing:telemetry:1.x.x"
 *      implementation "org.solverslib:core:0.3.x"
 *      implementation "org.solverslib:pedroPathing:0.3.x"
 *
 * 2) This class extends CommandOpMode. The lifecycle is:
 *      - initialize() is called during INIT on the driver station.
 *      - After you press PLAY, CommandScheduler runs and executes the scheduled commands.
 *
 * 3) Replace the follower initialization with your own helper if you have one.
 *      Example:
 *          follower = MyPedroFactory.createFollower(hardwareMap, telemetry);
 *
 * 4) Add and construct your subsystems in initialize(), then optionally register them.
 *      Example:
 *          private PlatterSubsystem platterSubsystem;
 *          private IntakeSubsystem intakeSubsystem;
 *          ...
 *
 *          platterSubsystem = new PlatterSubsystem(hardwareMap);
 *          intakeSubsystem  = new IntakeSubsystem(hardwareMap);
 *          ...
 *          register(platterSubsystem, intakeSubsystem, ...);
 *
 *      Then you can insert your own commands (shoot, intake, etc.) into the sequence
 *      alongside the FollowPathCommand steps.
 *
 * 5) Alliance / options selection (optional):
 *      - Override initLoop() if you want to choose alliance or tuning flags using gamepads.
 *      - Store flags (e.g. selectedAlliance, colorSortingEnabled) as fields.
 *      - Use those flags when building the command sequence in initialize().
 *
 * 6) Mirrored paths:
 *      - If you ran the converter with --mirror-alliance, this paths class also has
 *        methods like Path1Red(...), Path2Red(...).
 *      - You can choose between PathX(...) and PathXRed(...) based on alliance.
 *
 * 7) After generation:
 *      - Add @Autonomous(name = "...", group = "Autos") above this class.
 *      - Edit the SequentialCommandGroup below to insert waits, parallel groups,
 *        and mechanism commands as needed.
 */"""

    return f"""package {package};

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.seattlesolvers.solverslib.command.CommandOpMode;
import com.seattlesolvers.solverslib.command.SequentialCommandGroup;
import com.seattlesolvers.solverslib.pedroCommand.FollowPathCommand;
import com.pedropathing.follower.Follower;
import {paths_package}.{paths_class};

{guide_comment}
@Autonomous(name = "{class_name}", group = "Autos")
public class {class_name} extends CommandOpMode {{

    // Pedro follower used for driving the paths
    private Follower follower;

    @Override
    public void initialize() {{
        // TODO: Replace with your follower initialization helper if you have one
        follower = new Follower(hardwareMap);

        // TODO: Construct your subsystems here and optionally call register(...)
        // Example:
        //   platterSubsystem = new PlatterSubsystem(hardwareMap);
        //   intakeSubsystem  = new IntakeSubsystem(hardwareMap);
        //   register(platterSubsystem, intakeSubsystem);

        // Schedule the generated path sequence
        schedule(
            new SequentialCommandGroup(
                {command_list}
            )
        );
    }}
}}
"""


# ==============================
# CLI entrypoint
# ==============================

def main():
    import argparse

    parser = argparse.ArgumentParser(
        description=f"Convert Pedro Pathing .pp file into Java Paths + Auto (v{__version__})"
    )
    parser.add_argument("pp_file", type=Path, help=".pp file from visualizer")
    parser.add_argument(
        "--out-dir",
        type=Path,
        default=Path("."),
        help="Output root directory for Java (e.g. TeamCode/src/main/java)",
    )
    parser.add_argument(
        "--auto-class",
        default="GeneratedAuto",
        help="Name of generated auto class",
    )
    parser.add_argument(
        "--paths-class",
        default="PPPaths",
        help="Java class name for generated paths (e.g. NineBackPaths)",
    )
    parser.add_argument(
        "--paths-package",
        default="org.firstinspires.ftc.teamcode.Autos.Paths",
        help="Java package for paths class",
    )
    parser.add_argument(
        "--auto-package",
        default="org.firstinspires.ftc.teamcode.Autos",
        help="Java package for auto class",
    )
    parser.add_argument(
        "--mirror-alliance",
        action="store_true",
        help="Also generate mirrored (Red) paths by reflecting across field center line",
    )
    parser.add_argument(
        "--field-width",
        type=float,
        default=FIELD_WIDTH,
        help="Field width in same units as .pp (default 144.0)",
    )
    parser.add_argument(
        "--field-length",
        type=float,
        default=FIELD_LENGTH,
        help="Field length in same units as .pp (default 144.0)",
    )
    parser.add_argument(
        "--version",
        action="store_true",
        help="Print script version and exit",
    )

    args = parser.parse_args()

    if args.version:
        print(__version__)
        return

    export = load_pp(args.pp_file)

    # Optionally append mirrored (red) versions of each path
    if args.mirror_alliance:
        mirrored = [
            mirror_path(
                p,
                field_width=args.field_width,
                field_length=args.field_length,
                suffix="Red",
            )
            for p in export.paths
        ]
        export.paths.extend(mirrored)

    paths_java = generate_paths_java(
        export,
        package=args.paths_package,
        class_name=args.paths_class,
    )
    auto_java = generate_auto_java(
        export,
        package=args.auto_package,
        paths_package=args.paths_package,
        paths_class=args.paths_class,
        class_name=args.auto_class,
    )

    # Write files under out-dir respecting package structure
    paths_dir = args.out_dir / Path(args.paths_package.replace(".", "/"))
    auto_dir = args.out_dir / Path(args.auto_package.replace(".", "/"))
    paths_dir.mkdir(parents=True, exist_ok=True)
    auto_dir.mkdir(parents=True, exist_ok=True)

    paths_file = paths_dir / f"{args.paths_class}.java"
    auto_file = auto_dir / f"{args.auto_class}.java"

    paths_file.write_text(paths_java, encoding="utf-8")
    auto_file.write_text(auto_java, encoding="utf-8")

    print(f"[pedro_pp_to_java v{__version__}] Wrote:")
    print("  ", paths_file)
    print("  ", auto_file)


if __name__ == "__main__":
    main()
