package com.codeborne.selenide.conditions;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import org.openqa.selenium.WebElement;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static com.codeborne.selenide.conditions.ConditionHelpers.merge;
import static java.util.stream.Collectors.joining;

@ParametersAreNonnullByDefault
public class Or extends Condition {

  private final List<Condition> conditions;

  public Or(String name, Condition condition1, Condition condition2, Condition... conditions) {
    super(name);
    this.conditions = merge(condition1, condition2, conditions);
  }

  @Override
  public boolean apply(Driver driver, WebElement element) {
    for (Condition c : conditions) {
      if (c.apply(driver, element)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public String actualValue(Driver driver, WebElement element) {
    return conditions.stream().map(condition -> condition.actualValue(driver, element)).collect(joining(", "));
  }

  @Override
  public String toString() {
    String conditionsToString = conditions.stream().map(Condition::toString).collect(joining(" or "));
    return String.format("%s: %s", getName(), conditionsToString);
  }
}
