package de.jokergames.jfql.command;

import de.jokergames.jfql.command.executor.Executor;
import de.jokergames.jfql.command.executor.RemoteExecutor;
import de.jokergames.jfql.core.JFQL;
import de.jokergames.jfql.user.RemoteUser;
import de.jokergames.jfql.user.User;
import de.jokergames.jfql.user.UserService;

import java.util.List;
import java.util.Map;

/**
 * @author Janick
 * @language This commands is not a JFQL query. It is only for the DBMS management.
 */


public class UserCommand extends Command {

    public UserCommand() {
        super("USER", List.of("COMMAND", "CREATE", "PASSWORD", "DELETE", "ADD", "REMOVE", "DISPLAY", "PERMISSION"), List.of("USR"));
    }

    @Override
    public boolean handle(Executor executor, Map<String, List<String>> arguments, User user) {
        if (executor instanceof RemoteExecutor) {
            return false;
        }

        final UserService userService = JFQL.getInstance().getUserService();

        if (arguments.containsKey("CREATE") && arguments.containsKey("PASSWORD")) {
            String name = JFQL.getInstance().getFormatter().formatString(arguments.get("CREATE"));
            String password = JFQL.getInstance().getFormatter().formatString(arguments.get("PASSWORD"));

            if (userService.getUser(name) != null) {
                JFQL.getInstance().getConsole().logError("User '" + name + "' already exists!");
                return true;
            }

            userService.saveUser(new RemoteUser(name, password));
            JFQL.getInstance().getConsole().logInfo("User '" + name + "' was created.");
            return true;
        }

        if (arguments.containsKey("DELETE")) {
            String name = JFQL.getInstance().getFormatter().formatString(arguments.get("DELETE"));

            if (userService.getUser(name) == null) {
                JFQL.getInstance().getConsole().logError("User '" + name + "' doesn't exists!");
                return true;
            }

            final User usr = userService.getUser(name);

            if (usr.is(User.Property.NO_DELETE)) {
                JFQL.getInstance().getConsole().logError("Can't delete user '" + usr.getName() + "'!");
                return true;
            }

            JFQL.getInstance().getConsole().logInfo("User '" + name + "' was deleted.");
            usr.getFile().delete();
            return true;
        }

        if (arguments.containsKey("DISPLAY")) {
            String name = JFQL.getInstance().getFormatter().formatString(arguments.get("DISPLAY"));

            if (userService.getUser(name) == null) {
                JFQL.getInstance().getConsole().logError("User '" + name + "' doesn't exists!");
                return true;
            }

            final User usr = userService.getUser(name);
            JFQL.getInstance().getConsole().log(usr.toString());
            return true;
        }

        if (arguments.containsKey("ADD") && arguments.containsKey("PERMISSION")) {
            String name = JFQL.getInstance().getFormatter().formatString(arguments.get("ADD"));
            String permission = JFQL.getInstance().getFormatter().formatString(arguments.get("PERMISSION")).toLowerCase();

            if (userService.getUser(name) == null) {
                JFQL.getInstance().getConsole().logError("User '" + name + "' doesn't exists!");
                return true;
            }

            final User usr = userService.getUser(name);

            if (usr.is(User.Property.NO_EDIT)) {
                JFQL.getInstance().getConsole().logError("Can't edit user '" + usr.getName() + "'!");
                return true;
            }

            JFQL.getInstance().getConsole().logInfo("Add permission '" + permission + "' to user '" + usr.getName() + "'.");
            usr.getPermissions().add(permission);
            userService.saveUser(usr);
            return true;
        }

        if (arguments.containsKey("REMOVE") && arguments.containsKey("PERMISSION")) {
            String name = JFQL.getInstance().getFormatter().formatString(arguments.get("REMOVE"));
            String permission = JFQL.getInstance().getFormatter().formatString(arguments.get("PERMISSION")).toLowerCase();

            if (userService.getUser(name) == null) {
                JFQL.getInstance().getConsole().logError("User '" + name + "' doesn't exists!");
                return true;
            }

            final User usr = userService.getUser(name);

            if (usr.is(User.Property.NO_EDIT)) {
                JFQL.getInstance().getConsole().logError("Can't edit user '" + usr.getName() + "'!");
                return true;
            }

            JFQL.getInstance().getConsole().logInfo("Remove permission '" + permission + "' to user '" + usr.getName() + "'.");
            usr.getPermissions().remove(permission);
            userService.saveUser(usr);
            return true;
        }

        JFQL.getInstance().getConsole().logError("Unknown syntax!");
        return true;
    }
}
