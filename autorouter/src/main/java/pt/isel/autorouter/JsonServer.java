package pt.isel.autorouter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.Javalin;
import io.javalin.http.Handler;
import io.javalin.http.NotFoundResponse;
import jakarta.servlet.http.HttpServletResponse;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.sequences.Sequence;
import org.jetbrains.annotations.NotNull;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toMap;

public class JsonServer implements AutoCloseable {

    final static Javalin server = Javalin.create();
    final static ObjectMapper mapper = new ObjectMapper();

    public JsonServer(Stream<ArHttpRoute> routes) {
        routes.forEach(this::addRoute);
    }

    /**
     * Parses body request as Json and return Json back.
     */
    public final void addRoute(ArHttpRoute route) {
        choseRetType.get(route.retType()).
                invoke(route, httpHandlerForRoute(route));
    }

    private static Void sendSequence(ArHttpRoute route, Handler handler) {
        server.get(route.path(), ctx -> {
            HttpServletResponse response = ctx.res();
            var res = route.handler().handle(emptyMap(), emptyMap(), emptyMap());
            try (PrintWriter writer = response.getWriter()) {
                var sequence = (Sequence<Sequence<String>>) res.get();
                Iterator<Sequence<String>> iterator = sequence.iterator();
                ctx.contentType("text/html");
                while (iterator.hasNext()) {
                    Iterator<String> seq = iterator.next().iterator();
                    while (seq.hasNext()) {
                        String elem = seq.next();
                        writer.println("<p>" + elem + "<p>");
                    }
                    writer.println("<hr>"); // Line to divide text blocks
                    writer.flush();
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        });
        return null;
    }

    private static Void sendObject(ArHttpRoute route, Handler handler) {
        method.get(route.method()).invoke(route.path(), handler);
        return null;
    }

    private final Map<ArRetType, Function2<ArHttpRoute,Handler,Void>> choseRetType= new HashMap<>(){
        {
            put(ArRetType.SEQUENCE, JsonServer::sendSequence);
            put(ArRetType.OBJECT, JsonServer::sendObject);
        }
    };

    private static final Map<ArVerb, Function2<String,Handler,Javalin>> method= new HashMap<>(){
        {
            put(ArVerb.GET, server::get);
            put(ArVerb.POST, server::post);
            put(ArVerb.DELETE, server::delete);
            put(ArVerb.PUT, server::put);
        }
    };

    /**
     * Creates a Javalin Handler for an autorouter ArHttpRoute.
     * Parses body request as Json.
     */
    private static Handler httpHandlerForRoute(ArHttpRoute route) {
        return ctx -> {
            var routeArgs = ctx.pathParamMap();
            var queryArgs = ctx.queryParamMap().entrySet().stream().collect(toMap(Map.Entry::getKey, e -> e.getValue().get(0)));
            var bodyArgs = ctx.body().isEmpty() ? null : mapper.readValue(ctx.body(), Map.class);
            var res = route.handler().handle(routeArgs, queryArgs, bodyArgs);
            if (res.isPresent()) {
                ctx.json(res.get());
            } else {
                // Status code 404
                throw new NotFoundResponse();
            }
        };
    }

    public void start(int port) {
        server.start(port);
    }

    @Override
    public void close() {
        server.close();
    }

    @NotNull
    public Javalin javalin() {
        return server;
    }
}


