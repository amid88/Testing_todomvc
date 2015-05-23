package ua.net.itlabs.task2;


import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Screenshots;
import com.codeborne.selenide.SelenideElement;
import com.google.common.io.Files;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.interactions.Actions;
import ru.yandex.qatools.allure.annotations.Attachment;
import ru.yandex.qatools.allure.annotations.Step;


import java.io.File;
import java.io.IOException;

import static com.codeborne.selenide.CollectionCondition.*;
import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Condition.hidden;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;


public class TodoTest {

    SelenideElement clearCompleted = $("#clear-completed");
    SelenideElement todoCount = $("#todo-count");
    ElementsCollection todos = $$("#todo-list>li");
    String task1 = "create task1";
    String task2 = "create task2";
    String task3 = "create task3";
    String task4 = "create task4";
    String task1Edited = task1 + "_edited";
    Condition completed = cssClass("completed");
    Condition active = cssClass("active");


    @Step
    public void doubleClick(SelenideElement element){
       Actions action = new Actions(getWebDriver());
       action.doubleClick(element.toWebElement()).perform();
   }

    @Step
    public void addTask(String task) {
        $("#new-todo").setValue(task).pressEnter();
    }

    @Step
    public void editTask(String task, String taskEdited){
        doubleClick(todos.find(text(task)).find("label"));
        todos.find(cssClass("editing")).find(".edit").setValue(taskEdited).pressEnter();
    }

    @Step
    public void deleteTask(String task){
        todos.find(text(task)).hover();
        todos.find(text(task)).find(".destroy").click();
    }

    @Step
    public void assertEach(ElementsCollection elements, Condition someCondition) {
        for (SelenideElement element : elements) {
            element.shouldBe(someCondition);
        }
    }

    @Step
    public void checkItemsLeftCounter(int number){
        todoCount.shouldHave(text(Integer.toString(number)));
    }

    @Step
    public void checkCompletedCounter(int number){
        clearCompleted.shouldHave(text("(" + Integer.toString(number) + ")"));
    }

    public void clearCompleted(){
        clearCompleted.click();
        clearCompleted.shouldBe(hidden);
    }

    @Step
    public void toggleTask(String task){
        todos.find(text(task)).find(".toggle").click();
    }

    @Step
    public void toggleAll(){
        $("#toggle-all").click();
    }

    @Step
    public void setAllFilter(){
        $("[href='#/']").click();
    }

    @Step
    public void setActiveFilter(){
        $("[href='#/active']").click();
    }

    @Step
    public void setCompletedFilter(){
        $("[href='#/completed']").click();
    }

    @Before
    public void loadToDoMVC(){
        open("http://todomvc.com/");
        open("http://todomvc.com/examples/troopjs_require/#");
    }

    @After
    public void clearData(){
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
        addTask(task1);
        addTask(task2);
        addTask(task3);
        addTask(task4);
        todos.shouldHave(exactTexts(task1, task2, task3, task4));
        checkItemsLeftCounter(4);

        //edit task
        editTask(task1, task1Edited);
        todos.shouldHave(exactTexts(task1Edited, task2, task3, task4));
        checkItemsLeftCounter(4);

        //delete task
        deleteTask(task2);
        todos.shouldHave(exactTexts(task1 + "_edited", task3, task4));
        checkItemsLeftCounter(3);

        //complete task
        toggleTask(task4);
        checkItemsLeftCounter(2);
        checkCompletedCounter(1);
        setCompletedFilter();
        todos.filter(visible).shouldHave(exactTexts(task4));

        //clear completed task
        clearCompleted.click();
        setAllFilter();
        todos.filter(visible).shouldHave(exactTexts(task1 + "_edited", task3));
        checkItemsLeftCounter(2);

        //reopen task
        toggleTask(task3);
        checkItemsLeftCounter(1);
        checkCompletedCounter(1);
        toggleTask(task3);
        checkItemsLeftCounter(2);
        todos.shouldHave(texts(task1 + "_edited", task3));

        //complete all task
        toggleAll();
        checkItemsLeftCounter(0);
        checkCompletedCounter(2);

        //clear completed (all)
        clearCompleted();
        todos.shouldBe(empty);
    }

    @Test
    public void testAtActiveFilter(){

        //precondition
        addTask(task1);
        setActiveFilter();

        //create task
        addTask(task2);
        addTask(task3);
        assertEach(todos, active);
        checkItemsLeftCounter(3);
        todos.shouldHave(exactTexts(task1, task2, task3));

        //edit task
        editTask(task1, task1Edited);
        checkItemsLeftCounter(3);
        todos.shouldHave(exactTexts(task1Edited, task2, task3));

        //delete task
        deleteTask(task1Edited);
        checkItemsLeftCounter(2);
        todos.shouldHave(exactTexts(task2, task3));

        //complete tasks
        toggleAll();
        checkCompletedCounter(2);
        todos.filter(visible).shouldBe(empty);

        // clear completed
        clearCompleted();
        todos.shouldBe(empty);
    }

    @Test
    public void testAtCompletedFilter(){

        //precondition
        addTask(task1);
        setCompletedFilter();

        //create completed tasks
        addTask(task2);
        addTask(task3);
        checkItemsLeftCounter(3);
        toggleAll();
        assertEach(todos, completed);
        checkCompletedCounter(3);
        checkItemsLeftCounter(0);
        todos.shouldHave(exactTexts(task1, task2, task3));

        //delete task
        deleteTask(task2);
        checkCompletedCounter(2);
        checkItemsLeftCounter(0);
        todos.shouldHave(exactTexts(task1,task3));

        //reopen task
        toggleTask(task1);
        checkCompletedCounter(1);
        checkItemsLeftCounter(1);
        setActiveFilter();
        todos.filter(visible).shouldHave(exactTexts(task1));

        setCompletedFilter();
        toggleAll();
        checkCompletedCounter(2);
        checkItemsLeftCounter(0);
        todos.filter(visible).shouldHave(exactTexts(task1,task3));

        //clear completed
        clearCompleted();
        todos.shouldBe(empty);
    }
}

