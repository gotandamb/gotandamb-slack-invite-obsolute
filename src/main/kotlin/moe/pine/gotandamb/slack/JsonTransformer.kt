package moe.pine.gotandamb.slack

import com.google.gson.Gson
import spark.ResponseTransformer

class JsonTransformer : ResponseTransformer {
    private val gson: Gson by lazy { Gson() }

    override fun render(model: Any): String {
        return this.gson.toJson(model)
    }
}
