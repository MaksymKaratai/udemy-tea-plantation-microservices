package com.tea.plantation.controller;

import com.tea.plantation.dto.TeaDto;
import com.tea.plantation.services.TeaService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(TeaController.TEA_API)
public class TeaController extends CrudController<TeaDto, String> {
    public static final String TEA_API = "/v1/tea";

    public TeaController(TeaService service) {
        super(service);
    }

    @Override
    public String location() {
        return TEA_API;
    }
}
