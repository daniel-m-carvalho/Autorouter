package pt.isel.httpHandlers;

import pt.isel.*;
import pt.isel.Student;
import pt.isel.autorouter.ArHttpHandler;

import java.util.Map;
import java.util.Optional;

import static java.lang.Integer.parseInt;

public class AddStudentClassroomControllerArHttpHandler implements ArHttpHandler {
    private final ClassroomController controller;

    AddStudentClassroomControllerArHttpHandler(ClassroomController controller){ this.controller = controller; }


    @Override
    public Optional<?> handle(Map<String, String> routeArgs, Map<String, String> queryArgs, Map<String, String> bodyArgs) {
        return controller.addStudent(
                    routeArgs.get("classroom"),
                    parseInt(routeArgs.get("nr")),
                    new Student(
                            parseInt(bodyArgs.get("nr")),
                            bodyArgs.get("name"),
                            parseInt(bodyArgs.get("group")),
                            parseInt(bodyArgs.get("semester"))
                    )
                );
    }
}
