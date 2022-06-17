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
import kotlin.math.pow
import kotlin.math.sqrt
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
        // TODO: demo: use 20 rabbits and 3 foxes
        for (i in 0 until 20) {
            rabbitBirth(
                Random.nextDouble(0.0, CANVAS_WIDTH.toDouble()),
                Random.nextDouble(0.0, CANVAS_HEIGHT.toDouble()),
            )
        }
        for (i in 0 until 3) {
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
        val rabbit = Rabbit(rabbitCounter, x, y,
            fun (x: Double, y: Double) {rabbitBirth(x, y)},
        )
        ++rabbitCounter
        rabbitMap[rabbit.id] = rabbit
        babyRabbits.addLast(rabbit)
    }

    private fun eatRabbit(id: Int) {
        rabbitMap[id]!!.die()
        rabbitMap.remove(id)
    }

    private fun foxBirth(x: Double, y: Double) {
        val fox = Fox(foxCounter, x, y,
            fun (x: Double, y: Double): Rabbit? {return getClosestRabbit(x, y)},
            fun (x: Double, y: Double) {foxBirth(x, y)},
            fun (id: Int) {foxDied(id)},
            fun (id: Int) {eatRabbit(id)}
        )
        ++foxCounter
        foxMap[fox.id] = fox
        babyFoxes.addLast(fox)
    }

    // TODO: return close moving rabbit: naive look at all rabbits and use Pythagoras's distance
    private fun getClosestRabbit(x: Double, y: Double): Rabbit? {
        if (rabbitMap.isEmpty()) return null

        var closestRabbit = rabbitMap.values.elementAt(0)
        var shortestDist = sqrt((x - closestRabbit.x).pow(2) + (y - closestRabbit.y).pow(2))
        for (rabbit in rabbitMap.values) {
            val dist = sqrt((x - rabbit.x).pow(2) + (y - rabbit.y).pow(2))
            if (dist < shortestDist) {
                closestRabbit = rabbit
                shortestDist = dist
            }
        }
        return closestRabbit
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