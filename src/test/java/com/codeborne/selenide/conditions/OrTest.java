package com.codeborne.selenide.conditions;

import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.attribute;
import static org.assertj.core.api.Assertions.assertThat;

final class OrTest {
  @Test
  void name() {
    Or or = new Or("checked", attribute("checked", "true"), attribute("checked", "on"));
    assertThat(or).hasToString("checked: attribute checked=\"true\" or attribute checked=\"on\"");
  }
}
