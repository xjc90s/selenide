package integration;

import com.codeborne.selenide.junit5.TextReportExtension;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.codeborne.selenide.Condition.name;
import static com.codeborne.selenide.Condition.text;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith({TextReportExtension.class})
final class FrameWaitTest extends ITest {
  private static final Logger logger = LoggerFactory.getLogger(FrameWaitTest.class);

  @BeforeEach
  void setUp() {
    openFile("page_with_frames_with_delays.html");
    setTimeout(1000);
  }

  @RepeatedTest(5)
  void waitsUntilFrameAppears_inner() {
    switchTo().innerFrame("parentFrame");
    $("frame").shouldHave(name("childFrame_1"));
  }

  @RepeatedTest(5)
  void waitsUntilFrameAppears_byTitle() {
    switchTo().frame("leftFrame");
    $("h1").shouldHave(text("Page with dynamic select"));
  }

  @RepeatedTest(100)
  void waitsUntilFrameAppears_byIndex() throws InterruptedException {
    Thread.sleep(100);
    logger.info("************ frames.length={}", getFramesCount());
    logger.info("************ frames[0]={}", getFrameSource(0));
    logger.info("************ frames[1]={}", getFrameSource(1));
    logger.info("************ frames[2]={}", getFrameSource(2));
    logger.info("************ frames[3]={}", getFrameSource(3));

    switchTo().frame(2);

    $("h1").shouldHave(text("Page with JQuery"));
    assertThat(driver().source()).contains("Test::jquery");
  }

  private Number getFramesCount() {
    return driver().executeJavaScript("return window.frames.length");
  }
  private String getFrameSource(int index) {
    try {
      return driver().executeJavaScript("return window.frames[" + index + "].document.body.innerHTML.substring(0, 50)");
    }
    catch (WebDriverException e) {
      return StringUtils.substring(e.toString(), 0, 150);
    }
  }
}
