package top.yeelei.mall;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@MapperScan(basePackages = "top.yeelei.mall.model.dao")
@EnableSwagger2
public class YeeleiMallApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(YeeleiMallApiApplication.class, args);
    }

}
