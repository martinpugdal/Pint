package dk.martinersej.pint.utils.command;

public class CommandResult {

    private final SubCommand subCommand;
    private final Result result;
    private String wrongUsageMessage;

    public CommandResult(SubCommand subCommand, Result result) {
        this.subCommand = subCommand;
        this.result = result;
    }

    public static CommandResult getCommandResult(Result result, SubCommand subCommand) {
        return new CommandResult(subCommand, result);
    }

    public static CommandResult success(SubCommand subCommand) {
        return new CommandResult(subCommand, Result.SUCCESS);
    }

    public static CommandResult noPermission(SubCommand subCommand) {
        return new CommandResult(subCommand, Result.NO_PERMISSION);
    }

    public static CommandResult noSubCommandFound(SubCommand subCommand) {
        return new CommandResult(subCommand, Result.NO_SUB_COMMAND_FOUND);
    }

    public static CommandResult wrongUsage(SubCommand subCommand) {
        return new CommandResult(subCommand, Result.WRONG_USAGE);
    }

    public static CommandResult wrongUsage(SubCommand subCommand, String message) {
        CommandResult commandResult = new CommandResult(subCommand, Result.WRONG_USAGE);
        commandResult.setWrongUsageMessage(message);
        return commandResult;
    }

    public static CommandResult noConsole(SubCommand subCommand) {
        return new CommandResult(subCommand, Result.NO_CONSOLE);
    }

    public static CommandResult subCommandNotExists(SubCommand subCommand) {
        return new CommandResult(subCommand, Result.SUB_COMMAND_NOT_EXISTS);
    }

    public String getWrongUsageMessage() {
        return wrongUsageMessage;
    }

    public boolean wrongUsageMessageIsPresent() {
        return wrongUsageMessage != null && !wrongUsageMessage.isEmpty();
    }

    public void setWrongUsageMessage(String message) {
        this.wrongUsageMessage = message;
    }

    public SubCommand getSubCommand() {
        return subCommand;
    }

    public Result getResult() {
        return result;
    }
}