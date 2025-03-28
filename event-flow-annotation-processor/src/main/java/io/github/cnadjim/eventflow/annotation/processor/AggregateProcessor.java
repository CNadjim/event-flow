package io.github.cnadjim.eventflow.annotation.processor;


import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SupportedSourceVersion(SourceVersion.RELEASE_17)
@SupportedAnnotationTypes("io.github.cnadjim.eventflow.annotation.Aggregate")
public class AggregateProcessor extends AbstractProcessor {

    private Elements elementUtils;
    private Filer filer;
    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        elementUtils = processingEnv.getElementUtils();
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement annotation : annotations) {
            Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);

            for (Element element : annotatedElements) {
                if (element.getKind() != ElementKind.RECORD && element.getKind() != ElementKind.CLASS) {
                    messager.printMessage(Diagnostic.Kind.ERROR, "@Aggregate can only be applied to records or classes", element);
                    continue;
                }

                try {
                    TypeElement typeElement = (TypeElement) element;

                    // Generate no-arg constructor if needed (for classes only)
                    if (element.getKind() == ElementKind.CLASS) {
                        generateNoArgConstructorIfNeeded(typeElement);
                    }

                    if (element.getKind() == ElementKind.RECORD) {
                        generateBuilderForRecord(typeElement);
                    } else {
                        generateBuilderForClass(typeElement);
                    }
                } catch (IOException e) {
                    messager.printMessage(Diagnostic.Kind.ERROR,
                            "Failed to generate builder class: " + e.getMessage(), element);
                }
            }
        }

        return true;
    }

    private void generateNoArgConstructorIfNeeded(TypeElement classElement) throws IOException {
        // Check if the class already has a no-arg constructor
        List<ExecutableElement> constructors = ElementFilter.constructorsIn(classElement.getEnclosedElements());
        boolean hasNoArgConstructor = false;

        for (ExecutableElement constructor : constructors) {
            if (constructor.getParameters().isEmpty() &&
                    !constructor.getModifiers().contains(Modifier.PRIVATE)) {
                hasNoArgConstructor = true;
                break;
            }
        }

        if (!hasNoArgConstructor) {
            // Add a no-arg constructor to the original class
            String packageName = elementUtils.getPackageOf(classElement).getQualifiedName().toString();
            String className = classElement.getSimpleName().toString();
            String augmentedClassName = className + "WithNoArgConstructor";

            JavaFileObject javaFile = filer.createSourceFile(packageName + "." + augmentedClassName);

            try (PrintWriter out = new PrintWriter(javaFile.openWriter())) {
                // Package declaration
                out.println("package " + packageName + ";");
                out.println();

                // Class declaration
                String classModifiers = classElement.getModifiers().stream()
                        .filter(mod -> mod != Modifier.ABSTRACT && mod != Modifier.FINAL)
                        .map(Modifier::toString)
                        .collect(Collectors.joining(" "));

                out.println("public " + classModifiers + " class " + augmentedClassName + " extends " + className + " {");
                out.println();

                // No-arg constructor
                out.println("    /**");
                out.println("     * No-arg constructor automatically generated by AggregateProcessor.");
                out.println("     */");
                out.println("    public " + augmentedClassName + "() {");
                out.println("        super();");
                out.println("    }");
                out.println("}");
            }

            messager.printMessage(Diagnostic.Kind.NOTE,
                    "Generated a no-arg constructor for " + className + " in " + augmentedClassName,
                    classElement);
        }
    }

    private void generateBuilderForRecord(TypeElement recordElement) throws IOException {
        String packageName = elementUtils.getPackageOf(recordElement).getQualifiedName().toString();
        String recordName = recordElement.getSimpleName().toString();
        String builderName = recordName + "Builder";

        // Collect the record components
        List<? extends RecordComponentElement> recordComponents = recordElement.getRecordComponents();

        JavaFileObject builderFile = filer.createSourceFile(packageName + "." + builderName);

        try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {
            // Generate package declaration
            out.println("package " + packageName + ";");
            out.println();

            // Add imports for collections
            out.println("import java.util.*;");
            out.println("import java.util.function.Consumer;");
            out.println("import java.util.function.Supplier;");
            out.println("import java.util.stream.Collectors;");
            out.println("import java.util.stream.StreamSupport;");
            out.println();

            // Generate builder class
            out.println("public class " + builderName + " {");

            // Generate fields
            for (RecordComponentElement component : recordComponents) {
                String fieldName = component.getSimpleName().toString();
                TypeMirror fieldType = component.asType();

                out.println("    private " + fieldType + " " + fieldName + ";");
            }

            out.println();

            // Generate static factory methods
            out.println("    public static " + builderName + " builder() {");
            out.println("        return new " + builderName + "();");
            out.println("    }");
            out.println();

            out.println("    public static " + builderName + " from(" + recordName + " record) {");
            out.println("        return new " + builderName + "(record);");
            out.println("    }");
            out.println();

            // Generate constructor from record
            out.println("    private " + builderName + "(" + recordName + " record) {");
            for (RecordComponentElement component : recordComponents) {
                String fieldName = component.getSimpleName().toString();
                out.println("        this." + fieldName + " = record." + fieldName + "();");
            }
            out.println("    }");
            out.println();

            // Generate default constructor
            out.println("    private " + builderName + "() {");
            out.println("    }");
            out.println();

            // Generate setters for each field
            for (RecordComponentElement component : recordComponents) {
                generateSettersForField(out, component.getSimpleName().toString(), component.asType(), builderName);
            }

            // Generate build method
            out.println("    public " + recordName + " build() {");
            out.print("        return new " + recordName + "(");

            boolean first = true;
            for (RecordComponentElement component : recordComponents) {
                if (!first) {
                    out.print(", ");
                }
                out.print(component.getSimpleName());
                first = false;
            }

            out.println(");");
            out.println("    }");

            // Generate static class for toBuilder
            out.println();
            out.println("    public static class " + recordName + "Buildable {");
            out.println("        public static " + builderName + " toBuilder(" + recordName + " record) {");
            out.println("            return " + builderName + ".from(record);");
            out.println("        }");
            out.println("    }");

            out.println("}");
        }
    }

    private void generateBuilderForClass(TypeElement classElement) throws IOException {
        String packageName = elementUtils.getPackageOf(classElement).getQualifiedName().toString();
        String className = classElement.getSimpleName().toString();
        String builderName = className + "Builder";

        // Check for no-arg constructor
        List<ExecutableElement> constructors = ElementFilter.constructorsIn(classElement.getEnclosedElements());
        boolean hasNoArgConstructor = false;

        for (ExecutableElement constructor : constructors) {
            if (constructor.getParameters().isEmpty() &&
                    !constructor.getModifiers().contains(Modifier.PRIVATE)) {
                hasNoArgConstructor = true;
                break;
            }
        }

        // Adjust class name if we needed to generate a wrapper with no-arg constructor
        String targetClassName = className;
        if (!hasNoArgConstructor) {
            targetClassName = className + "WithNoArgConstructor";
        }

        // Collect all non-static fields
        List<VariableElement> fields = ElementFilter.fieldsIn(classElement.getEnclosedElements()).stream()
                .filter(field -> !field.getModifiers().contains(Modifier.STATIC))
                .toList();

        JavaFileObject builderFile = filer.createSourceFile(packageName + "." + builderName);

        try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {
            // Generate package declaration
            out.println("package " + packageName + ";");
            out.println();

            // Add imports for collections
            out.println("import java.util.*;");
            out.println("import java.util.function.Consumer;");
            out.println("import java.util.function.Supplier;");
            out.println("import java.util.stream.Collectors;");
            out.println();

            // Generate builder class
            out.println("public class " + builderName + " {");

            // Generate fields
            for (VariableElement field : fields) {
                String fieldName = field.getSimpleName().toString();
                TypeMirror fieldType = field.asType();

                out.println("    private " + fieldType + " " + fieldName + ";");
            }

            out.println();

            // Generate static factory methods
            out.println("    public static " + builderName + " builder() {");
            out.println("        return new " + builderName + "();");
            out.println("    }");
            out.println();

            out.println("    public static " + builderName + " from(" + className + " instance) {");
            out.println("        return new " + builderName + "(instance);");
            out.println("    }");
            out.println();

            // Generate constructor from instance
            out.println("    private " + builderName + "(" + className + " instance) {");
            for (VariableElement field : fields) {
                String fieldName = field.getSimpleName().toString();
                String getterMethodName = deriveGetterName(field);

                out.println("        try {");
                out.println("            java.lang.reflect.Method getter = " + className + ".class.getMethod(\"" +
                        getterMethodName + "\");");
                out.println("            this." + fieldName + " = (" + field.asType() + ") getter.invoke(instance);");
                out.println("        } catch (Exception e) {");
                out.println("            try {");
                out.println("                java.lang.reflect.Field field = " + className +
                        ".class.getDeclaredField(\"" + fieldName + "\");");
                out.println("                field.setAccessible(true);");
                out.println("                this." + fieldName + " = (" + field.asType() + ") field.get(instance);");
                out.println("            } catch (Exception ex) {");
                out.println("                System.err.println(\"Cannot access field: " + fieldName + " - \" + ex);");
                out.println("            }");
                out.println("        }");
            }
            out.println("    }");
            out.println();

            // Generate default constructor
            out.println("    private " + builderName + "() {");
            out.println("    }");
            out.println();

            // Generate setters for each field
            for (VariableElement field : fields) {
                generateSettersForField(out, field.getSimpleName().toString(), field.asType(), builderName);
            }

            // Generate build method
            out.println("    public " + className + " build() {");
            out.println("        try {");
            out.println("            " + targetClassName + " instance = new " + targetClassName + "();");

            for (VariableElement field : fields) {
                String fieldName = field.getSimpleName().toString();
                String setterMethodName = "set" + capitalize(fieldName);

                out.println("            try {");
                out.println("                java.lang.reflect.Method setter = " + targetClassName +
                        ".class.getMethod(\"" + setterMethodName + "\", " +
                        field.asType() + ".class);");
                out.println("                setter.invoke(instance, this." + fieldName + ");");
                out.println("            } catch (Exception e) {");
                out.println("                try {");
                out.println("                    java.lang.reflect.Field field = " + targetClassName +
                        ".class.getDeclaredField(\"" + fieldName + "\");");
                out.println("                    field.setAccessible(true);");
                out.println("                    field.set(instance, this." + fieldName + ");");
                out.println("                } catch (Exception ex) {");
                out.println("                    System.err.println(\"Cannot set field: " + fieldName + " - \" + ex);");
                out.println("                }");
                out.println("            }");
            }

            out.println("            return instance;");
            out.println("        } catch (Exception e) {");
            out.println("            throw new RuntimeException(\"Error building " + className + ": \" + e.getMessage(), e);");
            out.println("        }");
            out.println("    }");

            // Generate static class for toBuilder
            out.println();
            out.println("    public static class " + className + "Buildable {");
            out.println("        public static " + builderName + " toBuilder(" + className + " instance) {");
            out.println("            return " + builderName + ".from(instance);");
            out.println("        }");
            out.println("    }");

            out.println("}");
        }
    }

    private void generateSettersForField(PrintWriter out, String fieldName, TypeMirror fieldType, String builderName) {
        // Standard setter
        out.println("    public " + builderName + " " + fieldName + "(" + fieldType + " " + fieldName + ") {");
        out.println("        this." + fieldName + " = " + fieldName + ";");
        out.println("        return this;");
        out.println("    }");
        out.println();

        // Generate collection helpers for collection fields
        if (isCollection(fieldType)) {
            String rawType = getCollectionType(fieldType);
            String elementType = getElementType(fieldType);

            // Add element
            out.println("    public " + builderName + " add" + capitalize(fieldName) + "(" + elementType + " element) {");
            out.println("        if (this." + fieldName + " == null) {");
            out.println("            this." + fieldName + " = new " + getImplementationType(rawType) + "<>();");
            out.println("        }");
            out.println("        ((" + rawType + "<" + elementType + ">) this." + fieldName + ").add(element);");
            out.println("        return this;");
            out.println("    }");
            out.println();

            // Add all elements
            out.println("    public " + builderName + " add" + capitalize(fieldName) + "All(Collection<" + elementType + "> elements) {");
            out.println("        if (elements == null || elements.isEmpty()) {");
            out.println("            return this;");
            out.println("        }");
            out.println("        if (this." + fieldName + " == null) {");
            out.println("            this." + fieldName + " = new " + getImplementationType(rawType) + "<>();");
            out.println("        }");
            out.println("        ((" + rawType + "<" + elementType + ">) this." + fieldName + ").addAll(elements);");
            out.println("        return this;");
            out.println("    }");
            out.println();
        }

        // Generate consumer-based setter for complex objects
        if (!isPrimitive(fieldType) && !isString(fieldType) && !isCollection(fieldType)) {
            out.println("    public " + builderName + " " + fieldName + "(Consumer<" + fieldType + "> consumer) {");
            out.println("        if (this." + fieldName + " == null) {");
            out.println("            this." + fieldName + " = " + getDefaultValue(fieldType) + ";");
            out.println("        }");
            out.println("        consumer.accept(this." + fieldName + ");");
            out.println("        return this;");
            out.println("    }");
            out.println();
        }

        // Generate optional setter
        if (!isPrimitive(fieldType)) {
            out.println("    public " + builderName + " " + fieldName + "Optional(java.util.Optional<" + fieldType + "> optional) {");
            out.println("        this." + fieldName + " = optional.orElse(null);");
            out.println("        return this;");
            out.println("    }");
            out.println();
        }

        // Generate supplier setter
        out.println("    public " + builderName + " " + fieldName + "Supplier(Supplier<" + fieldType + "> supplier) {");
        out.println("        this." + fieldName + " = supplier.get();");
        out.println("        return this;");
        out.println("    }");
        out.println();
    }

    private String deriveGetterName(VariableElement field) {
        String fieldName = field.getSimpleName().toString();
        String capitalizedName = capitalize(fieldName);

        // For boolean fields, try isXxx pattern first
        if (field.asType().toString().equals("boolean")) {
            return "is" + capitalizedName;
        }

        return "get" + capitalizedName;
    }

    private boolean isCollection(TypeMirror type) {
        if (type.getKind() != TypeKind.DECLARED) {
            return false;
        }

        DeclaredType declaredType = (DeclaredType) type;
        Element element = declaredType.asElement();
        String typeName = element.toString();

        return typeName.equals("java.util.List") ||
                typeName.equals("java.util.Set") ||
                typeName.equals("java.util.Collection");
    }

    private String getCollectionType(TypeMirror type) {
        if (type.getKind() != TypeKind.DECLARED) {
            return "Collection";
        }

        DeclaredType declaredType = (DeclaredType) type;
        Element element = declaredType.asElement();
        return element.getSimpleName().toString();
    }

    private String getElementType(TypeMirror type) {
        if (type.getKind() != TypeKind.DECLARED) {
            return "Object";
        }

        DeclaredType declaredType = (DeclaredType) type;
        List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();

        if (typeArguments.isEmpty()) {
            return "Object";
        }

        return typeArguments.getFirst().toString();
    }

    private String getImplementationType(String collectionType) {
        return switch (collectionType) {
            case "List" -> "ArrayList";
            case "Set" -> "HashSet";
            case "Collection" -> "ArrayList";
            default -> "ArrayList";
        };
    }

    private boolean isPrimitive(TypeMirror type) {
        return type.getKind().isPrimitive();
    }

    private boolean isString(TypeMirror type) {
        return type.toString().equals("java.lang.String");
    }

    private String getDefaultValue(TypeMirror type) {
        if (type.getKind() == TypeKind.DECLARED) {
            String typeName = type.toString();
            if (typeName.startsWith("java.")) {
                return "null";
            }
            return "new " + typeName + "()";
        }
        return "null";
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }
}
