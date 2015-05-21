package ua.net.itlabs.task2;


import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.interactions.Actions;


import static com.codeborne.selenide.CollectionCondition.*;
import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
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


    public void doubleClick(SelenideElement element){
       Actions action = new Actions(getWebDriver());
       action.doubleClick(element.toWebElement()).perform();
   }

    public void addTask(String task) {
        $("#new-todo").setValue(task).pressEnter();
    }

    public void editTask(String task, String taskEdited){
        doubleClick(todos.find(text(task)).find("label"));
        $("input.edit").setValue(taskEdited).pressEnter();
    }

    public void deleteTask(String task){
        todos.find(text(task)).hover();
        todos.find(text(task)).find(".destroy").click();
    }

    public void assertEach(ElementsCollection elements, Condition someCondition) {
        for (SelenideElement element : elements) {
            element.shouldBe(someCondition);
        }
    }

    public void toggleTask(String task){
        todos.find(text(task)).find(".toggle").click();
    }

    public void toggleAll(){
        $("#toggle-all").click();
    }

    public void setAllFilter(){
        $("[href='#/']").click();
    }

    public void setActiveFilter(){
        $("[href='#/active']").click();
    }

    public void setCompletedFilter(){
        $("[href='#/completed']").click();
    }

    @BeforeClass
    public static void openToDoMVC(){
        open("http://todomvc.com/examples/troopjs_require/#");
    }

    @Before
    public void clearData(){
        executeJavaScript("localStorage.clear()");
        open("http://todomvc.com/");
    }

    @Test
    public void testAtAllFilter(){

        openToDoMVC();

        //create task
        addTask(task1);
        addTask(task2);
        addTask(task3);
        addTask(task4);
        todos.shouldHave(exactTexts(task1, task2, task3, task4));

        //check the number of active tasks
        todoCount.shouldHave(text("4"));

        //edit task
        editTask(task1, task1Edited);
        todos.shouldHave(exactTexts(task1Edited, task2, task3, task4));

        //delete task
        deleteTask(task2);
        todos.shouldHave(exactTexts(task1 + "_edited", task3, task4));

        //complete task(filter)
        toggleTask(task4);
        setCompletedFilter();
        todos.filter(visible).shouldHave(exactTexts(task4));


        //check the number of completed tasks
        $("#clear-completed").shouldHave(text("1"));

        //clear completed task
        clearCompleted.click();
        setAllFilter();
        todos.filter(visible).shouldHave(exactTexts(task1 + "_edited", task3));

        //reopen task
        toggleTask(task3);
        setCompletedFilter();
        toggleTask(task3);
        todos.filter(visible).shouldBe(empty);

        setAllFilter();
        todos.shouldHave(texts(task1 + "_edited", task3));

        //complete all task
        toggleAll();

        //clear completed (all)
        clearCompleted.click();
        todos.shouldBe(empty);


    }

    @Test
    public void testAtActiveFilter(){

        openToDoMVC();

        //create tasks
        addTask(task1);
        addTask(task2);
        addTask(task3);
        setActiveFilter();
        assertEach(todos, active);
        todos.shouldHave(exactTexts(task1, task2, task3));

        //edit task
        editTask(task1, task1Edited);
        todos.shouldHave(exactTexts(task1Edited, task2, task3));

        //delete task
        deleteTask(task1Edited);
        todos.shouldHave(exactTexts(task2, task3));

        //complete task
        toggleAll();
        todos.filter(visible).shouldBe(empty);

        // clear completed
        clearCompleted.click();
        todos.shouldBe(empty);

    }

    @Test
    public void testAtCompletedFilter(){

        openToDoMVC();

        //create completed tasks
        addTask(task1);
        addTask(task2);
        addTask(task3);
        toggleAll();
        setCompletedFilter();
        assertEach(todos, completed);
        todos.shouldHave(exactTexts(task1,task2,task3));

        //delete task
        deleteTask(task2);
        todos.shouldHave(exactTexts(task1,task3));

        //reopen task
        toggleTask(task1);
        todoCount.shouldHave(text("1"));
        setActiveFilter();
        todos.filter(visible).shouldHave(exactTexts(task1));

        setCompletedFilter();
        toggleAll();
        todos.filter(visible).shouldHave(exactTexts(task1,task3));

        //clear completed
        clearCompleted.click();
        todos.shouldBe(empty);
    }
}

