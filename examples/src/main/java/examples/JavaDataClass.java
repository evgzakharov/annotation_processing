package examples;

import processor.TestingClassJava;

@TestingClassJava(level = "INFO")
public class JavaDataClass {
    private final String name;
    private final String surname;

    public JavaDataClass(String name, String surname) {
        this.name = name;
        this.surname = surname;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }
}
