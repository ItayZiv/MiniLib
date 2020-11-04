package com.epic.minilib.commands;

import static java.util.Arrays.asList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class CommandScheduler {

    private static CommandScheduler instance;

    public static synchronized CommandScheduler getInstance() {
        if (instance == null)
            instance = new CommandScheduler();

        return instance;
    }

    //Subsystems and their assigned command (or null)
    private final Map<Subsystem, SubsystemState> m_subsystems = new LinkedHashMap<>();

    private final Set<Command> m_commands = new LinkedHashSet<>();

    private final Set<Command> m_commandQueue = new LinkedHashSet<>();

    CommandScheduler() {

    }

    public void schedule(Command... commands) {
        m_commandQueue.addAll(asList(commands));
    }

    public void run() {
        m_subsystems.keySet().forEach(Subsystem::periodic);

        m_subsystems.forEach(
                (subsystem, subsystemState) -> {
                    if (subsystemState.activeCommand == null) {
                        initCommand(subsystemState.defaultCommand);
                    }
                }
        );

        m_commands.forEach(this::executeCommand);

        m_commandQueue.forEach(this::initCommand);
    }

    public void registerSubsystem(Subsystem... subsystems) {
        for (Subsystem subsystem : subsystems) {
            m_subsystems.put(subsystem, new SubsystemState());
        }
    }

    public void releaseSubsystem(Subsystem... subsystems) {
        for (Subsystem subsystem : subsystems) {
            m_subsystems.remove(subsystem);
        }
    }

    public void setDefaultCommand(Subsystem subsystem, Command command) {
        if (command.isFinished())
            throw new IllegalArgumentException("Default commands shouldn't end");

        m_subsystems.get(subsystem).defaultCommand = command;
    }

    public Command getDefaultCommand(Subsystem subsystem) {
        return m_subsystems.get(subsystem).defaultCommand;
    }

    public void cancelCommand(Command command) {
        endCommand(command, true);
    }

    public boolean isScheduled(Command command) {
        return m_commands.contains(command);
    }

    private void initCommand(Command command)
    {
        // Assign all the requirements of the command and interrupt
        // any command that requires a subsystem this command requires
        for (Subsystem subsystem : command.getRequirements()) {
            Command _command = m_subsystems.get(subsystem).activeCommand;
            if (_command != null)
                endCommand(command, true);

            m_subsystems.get(subsystem).activeCommand = command;
        }

        command.initialize();

        m_commands.add(command);
    }

    private void executeCommand(Command command)
    {
        command.execute();

        if (command.isFinished()) {
            endCommand(command, false);
        }
    }

    private void endCommand(Command command, boolean interrupted)
    {
        // End the command and remove it from our collection of commands
        command.end(interrupted);
        m_commands.remove(command);

        // Free all requirements of the command
        command.getRequirements().forEach(
                subsystem -> m_subsystems.get(subsystem).activeCommand = null
        );
    }

    private static class SubsystemState {
        public Command activeCommand = null;
        public Command defaultCommand = null;
    }
}
