package com.example.actors

object Protocol {
  trait AlarmMessage

  case class EnableAlarm(pinCode: String) extends AlarmMessage
  case class DisableAlarm(pinCode: String) extends AlarmMessage

  case object ActivityEvent extends AlarmMessage
}
