package models

class BadAuthentication(msg: String) extends Error(msg)
class BadRequest(msg: String) extends Error(msg)
class NotMatchingParameters(msg: String) extends Error(msg)