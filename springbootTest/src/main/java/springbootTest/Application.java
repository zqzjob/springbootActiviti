package springbootTest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages={"com.demo.controller,com.demo.service"})
@EnableJpaRepositories("com.demo.dao") //jpa扫描dao
@EntityScan("com.demo.entity") //jpa扫描entity
@EnableAutoConfiguration(exclude = {SecurityAutoConfiguration.class}) //没有这个注解访问页面需要登录认证
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
