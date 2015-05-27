package ua.net.itlabs.task3;


import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Screenshots;
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
import static com.codeborne.selenide.Selenide.executeJavaScript;
import static com.codeborne.selenide.Selenide.open;


public class TodoTest {

    String task1 = "create task1";
    String task2 = "create task2";
    String task3 = "create task3";
    String task4 = "create task4";
    String task1Edited = task1 + "_edited";
    Condition completed = cssClass("completed");
    Condition active = cssClass("active");


    TodoMVCPage TodoMVC;
    public TodoTest() {
        TodoMVC = new TodoMVCPage();
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
        TodoMVC.addTask(task1);
        TodoMVC.addTask(task2);
        TodoMVC.addTask(task3);
        TodoMVC.addTask(task4);
        TodoMVC.todos.shouldHave(exactTexts(task1, task2, task3, task4));
        TodoMVC.checkItemsLeftCounter(4);

        //edit task
        TodoMVC.editTask(task1, task1Edited);
        TodoMVC.todos.shouldHave(exactTexts(task1Edited, task2, task3, task4));
        TodoMVC.checkItemsLeftCounter(4);

        //delete task
        TodoMVC.deleteTask(task2);
        TodoMVC.todos.shouldHave(exactTexts(task1 + "_edited", task3, task4));
        TodoMVC.checkItemsLeftCounter(3);

        //complete task
        TodoMVC.toggleTask(task4);
        TodoMVC.checkItemsLeftCounter(2);
        TodoMVC.checkCompletedCounter(1);
        TodoMVC.setCompletedFilter();
        TodoMVC.todos.filter(visible).shouldHave(exactTexts(task4));

        //clear completed task
        TodoMVC.clearCompleted.click();
        TodoMVC.filterAll();
        TodoMVC.todos.filter(visible).shouldHave(exactTexts(task1 + "_edited", task3));
        TodoMVC.checkItemsLeftCounter(2);

        //reopen task
        TodoMVC.toggleTask(task3);
        TodoMVC.checkItemsLeftCounter(1);
        TodoMVC.checkCompletedCounter(1);
        TodoMVC.toggleTask(task3);
        TodoMVC.checkItemsLeftCounter(2);
        TodoMVC.todos.shouldHave(texts(task1 + "_edited", task3));

        //complete all task
        TodoMVC.toggleAll();
        TodoMVC.checkItemsLeftCounter(0);
        TodoMVC.checkCompletedCounter(2);

        //clear completed (all)
        TodoMVC.clearCompleted();
        TodoMVC.todos.shouldBe(empty);
    }

    @Test
    public void testAtActiveFilter(){

        //precondition
        TodoMVC.addTask(task1);
        TodoMVC.setActiveFilter();

        //create task
        TodoMVC.addTask(task2);
        TodoMVC.addTask(task3);
        TodoMVC.assertEach(TodoMVC.todos, active);
        TodoMVC.checkItemsLeftCounter(3);
        TodoMVC.todos.shouldHave(exactTexts(task1, task2, task3));

        //edit task
        TodoMVC.editTask(task1, task1Edited);
        TodoMVC.checkItemsLeftCounter(3);
        TodoMVC.todos.shouldHave(exactTexts(task1Edited, task2, task3));

        //delete task
        TodoMVC.deleteTask(task1Edited);
        TodoMVC.checkItemsLeftCounter(2);
        TodoMVC.todos.shouldHave(exactTexts(task2, task3));

        //complete tasks
        TodoMVC.toggleAll();
        TodoMVC.checkCompletedCounter(2);
        TodoMVC.todos.filter(visible).shouldBe(empty);

        // clear completed
        TodoMVC.clearCompleted();
        TodoMVC.todos.shouldBe(empty);
    }

    @Test
    public void testAtCompletedFilter(){

        //precondition
        TodoMVC.addTask(task1);
        TodoMVC.setCompletedFilter();

        //create completed tasks
        TodoMVC.addTask(task2);
        TodoMVC.addTask(task3);
        TodoMVC.checkItemsLeftCounter(3);
        TodoMVC.toggleAll();
        TodoMVC.assertEach(TodoMVC.todos, completed);
        TodoMVC.checkCompletedCounter(3);
        TodoMVC.checkItemsLeftCounter(0);
        TodoMVC.todos.shouldHave(exactTexts(task1, task2, task3));

        //delete task
        TodoMVC.deleteTask(task2);
        TodoMVC.checkCompletedCounter(2);
        TodoMVC.checkItemsLeftCounter(0);
        TodoMVC.todos.shouldHave(exactTexts(task1,task3));

        //reopen task
        TodoMVC.toggleTask(task1);
        TodoMVC.checkCompletedCounter(1);
        TodoMVC.checkItemsLeftCounter(1);
        TodoMVC.setActiveFilter();
        TodoMVC.todos.filter(visible).shouldHave(exactTexts(task1));

        TodoMVC.setCompletedFilter();
        TodoMVC.toggleAll();
        TodoMVC.checkCompletedCounter(2);
        TodoMVC.checkItemsLeftCounter(0);
        TodoMVC.todos.filter(visible).shouldHave(exactTexts(task1,task3));

        //clear completed
        TodoMVC.clearCompleted();
        TodoMVC.todos.shouldBe(empty);
    }
}

