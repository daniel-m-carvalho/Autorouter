Compiled from "RemoveStudentClassroomControllerArHttpHandler.java"
public class pt.isel.httpHandlers.RemoveStudentClassroomControllerArHttpHandler implements pt.isel.autorouter.ArHttpHandler {
  pt.isel.httpHandlers.RemoveStudentClassroomControllerArHttpHandler(pt.isel.httphandlers.ClassroomController);
    Code:
       0: aload_0
       1: invokespecial #1                  // Method java/lang/Object."<init>":()V
       4: aload_0
       5: aload_1
       6: putfield      #7                  // Field controller:Lpt/isel/httphandlers/ClassroomController;
       9: return
    LineNumberTable:
      line 14: 0
    LocalVariableTable:
      Start  Length  Slot  Name   Signature
          0      10     0  this   Lpt/isel/httpHandlers/RemoveStudentClassroomControllerArHttpHandler;
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
      16: ldc           #23                 // String nr
      18: invokeinterface #15,  2           // InterfaceMethod java/util/Map.get:(Ljava/lang/Object;)Ljava/lang/Object;
      23: checkcast     #21                 // class java/lang/String
      26: invokestatic  #25                 // Method java/lang/Integer.parseInt:(Ljava/lang/String;)I
      29: invokevirtual #31                 // Method pt/isel/httphandlers/ClassroomController.removeStudent:(Ljava/lang/String;I)Ljava/util/Optional;
      32: areturn
    LineNumberTable:
      line 17: 0
    LocalVariableTable:
      Start  Length  Slot  Name   Signature
          0      33     0  this   Lpt/isel/httpHandlers/RemoveStudentClassroomControllerArHttpHandler;
          0      33     1 routeArgs   Ljava/util/Map;
          0      33     2 queryArgs   Ljava/util/Map;
          0      33     3 bodyArgs   Ljava/util/Map;
}
