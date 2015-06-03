package ua.net.itlabs.task2;


import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Screenshots;
import com.codeborne.selenide.SelenideElement;
import com.google.common.io.Files;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.qatools.allure.annotations.Attachment;

import java.io.File;
import java.io.IOException;

import static com.codeborne.selenide.CollectionCondition.empty;
import static com.codeborne.selenide.CollectionCondition.exactTexts;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;
import static ua.net.itlabs.pages.TodoMVC.*;

public class TodoTest {

    SelenideElement clearCompleted = $("#clear-completed");
    ElementsCollection todos = $$("#todo-list>li");
    String task1 = "create task1";
    String task2 = "create task2";
    String task3 = "create task3";
    String task4 = "create task4";
    String task1Edited = task1 + "_edited";

    @Before
    public void loadToDoMVC() {
        open("http://todomvc.com/examples/troopjs_require/#");
    }

    @After
    public void clearData() {
        executeJavaScript("localStorage.clear()");
        open("http://todomvc.com/");
    }

    @Test
    public void testAtAllFilter(){

        //create task
        addTask(task1);
        addTask(task2);
        addTask(task3);
        addTask(task4);
        todos.shouldHave(exactTexts(task1, task2, task3, task4));
        checkItemsLeftCounter(4);

        //edit task
        editTask(task1, task1 + "_edited");
        todos.shouldHave(exactTexts(task1 + "_edited", task2, task3, task4));
        checkItemsLeftCounter(4);

        //delete task
        deleteTask(task2);
        todos.shouldHave(exactTexts(task1 + "_edited", task3, task4));
        checkItemsLeftCounter(3);

        //complete task
        toggleTask(task4);
        checkItemsLeftCounter(2);
        setCompletedFilter();
        todos.filter(visible).shouldHave(exactTexts(task4));
        setActiveFilter();
        todos.filter(visible).shouldHave(exactTexts(task1 + "_edited", task3));

        //clear completed task
        clearCompleted.click();
        filterAll();
        todos.filter(visible).shouldHave(exactTexts(task1 + "_edited", task3));
        setActiveFilter();
        todos.filter(visible).shouldHave(exactTexts(task1 + "_edited", task3));
        checkItemsLeftCounter(2);

        //reopen task
        filterAll();
        toggleTask(task3);
        checkItemsLeftCounter(1);
        setCompletedFilter();
        todos.filter(visible).shouldHave(exactTexts(task3));
        toggleTask(task3);
        checkItemsLeftCounter(2);
        setActiveFilter();
        todos.filter(visible).shouldHave(exactTexts(task1 + "_edited", task3));

        //complete all task
        toggleAll();
        checkItemsLeftCounter(0);

        //clear completed (all)
        clearCompleted();
        todos.shouldBe(empty);
    }

    @Test
    public void testAtActiveFilter(){

        //precondition
        addTask(task1);
        addTask(task2);
        setActiveFilter();

        //create task
        addTask(task3);
        todos.filter(visible).shouldHave(exactTexts(task1, task2, task3));
        checkItemsLeftCounter(3);
        filterAll();
        todos.filter(visible).shouldHave(exactTexts(task1, task2, task3));

        //edit task
        setActiveFilter();
        editTask(task1, task1Edited);
        checkItemsLeftCounter(3);
        todos.filter(visible).shouldHave(exactTexts(task1Edited, task2, task3));
        filterAll();
        todos.filter(visible).shouldHave(exactTexts(task1Edited, task2, task3));

        //delete task
        setActiveFilter();
        deleteTask(task1Edited);
        checkItemsLeftCounter(2);
        todos.filter(visible).shouldHave(exactTexts(task2, task3));
        filterAll();
        todos.filter(visible).shouldHave(exactTexts(task2, task3));

        //complete tasks
        setActiveFilter();
        toggleAll();
        todos.filter(visible).shouldBe(empty);
        filterAll();
        todos.filter(visible).shouldHave(exactTexts(task2, task3));

        // clear completed
        clearCompleted();
        todos.shouldBe(empty);
    }

    @Test
    public void testAtCompletedFilter(){

        //precondition
        addTask(task1);
        addTask(task2);
        setCompletedFilter();

        //create completed task
        addTask(task3);
        checkItemsLeftCounter(3);
        todos.filter(visible).shouldBe(empty);
        toggleAll();
        checkItemsLeftCounter(0);
        todos.filter(visible).shouldHave(exactTexts(task1, task2, task3));

        //delete task
        deleteTask(task2);
        checkItemsLeftCounter(0);
        todos.filter(visible).shouldHave(exactTexts(task1, task3));
        filterAll();
        todos.filter(visible).shouldHave(exactTexts(task1, task3));

        //reopen task
        setCompletedFilter();
        toggleTask(task1);
        checkItemsLeftCounter(1);
        setActiveFilter();
        todos.filter(visible).shouldHave(exactTexts(task1));
        setCompletedFilter();
        toggleAll();
        checkItemsLeftCounter(0);
        todos.filter(visible).shouldHave(exactTexts(task1, task3));

        //clear completed
        clearCompleted();
        todos.shouldBe(empty);
    }

    @After
    public void postScreenshot() throws IOException {
        screenshot();
    }

    @Attachment(type = "image/png")
    public byte[] screenshot() throws IOException {
        File screenshot = Screenshots.getScreenShotAsFile();
        return Files.toByteArray(screenshot);
    }
}