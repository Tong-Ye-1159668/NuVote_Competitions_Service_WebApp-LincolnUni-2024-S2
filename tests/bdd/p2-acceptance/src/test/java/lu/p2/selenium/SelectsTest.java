package lu.p2.selenium;

import org.junit.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static shiver.me.timbers.matchers.Matchers.hasField;

public class SelectsTest {
    @Test
    public void Can_create_a_select() {

        final WebElement expected = mock(WebElement.class);

        // Given
        given(expected.getTagName()).willReturn("select");

        // When
        final Select actual = new Selects().create(expected);

        // Then
        assertTrue(hasField("element", expected).matches(actual));
    }
}