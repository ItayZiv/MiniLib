package com.epic.minilib.commands;

import java.util.Set;

public interface Command {

    default void initialize() {}

    default void execute() {}

    default void end(boolean interrupted) {}

    default boolean isFinished() {
        return false;
    }

    Set<Subsystem> getRequirements();

    default void schedule(boolean interruptible) {
        // todo impl via scheduler
    }

    default void schedule() {
        schedule(true);
    }

    default void cancel() {
        // todo impl via scheduler
    }

    default boolean isScheduled() {
        // todo impl via scheduler
        return false;
    }

    default boolean hasRequirement(Subsystem requirement) {
        return getRequirements().contains(requirement);
    }
}
