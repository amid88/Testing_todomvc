/**
 * Created by dmitriy on 25.05.15.
 */
package ua.net.itlabs;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.interactions.Actions;
import ru.yandex.qatools.allure.annotations.Step;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;


public class Methods {

    public static SelenideElement clearCompleted = $("#clear-completed");
    public static SelenideElement todoCount = $("#todo-count");
    public static ElementsCollection todos = $$("#todo-list>li");


    @Step
    public static void doubleClick(SelenideElement element) {
        Actions action = new Actions(getWebDriver());
        action.doubleClick(element.toWebElement()).perform();
    }

    @Step
    public static void addTask(String task) {
        $("#new-todo").setValue(task).pressEnter();
    }

    @Step
    public static void editTask(String task, String taskEdited) {
        doubleClick(todos.find(text(task)).find("label"));
        todos.find(cssClass("editing")).find(".edit").setValue(taskEdited).pressEnter();
    }

    @Step
    public static void deleteTask(String task) {
        todos.find(text(task)).hover();
        todos.find(text(task)).find(".destroy").click();
    }

    @Step
    public static void assertEach(ElementsCollection elements, Condition someCondition) {
        for (SelenideElement element : elements) {
            element.shouldBe(someCondition);
        }
    }

    @Step
    public static void checkItemsLeftCounter(int number) {
        todoCount.shouldHave(text(Integer.toString(number)));
    }

    @Step
    public static void checkCompletedCounter(int number) {
        clearCompleted.shouldHave(text("(" + Integer.toString(number) + ")"));
    }

    public static void clearCompleted() {
        clearCompleted.click();
        clearCompleted.shouldBe(hidden);
    }

    @Step
    public static void toggleTask(String task) {
        todos.find(text(task)).find(".toggle").click();
    }

    @Step
    public static void toggleAll() {
        $("#toggle-all").click();
    }

    @Step
    public static void setAllFilter() {
        $("[href='#/']").click();
    }

    @Step
    public static void setActiveFilter() {
        $("[href='#/active']").click();
    }

    @Step
    public static void setCompletedFilter() {
        $("[href='#/completed']").click();
    }
}