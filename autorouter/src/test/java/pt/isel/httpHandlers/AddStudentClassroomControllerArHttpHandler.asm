Compiled from "AddStudentClassroomControllerArHttpHandler.java"
public class pt.isel.httpHandlers.AddStudentClassroomControllerArHttpHandler implements pt.isel.autorouter.ArHttpHandler {
  pt.isel.httpHandlers.AddStudentClassroomControllerArHttpHandler(pt.isel.httphandlers.ClassroomController);
    Code:
       0: aload_0
       1: invokespecial #1                  // Method java/lang/Object."<init>":()V
       4: aload_0
       5: aload_1
       6: putfield      #7                  // Field controller:Lpt/isel/httphandlers/ClassroomController;
       9: return
    LineNumberTable:
      line 15: 0
    LocalVariableTable:
      Start  Length  Slot  Name   Signature
          0      10     0  this   Lpt/isel/httpHandlers/AddStudentClassroomControllerArHttpHandler;
          0      10     1 controller   Lpt/isel/httphandlers/ClassroomController;

  public java.util.Optional<?> handle(java.util.Map<java.lang.String, java.lang.String>, java.util.Map<java.lang.String, java.lang.String>, java.util.Map<java.lang.String, java.lang.String>);
    Code:
       0: aload_0
       1: getfield      #7                  // Field controller:Lpt/isel/httphandlers/ClassroomController;
       4: aload_1
       5: ldc           #13                 // String classroom
       7: invokeinterface #15,  2           // InterfaceMethod java/util/Map.get:(Ljava/lang/Object;)Ljava/lang/Object;
      12: checkcast     #21                 // class java/lang/String
      15: aload_1
      16: ldc           #23                 // String nr
      18: invokeinterface #15,  2           // InterfaceMethod java/util/Map.get:(Ljava/lang/Object;)Ljava/lang/Object;
      23: checkcast     #21                 // class java/lang/String
      26: invokestatic  #25                 // Method java/lang/Integer.parseInt:(Ljava/lang/String;)I
      29: new           #31                 // class pt/isel/httphandlers/Student
      32: dup
      33: aload_3
      34: ldc           #23                 // String nr
      36: invokeinterface #15,  2           // InterfaceMethod java/util/Map.get:(Ljava/lang/Object;)Ljava/lang/Object;
      41: checkcast     #21                 // class java/lang/String
      44: invokestatic  #25                 // Method java/lang/Integer.parseInt:(Ljava/lang/String;)I
      47: aload_3
      48: ldc           #33                 // String name
      50: invokeinterface #15,  2           // InterfaceMethod java/util/Map.get:(Ljava/lang/Object;)Ljava/lang/Object;
      55: checkcast     #21                 // class java/lang/String
      58: aload_3
      59: ldc           #35                 // String group
      61: invokeinterface #15,  2           // InterfaceMethod java/util/Map.get:(Ljava/lang/Object;)Ljava/lang/Object;
      66: checkcast     #21                 // class java/lang/String
      69: invokestatic  #25                 // Method java/lang/Integer.parseInt:(Ljava/lang/String;)I
      72: aload_3
      73: ldc           #37                 // String semester
      75: invokeinterface #15,  2           // InterfaceMethod java/util/Map.get:(Ljava/lang/Object;)Ljava/lang/Object;
      80: checkcast     #21                 // class java/lang/String
      83: invokestatic  #25                 // Method java/lang/Integer.parseInt:(Ljava/lang/String;)I
      86: invokespecial #39                 // Method pt/isel/httphandlers/Student."<init>":(ILjava/lang/String;II)V
      89: invokevirtual #42                 // Method pt/isel/httphandlers/ClassroomController.addStudent:(Ljava/lang/String;ILpt/isel/httphandlers/Student;)Ljava/util/Optional;
      92: areturn
    LineNumberTable:
      line 20: 0
      line 21: 7
      line 22: 18
      line 24: 36
      line 25: 50
      line 26: 61
      line 27: 75
      line 20: 89
    LocalVariableTable:
      Start  Length  Slot  Name   Signature
          0      93     0  this   Lpt/isel/httpHandlers/AddStudentClassroomControllerArHttpHandler;
          0      93     1 routeArgs   Ljava/util/Map;
          0      93     2 queryArgs   Ljava/util/Map;
          0      93     3 bodyArgs   Ljava/util/Map;
}
