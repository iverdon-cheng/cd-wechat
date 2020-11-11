package cn.iverdon;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan(basePackages = "cn.iverdon.mapper")
@ComponentScan(basePackages= {"org.n3r.idworker","cn.iverdon"})
public class CdWechatApplication {

    public static void main(String[] args) {
        SpringApplication.run(CdWechatApplication.class, args);
    }

}
