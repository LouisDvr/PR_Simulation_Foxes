package controller

import model.events.MoveOrderEvent
import tornadofx.Controller

class MoveController: Controller() {

    private var isMoving = false

    init {
        subscribe<MoveOrderEvent> {
            if (!isMoving) {
                start()
                isMoving = true
            }
        }
    }

    private fun start() {

    }
}