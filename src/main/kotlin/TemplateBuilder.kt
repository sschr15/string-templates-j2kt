package com.sschr15.templates

/**
 * A builder class for constructing templates using a given [StringTemplate.Processor].
 *
 * @param T The type of the result produced by the template processor.
 * @param E The type of the throwable that can be thrown by the template processor.
 */
class TemplateBuilder<T, E : Throwable> internal constructor(
    private val template: StringTemplate.Processor<T, E>,
    private val tempReplacement: String = "\u0000"
) {
    private val args = mutableListOf<Any?>()

    /**
     * Evaluate this String argument instead of directly concatenating it into the string.
     */
    operator fun Any?.not(): String {
        args.add(this)
        return tempReplacement
    }

    /**
     * Evaluate this String argument instead of directly concatenating it into the string.
     *
     * This function is provided in cases where the `not` operator cannot be used, such as when
     * working with booleans or other types whose `not` operator is already defined.
     */
    fun Any?.eval() = not()

    internal fun build(text: String): T {
        return template.process(StringTemplate.of(text.split(tempReplacement), args))
    }
}

/**
 * Use the given [StringTemplate.Processor] to process the text returned by the given block.
 * 
 * @sample com.sschr15.templates.example.example
 */
operator fun <T, E : Throwable> StringTemplate.Processor<T, E>.invoke(
    tempReplacement: String = "\u0000",
    block: TemplateBuilder<T, E>.() -> String
): T {
    val templateBuilder = TemplateBuilder(this, tempReplacement)
    val text = templateBuilder.block()
    return templateBuilder.build(text)
}
