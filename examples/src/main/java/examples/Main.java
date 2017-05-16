package examples;

public class Main {
    public static void main(String[] args) {
        JavaDataClass testDataClass = new JavaDataClass("Chester", "Bennington");

        String toStringFromSrc = testDataClass.toString();
        String toStringFromBuilder = JavaDataClassBuilder.build("Chester", "Bennington").toString();

        System.out.println("toStringFromSrc=" + toStringFromSrc);
        System.out.println("toStringFromBuilder=" + toStringFromBuilder);
    }
}
