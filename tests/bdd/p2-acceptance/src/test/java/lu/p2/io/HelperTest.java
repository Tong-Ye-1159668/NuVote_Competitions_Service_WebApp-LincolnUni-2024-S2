package lu.p2.io;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import java.util.List;

import static java.lang.Boolean.TRUE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;

public class HelperTest {

    private Helper helper;

    @Before
    public void setUp() {
        helper = new Helper();
    }

    @Test
    public void Can_get_one_element_from_a_list() {
        // Given
        final List<WebElement> list = List.of(mock(WebElement.class), mock(WebElement.class));

        // When
        final WebElement actual = helper.getRandomElement(list);

        // Then
        assertThat(TRUE, is(list.contains(actual)));
    }
}