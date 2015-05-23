package ua.net.itlabs.lesson5;


import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static com.codeborne.selenide.CollectionCondition.*;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Selenide.*;

public class TodoTest {

    SelenideElement clearCompleted = $("#clear-completed");
    ElementsCollection todos = $$("#todo-list>li");
    String task1 = "create task1";
    String task2 = "create task2";
    String task3 = "create task3";
    String task4 = "create task4";
    Condition completed = cssClass("completed");
    Condition active = cssClass("active");


    public void addTask(String task) {
        $("#new-todo").setValue(task).pressEnter();
    }

    public void assertEach(ElementsCollection elements, Condition someCondition) {
        for (SelenideElement element : elements) {
            element.shouldBe(someCondition);
        }
    }

    public void toogle(String taskText) {
        todos.findBy(exactText(taskText)).find(".toggle").click();

    }

    public static void checkItemsLeftCounter(int number){
        $("#todo-count>strong").shouldHave(exactText(Integer.toString(number)));
    }

    @BeforeClass
    public static void openToDoMVC(){
        open("http://todomvc.com/");
    }



    @Before
    public void clearData(){
        executeJavaScript("localStorage.clear()");
        open("http://todomvc.com/");
        open("http://todomvc.com/examples/troopjs_require/#");

    }



    @Test
    public void testCreateTask() {
        open("http://todomvc.com/examples/troopjs_require/#");
        todos.shouldBe(CollectionCondition.empty);

        addTask("a");
        addTask("b");
        addTask("c");
        toogle("c");
        todos.filter(visible).shouldHave(texts("a", "b", "c"));


    }


    @Test
    public void testAtAllFilter(){
        //create task
        addTask("aa");
        addTask("bb");
        addTask("cc");

        //edit task
        //delete task
        //complete task
        //filter
        //clear completed task
        //reopen task
        //complete all task
        //clear completed (all)

    }

    @Test
    public void testAtActiveFilter(){
        //create task
        //edit task
        //delete task
        //complete task
        //? clear completed

    }

    @Test
    public void testAtCompletedFilter(){
        //delete task
        //reopen task
        //clear completed


    }


}

