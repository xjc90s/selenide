package integration;

import com.codeborne.selenide.ex.FrameNotFoundException;
import com.codeborne.selenide.impl.Waiter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

import java.util.Objects;

import static com.codeborne.selenide.Condition.name;
import static com.codeborne.selenide.Condition.text;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

final class FramesTest extends ITest {
  private final Waiter waiter = new Waiter();

  @BeforeEach
  void openPage() {
    setTimeout(5000);
    openFile("page_with_frames.html");
  }

  private void waitForUrl(String url) {
    waiter.wait("current url", s -> driver().getCurrentFrameUrl().equals(getBaseUrl() + url), 4000, 50);
    assertThat(driver().getCurrentFrameUrl()).isEqualTo(getBaseUrl() + url);
  }

  private void waitForSource(String pageSource) {
    waiter.wait("current page source", s -> driver().source().contains(pageSource), 4000, 50);
    assertThat(driver().source()).contains(pageSource);
  }

  private void waitForTitle(String title) {
    waiter.wait("current page title", s -> Objects.equals(driver().title(), title), 4000, 50);
    assertThat(driver().title()).isEqualTo(title);
  }

  @RepeatedTest(100)
  void canSwitchIntoInnerFrame() {
    waitForTitle("Test::frames");

    switchTo().innerFrame("parentFrame");
    $("iframe").shouldHave(name("childFrame_1"));
    waitForUrl("/page_with_parent_frame.html");

    switchTo().innerFrame("parentFrame", "childFrame_1");
    waitForUrl("/hello_world.txt");
    waitForSource("Hello, WinRar!");

    switchTo().innerFrame("parentFrame", "childFrame_2");
    $("iframe").shouldHave(name("childFrame_2_1"));
    waitForUrl("/page_with_child_frame.html");

    switchTo().innerFrame("parentFrame", "childFrame_2", "childFrame_2_1");
    waitForUrl("/child_frame.txt");
    waitForSource("This is last frame!");

    switchTo().innerFrame("parentFrame");
    waitForUrl("/page_with_parent_frame.html");
    $("iframe").shouldHave(name("childFrame_1"));
  }

  @RepeatedTest(100)
  void switchToInnerFrame_withoutParameters_switchesToDefaultContent() {
    switchTo().innerFrame("parentFrame");
    $("iframe").shouldHave(name("childFrame_1"));

    switchTo().innerFrame();
    $("iframe").shouldHave(name("topFrame"));
  }

  @RepeatedTest(100)
  void canSwitchBetweenFramesByTitle() {
    waitForTitle("Test::frames");

    switchTo().frame("topFrame");
    waitForSource("Hello, WinRar!");

    switchTo().defaultContent();
    switchTo().frame("leftFrame");
    $("h1").shouldHave(text("Page with dynamic select"));

    switchTo().defaultContent();
    switchTo().frame("mainFrame");
    $("h1").shouldHave(text("Page with JQuery"));
  }

  @RepeatedTest(100)
  void canSwitchBetweenFramesByIndex() {
    assumeFalse(browser().isChrome());
    waitForTitle("Test::frames");

    switchTo().frame(0);
    waitForSource("Hello, WinRar!");

    switchTo().defaultContent();
    switchTo().frame(1);
    $("h1").shouldHave(text("Page with dynamic select"));

    switchTo().defaultContent();
    switchTo().frame(2);
    $("h1").shouldHave(text("Page with JQuery"));
  }

  @RepeatedTest(100)
  void throwsNoSuchFrameExceptionWhenSwitchingToAbsentFrameByElement() {
    waitForTitle("Test::frames");
    setTimeout(10);
    assertThatThrownBy(() -> {
      switchTo().frame("mainFrame");
      // $("#log") is present, but not frame.
      switchTo().frame($("#log"));
    })
      .isInstanceOf(FrameNotFoundException.class)
      .hasMessageStartingWith("No frame found with element: <div id=\"log\" displayed:false></div>");
  }

  @RepeatedTest(100)
  void throwsNoSuchFrameExceptionWhenSwitchingToAbsentFrameByTitle() {
    waitForTitle("Test::frames");

    setTimeout(10);
    assertThatThrownBy(() -> {
      switchTo().frame("absentFrame");
    })
      .isInstanceOf(FrameNotFoundException.class)
      .hasMessageStartingWith("No frame found with id/name: absentFrame");
  }

  @RepeatedTest(100)
  void throwsNoSuchFrameExceptionWhenSwitchingToAbsentFrameByIndex() {
    waitForTitle("Test::frames");

    setTimeout(10);
    assertThatThrownBy(() -> {
      switchTo().frame(Integer.MAX_VALUE);
    })
      .isInstanceOf(FrameNotFoundException.class)
      .hasMessageStartingWith("No frame found with index: " + Integer.MAX_VALUE);
  }

  @RepeatedTest(100)
  void attachesScreenshotWhenCannotFrameNotFound() {
    setTimeout(10);
    assertThatThrownBy(() -> switchTo().frame(33))
      .isInstanceOf(FrameNotFoundException.class)
      .hasMessageStartingWith("No frame found with index: 33")
      .hasMessageContaining("Screenshot: file:")
      .hasMessageContaining("Page source: file:")
      .hasMessageContaining("Caused by: TimeoutException:");
  }
}
