package de.jokergames.jfql.server.controller;

import de.jokergames.jfql.core.JFQL;
import de.jokergames.jfql.server.util.Method;
import io.javalin.http.Context;

/**
 * @author Janick
 */

public class ErrorController implements Controller {

    @ControllerHandler(path = "$handle.status", status = 404, method = Method.STATUS)
    public void handle404(Context context) {
        context.result(JFQL.getInstance().getServer().getResponseBuilder().buildNotFound().toString()).status(404);
    }

}
