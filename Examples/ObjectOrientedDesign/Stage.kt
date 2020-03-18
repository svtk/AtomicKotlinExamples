// ObjectOrientedDesign/Stage.kt
package oodesign

class Stage(val maze: String) {
  val robot = Robot(Room())
  val rooms: Rooms = mutableMapOf()
  private val view = View(this)
  val lines = maze.split("\n")
  val height = lines.size
  val width = lines[0].length
  fun teleportPairs() = rooms.values
    .filter {
      it.player is Teleport
    }.map {
      it.player as Teleport
    }.sortedBy {
      it.target
    }.zipWithNext()
  init { // The 'Builder' pattern:
    // Step 1: Create rooms with players:
    lines.withIndex().forEach { (row, line) ->
      line.withIndex().forEach { (col, ch) ->
        val room = Factory.make(ch, row, col)
        rooms[Pair(row, col)] = room
        if (ch == robot.symbol)
          robot.room = room
      }
    }
    // Step 2: Connect the doors
    rooms.forEach { (pair, r) ->
      r.doors.connect(
        pair.first, pair.second, rooms)
    }
    // Step 3: Connect the Teleport pairs
    for ((a, b) in teleportPairs()) {
      a.targetRoom = b.room
      b.targetRoom = a.room
    }
  }
  fun run(solution: String) {
    view.clear()
    view.display() // Show initial maze
    solution.filter { !it.isWhitespace() }
      .forEach {
        robot.move(urge(it))
        view.display()
        Thread.sleep(200L) // Pause
      }
  }
}

fun main() {
  Stage(stringMaze).teleportPairs()
    .forEach { p: Pair<Teleport, Teleport> ->
      println(p)
    }
}
