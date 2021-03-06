package view

import controller.MoveController
import javafx.scene.paint.Color
import model.CANVAS_HEIGHT
import model.CANVAS_WIDTH
import model.events.MoveOrderEvent
import model.events.RefreshEvent
import tornadofx.*

class FieldView: View("FieldView") {

    private var isMoving = false
    private val moveController = find(MoveController::class)

    override val root = borderpane {
        center = group {}
        bottom = hbox {
            button("Start") {
                action {
                    if (!isMoving) {
                        fire(MoveOrderEvent)
                        isMoving = true
                    }
                }
            }
        }
    }

    init {
        refresh()
        subscribe<RefreshEvent> { refresh() }
    }

    private fun refresh() {
        val rabbitList = moveController.getRabbitsPositions()
        val foxList = moveController.getFoxesPositions()
        root.center.replaceWith(group {
            // add invisible dots in the corners to prevent the group from contracting
            // it is important to keep track of the cursor when it is in some area without dots
            circle(0, 0, 0)
            circle(CANVAS_WIDTH, 0, 0)
            circle(0, CANVAS_HEIGHT, 0)
            circle(CANVAS_WIDTH, CANVAS_HEIGHT, 0)

            for ((x, y) in rabbitList) {
                circle(x, y, 5) { fill = Color.BROWN }
            }
            for ((x, y) in foxList) {
                circle(x, y, 8) { fill = Color.ORANGE }
            }
        })
    }
}