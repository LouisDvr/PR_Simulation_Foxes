package controller

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import model.CANVAS_HEIGHT
import model.CANVAS_WIDTH
import model.Rabbit
import model.events.MoveOrderEvent
import model.events.RefreshEvent
import tornadofx.Controller
import kotlin.random.Random

class MoveController: Controller() {

    private var isMoving = false
    private var rabbitCounter = 0
    private val rabbitMap = hashMapOf<Int, Rabbit>()
    private val babyRabbits = ArrayDeque<Rabbit>()

    init {
        for (i in 0 until 1) {
            rabbitBirth(
                Random.nextDouble(0.0, CANVAS_WIDTH.toDouble()),
                Random.nextDouble(0.0, CANVAS_HEIGHT.toDouble()),
            )
        }
        subscribe<MoveOrderEvent> {
            if (!isMoving) {
                start()
                isMoving = true
            }
        }
    }

    private fun start() {
        runBlocking {
            launch {
                while (true) {
                    while (!babyRabbits.isEmpty()) {
                        val rabbit = babyRabbits.first()
                        babyRabbits.removeFirst()
                        launch {
                            rabbit.live()
                        }
                    }
                    delay(200)
                }
            }
            launch {
                while (true) {
                    delay(200)
                    fire(RefreshEvent)
                }
            }
        }
    }

    private fun rabbitBirth(x: Double, y: Double) {
        val rabbit = Rabbit(rabbitCounter, x, y, fun (x: Double, y: Double) {rabbitBirth(x, y)})
        rabbitMap[rabbitCounter] = rabbit
        babyRabbits.addLast(rabbit)
        ++rabbitCounter
    }
    fun getRabbitsPositions(): List<Pair<Double, Double>> {
        return rabbitMap.values.map { rabbit -> Pair(rabbit.x, rabbit.y) }
    }
}