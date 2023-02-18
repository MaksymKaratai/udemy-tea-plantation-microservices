package com.tea.common.services;

import com.tea.common.mapper.EntityMapper;
import com.tea.common.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public abstract class BasicService<Entity, Dto, Id,
        Repo extends ListCrudRepository<Entity, Id> & ListPagingAndSortingRepository<Entity, Id>> {
    protected final EntityMapper<Entity, Dto> mapper;
    protected final Repo repository;

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
