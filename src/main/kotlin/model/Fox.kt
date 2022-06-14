package model

import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.random.Random

class Fox(val id: Int, var x: Double, var y: Double,
          val getClosestRabbit: (Double, Double) -> Rabbit,
          val giveBirth: (Double, Double) -> Unit,
          val sendDeath: (Int) -> Unit,
) {

    private var living = true
    private var prey: Rabbit? = null
    private var hunger = 0
    private var fed = 0

    suspend fun live() {
        while (living) {
            delay(Random.nextLong(250, 1000))
            scanSurroundings()
            if (prey != null) hunt()
            else move()
            if (hunger > 50) die()
        }
    }

    private fun scanSurroundings() {
        println("Scan surroundings")
        if (prey == null){
            val closestRabbit = getClosestRabbit(x, y)
            if (abs(x - closestRabbit.x) < 50 && abs(y - closestRabbit.y) < 50) {
                println("Prey targeted")
                prey = closestRabbit
            }
        }
    }

    // TODO: correct border conditions (+ adapt eat condition)
    private fun hunt() {
        println("Hunting")
        val dx = x - prey!!.x
        val newX = if (-25 < dx && dx < 25) x - dx else if (dx < -25) x + 25 else x - 25
        x = if (newX < 0) 0.0 else if (newX > CANVAS_WIDTH) CANVAS_WIDTH - 5.0 else newX

        val dy = y - prey!!.y
        val newY = if (-25 < dy && dy < 25) y - dy else if (dy < -25) y + 25 else y - 25
        y = if (newY < 0) 0.0 else if (newY > CANVAS_HEIGHT) CANVAS_HEIGHT - 10.0 else newY

        if (dx == 0.0 && dy == 0.0) eat()
    }

    private fun eat() {
        println("Eating")
        // TODO: kill prey
        prey = null
        hunger = 0
        ++fed
        if (fed > 10) {
            println("Give birth")
            giveBirth(x, y)
        }
    }

    // TODO: correct border conditions
    private fun move() {
        println("Moving")
        val newX = x + Random.nextDouble(-15.0, 15.0)
        x = if (newX < 0) 20.0 else if (newX > CANVAS_WIDTH) CANVAS_WIDTH - 20.0 else newX
        val newY = y + Random.nextDouble(-15.0, 15.0)
        y = if (newY < 0) 10.0 else if (newY > CANVAS_HEIGHT) CANVAS_HEIGHT - 10.0 else newY
        ++hunger
    }

    private fun die() {
        println("Dead")
        living = false
        sendDeath(id)
    }
}