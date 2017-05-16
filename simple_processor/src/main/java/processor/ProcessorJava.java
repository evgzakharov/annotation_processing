package processor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedOptions;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

public class ProcessorJava extends AbstractProcessor {
//    private Types typeUtils;
//    private Elements elementUtils;
//    private Filer filer;
//    private Messager messager;
//
//    @Override
//    public synchronized void init(ProcessingEnvironment processingEnv) {
//        super.init(processingEnv);
//        typeUtils = processingEnv.getTypeUtils();
//        elementUtils = processingEnv.getElementUtils();
//        filer = processingEnv.getFiler();
//        messager = processingEnv.getMessager();
//    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }

//    @Override
//    public Set<String> getSupportedOptions() {
//        SupportedOptions
//    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotataions = new LinkedHashSet<>();
        annotataions.add(TestingClassJava.class.getCanonicalName());
        return annotataions;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(TestingClassJava.class)) {
            if (element.getKind() != ElementKind.CLASS) {
                error(element, "Only classes can be annotated with " + TestingClassJava.class.getCanonicalName());
                continue;
            }

            TestingClassJava annotation = element.getAnnotation(TestingClassJava.class);
            String logLevel = annotation.level();

            try {
                TypeElement targetElement = (TypeElement) element;

                String elementFullName = targetElement.getQualifiedName().toString();
                ClassName classWrap = ClassName.bestGuess(elementFullName);

                MethodSpec buildMethod = MethodSpec.methodBuilder("build")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .returns(classWrap)
                        .addParameter(String.class, "name")
                        .addParameter(String.class, "surname")
                        .addStatement("return new $T(name, surname)", classWrap)
                        .build();

                String builderClassName = element.getSimpleName() + "Builder";
                TypeSpec elementBuilder = TypeSpec.classBuilder(builderClassName)
                        .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                        .addMethod(buildMethod)
                        .build();

                File destFile = new File("build/generated/source/apt/main");

                System.out.println("dest folder="+destFile.getAbsolutePath());

                JavaFile.builder(classWrap.packageName(), elementBuilder)
                        .build()
                        .writeTo(destFile);



            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return true;
    }

    private void error(Element element, String errorMsg) {
        System.out.println(String.format("ERROR: %s is invalid. %s", element.toString(), errorMsg));
    }
}
