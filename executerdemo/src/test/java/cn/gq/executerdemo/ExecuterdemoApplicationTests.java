package cn.gq.executerdemo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ExecuterdemoApplicationTests {

	@Test
	public void contextLoads() throws InterruptedException{
		for(int i = 0;i < 5;i++){
			System.out.print(i);
			Thread.sleep(1000);
		}
	}

}
