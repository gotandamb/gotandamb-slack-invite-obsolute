@file:JvmName("Main")

package moe.pine.gotandamb.slack

import com.mashape.unirest.http.Unirest
import spark.*
import spark.Spark.get
import spark.Spark.post
import spark.template.mustache.MustacheTemplateEngine

fun main(args: Array<String>) {
    Spark.ipAddress(System.getenv("OPENSHIFT_JBOSSEWS_IP") ?: "0.0.0.0")
    Spark.port(Integer.parseInt(System.getenv("OPENSHIFT_JBOSSEWS_HTTP_PORT") ?: "5353"))
    Spark.staticFileLocation("/public")

    get("/", { request: Request, response: Response ->
        ModelAndView(emptyMap<String, Any>(), "index.mustache")
    }, MustacheTemplateEngine())

    post("/invite", Route { request: Request, response: Response ->
        val token: String? = System.getenv("SLACK_API_TOKEN")
        val team: String = "gotandamb"
        val email: String? = request.queryParams("email")

        response.type("application/json")

        when {
            token.isNullOrEmpty() -> hashMapOf("ok" to false, "error" to "invalid_token")
            team.isNullOrEmpty() -> hashMapOf("ok" to false, "error" to "invalid_team")
            email.isNullOrEmpty() -> hashMapOf("ok" to false, "error" to "invalid_parameters")
            else -> {
                val url = "https://$team.slack.com/api/users.admin.invite"
                val res = Unirest.post(url)
                        .field("email", email)
                        .field("token", token)
                        .field("set_active", "true")
                        .asJson()

                val body = res.body?.`object`
                val ok = body?.getBoolean("ok")
                val error = body?.optString("error")

                hashMapOf("ok" to ok, "error" to error)
            }
        }
    }, JsonTransformer())
}
