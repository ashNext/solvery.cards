package solvery.cards.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import solvery.cards.controller.converter.DateTimeFormatters.LocalDateFormatter;

@Configuration
public class MvcConfig implements WebMvcConfigurer {


  private final LocaleChangeInterceptor localeChangeInterceptor;

  public MvcConfig(LocaleChangeInterceptor localeChangeInterceptor) {
    this.localeChangeInterceptor = localeChangeInterceptor;
  }

  @Override
  public void addViewControllers(ViewControllerRegistry registry) {
    registry.addViewController("/home").setViewName("home");
    registry.addViewController("/").setViewName("home");
    registry.addViewController("/card").setViewName("card");
    registry.addViewController("/login").setViewName("login");
    registry.addViewController("/registration").setViewName("registration");
    registry.addViewController("/operation").setViewName("operation");
  }

  @Override
  public void addFormatters(FormatterRegistry registry) {
    registry.addFormatter(new LocalDateFormatter());
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(localeChangeInterceptor);
  }
}
