package model

import kotlinx.coroutines.delay
import kotlin.random.Random

class Rabbit(
    val id: Int, var x: Double, var y: Double,
    val giveBirth: (Double, Double) -> Unit,
) {

    private var gestationCounter = 0
    private var living = true

    suspend fun live() {
        while (living) {
            delay(Random.nextLong(250, 1000))
            if (Random.nextBoolean()) {
                move()
            } else {
                hide()
            }
        }
    }

    // TODO: correct border conditions
    private fun move() {
        val newX = x + Random.nextDouble(-15.0, 15.0)
        x = if (newX < 0) 0.0 else if (newX > CANVAS_WIDTH) CANVAS_WIDTH - 5.0 else newX
        val newY = y + Random.nextDouble(-15.0, 15.0)
        y = if (newY < 0) 0.0 else if (newY > CANVAS_HEIGHT) CANVAS_HEIGHT - 10.0 else newY
    }

    private fun hide() {
        if (gestationCounter > 10) {
            giveBirth(x, y)
            gestationCounter = 0
        } else {
            ++gestationCounter
        }
    }

    fun die() {
        living = false
    }
}