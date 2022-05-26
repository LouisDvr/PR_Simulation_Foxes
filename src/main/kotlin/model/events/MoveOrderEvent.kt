package model.events

import tornadofx.FXEvent
import tornadofx.EventBus.RunOn.*

object MoveOrderEvent: FXEvent(BackgroundThread)
