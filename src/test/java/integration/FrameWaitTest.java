package integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

import static com.codeborne.selenide.Condition.name;
import static com.codeborne.selenide.Condition.text;

class FrameWaitTest extends ITest {
  @BeforeEach
  void setUp() {
    openFile("page_with_frames_with_delays.html");
  }

  @RepeatedTest(100)
  void waitsUntilFrameAppears_inner() {
    switchTo().innerFrame("parentFrame");
    $("frame").shouldHave(name("childFrame_1"));
  }

  @RepeatedTest(100)
  void waitsUntilFrameAppears_byTitle() {
    switchTo().frame("leftFrame");
    $("h1").shouldHave(text("Page with dynamic select"));
  }
}
