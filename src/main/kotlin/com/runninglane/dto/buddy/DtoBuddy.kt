package com.runninglane.dto.buddy

object DtoBuddy {
    fun implement(
        `interface`: Class<*>,
        packageName: String = `interface`.packageName,
        name: String = `interface`.simpleName + "\$Dto",
        nameSuffix: String? = null
    ): Class<*> = TODO("Not yet implemented")

    fun <DTO> create(concrete: Class<*>, params: Map<String, Any?>): DTO = TODO("Not yet implemented")

    fun <DTO> populate(dto: DTO, params: Map<String, Any?>) { TODO("Not yet implemented") }
}