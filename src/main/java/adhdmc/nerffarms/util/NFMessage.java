package adhdmc.nerffarms.util;

public enum NFMessage {
    PLUGIN_RELOADED("<gold>NerfFarms has been reloaded!"),
    PLUGIN_RELOADED_WITH_ERRORS("<red>Your config had <errors> error(s). Check your console for details."),
    PLUGIN_USELESS("<red>Your config does not modify mob drops or exp, this can be due to an error with the modification-type setting or it was set to neither!\n<gold>This plugin will do nothing in this state."),
    INCORRECT_INPUT("<red><hover:show_text:'<gray>/nerffarms help'><click:suggest_command:'/nerffarms help'>Sorry! You input the command incorrectly. Please use /nerffarms help to see all commands.</click></hover>"),
    PLUGIN_INFO("<click:open_url:'https://github.com/ADHDMC/NerfFarms'><gray><hover:show_text:'<aqua>Click to visit the GitHub repository'><green>NerfFarms</green> <white>|</white> Version: <green><version></green>\n<desc>\nAuthors: <green><author></hover></click>"),
    NO_PERMISSION("<red>You do not have permission to run this command!"),
    HELP("""
            <gray>• <aqua><click:suggest_command:'/nerffarms reload'><hover:show_text:'<yellow>/nerffarms reload'>/nerffarms reload
                <gray>Reloads the NerfFarms config file""");
    String message;

    NFMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
