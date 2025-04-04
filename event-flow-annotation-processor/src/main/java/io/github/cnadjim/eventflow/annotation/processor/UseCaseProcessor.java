package io.github.cnadjim.eventflow.annotation.processor;

import io.github.cnadjim.eventflow.annotation.UseCase;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@SupportedSourceVersion(SourceVersion.RELEASE_21)
@SupportedAnnotationTypes("io.github.cnadjim.eventflow.annotation.UseCase")
public class UseCaseProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(UseCase.class)) {
            if (element.getKind() != ElementKind.INTERFACE) {
                processingEnv.getMessager().printMessage(
                        Diagnostic.Kind.ERROR,
                        "@UseCase can only be applied to interfaces",
                        element
                );
                continue;
            }

            List<ExecutableElement> abstractMethods = getExecutableElements((TypeElement) element);

            if (abstractMethods.size() != 1) {
                processingEnv.getMessager().printMessage(
                        Diagnostic.Kind.ERROR,
                        "@UseCase interfaces must have exactly one abstract method, found " +
                                abstractMethods.size(),
                        element
                );
            }
        }
        return true;
    }

    private static List<ExecutableElement> getExecutableElements(TypeElement element) {
        List<ExecutableElement> abstractMethods = new ArrayList<>();

        for (Element enclosed : element.getEnclosedElements()) {
            if (enclosed.getKind() == ElementKind.METHOD) {
                ExecutableElement method = (ExecutableElement) enclosed;
                Set<Modifier> modifiers = method.getModifiers();
                if (!modifiers.contains(Modifier.DEFAULT) &&
                        !modifiers.contains(Modifier.STATIC)) {
                    abstractMethods.add(method);
                }
            }
        }
        return abstractMethods;
    }
}
