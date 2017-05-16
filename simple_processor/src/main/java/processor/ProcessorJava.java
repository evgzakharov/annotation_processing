package processor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import kotlin.collections.SetsKt;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class ProcessorJava extends AbstractProcessor {
    private Types typeUtils;
    private Elements elementUtils;
    private Filer filer;
    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        typeUtils = processingEnv.getTypeUtils();
        elementUtils = processingEnv.getElementUtils();
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
    }

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

            for (Element enclosedElement : element.getEnclosedElements()) {
                if (enclosedElement.getKind() == ElementKind.FIELD) {
                    Set<Modifier> modifiers = enclosedElement.getModifiers();
                    StringBuilder sb = new StringBuilder();
                    if (modifiers.contains(Modifier.PRIVATE)) {
                        sb.append("private ");
                    } else if (modifiers.contains(Modifier.PROTECTED)) {
                        sb.append("protected ");
                    } else if (modifiers.contains(Modifier.PUBLIC)) {
                        sb.append("public ");
                    }
                    if (modifiers.contains(Modifier.STATIC))
                        sb.append("static ");
                    if (modifiers.contains(Modifier.FINAL))
                        sb.append("final ");
                    sb.append(enclosedElement.asType()).append(" ").append(enclosedElement.getSimpleName());
                    System.out.println(sb);
                }
            }

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
