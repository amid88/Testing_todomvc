package ua.net.itlabs.task1;


import com.codeborne.selenide.Condition;
import org.junit.Test;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.CollectionCondition.exactTexts;
import static com.codeborne.selenide.CollectionCondition.texts;
import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.CollectionCondition.empty;

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

    @Test
    public void testCreateTask() {
        open("http://todomvc.com/examples/troopjs_require/#");
        todos.shouldBe(empty);

        //create tasks
        addTask(task1);
        addTask(task2);
        addTask(task3);
        addTask(task4);
        todos.shouldHave(exactTexts(task1, task2, task3, task4));

        //delete task
        todos.find(text(task2)).hover();
        todos.find(text(task2)).find(".destroy").click();
        todos.shouldHave(exactTexts(task1, task3, task4));

        //mark complete & cleared
        todos.find(text(task4)).find(".toggle").click();
        todos.filter(completed).shouldHave(texts(task4));
        clearCompleted.click();

        todos.shouldHave(exactTexts(task1, task3));
        assertEach(todos, active);

        //mark all completed & clear
        $("#toggle-all").click();
        clearCompleted.click();

        todos.shouldBe(empty);
        
    }
}