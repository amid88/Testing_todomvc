package ua.net.itlabs.task3;


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

import static com.codeborne.selenide.CollectionCondition.empty;
import static com.codeborne.selenide.CollectionCondition.exactTexts;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;


public class TodoTest {

    SelenideElement clearCompleted = $("#clear-completed");
    ElementsCollection todos = $$("#todo-list>li");
    String task1 = "create task1";
    String task2 = "create task2";
    String task3 = "create task3";
    String task4 = "create task4";
    
    TodoMVCPage pageObject;
    public TodoTest() {
        pageObject = new TodoMVCPage();
    }

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
        pageObject.addTask(task1);
        pageObject.addTask(task2);
        pageObject.addTask(task3);
        pageObject.addTask(task4);
        todos.shouldHave(exactTexts(task1, task2, task3, task4));
        pageObject.checkItemsLeftCounter(4);

        //edit task
        pageObject.editTask(task1, task1 + "_edited");
        todos.shouldHave(exactTexts(task1 + "_edited", task2, task3, task4));
        pageObject.checkItemsLeftCounter(4);

        //delete task
        pageObject.deleteTask(task2);
        todos.shouldHave(exactTexts(task1 + "_edited", task3, task4));
        pageObject.checkItemsLeftCounter(3);

        //complete task
        pageObject.toggleTask(task4);
        pageObject.checkItemsLeftCounter(2);
        pageObject.setCompletedFilter();
        todos.filter(visible).shouldHave(exactTexts(task4));
        pageObject.setActiveFilter();
        todos.filter(visible).shouldHave(exactTexts(task1 + "_edited", task3));

        //clear completed task
        clearCompleted.click();
        pageObject.filterAll();
        todos.filter(visible).shouldHave(exactTexts(task1 + "_edited", task3));
        pageObject.setActiveFilter();
        todos.filter(visible).shouldHave(exactTexts(task1 + "_edited", task3));
        pageObject.checkItemsLeftCounter(2);

        //reopen task
        pageObject.filterAll();
        pageObject.toggleTask(task3);
        pageObject.checkItemsLeftCounter(1);
        pageObject.setCompletedFilter();
        todos.filter(visible).shouldHave(exactTexts(task3));
        pageObject.toggleTask(task3);
        pageObject.checkItemsLeftCounter(2);
        pageObject.setActiveFilter();
        todos.filter(visible).shouldHave(exactTexts(task1 + "_edited", task3));

        //complete all task
        pageObject.toggleAll();
        pageObject.checkItemsLeftCounter(0);

        //clear completed (all)
        pageObject.clearCompleted();
        todos.shouldBe(empty);
    }

    @Test
    public void testAtActiveFilter(){

        //precondition
        pageObject.addTask(task1);
        pageObject.addTask(task2);
        pageObject.setActiveFilter();

        //create task
        pageObject.addTask(task3);
        todos.filter(visible).shouldHave(exactTexts(task1, task2, task3));
        pageObject.checkItemsLeftCounter(3);
        pageObject.filterAll();
        todos.filter(visible).shouldHave(exactTexts(task1, task2, task3));

        //edit task
        pageObject.setActiveFilter();
        pageObject.editTask(task1, task1 + "_edited");
        pageObject.checkItemsLeftCounter(3);
        todos.filter(visible).shouldHave(exactTexts(task1 + "_edited", task2, task3));
        pageObject.filterAll();
        todos.filter(visible).shouldHave(exactTexts(task1 + "_edited", task2, task3));

        //delete task
        pageObject.setActiveFilter();
        pageObject.deleteTask(task1 + "_edited");
        pageObject.checkItemsLeftCounter(2);
        todos.filter(visible).shouldHave(exactTexts(task2, task3));
        pageObject.filterAll();
        todos.filter(visible).shouldHave(exactTexts(task2, task3));

        //complete tasks
        pageObject.setActiveFilter();
        pageObject.toggleAll();
        todos.filter(visible).shouldBe(empty);
        pageObject.filterAll();
        todos.filter(visible).shouldHave(exactTexts(task2, task3));

        // clear completed
        pageObject.clearCompleted();
        todos.shouldBe(empty);
    }

    @Test
    public void testAtCompletedFilter(){

        //precondition
        pageObject.addTask(task1);
        pageObject.addTask(task2);
        pageObject.setCompletedFilter();

        //create completed task
        pageObject.addTask(task3);
        pageObject.checkItemsLeftCounter(3);
        todos.filter(visible).shouldBe(empty);
        pageObject.toggleAll();
        pageObject.checkItemsLeftCounter(0);
        todos.filter(visible).shouldHave(exactTexts(task1, task2, task3));

        //delete task
        pageObject.deleteTask(task2);
        pageObject.checkItemsLeftCounter(0);
        todos.filter(visible).shouldHave(exactTexts(task1, task3));
        pageObject.filterAll();
        todos.filter(visible).shouldHave(exactTexts(task1, task3));

        //reopen task
        pageObject.setCompletedFilter();
        pageObject.toggleTask(task1);
        pageObject.checkItemsLeftCounter(1);
        pageObject.setActiveFilter();
        todos.filter(visible).shouldHave(exactTexts(task1));
        pageObject.setCompletedFilter();
        pageObject.toggleAll();
        pageObject.checkItemsLeftCounter(0);
        todos.filter(visible).shouldHave(exactTexts(task1, task3));

        //clear completed
        pageObject.clearCompleted();
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