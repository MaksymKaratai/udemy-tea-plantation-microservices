package com.tea.plantation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tea.plantation.dto.TeaDto;
import com.tea.plantation.services.TeaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;

import static com.tea.plantation.controller.TeaController.TEA_API;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(MongoBeansStub.class)
@WebMvcTest(controllers = TeaController.class)
class TeaControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    TeaService teaService;

    @Test
    void getById() throws Exception {
        var tea = testTea();
        var id = "1";
        tea.setId(id);
        var customerJson = objectMapper.writeValueAsString(tea);

        when(teaService.findById(id)).thenReturn(tea);

        mockMvc.perform(get(TEA_API + "/" + id))
                .andDo(log())
                .andExpect(status().isOk())
                .andExpect(content().json(customerJson));
    }

    @Test
    void create() throws Exception {
        var tea = testTea();
        var id = "1";
        var customerJson = objectMapper.writeValueAsString(tea);
        tea.setId(id);

        when(teaService.create(any())).thenReturn(tea);

        mockMvc.perform(post(TEA_API).contentType(MediaType.APPLICATION_JSON).content(customerJson))
                .andDo(log())
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.header().exists(LOCATION));
    }

    @Test
    void deleteById() throws Exception {
        var id = "1";

        doNothing().when(teaService).delete(id);

        mockMvc.perform(delete(TEA_API + "/" + id))
                .andDo(log())
                .andExpect(status().isNoContent());
    }

    private TeaDto testTea() {
        return TeaDto.builder()
                .name("White Dragon")
                .type("Plain")
                .upc("12345678")
                .price(BigDecimal.valueOf(1))
                .build();
    }
}