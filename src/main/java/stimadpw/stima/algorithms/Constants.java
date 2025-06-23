package stimadpw.stima.algorithms;

/**
 * A utility class to hold all magic numbers and tunable constants for the pathfinder.
 */
public final class Constants {

    private Constants() {}

    // --- TIMING & DELAY CONSTANTS ---
    public static final long PATHFINDING_TIMEOUT_MS = 150000L;

    // --- MOVEMENT PHYSICS & THRESHOLDS ---
    public static final double DEFAULT_WALKING_FORCE = 0.3D;

    // --- JUMP COST CALCULATION CONSTANTS ---
    public static final double JUMP_COST_DISTANCE_SCALAR = 1.3;
    public static final double JUMP_COST_BENCHMARK_FORCE = 0.39D;

    // --- JUMP FORCE CONSTANTS ---
    public static final double FORCE_LONG_JUMP_A = 0.39D;
    public static final double FORCE_LONG_JUMP_B = 0.35D;
    public static final double FORCE_MEDIUM_JUMP_A = 0.32D;
    public static final double FORCE_MEDIUM_JUMP_B = 0.30D;
    public static final double FORCE_MEDIUM_JUMP_C = 0.25D;
    public static final double FORCE_SHORT_JUMP_A = 0.18D;
    public static final double FORCE_SHORT_JUMP_B = 0.15D;
    public static final double FORCE_FALLING_MOVE = 0.1D;
}