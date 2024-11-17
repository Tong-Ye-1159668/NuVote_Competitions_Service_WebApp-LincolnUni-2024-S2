package lu.p2.selenium;

import org.openqa.selenium.By;

import static java.lang.String.format;

public class Bys {

    public static By id(String id) {
        return By.id(id);
    }

    public static By attribute(String tag, String name, String value) {
        return By.xpath(format(".//%s[contains(@%s, \"%s\")]", tag, name, value));
    }

    public static By idThatContains(String text) {
        return attribute("*", "id", text);
    }

    public static By text(String tag, String text) {
        return By.xpath(format(".//%s[text()[contains(., \"%s\")]]", tag, text));
    }

    public static By label(String name) {
        return text("label", name);
    }

    public static By value(String value) {
        return attribute("input", "value", value);
    }

    public static By className(String className) {
        return By.className(className);
    }

    public static By parentClassName(String tag, String className) {
        return By.xpath(format("./ancestor::%s[contains(@class, \"%s\")][1]", tag, className));
    }

    public static By name(String tag, String name) {
        return attribute(tag, "name", name);
    }

    public static By tagName(String tagName) {
        return By.tagName(tagName);
    }

    public static By followingSibling() {
        return followingSibling("*");
    }

    public static By followingSibling(String tagName) {
        return By.xpath(format("./following-sibling::%s[1]", tagName));
    }
}