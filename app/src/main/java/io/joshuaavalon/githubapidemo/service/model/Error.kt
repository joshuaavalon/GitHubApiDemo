package io.joshuaavalon.githubapidemo.service.model

import com.google.gson.annotations.SerializedName


class Error(@SerializedName("message") val message: String)