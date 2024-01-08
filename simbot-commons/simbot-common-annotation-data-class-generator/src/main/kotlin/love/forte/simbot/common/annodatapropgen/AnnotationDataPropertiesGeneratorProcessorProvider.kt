package love.forte.simbot.common.annodatapropgen

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider


/**
 *
 * @author ForteScarlet
 */
class AnnotationDataPropertiesGeneratorProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor =
        AnnotationDataPropertiesGeneratorProcessor(environment)
}
