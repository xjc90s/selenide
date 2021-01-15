package com.codeborne.selenide.conditions;

import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.attribute;
import static org.assertj.core.api.Assertions.assertThat;

final class AndTest {
  @Test
  void verifyToString() {
    And and = new And("checked", attribute("checked", "true"), attribute("checked", "on"));
    assertThat(and).hasToString("checked: attribute checked=\"true\" and attribute checked=\"on\"");
  }
}
