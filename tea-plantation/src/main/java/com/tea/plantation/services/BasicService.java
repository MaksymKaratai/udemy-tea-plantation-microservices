package com.tea.plantation.services;

import com.tea.plantation.exception.EntityNotFoundException;
import com.tea.plantation.mapper.EntityMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public abstract class BasicService<Entity, Dto, Id> {
    protected final EntityMapper<Entity, Dto> mapper;
    protected final MongoRepository<Entity, Id> repository;

    public List<Dto> findAll(Pageable pageable) {
        Page<Entity> all = repository.findAll(pageable);
        return all.map(mapper::toDto).getContent();
    }

    public Dto findById(Id id) {
        return repository.findById(id)
                         .map(mapper::toDto)
                         .orElseThrow(() -> new EntityNotFoundException(id));
    }

    public Dto create(Dto dto) {
        Entity entity = mapper.toEntity(dto);
        Entity saved = repository.save(entity);
        return mapper.toDto(saved);
    }

    public void delete(Id id) {
        repository.deleteById(id);
    }
}
