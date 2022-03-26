package karschner.eric.buzzer

import org.json.JSONObject

data class State(val state: String, val clue: String, val response: String, val cost: Int, val player: String, val players: Array<Player>) {
    companion object {
        fun fromJson(json: JSONObject): State {
            val playersJson = json.getJSONObject("players")
            val players = mutableListOf<Player>()

            playersJson.names()?.let {
                for (i in 0 until it.length()) {
                    val name = it.getString(i)
                    players.add(Player(name, playersJson.getInt(name)))
                }
            }

            return State(
                json.getString("state"),
                json.getString("clue"),
                json.getString("response"),
                json.getInt("cost"),
                json.getString("player"),
                players.toTypedArray()
            )
        }
    }
}

data class Player(val name: String, val balance: Int)

data class Position(val row: Int, val column: Int)