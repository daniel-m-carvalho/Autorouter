package pt.isel.autorouter

import org.slf4j.LoggerFactory
import java.io.IOException
import java.nio.file.*
import java.nio.file.StandardWatchEventKinds.*

class WatchNewFilesContent(private val path: Path) {

    private val service: WatchService = path.fileSystem.newWatchService()

    fun watchNewFilesContent(): Sequence<Sequence<String>> {
        return sequence {
            path.fileSystem.newWatchService().use { service ->
                path.register(service, ENTRY_CREATE, ENTRY_MODIFY, OVERFLOW)
                while (true) {
                    val key = service.take()
                    val events = key.pollEvents()
                    // Process each event in the list of events
                    for (event in events) {
                        when (event.kind()) {
                            /*
                            * If the event is an overflow, it means that events might have been lost or discarded
                            * In this case, we just ignore the event
                            */
                            OVERFLOW -> continue
                            // Check if the event corresponds to file creation or modification
                            ENTRY_CREATE, ENTRY_MODIFY -> {
                                Thread.sleep(200)   // Delay to ensure the IDE file creation process is complete
                                // Resolve the full path of the file
                                val filePath = path.resolve(event.context().toString())
                                // Read all lines of the file and add them to the fileContent list
                                yield(yieldLinesFromFile(filePath))
                            }
                        }
                    }
                    if (!key.reset()) {
                        break // Exit the loop if the key is no longer valid
                    }
                }
            }
        }
    }

    private fun yieldLinesFromFile(filePath: Path): Sequence<String> {
        return try {
            Files.newBufferedReader(filePath).lineSequence()
        } catch (e: IOException) {
            // If an error occurs, log the error and return an empty sequence
            // Error can occur during the file creation process from the IDE
            logger.error("Error reading file $filePath", e.message)
            emptySequence()
        }
    }

    fun close() {
        service.close()
    }

    companion object {
        private val logger = LoggerFactory.getLogger(WatchNewFilesContent::class.java)
    }
}
