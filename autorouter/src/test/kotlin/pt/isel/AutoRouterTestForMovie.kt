package pt.isel

import pt.isel.autorouter.ArHttpRoute
import pt.isel.autorouter.ArVerb
import pt.isel.autorouter.autorouterDynamic
import pt.isel.autorouter.autorouterReflect
import kotlin.jvm.optionals.toList
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
class AutoRouterTestForMovie {
    @Test fun get_movies_via_reflection() {
        get_movies(
            MovieController().autorouterReflect().toList()
        )
    }

    @Test fun get_movies_via_dynamic() {
        get_movies(
            MovieController().autorouterDynamic().toList()
        )
    }

    private fun get_movies(routes: List<ArHttpRoute>){
        val r = routes.first { it.path == "/groups/{group}" }
        val res = r.handler.handle(
            mapOf("group" to "group_1"),
            emptyMap(),
            emptyMap())
        assertContentEquals(
            listOf(
                Movie(1, "The Shawshank Redemption", 1994, "Drama"),
                Movie(2, "The Godfather", 1972, "Crime"),
                Movie(3, "The Dark Knight", 2008, "Action"),
                Movie(4, "12 Angry Men", 1957, "Drama")
            ),
            res.get() as List<Movie>)
    }


    @Test fun get_movies_by_id_via_reflection() {
        get_movies_by_id(
            MovieController().autorouterReflect().toList()
        )
    }

    @Test fun get_movies_by_id_via_dynamic() {
        get_movies_by_id(
            MovieController().autorouterDynamic().toList()
        )
    }

    private fun get_movies_by_id(routes: List<ArHttpRoute>){
        val r = routes.first { it.path == "/groups/{group}" }
        val res = r.handler.handle(
            mapOf("group" to "group_1"),
            mapOf("title" to "Knight"),
            emptyMap())
        assertEquals(

            listOf( Movie(3, "The Dark Knight", 2008, "Action")),
            res.get())
    }


    @Test fun add_movie_via_reflection() {
        add_movie(
            MovieController().autorouterReflect().toList()
        )
    }

    @Test fun add_movie_via_dynamic() {
        add_movie(
            MovieController().autorouterDynamic().toList()
        )
    }

    private fun add_movie(routes: List<ArHttpRoute>){
        val r = routes.first { it.path == "/groups/{group}/movies/{id}" && it.method == ArVerb.PUT }
        val res = r.handler.handle(
            mapOf("group" to "group_1", "id" to "11"),
            emptyMap(),
            mapOf("id" to "11", "title" to "The Godfather Part II", "year" to "1974",
                "genre" to  "Crime"))
        assertEquals(
            Movie(11, "The Godfather Part II", 1974, "Crime"),
            res.get())
    }


    @Test fun delete_movie_via_reflection() {
        delete_movie(
            MovieController().autorouterReflect().toList()
        )
    }

    @Test fun delete_movie_via_dynamic() {
        delete_movie(
            MovieController().autorouterDynamic().toList()
        )
    }

    private fun delete_movie(routes: List<ArHttpRoute>){
        val r = routes.first { it.path == "/groups/{group}/movies/{id}" && it.method == ArVerb.DELETE }
        val res = r.handler.handle(
            mapOf("group" to "group_1", "id" to "1"),
            emptyMap(),
            emptyMap())
        assertEquals(
            Movie(1, "The Shawshank Redemption", 1994, "Drama"),
            res.get())
    }

}