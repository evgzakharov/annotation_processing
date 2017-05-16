//package processor
//
//import javax.annotation.processing.AbstractProcessor
//import javax.annotation.processing.ProcessingEnvironment
//import javax.annotation.processing.RoundEnvironment
//import javax.lang.model.element.TypeElement
//
//class ProcessorKotlin: AbstractProcessor() {
//    override fun init(processingEnv: ProcessingEnvironment) {
//        super.init(processingEnv)
//    }
//
//    override fun getSupportedAnnotationTypes(): MutableSet<String> =
//        mutableSetOf(TestingClassKotlin::class.java.canonicalName)
//
//    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
//        return false
//    }
//
//}