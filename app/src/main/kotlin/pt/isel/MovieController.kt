package pt.isel

import com.fasterxml.jackson.annotation.JsonProperty
import pt.isel.autorouter.ArVerb.*
import pt.isel.autorouter.annotations.*
import java.util.*

//Inspired in last semester's project on IPW
data class Movie(
    @JsonProperty val id: Int,
    @JsonProperty val title: String,
    @JsonProperty val year: Int,
    @JsonProperty val genre: String
)

class MovieController {

        private val movies = mutableMapOf(
            "group_1" to listOf(
            Movie(1, "The Shawshank Redemption", 1994,"Drama"),
            Movie(2, "The Godfather", 1972, "Crime"),
            Movie(3, "The Dark Knight", 2008, "Action"),
            Movie(4, "12 Angry Men", 1957, "Drama"),
            ),
            "group_2" to listOf(
            Movie(5, "Schindler's List", 1993, "Biography"),
            Movie(6, "The Lord of the Rings: The Return of the King", 2003, "Adventure"),
            Movie(7, "Pulp Fiction", 1994, "Crime"),
            Movie(8, "The Good, the Bad and the Ugly", 1966, "Western"),
            Movie(9, "Fight Club", 1999, "Drama"),
            Movie(10, "Forrest Gump", 1994, "Romance")
            )
        )

        /**
         * Example:
         *   http://localhost:4000/groups/group_1
         */
        @AutoRoute("/groups/{group}")
        fun getMovies(@ArRoute group: String, @ArQuery title: String?): Optional<List<Movie>>{
            return movies[group]
                ?.let {
                    if(title == null) Optional.of(it)
                    else Optional.of(it.filter { st -> st.title.contains(title)})
                }
                ?: Optional.empty()
        }


        /**
         * Example:
         *   curl --header "Content-Type: application/json" \
         *     --request PUT \
         *     --data '{"title":"The Godfather","year":1972,"genres":["Crime","Drama","Thriller"]}' \
         *     http://localhost:4000/movies/2
         */
        @AutoRoute("/groups/{group}/movies/{id}", method = PUT)
        fun addMovie(@ArRoute group: String, @ArRoute id: Int, @ArBody movie: Movie): Optional<Movie> {
            if(id != movie.id) return Optional.empty() // return 409 instead ?
            val stds = movies[group] ?: emptyList()
            movies[group] = stds.filter { it.id != id } + movie
            return Optional.of(movie)
        }

        /**
         * Example:
         *   curl --request DELETE http://localhost:4000/movies/1
         */
        @AutoRoute("/groups/{group}/movies/{id}", method = DELETE)
        fun deleteMovie(@ArRoute group:String, @ArRoute id: Int): Optional<Movie> {
            val stds = movies[group] ?: return Optional.empty()
            val s = stds.firstOrNull { it.id == id } ?: return Optional.empty()
            movies[group] = stds.filter { it.id != id }
            return Optional.of(s)
        }

}