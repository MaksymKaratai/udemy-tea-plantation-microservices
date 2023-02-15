package com.tea.plantation.services;

import com.tea.plantation.domain.Tea;
import com.tea.plantation.dto.TeaDto;
import com.tea.plantation.mapper.TeaMapper;
import com.tea.plantation.repository.TeaRepository;
import org.springframework.stereotype.Service;

@Service
public class TeaService extends BasicService<Tea, TeaDto, String> {
    public TeaService(TeaMapper mapper, TeaRepository repository) {
        super(mapper, repository);
    }
}
