package com.fjminbao;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@MapperScan("com.fjminbao.mapper")//@MapperScan("包名")  自动装配指定包下所有Mapper, 省得在每个Mappe接口上写 @Mapper。也叫开启Mapper扫描
//@EnableAsync //启用异步注解功能
@SpringBootApplication
public class ConvertServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConvertServerApplication.class, args);
	}

}
