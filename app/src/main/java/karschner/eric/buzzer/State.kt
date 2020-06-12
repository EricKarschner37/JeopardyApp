package karschner.eric.buzzer

import org.json.JSONObject

data class State(val state: String, val question: Question, val player: String, val players: Array<Player>) {
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

            val question = Question(
                json.getString("clue"),
                json.getString("answer"),
                json.getInt("cost")
            )

            return State(
                json.getString("state"),
                question,
                json.getString("player"),
                players.toTypedArray()
            )
        }
    }
}

data class Question(val clue: String, val answer: String, val cost: Int)

data class Player(val name: String, val balance: Int)