package model

import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.random.Random

class Fox(
    val id: Int, var x: Double, var y: Double,
    val getClosestRabbit: (Double, Double) -> Rabbit?,
    val giveBirth: (Double, Double) -> Unit,
    val sendDeath: (Int) -> Unit,
    val eatRabbit: (Int) -> Unit,
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
        if (prey == null){
            val closestRabbit = getClosestRabbit(x, y)
            if (closestRabbit != null && abs(x - closestRabbit.x) < 50 && abs(y - closestRabbit.y) < 50) {
                prey = closestRabbit
            }
        }
    }

    private fun hunt() {
        val dx = x - prey!!.x
        val newX = if (-25 < dx && dx < 25) x - dx else if (dx < -25) x + 25 else x - 25
        x = if (newX < 0) 4.0 else if (newX > CANVAS_WIDTH) CANVAS_WIDTH - 4.0 else newX

        val dy = y - prey!!.y
        val newY = if (-25 < dy && dy < 25) y - dy else if (dy < -25) y + 25 else y - 25
        y = if (newY < 0) 4.0 else if (newY > CANVAS_HEIGHT) CANVAS_HEIGHT - 4.0 else newY

        if (dx < 1.5 && dy < 1.5) eat() // if both are against the border of the canvas, there will be 1.5 non-nullable dist
    }

    private fun eat() {
        eatRabbit(prey!!.id)
        prey = null
        hunger = 0
        ++fed
        if (fed > 10) giveBirth(x, y)
    }

    private fun move() {
        val newX = x + Random.nextDouble(-15.0, 15.0)
        x = if (newX < 0) 4.0 else if (newX > CANVAS_WIDTH) CANVAS_WIDTH - 4.0 else newX
        val newY = y + Random.nextDouble(-15.0, 15.0)
        y = if (newY < 0) 4.0 else if (newY > CANVAS_HEIGHT) CANVAS_HEIGHT - 4.0 else newY
        ++hunger
    }

    private fun die() {
        living = false
        sendDeath(id)
    }
}