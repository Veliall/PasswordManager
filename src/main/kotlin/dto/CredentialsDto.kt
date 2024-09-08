package dto

import java.net.URI

data class WebCredentialsDto(
    val site: URI,
    val login: String,
    val password: String,
)