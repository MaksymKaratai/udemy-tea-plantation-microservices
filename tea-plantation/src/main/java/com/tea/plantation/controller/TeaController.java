package com.tea.plantation.controller;

import com.tea.plantation.domain.Tea;
import com.tea.plantation.repository.TeaRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(TeaController.TEA_API)
public class TeaController extends CrudController<Tea, String> {
    public static final String TEA_API = "/v1/tea";

    public TeaController(TeaRepository repo) {
        super(repo);
    }

    @Override
    public String location() {
        return TEA_API;
    }
}
