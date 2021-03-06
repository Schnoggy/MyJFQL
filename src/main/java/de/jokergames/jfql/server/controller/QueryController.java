package de.jokergames.jfql.server.controller;

import de.jokergames.jfql.command.executor.RemoteExecutor;
import de.jokergames.jfql.core.JFQL;
import de.jokergames.jfql.event.ClientLoginEvent;
import de.jokergames.jfql.server.util.Method;
import de.jokergames.jfql.server.util.RequestReader;
import de.jokergames.jfql.user.User;
import de.jokergames.jfql.user.UserService;
import io.javalin.http.Context;
import org.json.JSONObject;

/**
 * @author Janick
 */

public class QueryController implements Controller {

    @ControllerHandler(path = "/query", method = Method.POST)
    public void handleQuery(Context context) throws Exception {
        final UserService userService = JFQL.getInstance().getUserService();

        final JSONObject jsonObject = new RequestReader(context.req).jsonRequest();
        final RemoteExecutor executor = new RemoteExecutor(context.req.getRemoteAddr(), context);

        try {
            User user;

            {
                JSONObject auth = jsonObject.getJSONObject("auth");

                if (userService.getUser(auth.getString("user")) == null) {
                    JFQL.getInstance().getEventService().callEvent(ClientLoginEvent.TYPE, new ClientLoginEvent(executor, false));
                    executor.sendForbidden();
                    return;
                }

                user = userService.getUser(auth.getString("user"));

                if (user.is(User.Property.CONSOLE)) {
                    JFQL.getInstance().getEventService().callEvent(ClientLoginEvent.TYPE, new ClientLoginEvent(executor, false));
                    executor.sendForbidden();
                    return;
                }

                if (!user.getPassword().equals(auth.getString("password"))) {
                    JFQL.getInstance().getEventService().callEvent(ClientLoginEvent.TYPE, new ClientLoginEvent(executor, false));
                    executor.sendForbidden();
                    return;
                }

            }

            if (jsonObject.getString("query").equals("#connect")) {
                executor.status(200);
                return;
            }

            JFQL.getInstance().getEventService().callEvent(ClientLoginEvent.TYPE, new ClientLoginEvent(executor, true));
            JFQL.getInstance().getConsole().logInfo("[" + executor.getName() + "] queried [\"" + jsonObject.getString("query") + "\"].");

            boolean exec = JFQL.getInstance().getCommandService().execute(user, executor, JFQL.getInstance().getFormatter().formatCommand(jsonObject.getString("query")));

            if (!exec) {
                executor.sendForbidden();
            }

        } catch (Exception ex) {
            executor.sendError(ex);
        }
    }

}
