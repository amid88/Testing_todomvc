package ua.net.itlabs.task3;


import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Screenshots;
import com.codeborne.selenide.SelenideElement;
import com.google.common.io.Files;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.qatools.allure.annotations.Attachment;
import ua.net.itlabs.pages.TodoMVCPage;

import java.io.File;
import java.io.IOException;

import static com.codeborne.selenide.CollectionCondition.*;
import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;


public class TodoTest {

    SelenideElement clearCompleted = $("#clear-completed");
    ElementsCollection todos = $$("#todo-list>li");
    String task1 = "create task1";
    String task2 = "create task2";
    String task3 = "create task3";
    String task4 = "create task4";
    String task1Edited = task1 + "_edited";
    Condition completed = cssClass("completed");
    Condition active = cssClass("active");

    //some text
    //more text
    TodoMVCPage pageObject;
    public TodoTest() {
        pageObject = new TodoMVCPage();
    }

    @Before
    public void loadToDoMVC() {
        open("http://todomvc.com/");
        open("http://todomvc.com/examples/troopjs_require/#");
    }

    @After
    public void clearData() {
        executeJavaScript("localStorage.clear()");
    }

    @After
    public void tearDown() throws IOException {
        screenshot();
    }

    @Attachment(type = "image/png")
    public byte[] screenshot() throws IOException {
        File screenshot = Screenshots.getScreenShotAsFile();
        return Files.toByteArray(screenshot);
    }

    @Test
    public void testAtAllFilter(){

        //create task
        pageObject.addTask(task1);
        pageObject.addTask(task2);
        pageObject.addTask(task3);
        pageObject.addTask(task4);
        todos.shouldHave(exactTexts(task1, task2, task3, task4));
        pageObject.checkItemsLeftCounter(4);

        //edit task
        pageObject.editTask(task1, task1Edited);
        todos.shouldHave(exactTexts(task1Edited, task2, task3, task4));
        pageObject.checkItemsLeftCounter(4);

        //delete task
        pageObject.deleteTask(task2);
        todos.shouldHave(exactTexts(task1 + "_edited", task3, task4));
        pageObject.checkItemsLeftCounter(3);

        //complete task
        pageObject.toggleTask(task4);
        pageObject.checkItemsLeftCounter(2);
        pageObject.checkCompletedCounter(1);
        pageObject.setCompletedFilter();
        todos.filter(visible).shouldHave(exactTexts(task4));

        //clear completed task
        clearCompleted.click();
        pageObject.filterAll();
        todos.filter(visible).shouldHave(exactTexts(task1 + "_edited", task3));
        pageObject.checkItemsLeftCounter(2);

        //reopen task
        pageObject.toggleTask(task3);
        pageObject.checkItemsLeftCounter(1);
        pageObject.checkCompletedCounter(1);
        pageObject.toggleTask(task3);
        pageObject.checkItemsLeftCounter(2);
        todos.shouldHave(texts(task1 + "_edited", task3));

        //complete all task
        pageObject.toggleAll();
        pageObject.checkItemsLeftCounter(0);
        pageObject.checkCompletedCounter(2);

        //clear completed (all)
        pageObject.clearCompleted();
        todos.shouldBe(empty);
    }

    @Test
    public void testAtActiveFilter(){

        //precondition
        pageObject.addTask(task1);
        pageObject.setActiveFilter();

        //create task
        pageObject.addTask(task2);
        pageObject.addTask(task3);
        pageObject.assertEach(todos, active);
        pageObject.checkItemsLeftCounter(3);
        todos.shouldHave(exactTexts(task1, task2, task3));

        //edit task
        pageObject.editTask(task1, task1Edited);
        pageObject.checkItemsLeftCounter(3);
        todos.shouldHave(exactTexts(task1Edited, task2, task3));

        //delete task
        pageObject.deleteTask(task1Edited);
        pageObject.checkItemsLeftCounter(2);
        todos.shouldHave(exactTexts(task2, task3));

        //complete tasks
        pageObject.toggleAll();
        pageObject.checkCompletedCounter(2);
        todos.filter(visible).shouldBe(empty);

        // clear completed
        pageObject.clearCompleted();
        todos.shouldBe(empty);
    }

    @Test
    public void testAtCompletedFilter(){

        //precondition
        pageObject.addTask(task1);
        pageObject.setCompletedFilter();

        //create completed tasks
        pageObject.addTask(task2);
        pageObject.addTask(task3);
        pageObject.checkItemsLeftCounter(3);
        pageObject.toggleAll();
        pageObject.assertEach(todos, completed);
        pageObject.checkCompletedCounter(3);
        pageObject.checkItemsLeftCounter(0);
        todos.shouldHave(exactTexts(task1, task2, task3));

        //delete task
        pageObject.deleteTask(task2);
        pageObject.checkCompletedCounter(2);
        pageObject.checkItemsLeftCounter(0);
        todos.shouldHave(exactTexts(task1,task3));

        //reopen task
        pageObject.toggleTask(task1);
        pageObject.checkCompletedCounter(1);
        pageObject.checkItemsLeftCounter(1);
        pageObject.setActiveFilter();
        todos.filter(visible).shouldHave(exactTexts(task1));

        pageObject.setCompletedFilter();
        pageObject.toggleAll();
        pageObject.checkCompletedCounter(2);
        pageObject.checkItemsLeftCounter(0);
        todos.filter(visible).shouldHave(exactTexts(task1,task3));

        //clear completed
        pageObject.clearCompleted();
        todos.shouldBe(empty);
    }
}

