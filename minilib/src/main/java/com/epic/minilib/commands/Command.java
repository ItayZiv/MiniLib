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

    default void schedule() {
        CommandScheduler.getInstance().schedule(this);
    }

    default void cancel() {
        CommandScheduler.getInstance().cancelCommand(this);
    }

    default boolean isScheduled() {
        return CommandScheduler.getInstance().isScheduled(this);
    }

    default boolean hasRequirement(Subsystem requirement) {
        return getRequirements().contains(requirement);
    }
}
