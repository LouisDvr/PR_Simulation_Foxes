package controller

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import model.CANVAS_HEIGHT
import model.CANVAS_WIDTH
import model.Fox
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
    private var foxCounter = 0
    private val foxMap = hashMapOf<Int, Fox>()
    private val babyFoxes = ArrayDeque<Fox>()

    init {
        for (i in 0 until 1) {
            rabbitBirth(
                Random.nextDouble(0.0, CANVAS_WIDTH.toDouble()),
                Random.nextDouble(0.0, CANVAS_HEIGHT.toDouble()),
            )
        }
        for (i in 0 until 1) {
            foxBirth(
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
                    delay(50)
                }
            }
            launch {
                while (true) {
                    while (!babyFoxes.isEmpty()) {
                        val fox = babyFoxes.first()
                        babyFoxes.removeFirst()
                        launch {
                            fox.live()
                        }
                    }
                    delay(50)
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
        ++rabbitCounter
        rabbitMap[rabbit.id] = rabbit
        babyRabbits.addLast(rabbit)
    }

    private fun foxBirth(x: Double, y: Double) {
        val fox = Fox(foxCounter, x, y,
            fun (x: Double, y: Double): Rabbit {return getClosestRabbit(x, y)},
            fun (x: Double, y: Double) {foxBirth(x, y)},
            fun (id: Int) {foxDied(id)},
        )
        ++foxCounter
        foxMap[fox.id] = fox
        babyFoxes.addLast(fox)
    }

    // TODO: return closest rabbit
    private fun getClosestRabbit(x: Double, y: Double): Rabbit {
        val id = Random.nextInt(0, rabbitMap.keys.size)
        return rabbitMap[id]!!
    }

    private fun foxDied(id: Int) {
        foxMap.remove(id)
    }

    fun getRabbitsPositions(): List<Pair<Double, Double>> {
        return rabbitMap.values.map { rabbit -> Pair(rabbit.x, rabbit.y) }
    }

    fun getFoxesPositions(): List<Pair<Double, Double>> {
        return foxMap.values.map { fox -> Pair(fox.x, fox.y) }
    }
}