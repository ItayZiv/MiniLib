package com.epic.minilib.commands;

public interface Subsystem {

    default void periodic() {}

    default void setDefaultCommand(Command defaultCommand) {
        // todo impl via scheduler
    }

    default Command getDefaultCommand() {
        // todo impl via scheduler
        return null;
    }

    default void register() {
        // todo impl via scheduler
    }
}
