package karschner.eric.buzzer

import org.json.JSONObject

data class State(val message: String, val buzzersOpen: Boolean, val selectedPlayer: String, val cost: Int, val clue: String, val response: String, val players: Map<String, Player>)

data class Player(val name: String, val balance: Int)

data class Position(val row: Int, val column: Int)