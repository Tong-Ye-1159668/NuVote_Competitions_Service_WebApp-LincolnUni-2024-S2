package lu.p2.selenium;

import org.junit.Test;
import org.openqa.selenium.By;

import static java.lang.String.format;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;
import static org.openqa.selenium.By.ByClassName;
import static org.openqa.selenium.By.ById;
import static org.openqa.selenium.By.ByTagName;
import static shiver.me.timbers.data.random.RandomStrings.someAlphaString;
import static shiver.me.timbers.data.random.RandomStrings.someString;
import static shiver.me.timbers.matchers.Matchers.hasField;

public class BysTest {

    @Test
    public void Instantiation_fo_coverage() {

        new Bys();

    }

    @Test
    public void Can_create_a_by_id() {

        // Given
        final String id = someString();

        // When
        final By actual = Bys.id(id);

        // Then
        assertTrue(instanceOf(ById.class).matches(actual));
        assertTrue(hasField("id", id).matches(actual));
    }

    @Test
    public void Can_create_a_by_attribute() {

        // Given
        final String tag = someString();
        final String name = someString();
        final String value = someString();

        // When
        final By actual = Bys.attribute(tag, name, value);

        // Then
        assertTrue(hasField("xpathExpression", format(".//%s[contains(@%s, \"%s\")]", tag, name, value)).matches(actual));
    }

    @Test
    public void Can_create_a_by_id_that_contains_text() {

        // Given
        final String text = someString();

        // When
        final By actual = Bys.idThatContains(text);

        // Then
        assertTrue(hasField("xpathExpression", format(".//*[contains(@id, \"%s\")]", text)).matches(actual));
    }

    @Test
    public void Can_create_a_by_class_name() {

        // Given
        final String className = someAlphaString();

        // When
        final By actual = Bys.className(className);

        // Then
        assertTrue(instanceOf(ByClassName.class).matches(actual));
        assertTrue(hasField("className", className).matches(actual));
    }

    @Test
    public void Can_create_a_by_text() {

        // Given
        final String tag = someString();
        final String text = someString();

        // When
        final By actual = Bys.text(tag, text);

        // Then
        assertTrue(hasField("xpathExpression", format(".//%s[text()[contains(., \"%s\")]]", tag, text)).matches(actual));
    }

    @Test
    public void Can_create_a_by_value() {

        // Given
        final String value = someString(5);

        // When
        final By actual = Bys.value(value);

        // Then
        assertTrue(hasField("xpathExpression", format(".//input[contains(@value, \"%s\")]", value)).matches(actual));
    }

    @Test
    public void Can_create_a_by_label() {

        // Given
        final String text = someString();

        // When
        final By actual = Bys.label(text);

        // Then
        assertTrue(hasField("xpathExpression", format(".//label[text()[contains(., \"%s\")]]", text)).matches(actual));
    }

    @Test
    public void Can_create_a_by_parent_class_name() {

        // Given
        final String tag = someString();
        final String className = someString();

        // When
        final By actual = Bys.parentClassName(tag, className);

        // Then
        assertTrue(hasField(
                "xpathExpression", format("./ancestor::%s[contains(@class, \"%s\")][1]", tag, className)).matches(actual)
        );
    }

    @Test
    public void Can_create_a_by_name() {

        // Given
        final String tag = someString(5);
        final String name = someString(8);

        // When
        final By actual = Bys.name(tag, name);

        // Then
        assertTrue(hasField("xpathExpression", format(".//%s[contains(@name, \"%s\")]", tag, name)).matches(actual));
    }

    @Test
    public void Can_create_a_by_tag_name() {

        // Given
        final String tagName = someString();

        // When
        final By actual = Bys.tagName(tagName);

        // Then
        assertTrue(instanceOf(ByTagName.class).matches(actual));
        assertTrue(hasField("tagName", tagName).matches(actual));
    }

    @Test
    public void Can_create_a_by_following_sibling() {

        // When
        final By actual = Bys.followingSibling();

        // Then
        assertThat(actual, is(By.xpath("./following-sibling::*[1]")));
    }

}