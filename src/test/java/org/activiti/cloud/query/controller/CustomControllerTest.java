package org.activiti.cloud.query.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc(secure = false)
@SpringBootTest
public class CustomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testABPMNendpoint() throws Exception {

        //when
        MvcResult result = mockMvc.perform(get("/abpm"))
                .andReturn();

        assertThat(result.getResponse().getContentAsString()).isEqualTo("Greetings from Spring Boot!");
    }

}