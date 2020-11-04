package com.epic.minilib.commands;

public interface Subsystem {

    default void periodic() {}

    default void setDefaultCommand(Command defaultCommand) {
        CommandScheduler.getInstance().setDefaultCommand(this, defaultCommand);
    }

    default Command getDefaultCommand() {
        return CommandScheduler.getInstance().getDefaultCommand(this);
    }

    default void register() {
        CommandScheduler.getInstance().registerSubsystem(this);
    }
}
