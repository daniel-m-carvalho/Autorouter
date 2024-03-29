# Autorouter

Automatic HTTP handlers for a controller object with specific annotations.

## Assignment 1 - Types at runtime and Reflection API

The AutoRouter library allows the automatic creation of HTTP handlers for a
[`pt.isel.autorouter.JsonServer`](autorouter/src/main/java/pt/isel/autorouter/JsonServer.java)
based on a router object with specific annotations, according to the next [example of `ClassroomController`](#classroomController-example).

All methods annotated with `@AutoRoute` and returning an `Optional` are eligible
for HTTP handlers.
To avoid ambiguity with existing types of JDK we choose to prefix annotations with `Ar`, i.e. `ArRoute`,
`ArQuery`, `ArBody`.

For simplicity, [`JsonServer`](autorouter/src/main/java/pt/isel/autorouter/JsonServer.java)
is only responding with status codes of 200, 404, and 500, depending
on whether the handler returns a fulfilled `Optional`, an empty `Optional` or an exception.
(you may consider the use of an alternative `Either`, or other type to enhance responses)

1. Implement the Java function `Stream<ArHttpRoute> autorouterReflect(Object controller)`, which builds
   a stream of [`ArHttpRoute`](autorouter/src/main/java/pt/isel/autorouter/ArHttpRoute.java)
   objects for each eligible method in given `controller` object parameter.

2. Implement another example of a controller object for a different domain, such as playlist,
   movies, football teams, basket, moto gp, or any other of your choice.
   Implement the corresponding tests to validate that all routes generated with `autorouterReflect`
   for your controller class are correctly invoked for each HTTP request.

The next figure shows the resulting stream of
[`ArHttpRoute`](autorouter/src/main/java/pt/isel/autorouter/ArHttpRoute.java) objects
for the example of a [`ClassroomRouter` instance](#classroomrouter-example).
The `autorouterReflect` can be use in Kotlin through a statement such as:

```kotlin
ClassroomRouter().autorouterReflect().jsonServer().start(4000)
```

<img src="handlers-for-classroom-router.png">

#### ClassroomController example

```kotlin
class ClassroomController {
    /**
     * Example: http://localhost:4000/classroom/i42d?student=jo
     */
    @Synchronized
    @AutoRoute("/classroom/{classroom}")
    fun search(@ArRoute classroom: String, @ArQuery student: String?): Optional<List<Student>> {
        ...
    }
    /**
     * Example:
     *   curl --header "Content-Type: application/json" \
     *     --request PUT \
     *     --data '{"nr": "7777", "name":"Ze Gato","group":"11", "semester":"3"}' \
     *     http://localhost:4000/classroom/i42d/students/7777
     */
    @Synchronized
    @AutoRoute("/classroom/{classroom}/students/{nr}", method = PUT)
    fun addStudent(
        @ArRoute classroom: String,
        @ArRoute nr: Int,
        @ArBody s: Student
    ): Optional<Student> {
        ...
    }
    /**
     * Example:
     *   curl --request DELETE http://localhost:4000/classroom/i42d/students/4536
     */
    @Synchronized
    @AutoRoute("/classroom/{classroom}/students/{nr}", method = DELETE)
    fun removeStudent(@ArRoute classroom: String, @ArRoute nr: Int) : Optional<Student> {
        ...
    }
}
```

## Assignment 2 - Dynamic Code Generator and Performance Evaluation with JMH

In this workout we follow a different approach to invoke the functions of a controller object.
Instead of using Reflection we will generate different implementations of `ArHttpHandler`
for each function in controller object, as denoted in the next figure.
Notice, these implementations (e.g. `ArHttpHandlerSearch`, `ArHttpHandlerAddStudent`, `ArHttpHandlerRemoveStudent`)
do not use reflection to call the methods of `ClassroomController`.

Implement the Java function `Stream<ArHttpRoute> autorouterDynamic(Object controller)`, which builds
a stream of [`ArHttpRoute`](autorouter/src/main/java/pt/isel/autorouter/ArHttpRoute.java)
objects for each eligible method in given `controller` object parameter.

<img src="dynamic-handlers-for-classroom-router.png">

### Usage

To run these benchmarks on you local machine just run:

```
./gradlew jmhJar
```

And then:

```
java -jar autorouter-bench/build/libs/autorouter-bench-jmh.jar -i 4 -wi 4 -f 1 -r 2 -w 2
```

- `-i` 4 iterations
- `-wi` 4 warmup iterations
- `-f` 1 fork
- `-r` 2 run each iteration for 2 seconds
- `-w` 2 run each warmup iteration for 2 seconds.

### Results

| ![Benchmark results](docs/img.png) |
|:----------------------------------:|
| *Autorouter JMH BenchMark Results* |

Below are the results of the benchmark for the different domains and approaches,
regarding the performance of each method compared to the baseline approach.

#### Domain Empty:

|     Method     | Approach | Performance |
|:--------------:|:--------:|:-----------:|
|   addStudent   | reflect  |    4.37     |
|   addStudent   | dynamic  |    1.36     |
| removeStudent  | reflect  |    5.41     |
| removeStudent  | dynamic  |    0.97     |
|     search     | reflect  |    23.1     | 
|     search     | dynamic  |    1.00     |

#### Domain Classroom:

|    Method     | Approach | Performance |
|:-------------:|:--------:|:-----------:|
|  addStudent   | reflect  |    2.66     |
|  addStudent   | dynamic  |    1.08     |
| removeStudent | reflect  |    4.63     |
| removeStudent | dynamic  |    1.05     |
|    search     | reflect  |    21.0     |
|    search     | dynamic  |    0.99     |

The **performance** is calculated as the ratio between the number of operations in ms of the baseline approach and the number of operations in ms of the current approach,
and represents the performance loss or gain of the current approach compared to the baseline approach of the current domain.

The results show
the **dynamic code** generation approach is **much faster** than the reflection approach in all methods of a given domain.

## Assignment 3 - Lazy Sequences

### Part 1 - `Path.watchNewFilesContent()`

Build an extension function `Path.watchNewFilesContent():
Sequence<Sequence<String>>` that registers a `WatchService` to given `Path` and
returns an sequence with the content of new or modified files
(i.e. each files's content is a `Sequence<String>`).

To that end, you should first create a new `WatchService` by using the
`newWatchService` method of the `FileSystem` class, as follows:

```kotlin
val service = path.fileSystem.newWatchService()
```

After that, you may register that path as a `Watchable` of the `WatchService` through:

```kotlin
path.register(service, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE);
```

In former example we are registering for events that create, modify or delete an entry
in a directory. More details [here](https://docs.oracle.com/javase/tutorial/essential/io/notification.html#process)

Next is an example of an event processing loop:

```kotlin
path.fileSystem.newWatchService().use { service ->
    // Register the path to the service and watch for events
    path.register(service, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE);

    // Start the infinite polling loop
    while (true) {
        val key = service.take()
        // Dequeueing events
        for (watchEvent in key.pollEvents()) {
            // Get the type of the event
            when(watchEvent.kind()) {
                OVERFLOW -> continue  // loop
                ENTRY_CREATE -> ...
                ENTRY_MODIFY -> ...
                ENTRY_DELETE -> ...
            }
        }
        if (!key.reset()) {
            break // loop
        }
    }
}
```

### Part 2 - Testing

You should develop a unit test that checks if the resulting sequence of
the `watchNewFilesContent` produces a new `Sequence<String>` with the content
of new files.

Your tests, should also confirm that making a concurrent change to the
content of the new file after the File has been watched and before getting
the first item from iterator is visible on the iteration.

### Part 3 - JsonServer

Make a proposal and modify the annotations API of the AutoRouter library to let
programmers express routes that return an sequence.

You should also modify the `JsonServer` to support this new kind of routes that
should continuously write a new HTML paragraph for each `String` in a sequence.
To that end, you may use the underlying Javalin `OutputStream` through a
`PrintWriter`, such as:

```kotlin
Javalin.create().also {
    it.get("/") { ctx ->
        ctx.res().writer.use { writer ->
            writer.println("....")
            writer.flush()
        }
    }
    it.start(300)
}
```

Consider the response is never finished by the server, but only when the client
terminates the connection.

https://github.com/isel-leic-ave/autorouter/assets/578217/3c25c866-58c8-4dd1-80e4-3ac5fe332b22

## Authors

- Daniel Carvalho
- Gonçalo Frutuoso
- João Ramos

---

Instituto Superior de Engenharia de Lisboa<br>
BSc in Computer Science and Engineering<br>
Languages and Managed Runtimes<br>
Summer Semester of 2022/2023
