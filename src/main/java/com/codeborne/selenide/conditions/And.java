package com.codeborne.selenide.conditions;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static com.codeborne.selenide.conditions.ConditionHelpers.merge;
import static java.util.stream.Collectors.joining;

@ParametersAreNonnullByDefault
public class And extends Condition {

  private final List<Condition> conditions;
  private Condition lastFailedCondition;

  public And(String name, Condition condition1, Condition condition2, Condition... conditions) {
    super(name);
    this.conditions = merge(condition1, condition2, conditions);
  }

  @CheckReturnValue
  @Override
  public boolean apply(Driver driver, WebElement element) {
    lastFailedCondition = null;

    for (Condition c : conditions) {
      if (!c.apply(driver, element)) {
        lastFailedCondition = c;
        return false;
      }
    }
    return true;
  }

  @CheckReturnValue
  @Override
  public String actualValue(Driver driver, WebElement element) {
    return lastFailedCondition == null ? null : lastFailedCondition.actualValue(driver, element);
  }

  @Nonnull
  @CheckReturnValue
  @Override
  public String toString() {
    return lastFailedCondition == null ? getDefaultDescription() : lastFailedCondition.toString();
  }

  private String getDefaultDescription() {
    String conditionsToString = conditions.stream().map(Condition::toString).collect(joining(" and "));
    return String.format("%s: %s", getName(), conditionsToString);
  }
}
