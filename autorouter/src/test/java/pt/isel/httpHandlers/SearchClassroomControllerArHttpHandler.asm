Compiled from "SearchClassroomControllerArHttpHandler.java"
public class pt.isel.httpHandlers.SearchClassroomControllerArHttpHandler implements pt.isel.autorouter.ArHttpHandler {
  public pt.isel.httpHandlers.SearchClassroomControllerArHttpHandler(pt.isel.httphandlers.ClassroomController);
    Code:
       0: aload_0
       1: invokespecial #1                  // Method java/lang/Object."<init>":()V
       4: aload_0
       5: aload_1
       6: putfield      #7                  // Field controller:Lpt/isel/httphandlers/ClassroomController;
       9: return
    LineNumberTable:
      line 13: 0
      line 14: 4
      line 15: 9
    LocalVariableTable:
      Start  Length  Slot  Name   Signature
          0      10     0  this   Lpt/isel/httpHandlers/SearchClassroomControllerArHttpHandler;
          0      10     1 controller   Lpt/isel/httphandlers/ClassroomController;

  public java.util.Optional<?> handle(java.util.Map<java.lang.String, java.lang.String>, java.util.Map<java.lang.String, java.lang.String>, java.util.Map<java.lang.String, java.lang.String>);
    Code:
       0: aload_0
       1: getfield      #7                  // Field controller:Lpt/isel/httphandlers/ClassroomController;
       4: aload_1
       5: ldc           #13                 // String classroom
       7: invokeinterface #15,  2           // InterfaceMethod java/util/Map.get:(Ljava/lang/Object;)Ljava/lang/Object;
      12: checkcast     #21                 // class java/lang/String
      15: aload_2
      16: ldc           #23                 // String student
      18: invokeinterface #15,  2           // InterfaceMethod java/util/Map.get:(Ljava/lang/Object;)Ljava/lang/Object;
      23: checkcast     #21                 // class java/lang/String
      26: invokevirtual #25                 // Method pt/isel/httphandlers/ClassroomController.search:(Ljava/lang/String;Ljava/lang/String;)Ljava/util/Optional;
      29: areturn
    LineNumberTable:
      line 18: 0
    LocalVariableTable:
      Start  Length  Slot  Name   Signature
          0      30     0  this   Lpt/isel/httpHandlers/SearchClassroomControllerArHttpHandler;
          0      30     1 routeArgs   Ljava/util/Map;
          0      30     2 queryArgs   Ljava/util/Map;
          0      30     3 bodyArgs   Ljava/util/Map;
}
