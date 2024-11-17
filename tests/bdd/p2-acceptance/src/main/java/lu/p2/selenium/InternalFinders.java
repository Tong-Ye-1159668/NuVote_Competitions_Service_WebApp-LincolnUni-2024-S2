package lu.p2.selenium;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.springframework.stereotype.Component;
import shiver.me.timbers.waiting.Options;
import shiver.me.timbers.waiting.Waiter;

import java.util.List;

@Component
public class InternalFinders {

    private final Selects selects;

    InternalFinders(Selects selects) {
        this.selects = selects;
    }

    WebElement findById(SearchContext parent, String id) {
        return parent.findElement(Bys.id(id));
    }

    WebElement findByIdThatContains(SearchContext parent, String text) {
        return parent.findElement(Bys.idThatContains(text));
    }

    WebElement findByClassName(SearchContext parent, String className) {
        return parent.findElement(Bys.className(className));
    }

    List<WebElement> findAllByClassName(SearchContext parent, String className) {
        return parent.findElements(Bys.className(className));
    }

    WebElement findByText(SearchContext parent, String tag, String text) {
        return parent.findElement(Bys.text(tag, text));
    }

    List<WebElement> findAllByText(SearchContext parent, String tag, String text) {
        return parent.findElements(Bys.text(tag, text));
    }

    WebElement findParentByClassName(SearchContext element, String tag, String className) {
        return element.findElement(Bys.parentClassName(tag, className));
    }

    WebElement findByName(SearchContext parent, String tag, String name) {
        return parent.findElement(Bys.name(tag, name));
    }

    WebElement findByLabel(SearchContext parent, String name) {
        return parent.findElement(Bys.id(parent.findElement(Bys.label(name)).getAttribute("for")));
    }

    WebElement findByValue(SearchContext parent, String value) {
        return parent.findElement(Bys.value(value));
    }

    void choose(WebElement element) {
        new Waiter(new Options().willWaitForTrue(true)).wait(() -> {
            element.click();
            return Boolean.valueOf(element.isSelected());
        });
    }

    void setText(WebElement element, String text) {
        element.clear();
        element.sendKeys(text == null ? "" : text);
    }

    void select(WebElement element, String option) {
        final Select select = selects.create(element);
        select.selectByVisibleText(option);
    }

    WebElement findByTagName(SearchContext parent, String tagName) {
        return parent.findElement(Bys.tagName(tagName));
    }

    WebElement findByAttribute(SearchContext parent, String tag, String name, String value) {
        return parent.findElement(Bys.attribute(tag, name, value));
    }
}