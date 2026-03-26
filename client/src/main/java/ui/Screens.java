package ui;
import static ui.EscapeSequences.*;

public class Screens {

    public static final String SEPERATOR = "-".repeat(15);

    public static final String LOGGED_OUT_HELP_SCREEN = SET_TEXT_COLOR_GREEN
            + "register <USERNAME> <PASSWORD> <EMAIL>"
            + SET_TEXT_COLOR_DARK_GREY
            + " - register if you don't have an account already\n"
            + SET_TEXT_COLOR_GREEN
            + "login <USERNAME> <PASSWORD>"
            + SET_TEXT_COLOR_DARK_GREY
            + " - to play!\n"
            + SET_TEXT_COLOR_GREEN
            + "help"
            + SET_TEXT_COLOR_DARK_GREY
            + " - view commands\n";

    public static final String LOGGED_IN_HELP_SCREEN = SET_TEXT_COLOR_GREEN
            + "create <NAME>"
            + SET_TEXT_COLOR_DARK_GREY
            + " - create a new game\n"
            + SET_TEXT_COLOR_GREEN
            + "list"
            + SET_TEXT_COLOR_DARK_GREY
            + " - list all games\n"
            + SET_TEXT_COLOR_GREEN
            + "join <ID> [WHITE|BLACK]"
            + SET_TEXT_COLOR_DARK_GREY
            + " - join a game as a player\n"
            + SET_TEXT_COLOR_GREEN
            + "observe <ID>"
            + SET_TEXT_COLOR_DARK_GREY
            + " - observe a game\n"
            + SET_TEXT_COLOR_GREEN
            + "logout"
            + SET_TEXT_COLOR_DARK_GREY
            + " - return to the prelogin menu\n"
            + SET_TEXT_COLOR_GREEN
            + "quit"
            + SET_TEXT_COLOR_DARK_GREY
            + " - exit the client\n"
            + SET_TEXT_COLOR_GREEN
            + "help"
            + SET_TEXT_COLOR_DARK_GREY
            + " - view lobby commands\n"
            + RESET_ALL;

}
