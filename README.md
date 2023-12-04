# string-templates-j2kt

A simple library bringing Java's new [String templates](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/StringTemplate.html)
to Kotlin.

## Java's templates

String templates are a new (preview-ish) feature starting in Java 21. They provide a way to more easily concatenate
strings but can also be used in many other ways.

Java has three included templates: `STR`, `RAW`, and `FMT`. Other libraries are able to provide any other type of
template by creating an instance of `StringTemplate.Processor<ReturnType, PossibleException>`. This library wraps
such processors to provide a cleaner interface.

### `STR`

```java
String name = "John";
String str = STR."Hello, \{name}!"; // Hello, John!
```

Kotlin already has native support for this style:

```kotlin
val name = "John"
val str = "Hello, $name!"
```

### `RAW` in Java

This provides the underlying `StringTemplate` object created through a concatenation.

Correct usage:

```java
String name = "John";
StringTemplate template = RAW."Hello, \{name}!";
```

Additional (messier) method to create such a template:

```java
String name = "John";
StringTemplate template = StringTemplate.of(List.of("Hello, ", "!"), List.of(name));
```

### Other templates in Java

Example using Java's built-in `FMT` template (from `java.util.FormatProcessor`)

```java
String name = "John";
float money = 1.5f;
String str = FMT."Hello, %s\{name}! You have $%.2f\{money}!";
// Hello, John! You have $1.50!
```

## This library

This library brings support for templates to Kotlin. As it requires some rather _interesting_ syntax,
it is implemented slightly differently.

Using this library requires importing `com.sschr15.templates.invoke`. If using IntelliJ IDEA, it likes
to not import this automatically, so you may need to do it manually.

To use templates, specify the processor as a "name" for a function block. Inside, return a `String`.
Any parameters inside concatenation templates with a preceding `!` or a succeeding `.eval()` will be
passed to the template processor rather than being directly concatenated.

For example, using the `RAW` and `FMT` processors:

```kotlin
val name = "John"
val template = RAW { "Hello, ${!name}!" } // StringTemplate.of(listOf("Hello, ", "!"), listOf(name))

val money = 1.5f
val text = FMT { "Hello, %s${!name}! You have $%.2f${!money}!" } // Hello, John! You have $1.50!
```

Any other processors can also be used. See the [tests](src/test/kotlin/Tests.kt) for examples,
including a custom "SQL" processor
