package pt.isel.httpHandlers;

import pt.isel.ClassroomController;
import pt.isel.autorouter.ArHttpHandler;

import java.util.Map;
import java.util.Optional;

    public class SearchClassroomControllerArHttpHandler implements ArHttpHandler {
        private final ClassroomController controller;

        public SearchClassroomControllerArHttpHandler(ClassroomController controller){ this.controller = controller; }
        @Override
        public Optional<?> handle(Map<String, String> routeArgs, Map<String, String> queryArgs, Map<String, String> bodyArgs) {
            return controller.search(routeArgs.get("classroom"), queryArgs.get("student"));
        }
    }
