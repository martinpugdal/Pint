package dk.martinersej.pint.utils.command;

public enum Result {
    NO_PERMISSION,
    NO_SUB_COMMAND_FOUND,
    SUCCESS,
    WRONG_USAGE;

    public static CommandResult getCommandResult(Result result, SubCommand subCommand) {
        return new CommandResult(subCommand, result);
    }
}
