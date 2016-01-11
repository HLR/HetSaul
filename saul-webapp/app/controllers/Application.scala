package controllers

import play.api.mvc._

class Application extends Controller {

  def index = Action { implicit request =>
    Ok(views.html.main("Your new application is ready."))
  }
  def updateCode = Action(parse.json) { implicit request =>
    Ok("Ajax"+request.body)
  }

}
