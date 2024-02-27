package pt.isel.httpHandlers;

import pt.isel.ClassroomController;
import pt.isel.autorouter.ArHttpHandler;

import java.util.Map;
import java.util.Optional;

import static java.lang.Integer.parseInt;

public class RemoveStudentClassroomControllerArHttpHandler implements ArHttpHandler {
    private final ClassroomController controller;

    RemoveStudentClassroomControllerArHttpHandler(ClassroomController controller){ this.controller = controller; }
    @Override
    public Optional<?> handle(Map<String, String> routeArgs, Map<String, String> queryArgs, Map<String, String> bodyArgs) {
        return controller.removeStudent(routeArgs.get("classroom"), parseInt(queryArgs.get("nr")));
    }
}
