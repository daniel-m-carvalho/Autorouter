package pt.isel

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.slf4j.LoggerFactory
import pt.isel.autorouter.WatchNewFilesContent
import java.lang.Thread.sleep
import java.nio.file.Files
import java.nio.file.Path
import kotlin.concurrent.thread
import kotlin.test.*

class WatchFileContentTests {

    companion object {
        private val logger = LoggerFactory.getLogger(WatchFileContentTests::class.java)
        private var tempDir: Path? = null
        private var filePath: Path? = null
        private var watchContent: WatchNewFilesContent? = null
    }

    @BeforeEach
    fun initiate(){
        /*create directory path and file path and watcher*/
        tempDir = Files.createTempDirectory("watch-test")
        filePath = Files.createTempFile(tempDir!!, "test", ".txt")
        watchContent = WatchNewFilesContent(tempDir!!)
    }

    @AfterEach
    fun end(): Unit {
        /*delete directory path and file path and watcher*/
        Files.deleteIfExists(filePath!!)
        Files.deleteIfExists(tempDir!!)
        watchContent?.close()
        /* Delay to ensure that the service is closed*/
        sleep(200)
    }

    @Test
    fun `WatchNewFilesContent checks created files test`() {
        val watchSequence = watchContent!!.watchNewFilesContent()
        // Create a separate thread to modify the file after it has been watched
        thread {
            // Delay to ensure the watch sequence is active
            Thread.sleep(200)
            // Modify the content of the file
            Files.write(filePath!!, "Created Content".toByteArray())
            logger.info(filePath.toString())
        }
        // Get the first sequence from the watchSequence iterator
        val iter = watchSequence.iterator()
        //see if it has next before getting it
        if(iter.hasNext()){
            val line = iter.next()
            //Get the second sequence iterator
            val iter2 = line.iterator()
            //see if it has next before getting it
            if(iter2.hasNext())
                assertEquals("Created Content", iter2.next())
        }
        // Delay to ensure that the service is closed
        sleep(200)
    }

    @Test
    fun `WatchNewFilesContent checks modified files test`() {
        val watchSequence = watchContent!!.watchNewFilesContent()
        // Create a separate thread to modify the file after it has been watched

        thread {
            // Delay to ensure the watch sequence is active
            sleep(200)
            // Modify the content of the file
            Files.write(filePath!!, "Created Content".toByteArray())
            // Delay to ensure that the text is written
            sleep(200)
            logger.info("Leaving first thread")
        }
        //Get the first sequence iterator
        var iter = watchSequence.iterator()
        //see if it has next before getting it
        if(iter.hasNext()){
            val line = iter.next()
            //Get the second sequence iterator
            val iter2 = line.iterator()
            //see if it has next before getting it
            if(iter2.hasNext())
            {
                assertEquals("Created Content", iter2.next())
                logger.info("Created Content")
            }
        }
        thread {
            // Delay to ensure the watch sequence is active
            sleep(200)
            // Modify the content of the file
            Files.write(filePath!!, "Modified Content".toByteArray())
            // Delay to ensure that the text is written
            sleep(200)
            logger.info("Leaving second thread")
        }

        //get the first sequence iterator
        iter = watchSequence.iterator()
        //see if it has next before getting it
        if(iter.hasNext()){
            val line = iter.next()
            //Get the second sequence iterator
            val iter2 = line.iterator()
            //see if it has next before getting it
            if(iter2.hasNext())
                assertEquals("Modified Content", iter2.next())
            logger.info("Modified Content")
        }
    }
}