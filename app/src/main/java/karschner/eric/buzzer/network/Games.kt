package karschner.eric.buzzer.network

import android.util.Log
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import retrofit2.Call
import retrofit2.http.GET
import java.lang.reflect.Type

interface Games {
    @GET("api/games")
    fun getGames(): Call<List<Int>>
}